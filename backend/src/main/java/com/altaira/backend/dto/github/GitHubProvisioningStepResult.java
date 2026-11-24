package com.altaira.backend.dto.github;

/**
 * Data exchanged by the GitHub API for GitHub provisioning step result.
 *
 * @param step step
 * @param executed executed
 * @param action action being recorded
 * @param reason explanation of the decision
 */
public record GitHubProvisioningStepResult(
        String step,
        boolean executed,
        String action,
        String reason
) {}
