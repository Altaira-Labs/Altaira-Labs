import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;

import javax.lang.model.element.Modifier;
import javax.tools.ToolProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Checks for JavaDoc on declarations written by contributors. JavaDoc itself checks markup,
 * cross-references and tags. This pass also covers private declarations and test helpers, while
 * leaving compiler-supplied constructors, record accessors and local variables alone.
 */
public final class CheckJavaDocs {
    /** Discrepancies printed after all source trees have been visited. */
    private final List<String> missing = new ArrayList<>();
    /** Number of explicit types, members and record components checked so far. */
    private int declarations;

    /** Creates a checker for one invocation from the repository root. */
    private CheckJavaDocs() {}

    /**
     * Parses production and test sources with the JDK compiler and exits unsuccessfully
     * when a source declaration lacks its explanation.
     *
     * @param args optional backend source root, relative to the shell's working directory
     * @throws Exception if the compiler cannot read the supplied sources
     */
    public static void main(String[] args) throws Exception {
        Path root = args.length == 0 ? Path.of("backend", "src") : Path.of(args[0]);
        CheckJavaDocs checker = new CheckJavaDocs();
        var compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) throw new IllegalStateException("Run this check with JDK 21.");

        try (var paths = Files.walk(root); var files = compiler.getStandardFileManager(null, null, null)) {
            var sources = paths.filter(path -> {
                String file = path.getFileName().toString();
                return file.endsWith(".java") && !file.startsWith("._");
            }).sorted().toList();
            if (sources.isEmpty()) throw new IllegalArgumentException("No Java sources under " + root);
            Set<String> packagesWithTypes = new HashSet<>();
            Set<String> packagesWithDocs = new HashSet<>();
            var task = (JavacTask) compiler.getTask(null, files, null, List.of("-proc:none", "--release", "21"),
                    null, files.getJavaFileObjectsFromPaths(sources));
            DocTrees trees = DocTrees.instance(task);
            for (CompilationUnitTree unit : task.parse()) {
                String pkg = unit.getPackageName().toString();
                if (unit.getSourceFile().getName().endsWith("/package-info.java")) {
                    DocCommentTree doc = trees.getDocCommentTree(TreePath.getPath(unit, unit.getPackage()));
                    if (doc != null && !doc.getFullBody().isEmpty()) packagesWithDocs.add(pkg);
                }
                for (Tree node : unit.getTypeDecls()) {
                    if (node instanceof ClassTree type) {
                        packagesWithTypes.add(pkg);
                        checker.checkType(unit, trees, type, type.getSimpleName().toString());
                    }
                }
            }
            packagesWithTypes.removeAll(packagesWithDocs);
            for (String name : packagesWithTypes) checker.missing.add("Undocumented package " + name);
        }
        checker.missing.forEach(System.err::println);
        System.out.printf("%d declarations checked, %d documentation gaps.%n",
                checker.declarations, checker.missing.size());
        if (!checker.missing.isEmpty()) System.exit(1);
    }

    /**
     * Walks the direct members of a type. A record component is described by the corresponding
     * parameter tag on the record because it also defines a field and accessor implicitly.
     *
     * @param unit source file that owns the declaration
     * @param trees documentation lookup for the current parse
     * @param type declaration whose members are being visited
     * @param name qualified nesting path used to pinpoint a missing explanation
     */
    private void checkType(CompilationUnitTree unit, DocTrees trees, ClassTree type, String name) {
        DocCommentTree doc = requireComment(unit, trees, type, name);
        Set<String> components = new HashSet<>();
        if (doc != null) {
            for (DocTree tag : doc.getBlockTags()) {
                if (tag instanceof ParamTree param && !param.isTypeParameter() && !param.getDescription().isEmpty()) {
                    components.add(param.getName().getName().toString());
                }
            }
        }

        for (Tree member : type.getMembers()) {
            if (member instanceof ClassTree nested) {
                checkType(unit, trees, nested, name + "." + nested.getSimpleName());
            } else if (member instanceof MethodTree method) {
                requireComment(unit, trees, method, name + "#" + method.getName());
            } else if (member instanceof VariableTree field) {
                String fieldName = field.getName().toString();
                if (type.getKind() == Tree.Kind.RECORD && !field.getModifiers().getFlags().contains(Modifier.STATIC)) {
                    declarations++;
                    if (!components.contains(fieldName)) missing.add(name + " lacks @param " + fieldName);
                } else {
                    requireComment(unit, trees, field, name + "#" + fieldName);
                }
            }
        }
    }

    /**
     * Resolves JavaDoc at its exact syntax node, so comments on adjacent members do not
     * accidentally count toward coverage.
     *
     * @param unit source file containing the node
     * @param trees documentation lookup for the current parse
     * @param node explicit declaration to check
     * @param label name used in diagnostics
     * @return the attached JavaDoc, or null when no comment exists
     */
    private DocCommentTree requireComment(CompilationUnitTree unit, DocTrees trees, Tree node, String label) {
        declarations++;
        DocCommentTree doc = trees.getDocCommentTree(TreePath.getPath(unit, node));
        if (doc == null || doc.getFullBody().isEmpty()) {
            missing.add(unit.getPackageName() + "." + label);
        }
        return doc;
    }
}
