package com.altaira.backend.dto.provisioning;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Fields the API returns for provisioning plan.
 *
 * @param id unique identifier of the record
 * @param leadId identifier of the source sales enquiry
 * @param assessmentId identifier of the assessment attached to the lead
 * @param route delivery route selected by the planning policy
 * @param automationLevel degree of automation associated with the step
 * @param automationScope automation scope
 * @param status current state in the workflow
 * @param dryRun whether the operation is being simulated
 * @param executionAllowed whether the plan allows an execution step
 * @param normalizedRequirements normalized requirements
 * @param decisionReason decision reason
 * @param costEstimate cost estimate
 * @param risks concerns to consider before carrying out the plan
 * @param tools tools included in the delivery decision
 * @param items items
 * @param manualSteps work that still requires a person
 * @param externalResources external resources
 * @param tracks tracks
 * @param sharedResources shared resources
 * @param createdAt time the record was created
 * @param updatedAt time of the most recent update
 */
public record ProvisioningPlanResponse(
        UUID id,
        UUID leadId,
        UUID assessmentId,
        String route,
        String automationLevel,
        String automationScope,
        String status,
        boolean dryRun,
        boolean executionAllowed,
        Map<String, Boolean> normalizedRequirements,
        String decisionReason,
        String costEstimate,
        List<String> risks,
        List<Tool> tools,
        List<PlanItem> items,
        List<ManualStep> manualSteps,
        List<ExternalResource> externalResources,
        List<Track> tracks,
        List<SharedResource> sharedResources,
        Instant createdAt,
        Instant updatedAt
) {
    /**
     * Represents tool in the provisioning plan response payload.
     *
     * @param id unique identifier of the record
     * @param key key used to identify the item
     * @param displayName user-facing label
     * @param selectionState current selection state of the proposed tool
     * @param automationLevel degree of automation associated with the step
     * @param required whether the item is required
     * @param reason explanation of the decision
     */
    public record Tool(
            UUID id,
            String key,
            String displayName,
            String selectionState,
            String automationLevel,
            boolean required,
            String reason
    ) {}

    /**
     * Represents plan item in the provisioning plan response payload.
     *
     * @param id unique identifier of the record
     * @param providerKey key identifying the integration provider
     * @param resourceType kind of external or internal resource
     * @param resourceName name assigned to the resource
     * @param action action being recorded
     * @param status current state in the workflow
     * @param required whether the item is required
     * @param reason explanation of the decision
     */
    public record PlanItem(
            UUID id,
            String providerKey,
            String resourceType,
            String resourceName,
            String action,
            String status,
            boolean required,
            String reason
    ) {}

    /**
     * Represents manual step in the provisioning plan response payload.
     *
     * @param id unique identifier of the record
     * @param providerKey key identifying the integration provider
     * @param title short heading shown to a reader
     * @param reason explanation of the decision
     * @param required whether the item is required
     * @param status current state in the workflow
     */
    public record ManualStep(
            UUID id,
            String providerKey,
            String title,
            String reason,
            boolean required,
            String status
    ) {}

    /**
     * Represents external resource in the provisioning plan response payload.
     *
     * @param id unique identifier of the record
     * @param providerKey key identifying the integration provider
     * @param resourceType kind of external or internal resource
     * @param externalResourceId identifier assigned by the external provider
     * @param externalUrl link to the resource in the external system
     * @param status current state in the workflow
     * @param idempotencyKey key used to recognize a repeated operation
     */
    public record ExternalResource(
            UUID id,
            String providerKey,
            String resourceType,
            String externalResourceId,
            String externalUrl,
            String status,
            String idempotencyKey
    ) {}

    /**
     * Represents track in the provisioning plan response payload.
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
     * @param manualSteps work that still requires a person
     * @param risks concerns to consider before carrying out the plan
     */
    public record Track(
            String track,
            String route,
            String ruleId,
            List<String> matchedSignals,
            String reason,
            double confidence,
            boolean requiresManualDecision,
            String automationLevel,
            List<DecisionTool> tools,
            List<DecisionManualStep> manualSteps,
            List<String> risks
    ) {}

    /**
     * Represents decision tool in the provisioning plan response payload.
     *
     * @param key key used to identify the item
     * @param displayName user-facing label
     * @param selectionState current selection state of the proposed tool
     * @param automationLevel degree of automation associated with the step
     * @param required whether the item is required
     * @param reason explanation of the decision
     */
    public record DecisionTool(
            String key,
            String displayName,
            String selectionState,
            String automationLevel,
            boolean required,
            String reason
    ) {}

    /**
     * Represents decision manual step in the provisioning plan response payload.
     *
     * @param providerKey key identifying the integration provider
     * @param title short heading shown to a reader
     * @param reason explanation of the decision
     * @param required whether the item is required
     */
    public record DecisionManualStep(
            String providerKey,
            String title,
            String reason,
            boolean required
    ) {}

    /**
     * Represents shared resource in the provisioning plan response payload.
     *
     * @param key key used to identify the item
     * @param displayName user-facing label
     * @param selectionState current selection state of the proposed tool
     * @param automationLevel degree of automation associated with the step
     * @param required whether the item is required
     * @param reason explanation of the decision
     * @param usedByTracks used by tracks
     */
    public record SharedResource(
            String key,
            String displayName,
            String selectionState,
            String automationLevel,
            boolean required,
            String reason,
            List<String> usedByTracks
    ) {}
}
