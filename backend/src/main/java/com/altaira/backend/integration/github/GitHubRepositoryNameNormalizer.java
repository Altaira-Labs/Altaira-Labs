package com.altaira.backend.integration.github;

import java.text.Normalizer;
import java.util.Locale;

/** Converts a proposed repository name into a format accepted by the GitHub provisioning adapter. */
public final class GitHubRepositoryNameNormalizer {

    /** Constructs {@code GitHubRepositoryNameNormalizer} with its default state. */
    private GitHubRepositoryNameNormalizer() {}

    /**
     * Strips accents, lowercases the name and replaces unsupported repository-name characters before validating the result. Uses locale-independent case normalization.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    public static String normalize(String value) {
        String normalized = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9._-]+", "-")
                .replaceAll("^[._-]+|[._-]+$", "")
                .replaceAll("-{2,}", "-");

        if (normalized.isBlank()) {
            return "altaira-client-workspace";
        }
        if (normalized.length() > 80) {
            return normalized.substring(0, 80).replaceAll("[._-]+$", "");
        }
        return normalized;
    }
}
