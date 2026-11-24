package com.altaira.backend.dto.github;

/**
 * Data exchanged by the GitHub API for GitHub repository summary.
 *
 * @param name name used to identify the item
 * @param fullName name as displayed to a person
 * @param privateRepository persistence access for private
 * @param defaultBranch default branch
 */
public record GitHubRepositorySummary(
        String name,
        String fullName,
        boolean privateRepository,
        String defaultBranch
) {}
