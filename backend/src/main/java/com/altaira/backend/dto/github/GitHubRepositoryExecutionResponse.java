package com.altaira.backend.dto.github;

import java.util.UUID;

/**
 * Fields the API returns for GitHub repository execution.
 *
 * @param planId identifier of the saved provisioning plan
 * @param runId identifier of the provisioning run
 * @param stepId identifier of the individual execution step
 * @param provider integration adapter in use
 * @param status current state in the workflow
 * @param attempts attempts
 * @param dryRun whether the operation is being simulated
 * @param executionAllowed whether the plan allows an execution step
 * @param created whether a resource was created
 * @param reused whether an existing resource was used
 * @param externalResourceId identifier assigned by the external provider
 * @param externalUrl link to the resource in the external system
 * @param message text associated with the operation
 */
public record GitHubRepositoryExecutionResponse(
        UUID planId,
        UUID runId,
        UUID stepId,
        String provider,
        String status,
        int attempts,
        boolean dryRun,
        boolean executionAllowed,
        boolean created,
        boolean reused,
        String externalResourceId,
        String externalUrl,
        String message
) {}
