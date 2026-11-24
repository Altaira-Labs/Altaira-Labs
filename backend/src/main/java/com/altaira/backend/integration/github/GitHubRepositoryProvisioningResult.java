package com.altaira.backend.integration.github;

/**
 * Outcome of GitHub repository provisioning, shared with the component that requested the operation.
 *
 * @param created whether a resource was created
 * @param reused whether an existing resource was used
 * @param externalResourceId identifier assigned by the external provider
 * @param fullName name as displayed to a person
 * @param externalUrl link to the resource in the external system
 * @param visibility audience allowed to see the item
 */
public record GitHubRepositoryProvisioningResult(
        boolean created,
        boolean reused,
        String externalResourceId,
        String fullName,
        String externalUrl,
        String visibility
) {}
