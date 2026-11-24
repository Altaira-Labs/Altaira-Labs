package com.altaira.backend.dto.github;

import jakarta.validation.constraints.NotBlank;

/**
 * Explicit confirmation from staff requesting the approved plan’s GitHub repository operation.
 *
 * @param confirmation confirmation text checked by the execution service before contacting the provider
 */
public record ExecuteGitHubRepositoryRequest(
        @NotBlank(message = "confirmation is required") String confirmation
) {}
