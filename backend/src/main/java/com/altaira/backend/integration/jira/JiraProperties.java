package com.altaira.backend.integration.jira;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

/** Binds the server-side Jira connection, project and credential settings. */
@Component
public class JiraProperties {

    /** Project key used by Jira properties. */
    private static final Pattern PROJECT_KEY = Pattern.compile("[A-Z][A-Z0-9_]{1,19}");

    /** Whether the capability is enabled. */
    private final boolean enabled;
    /** Whether provisioning is enabled. */
    private final boolean provisioningEnabled;
    /** Whether the operation is being simulated. */
    private final boolean dryRun;
    /** URL for base. */
    private final String baseUrl;
    /** Contact email address. */
    private final String email;
    /** Credential for api. */
    private final String apiToken;
    /** Key identifying the project. */
    private final String projectKey;
    /** Issue type. */
    private final String issueType;

    /**
     * Constructs {@code JiraProperties} using the supplied enabled, provisioning enabled, dry run and remaining fields.
     *
     * @param enabled whether the capability is enabled
     * @param provisioningEnabled whether provisioning is enabled
     * @param dryRun whether the operation is being simulated
     * @param baseUrl URL for base
     * @param email contact email address
     * @param apiToken credential for api
     * @param projectKey key identifying the project
     * @param issueType issue type
     */
    public JiraProperties(
            @Value("${altaira.jira.enabled:false}") boolean enabled,
            @Value("${altaira.jira.provisioning.enabled:false}") boolean provisioningEnabled,
            @Value("${altaira.jira.dry-run:true}") boolean dryRun,
            @Value("${altaira.jira.base-url:}") String baseUrl,
            @Value("${altaira.jira.email:}") String email,
            @Value("${altaira.jira.api-token:}") String apiToken,
            @Value("${altaira.jira.project-key:}") String projectKey,
            @Value("${altaira.jira.issue-type:Task}") String issueType
    ) {
        this.enabled = enabled;
        this.provisioningEnabled = provisioningEnabled;
        this.dryRun = dryRun;
        this.baseUrl = trim(baseUrl).replaceAll("/+$", "");
        this.email = trim(email);
        this.apiToken = trim(apiToken);
        this.projectKey = trim(projectKey).toUpperCase();
        this.issueType = trim(issueType).isBlank() ? "Task" : trim(issueType);
    }

    /**
     * Checks whether the operation can mutate resources.
     *
     * @return true if the caller can mutate resources
     */
    public boolean canMutateResources() {
        return enabled && provisioningEnabled && !dryRun;
    }

    /**
     * Checks whether the operation can attempt connection.
     *
     * @return true if the caller can attempt connection
     */
    public boolean canAttemptConnection() {
        return enabled
                && !baseUrl.isBlank()
                && !email.isBlank()
                && !apiToken.isBlank()
                && PROJECT_KEY.matcher(projectKey).matches();
    }

    /**
     * Resolves the resource path against the configured provider API.
     *
     * @param path path used to locate the target
     * @return the uri
     */
    public URI apiUri(String path) {
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        try {
            URI uri = new URI(baseUrl + normalizedPath);
            boolean secure = "https".equalsIgnoreCase(uri.getScheme());
            boolean localTest = "http".equalsIgnoreCase(uri.getScheme())
                    && ("127.0.0.1".equals(uri.getHost()) || "localhost".equalsIgnoreCase(uri.getHost()));
            if ((!secure && !localTest) || uri.getUserInfo() != null || uri.getHost() == null) {
                throw JiraIntegrationException.safe("Jira base URL is not an approved HTTPS endpoint.");
            }
            return uri;
        } catch (URISyntaxException exception) {
            throw JiraIntegrationException.safe("Jira base URL is invalid. Check JIRA_BASE_URL.");
        }
    }

    /**
     * Returns the contact email address for this Jira properties.
     *
     * @return contact email address
     */
    public String email() {
        return email;
    }

    /**
     * Returns the credential for api for this Jira properties.
     *
     * @return credential for api
     */
    String apiTokenConfiguredValue() {
        return apiToken;
    }

    /**
     * Returns the key identifying the project for this Jira properties.
     *
     * @return key identifying the project
     */
    public String projectKey() {
        return projectKey;
    }

    /** {@return issue type} */
    public String issueType() {
        return issueType;
    }

    /**
     * Trims surrounding whitespace. Absent values use an empty string.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
