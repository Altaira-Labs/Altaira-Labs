package com.altaira.backend.integration.provisioning;

import com.altaira.backend.model.AutomationLevel;

/** Describes how a tool prepares a proposed resource and whether this adapter supports execution. */
public interface ProvisioningProvider {
    /**
     * Returns the stable identifier used to look up this provider.
     *
     * @return the resulting text
     */
    String key();
    /**
     * Returns the provider label shown in the plan.
     *
     * @return the resulting text
     */
    String displayName();
    /**
     * Reports how much of the requested delivery this adapter supports.
     *
     * @return the automation level
     */
    AutomationLevel automationLevel();

    /**
     * Builds the provider result for a requested resource.
     *
     * @param request input carrying provisioning
     * @return the provisioning result
     */
    default ProvisioningResult prepare(ProvisioningRequest request) {
        if (request.blockWhenCredentialsMissing() && !request.credentialsConfigured()) {
            return ProvisioningResult.blocked(request);
        }
        return ProvisioningResult.dryRun(request);
    }

    /**
     * Returns {@code false} for execution supported on this provisioning provider.
     *
     * @return {@code false} for this implementation
     */
    default boolean executionSupported() {
        return false;
    }
}
