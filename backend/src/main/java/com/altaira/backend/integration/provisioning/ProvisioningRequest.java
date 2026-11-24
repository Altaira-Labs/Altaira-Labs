package com.altaira.backend.integration.provisioning;

import java.util.Map;

/**
 * Groups the provisioning request data used by provisioning.
 *
 * @param trackKey key identifying the track
 * @param action action being recorded
 * @param inputSummary input summary
 * @param credentialsConfigured credentials configured
 * @param blockWhenCredentialsMissing block when credentials missing
 * @param missingCredentialsMessage missing credentials message
 */
public record ProvisioningRequest(
        String trackKey,
        String action,
        Map<String, Object> inputSummary,
        boolean credentialsConfigured,
        boolean blockWhenCredentialsMissing,
        String missingCredentialsMessage
) {
    /**
     * Constructs {@code ProvisioningRequest} using the supplied track key, action, input summary and remaining fields.
     *
     * @param trackKey key identifying the track
     * @param action action being recorded
     * @param inputSummary input summary
     * @param credentialsConfigured credentials configured
     * @param blockWhenCredentialsMissing block when credentials missing
     * @param missingCredentialsMessage missing credentials message
     */
    public ProvisioningRequest {
        inputSummary = inputSummary == null ? Map.of() : Map.copyOf(inputSummary);
    }
}
