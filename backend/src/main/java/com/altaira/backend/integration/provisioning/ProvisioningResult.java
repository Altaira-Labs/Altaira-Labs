package com.altaira.backend.integration.provisioning;

import com.altaira.backend.model.ProvisioningStepStatus;

import java.util.Map;

/**
 * Outcome of provisioning, shared with the component that requested the operation.
 *
 * @param status current state in the workflow
 * @param manualActionRequired manual action required
 * @param safeError error detail suitable for showing to the caller
 * @param externalResourceId identifier assigned by the external provider
 * @param externalUrl link to the resource in the external system
 * @param inputSummary input summary
 */
public record ProvisioningResult(
        ProvisioningStepStatus status,
        boolean manualActionRequired,
        String safeError,
        String externalResourceId,
        String externalUrl,
        Map<String, Object> inputSummary
) {
    /**
     * Constructs {@code ProvisioningResult} using the supplied status, manual action required, safe error and remaining fields.
     *
     * @param status current state in the workflow
     * @param manualActionRequired manual action required
     * @param safeError error detail suitable for showing to the caller
     * @param externalResourceId identifier assigned by the external provider
     * @param externalUrl link to the resource in the external system
     * @param inputSummary input summary
     */
    public ProvisioningResult {
        inputSummary = inputSummary == null ? Map.of() : Map.copyOf(inputSummary);
    }

    /**
     * Builds the preview result describing what would be provisioned.
     *
     * @param request input carrying provisioning
     * @return the provisioning result
     */
    public static ProvisioningResult dryRun(ProvisioningRequest request) {
        return new ProvisioningResult(
                ProvisioningStepStatus.DRY_RUN,
                false,
                null,
                null,
                null,
                request.inputSummary()
        );
    }

    /**
     * Builds the outcome for a provider step that cannot proceed.
     *
     * @param request input carrying provisioning
     * @return the provisioning result
     */
    public static ProvisioningResult blocked(ProvisioningRequest request) {
        return new ProvisioningResult(
                ProvisioningStepStatus.BLOCKED,
                true,
                request.missingCredentialsMessage(),
                null,
                null,
                request.inputSummary()
        );
    }

    /**
     * Returns an outcome explaining that no adapter is registered for the selected provider.
     *
     * @param request input carrying provisioning
     * @return the provisioning result
     */
    public static ProvisioningResult unregistered(ProvisioningRequest request) {
        return new ProvisioningResult(
                ProvisioningStepStatus.DRY_RUN,
                true,
                "No execution adapter is registered; admin review is required before provisioning.",
                null,
                null,
                request.inputSummary()
        );
    }
}
