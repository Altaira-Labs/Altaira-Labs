package com.altaira.backend.dto.github;

import java.util.List;
import java.util.Map;

/**
 * Fields the API returns for GitHub integration status.
 *
 * @param enabled whether the capability is enabled
 * @param provisioningEnabled whether provisioning is enabled
 * @param dryRun whether the operation is being simulated
 * @param org org
 * @param appConfigured app configured
 * @param installationIdConfigured installation id configured
 * @param installationReachable installation reachable
 * @param repositoriesVisible repositories visible
 * @param permissions permissions
 * @param errors errors
 */
public record GitHubIntegrationStatusResponse(
        boolean enabled,
        boolean provisioningEnabled,
        boolean dryRun,
        String org,
        boolean appConfigured,
        boolean installationIdConfigured,
        boolean installationReachable,
        GitHubRepositoriesVisible repositoriesVisible,
        Map<String, String> permissions,
        List<String> errors
) {}
