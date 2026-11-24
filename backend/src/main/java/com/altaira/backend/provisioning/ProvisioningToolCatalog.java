package com.altaira.backend.provisioning;

import com.altaira.backend.integration.provisioning.ProvisioningProvider;
import com.altaira.backend.integration.provisioning.ProvisioningProviderRegistry;
import com.altaira.backend.model.AutomationLevel;

import java.util.Map;

/** Defines the tools the planner can recommend and the level of automation associated with each. */
public final class ProvisioningToolCatalog {

    /** Display names used by provisioning tool catalog. */
    private static final Map<String, String> DISPLAY_NAMES = Map.ofEntries(
            Map.entry("ALTAIRA", "Altaira Workspace"),
            Map.entry("AUTH", "Authentication"),
            Map.entry("AUTOMATION_PLATFORM", "Automation platform"),
            Map.entry("BI", "Business intelligence platform"),
            Map.entry("BOOKING_SAAS", "Booking SaaS"),
            Map.entry("CALENDAR", "Calendar integration"),
            Map.entry("CMS", "Content management system"),
            Map.entry("ECOMMERCE_PLATFORM", "Ecommerce platform"),
            Map.entry("EXTERNAL_CRM", "External CRM"),
            Map.entry("FIGMA", "Figma"),
            Map.entry("I18N", "Internationalization"),
            Map.entry("QUEUE", "Queue"),
            Map.entry("REALTIME", "Realtime infrastructure"),
            Map.entry("TASKS", "Task management"),
            Map.entry("WEBHOOK", "Webhook intake")
    );

    /** Provider registry. */
    private final ProvisioningProviderRegistry providerRegistry;

    /**
     * Constructs {@code ProvisioningToolCatalog} using the supplied provider registry.
     *
     * @param providerRegistry provider registry
     */
    public ProvisioningToolCatalog(ProvisioningProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    /**
     * Marks a tool as selected together with its rationale.
     *
     * @param key key used to identify the item
     * @param required whether the item is required
     * @param reason explanation of the decision
     * @return the value produced by this operation
     */
    public ProvisioningDecision.ToolDecision select(String key, boolean required, String reason) {
        ProvisioningProvider provider = providerRegistry.find(key).orElse(null);
        return new ProvisioningDecision.ToolDecision(
                key,
                provider == null ? DISPLAY_NAMES.getOrDefault(key, key) : provider.displayName(),
                "selected",
                provider == null ? AutomationLevel.A3 : provider.automationLevel(),
                required,
                reason
        );
    }

    /**
     * Records why a tool was excluded from the plan.
     *
     * @param key key used to identify the item
     * @param reason explanation of the decision
     * @return the value produced by this operation
     */
    public ProvisioningDecision.ToolDecision exclude(String key, String reason) {
        ProvisioningProvider provider = providerRegistry.find(key).orElse(null);
        return new ProvisioningDecision.ToolDecision(
                key,
                provider == null ? DISPLAY_NAMES.getOrDefault(key, key) : provider.displayName(),
                "excluded",
                AutomationLevel.M,
                false,
                reason
        );
    }

    /**
     * Builds an item for inclusion in a proposed delivery plan.
     *
     * @param provider integration adapter in use
     * @param type kind of item or operation
     * @param name name used to identify the item
     * @param action action being recorded
     * @param reason explanation of the decision
     * @return the value produced by this operation
     */
    public ProvisioningDecision.PlanItemDecision item(
            String provider,
            String type,
            String name,
            String action,
            String reason
    ) {
        return new ProvisioningDecision.PlanItemDecision(provider, type, name, action, true, reason);
    }

    /**
     * Builds a manual delivery step and records why human action is needed.
     *
     * @param key key used to identify the item
     * @param title short heading shown to a reader
     * @param reason explanation of the decision
     * @param required whether the item is required
     * @return the value produced by this operation
     */
    public ProvisioningDecision.ManualStepDecision manual(
            String key,
            String title,
            String reason,
            boolean required
    ) {
        return new ProvisioningDecision.ManualStepDecision(key, title, reason, required);
    }
}
