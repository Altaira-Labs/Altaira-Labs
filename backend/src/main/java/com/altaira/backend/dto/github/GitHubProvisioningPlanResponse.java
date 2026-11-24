package com.altaira.backend.dto.github;

import java.util.List;
import java.util.Map;

/**
 * Fields the API returns for GitHub provisioning plan.
 *
 * @param dryRun whether the operation is being simulated
 * @param enabled whether the capability is enabled
 * @param provisioningEnabled whether provisioning is enabled
 * @param executionAllowed whether the plan allows an execution step
 * @param org org
 * @param repoToCreate repo to create
 * @param normalizedName normalized name
 * @param visibility audience allowed to see the item
 * @param description human-readable description
 * @param initialFilesProposed initial files proposed
 * @param labelsProposed labels proposed
 * @param issuesProposed issues proposed
 * @param risks concerns to consider before carrying out the plan
 * @param manualSteps work that still requires a person
 * @param preparedSteps prepared steps
 */
public record GitHubProvisioningPlanResponse(
        boolean dryRun,
        boolean enabled,
        boolean provisioningEnabled,
        boolean executionAllowed,
        String org,
        String repoToCreate,
        String normalizedName,
        String visibility,
        String description,
        List<String> initialFilesProposed,
        List<String> labelsProposed,
        List<String> issuesProposed,
        List<String> risks,
        List<String> manualSteps,
        Map<String, GitHubProvisioningStepResult> preparedSteps
) {}
