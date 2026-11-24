package com.altaira.backend.provisioning;

import java.util.List;
import java.util.Map;

/**
 * Groups the provisioning audit scenario data used by provisioning.
 *
 * @param id unique identifier of the record
 * @param service service in scope
 * @param name name used to identify the item
 * @param demo whether this scenario is part of the demonstration set
 * @param formKey key selecting the discovery questionnaire
 * @param responses answers supplied in the discovery questionnaire
 * @param recommendedServices catalogue services proposed after discovery
 * @param expected expected outcome for the scenario
 * @param missingFormFields missing form fields
 */
public record ProvisioningAuditScenario(
        String id,
        String service,
        String name,
        boolean demo,
        String formKey,
        Map<String, Object> responses,
        List<String> recommendedServices,
        Expected expected,
        List<String> missingFormFields
) {
    /**
     * Groups the expected data used by provisioning audit scenario.
     *
     * @param requirements requirements
     * @param route delivery route selected by the planning policy
     * @param includedTools included tools
     * @param excludedTools excluded tools
     * @param automationLevel degree of automation associated with the step
     * @param manualSteps work that still requires a person
     * @param riskKeywords risk keywords
     */
    public record Expected(
            List<String> requirements,
            String route,
            List<String> includedTools,
            List<String> excludedTools,
            String automationLevel,
            List<String> manualSteps,
            List<String> riskKeywords
    ) {}
}
