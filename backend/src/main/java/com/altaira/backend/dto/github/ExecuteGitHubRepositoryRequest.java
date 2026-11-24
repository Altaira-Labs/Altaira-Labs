package com.altaira.backend.dto.github;

import jakarta.validation.constraints.NotBlank;

/**
 * Input for execute GitHub repository in the GitHub API.
 *
 * @param confirmation confirmation
 */
public record ExecuteGitHubRepositoryRequest(
        @NotBlank(message = "confirmation is required") String confirmation
) {}
