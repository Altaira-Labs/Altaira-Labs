package com.altaira.backend.provisioning;

import com.altaira.backend.model.AutomationLevel;
import com.altaira.backend.model.ProvisioningRoute;

import java.util.List;

/**
 * Records the chosen delivery route, its reasons and the tools, risks and manual steps for one service track.
 *
 * @param track service track within the delivery plan
 * @param route delivery route selected by the planning policy
 * @param ruleId identifier of the rule behind the decision
 * @param matchedSignals observations that triggered the planning rule
 * @param reason explanation of the decision
 * @param confidence confidence
 * @param requiresManualDecision requires manual decision
 * @param automationLevel degree of automation associated with the step
 * @param tools tools included in the delivery decision
 * @param items items
 * @param manualSteps work that still requires a person
 * @param risks concerns to consider before carrying out the plan
 */
public record TrackDecision(
        ProvisioningTrack track,
        ProvisioningRoute route,
        String ruleId,
        List<String> matchedSignals,
        String reason,
        double confidence,
        boolean requiresManualDecision,
        AutomationLevel automationLevel,
        List<ProvisioningDecision.ToolDecision> tools,
        List<ProvisioningDecision.PlanItemDecision> items,
        List<ProvisioningDecision.ManualStepDecision> manualSteps,
        List<String> risks
) {
    /**
     * Captures an immutable copy of the list inputs for this track decision.
     *
     * @param track service track within the delivery plan
     * @param route delivery route selected by the planning policy
     * @param ruleId identifier of the rule behind the decision
     * @param matchedSignals observations that triggered the planning rule
     * @param reason explanation of the decision
     * @param confidence confidence
     * @param requiresManualDecision requires manual decision
     * @param automationLevel degree of automation associated with the step
     * @param tools tools included in the delivery decision
     * @param items items
     * @param manualSteps work that still requires a person
     * @param risks concerns to consider before carrying out the plan
     */
    public TrackDecision {
        matchedSignals = List.copyOf(matchedSignals);
        tools = List.copyOf(tools);
        items = List.copyOf(items);
        manualSteps = List.copyOf(manualSteps);
        risks = List.copyOf(risks);
    }
}
