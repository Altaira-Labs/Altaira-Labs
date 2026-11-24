package com.altaira.backend.provisioning;

import java.util.List;

/**
 * Outcome of provisioning scenario audit, shared with the component that requested the operation.
 *
 * @param scenario scenario
 * @param actualRequirements actual requirements
 * @param actualRoute actual route
 * @param actualTracks actual tracks
 * @param actualRuleIds identifiers of the actual rule records
 * @param actualSharedResources actual shared resources
 * @param actualIncludedTools actual included tools
 * @param actualExcludedTools actual excluded tools
 * @param actualAutomationLevel actual automation level
 * @param actualManualSteps actual manual steps
 * @param actualRisks actual risks
 * @param mismatches mismatches
 */
record ProvisioningScenarioAuditResult(
        ProvisioningAuditScenario scenario,
        List<String> actualRequirements,
        String actualRoute,
        List<String> actualTracks,
        List<String> actualRuleIds,
        List<String> actualSharedResources,
        List<String> actualIncludedTools,
        List<String> actualExcludedTools,
        String actualAutomationLevel,
        List<String> actualManualSteps,
        List<String> actualRisks,
        List<String> mismatches
) {
    /**
     * Determines whether passes applies.
     *
     * @return true when the check succeeds
     */
    boolean passes() {
        return mismatches.isEmpty();
    }
}
