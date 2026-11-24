package com.altaira.backend.service;

import com.altaira.backend.dto.provisioning.CreateProvisioningDryRunRequest;
import com.altaira.backend.dto.provisioning.ProvisioningPlanResponse;
import com.altaira.backend.entity.*;
import com.altaira.backend.exception.LeadNotFoundException;
import com.altaira.backend.integration.provisioning.ProvisioningProviderRegistry;
import com.altaira.backend.model.ProvisioningPlanStatus;
import com.altaira.backend.provisioning.ProvisioningDecision;
import com.altaira.backend.provisioning.ProvisioningDecisionEngine;
import com.altaira.backend.provisioning.ProvisioningRequirementNormalizer;
import com.altaira.backend.provisioning.RequirementSet;
import com.altaira.backend.provisioning.SharedResourceDecision;
import com.altaira.backend.provisioning.TrackDecision;
import com.altaira.backend.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Builds a reviewable delivery plan from a lead assessment. The plan records the chosen tools,
 * resources and manual steps before anyone requests an external provider operation.
 */
@Service
@Transactional
public class ProvisioningPlanService {

    /** Persistence access for lead. */
    private final LeadRepository leadRepository;
    /** Persistence access for assessment. */
    private final LeadAssessmentRepository assessmentRepository;
    /** Persistence access for plan. */
    private final ProvisioningPlanRepository planRepository;
    /** Persistence access for item. */
    private final ProvisioningPlanItemRepository itemRepository;
    /** Persistence access for tool. */
    private final ProvisioningSelectedToolRepository toolRepository;
    /** Persistence access for manual step. */
    private final ProvisioningManualStepRepository manualStepRepository;
    /** Persistence access for external resource. */
    private final ProvisioningExternalResourceRepository externalResourceRepository;
    /** Requirement normalizer. */
    private final ProvisioningRequirementNormalizer requirementNormalizer;
    /** Decision engine. */
    private final ProvisioningDecisionEngine decisionEngine;
    /** Provider registry. */
    private final ProvisioningProviderRegistry providerRegistry;
    /** Jackson mapper used to read or write JSON. */
    private final ObjectMapper objectMapper;

    /**
     * Creates the provisioning plan service with its injected collaborators.
     *
     * @param leadRepository persistence access for lead
     * @param assessmentRepository persistence access for assessment
     * @param planRepository persistence access for plan
     * @param itemRepository persistence access for item
     * @param toolRepository persistence access for tool
     * @param manualStepRepository persistence access for manual step
     * @param externalResourceRepository persistence access for external resource
     * @param requirementNormalizer requirement normalizer
     * @param decisionEngine decision engine
     * @param providerRegistry provider registry
     * @param objectMapper Jackson mapper used to read or write JSON
     */
    public ProvisioningPlanService(
            LeadRepository leadRepository,
            LeadAssessmentRepository assessmentRepository,
            ProvisioningPlanRepository planRepository,
            ProvisioningPlanItemRepository itemRepository,
            ProvisioningSelectedToolRepository toolRepository,
            ProvisioningManualStepRepository manualStepRepository,
            ProvisioningExternalResourceRepository externalResourceRepository,
            ProvisioningRequirementNormalizer requirementNormalizer,
            ProvisioningDecisionEngine decisionEngine,
            ProvisioningProviderRegistry providerRegistry,
            ObjectMapper objectMapper
    ) {
        this.leadRepository = leadRepository;
        this.assessmentRepository = assessmentRepository;
        this.planRepository = planRepository;
        this.itemRepository = itemRepository;
        this.toolRepository = toolRepository;
        this.manualStepRepository = manualStepRepository;
        this.externalResourceRepository = externalResourceRepository;
        this.requirementNormalizer = requirementNormalizer;
        this.decisionEngine = decisionEngine;
        this.providerRegistry = providerRegistry;
        this.objectMapper = objectMapper;
    }

    /**
     * Builds a draft from a saved assessment. Generating it again reuses the plan for that assessment,
     * resets its status to draft and replaces its tools, items and manual steps.
     *
     * @param leadId identifier of the source sales enquiry
     * @param request optional selection of the assessment to use, defaulting to the latest for this lead
     * @return the persisted preview with the selected delivery tracks and their reasons
     */
    public ProvisioningPlanResponse generateDryRun(UUID leadId, CreateProvisioningDryRunRequest request) {
        LeadEntity lead = findLead(leadId);
        LeadAssessmentEntity assessment = resolveAssessment(lead, request == null ? null : request.getAssessmentId());
        Map<String, Object> responses = readMap(assessment.getResponsesJson());
        List<String> recommendedServices = readList(assessment.getRecommendedServicesJson());
        RequirementSet requirements = requirementNormalizer.normalize(
                assessment.getFormKey(),
                responses,
                recommendedServices
        );
        ProvisioningDecision decision = decisionEngine.decide(
                requirements,
                assessment.getFormKey(),
                responses,
                recommendedServices,
                lead.getBusinessName()
        );

        ProvisioningPlanEntity plan = planRepository.findByAssessment(assessment)
                .orElseGet(ProvisioningPlanEntity::new);
        plan.setLead(lead);
        plan.setAssessment(assessment);
        plan.setRouteKey(decision.route().name());
        plan.setAutomationLevel(decision.automationLevel().name());
        plan.setAutomationScope(decision.automationScope());
        plan.setStatus(ProvisioningPlanStatus.DRAFT.value());
        plan.setDryRun(true);
        plan.setNormalizedRequirementsJson(writeJson(requirements.asMap()));
        plan.setDecisionReason(decision.reason());
        plan.setRisksJson(writeJson(decision.risks()));
        plan.setCostEstimate(decision.costEstimate());
        plan.setTracksJson(writeJson(mapTracks(decision.tracks())));
        plan.setSharedResourcesJson(writeJson(mapSharedResources(decision.sharedResources())));
        plan = planRepository.saveAndFlush(plan);

        replacePlanDetails(plan, decision);
        return map(plan);
    }

    /**
     * Lists the delivery plans associated with a known sales enquiry.
     *
     * @param leadId identifier of the source sales enquiry
     * @return plans for the lead, most recently updated first
     */
    @Transactional(readOnly = true)
    public List<ProvisioningPlanResponse> getPlansForLead(UUID leadId) {
        LeadEntity lead = findLead(leadId);
        return planRepository.findAllByLeadOrderByUpdatedAtDesc(lead)
                .stream()
                .map(this::map)
                .toList();
    }

    /**
     * Loads a saved preview with its selected tools, resources and outstanding manual work.
     *
     * @param planId identifier of the saved provisioning plan
     * @return the detailed plan identified by the request
     */
    @Transactional(readOnly = true)
    public ProvisioningPlanResponse getPlan(UUID planId) {
        return map(findPlan(planId));
    }

    /**
     * Changes the review state of a dry-run plan. The supported states are draft, awaiting approval and
     * approved.
     *
     * @param planId identifier of the saved provisioning plan
     * @param rawStatus requested dry-run review state
     * @return the plan with its saved review state
     * @throws IllegalArgumentException if the requested review state is unsupported
     */
    public ProvisioningPlanResponse updateStatus(UUID planId, String rawStatus) {
        ProvisioningPlanStatus status = ProvisioningPlanStatus.parse(rawStatus);
        if (!List.of(
                ProvisioningPlanStatus.DRAFT,
                ProvisioningPlanStatus.AWAITING_APPROVAL,
                ProvisioningPlanStatus.APPROVED
        ).contains(status)) {
            throw new IllegalArgumentException(
                    "Dry-run plans can only be draft, awaiting_approval or approved"
            );
        }
        ProvisioningPlanEntity plan = findPlan(planId);
        plan.setStatus(status.value());
        return map(planRepository.save(plan));
    }

    /**
     * Rebuilds the plan details from the latest decision for the selected assessment.
     *
     * @param plan proposed or saved delivery plan
     * @param decision decision
     */
    private void replacePlanDetails(ProvisioningPlanEntity plan, ProvisioningDecision decision) {
        externalResourceRepository.deleteAllByPlan(plan);
        itemRepository.deleteAllByPlan(plan);
        manualStepRepository.deleteAllByPlan(plan);
        toolRepository.deleteAllByPlan(plan);
        externalResourceRepository.flush();
        itemRepository.flush();
        manualStepRepository.flush();
        toolRepository.flush();

        for (int index = 0; index < decision.tools().size(); index++) {
            ProvisioningDecision.ToolDecision source = decision.tools().get(index);
            ProvisioningSelectedToolEntity entity = new ProvisioningSelectedToolEntity();
            entity.setPlan(plan);
            entity.setToolKey(source.key());
            entity.setDisplayName(source.displayName());
            entity.setSelectionState(source.selectionState());
            entity.setAutomationLevel(source.automationLevel().name());
            entity.setRequired(source.required());
            entity.setReason(source.reason());
            entity.setSortOrder(index);
            toolRepository.save(entity);
        }

        for (int index = 0; index < decision.items().size(); index++) {
            ProvisioningDecision.PlanItemDecision source = decision.items().get(index);
            ProvisioningPlanItemEntity entity = new ProvisioningPlanItemEntity();
            entity.setPlan(plan);
            entity.setProviderKey(source.providerKey());
            entity.setResourceType(source.resourceType());
            entity.setResourceName(source.resourceName());
            entity.setAction(source.action());
            entity.setStatus("planned");
            entity.setRequired(source.required());
            entity.setReason(source.reason());
            entity.setSortOrder(index);
            itemRepository.save(entity);

            if (providerRegistry.find(source.providerKey()).isPresent()) {
                ProvisioningExternalResourceEntity placeholder = new ProvisioningExternalResourceEntity();
                placeholder.setPlan(plan);
                placeholder.setProviderKey(source.providerKey());
                placeholder.setResourceType(source.resourceType());
                placeholder.setStatus("placeholder");
                placeholder.setIdempotencyKey(plan.getId() + ":" + source.providerKey() + ":" + source.resourceType() + ":" + index);
                externalResourceRepository.save(placeholder);
            }
        }

        for (int index = 0; index < decision.manualSteps().size(); index++) {
            ProvisioningDecision.ManualStepDecision source = decision.manualSteps().get(index);
            ProvisioningManualStepEntity entity = new ProvisioningManualStepEntity();
            entity.setPlan(plan);
            entity.setProviderKey(source.providerKey());
            entity.setTitle(source.title());
            entity.setReason(source.reason());
            entity.setRequired(source.required());
            entity.setStatus("pending");
            entity.setSortOrder(index);
            manualStepRepository.save(entity);
        }
    }

    /**
     * Resolves an explicitly requested assessment for this lead, or selects the lead's most recently
     * updated assessment.
     *
     * @param lead sales enquiry in scope
     * @param assessmentId optional assessment to select instead of the latest for the lead
     * @return the saved assessment from which this plan will be derived
     * @throws IllegalArgumentException if the selected assessment is unknown, belongs to another lead, or no assessment is available
     */
    private LeadAssessmentEntity resolveAssessment(LeadEntity lead, UUID assessmentId) {
        if (assessmentId != null) {
            LeadAssessmentEntity assessment = assessmentRepository.findById(assessmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Assessment not found: " + assessmentId));
            if (!assessment.getLead().getId().equals(lead.getId())) {
                throw new IllegalArgumentException("Assessment does not belong to this lead");
            }
            return assessment;
        }
        return assessmentRepository.findAllByLeadOrderByUpdatedAtDesc(lead)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "A saved lead assessment is required before generating a provisioning plan"
                ));
    }

    /**
     * Looks up the sales enquiry that owns the assessment and plan.
     *
     * @param id unique identifier of the record
     * @return the persisted lead for the supplied identifier
     * @throws com.altaira.backend.exception.LeadNotFoundException if the sales enquiry does not exist
     */
    private LeadEntity findLead(UUID id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new LeadNotFoundException(id));
    }

    /**
     * Locates the stored delivery plan before reading its details or changing its review state.
     *
     * @param id unique identifier of the record
     * @return the plan entity matching the supplied identifier
     * @throws IllegalArgumentException if no plan has the supplied identifier
     */
    private ProvisioningPlanEntity findPlan(UUID id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provisioning plan not found: " + id));
    }

    /**
     * Assembles the readable plan from stored decisions and the ordered tools, plan items, manual steps
     * and external resources.
     *
     * @param plan proposed or saved delivery plan
     * @return the full plan returned to staff for review
     */
    private ProvisioningPlanResponse map(ProvisioningPlanEntity plan) {
        List<ProvisioningPlanResponse.Tool> tools = toolRepository.findAllByPlanOrderBySortOrder(plan)
                .stream()
                .map(tool -> new ProvisioningPlanResponse.Tool(
                        tool.getId(),
                        tool.getToolKey(),
                        tool.getDisplayName(),
                        tool.getSelectionState(),
                        tool.getAutomationLevel(),
                        tool.isRequired(),
                        tool.getReason()
                ))
                .toList();
        List<ProvisioningPlanResponse.PlanItem> items = itemRepository.findAllByPlanOrderBySortOrder(plan)
                .stream()
                .map(item -> new ProvisioningPlanResponse.PlanItem(
                        item.getId(),
                        item.getProviderKey(),
                        item.getResourceType(),
                        item.getResourceName(),
                        item.getAction(),
                        item.getStatus(),
                        item.isRequired(),
                        item.getReason()
                ))
                .toList();
        List<ProvisioningPlanResponse.ManualStep> manualSteps = manualStepRepository.findAllByPlanOrderBySortOrder(plan)
                .stream()
                .map(step -> new ProvisioningPlanResponse.ManualStep(
                        step.getId(),
                        step.getProviderKey(),
                        step.getTitle(),
                        step.getReason(),
                        step.isRequired(),
                        step.getStatus()
                ))
                .toList();
        List<ProvisioningPlanResponse.ExternalResource> resources =
                externalResourceRepository.findAllByPlanOrderByProviderKey(plan)
                        .stream()
                        .map(resource -> new ProvisioningPlanResponse.ExternalResource(
                                resource.getId(),
                                resource.getProviderKey(),
                                resource.getResourceType(),
                                resource.getExternalResourceId(),
                                resource.getExternalUrl(),
                                resource.getStatus(),
                                resource.getIdempotencyKey()
                        ))
                        .toList();

        return new ProvisioningPlanResponse(
                plan.getId(),
                plan.getLead().getId(),
                plan.getAssessment().getId(),
                plan.getRouteKey(),
                plan.getAutomationLevel(),
                plan.getAutomationScope(),
                plan.getStatus(),
                plan.isDryRun(),
                false,
                readBooleanMap(plan.getNormalizedRequirementsJson()),
                plan.getDecisionReason(),
                plan.getCostEstimate(),
                readList(plan.getRisksJson()),
                tools,
                items,
                manualSteps,
                resources,
                readTracks(plan.getTracksJson()),
                readSharedResources(plan.getSharedResourcesJson()),
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );
    }

    /**
     * Exposes each delivery track's route, rule, reasoning, tools and manual work.
     *
     * @param tracks tracks
     * @return track decisions in the same order as the decision engine supplied them
     */
    private List<ProvisioningPlanResponse.Track> mapTracks(List<TrackDecision> tracks) {
        return tracks.stream()
                .map(track -> new ProvisioningPlanResponse.Track(
                        track.track().name(),
                        track.route().name(),
                        track.ruleId(),
                        track.matchedSignals(),
                        track.reason(),
                        track.confidence(),
                        track.requiresManualDecision(),
                        track.automationLevel().name(),
                        track.tools().stream()
                                .map(tool -> new ProvisioningPlanResponse.DecisionTool(
                                        tool.key(),
                                        tool.displayName(),
                                        tool.selectionState(),
                                        tool.automationLevel().name(),
                                        tool.required(),
                                        tool.reason()
                                ))
                                .toList(),
                        track.manualSteps().stream()
                                .map(step -> new ProvisioningPlanResponse.DecisionManualStep(
                                        step.providerKey(),
                                        step.title(),
                                        step.reason(),
                                        step.required()
                                ))
                                .toList(),
                        track.risks()
                ))
                .toList();
    }

    /**
     * Lists each shared resource together with the tracks that depend on it.
     *
     * @param resources resources
     * @return resource decisions and the names of their consuming tracks
     */
    private List<ProvisioningPlanResponse.SharedResource> mapSharedResources(
            List<SharedResourceDecision> resources
    ) {
        return resources.stream()
                .map(resource -> new ProvisioningPlanResponse.SharedResource(
                        resource.key(),
                        resource.displayName(),
                        resource.selectionState(),
                        resource.automationLevel().name(),
                        resource.required(),
                        resource.reason(),
                        resource.usedByTracks().stream().map(Enum::name).toList()
                ))
                .toList();
    }

    /**
     * Encodes structured provisioning data for the plan's JSON fields.
     *
     * @param value decision data to store on the plan
     * @return the JSON text to persist
     * @throws IllegalArgumentException if the supplied data cannot be serialized
     */
    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Provisioning data could not be serialized", exception);
        }
    }

    /**
     * Reads the assessment answers used to evaluate the delivery requirements.
     *
     * @param json assessment answers as a JSON object
     * @return the stored answers keyed by question
     * @throws IllegalStateException when validation reports “Stored assessment responses are invalid”
     */
    private Map<String, Object> readMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Stored assessment responses are invalid", exception);
        }
    }

    /**
     * Reads the normalized requirement flags saved on the plan.
     *
     * @param json normalized requirement flags as a JSON object
     * @return requirement names mapped to their saved boolean values
     * @throws IllegalStateException when validation reports “Stored provisioning requirements are invalid”
     */
    private Map<String, Boolean> readBooleanMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Stored provisioning requirements are invalid", exception);
        }
    }

    /**
     * Decodes a stored list such as the assessment's recommended services or the plan's risks.
     *
     * @param json the stored JSON array of strings
     * @return the strings in their stored order
     * @throws IllegalStateException when validation reports “Stored provisioning list is invalid”
     */
    private List<String> readList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Stored provisioning list is invalid", exception);
        }
    }

    /**
     * Restores the track decisions saved with the plan.
     *
     * @param json the plan's stored track decisions
     * @return the saved track decisions, or an empty list when the field is absent or blank
     * @throws IllegalStateException when validation reports “Stored provisioning tracks are invalid”
     */
    private List<ProvisioningPlanResponse.Track> readTracks(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Stored provisioning tracks are invalid", exception);
        }
    }

    /**
     * Restores the shared resource decisions saved with the plan.
     *
     * @param json the plan's stored shared resource decisions
     * @return the saved resource decisions, or an empty list when the field is absent or blank
     * @throws IllegalStateException when validation reports “Stored shared provisioning resources are invalid”
     */
    private List<ProvisioningPlanResponse.SharedResource> readSharedResources(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Stored shared provisioning resources are invalid", exception);
        }
    }
}
