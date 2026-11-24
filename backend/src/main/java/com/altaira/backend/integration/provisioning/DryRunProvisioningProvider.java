package com.altaira.backend.integration.provisioning;

import com.altaira.backend.model.AutomationLevel;

/** Describes the result a provider would produce while making no external changes. */
abstract class DryRunProvisioningProvider implements ProvisioningProvider {

    /** Key used to identify the item. */
    private final String key;
    /** User-facing label. */
    private final String displayName;
    /** Degree of automation associated with the step. */
    private final AutomationLevel automationLevel;

    /**
     * Constructs {@code DryRunProvisioningProvider} using the supplied key, display name, automation level.
     *
     * @param key key used to identify the item
     * @param displayName user-facing label
     * @param automationLevel degree of automation associated with the step
     */
    protected DryRunProvisioningProvider(String key, String displayName, AutomationLevel automationLevel) {
        this.key = key;
        this.displayName = displayName;
        this.automationLevel = automationLevel;
    }

    /**
     * Returns the key used to identify the item for this dry run provisioning provider.
     *
     * @return key used to identify the item
     */
    @Override
    public String key() {
        return key;
    }

    /**
     * Returns the user-facing label for this dry run provisioning provider.
     *
     * @return user-facing label
     */
    @Override
    public String displayName() {
        return displayName;
    }

    /**
     * Returns the degree of automation associated with the step for this dry run provisioning provider.
     *
     * @return degree of automation associated with the step
     */
    @Override
    public AutomationLevel automationLevel() {
        return automationLevel;
    }
}
