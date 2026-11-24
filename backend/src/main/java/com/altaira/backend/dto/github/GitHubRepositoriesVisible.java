package com.altaira.backend.dto.github;

import java.util.List;

/**
 * Data exchanged by the GitHub API for GitHub repositories visible.
 *
 * @param count number of matching items
 * @param repositories repositories
 */
public record GitHubRepositoriesVisible(
        int count,
        List<GitHubRepositorySummary> repositories
) {}
