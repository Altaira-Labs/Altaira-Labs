package com.altaira.backend.service;

import com.altaira.backend.dto.jira.ExecuteJiraIssuesRequest;
import com.altaira.backend.dto.jira.JiraIssueExecutionResponse;
import com.altaira.backend.entity.CommercialFlowEntity;
import com.altaira.backend.entity.ProvisioningExternalResourceEntity;
import com.altaira.backend.entity.ProvisioningPlanEntity;
import com.altaira.backend.entity.ProvisioningRunEntity;
import com.altaira.backend.entity.ProvisioningStepEntity;
import com.altaira.backend.integration.jira.JiraIntegrationException;
import com.altaira.backend.integration.jira.JiraIssueProvisioner;
import com.altaira.backend.integration.jira.JiraIssueProvisioningResult;
import com.altaira.backend.integration.jira.JiraProperties;
import com.altaira.backend.model.CommercialClientStatus;
import com.altaira.backend.model.CommercialPaymentStatus;
import com.altaira.backend.model.CommercialWorkspaceStatus;
import com.altaira.backend.model.ProvisioningPlanStatus;
import com.altaira.backend.model.ProvisioningStepStatus;
import com.altaira.backend.repository.CommercialFlowRepository;
import com.altaira.backend.repository.ProvisioningExternalResourceRepository;
import com.altaira.backend.repository.ProvisioningPlanRepository;
import com.altaira.backend.repository.ProvisioningRunRepository;
import com.altaira.backend.repository.ProvisioningStepRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Converts approved dry-run Jira steps into idempotent Jira issues only after all
 * commercial and provider execution gates pass. Client-visible tasks remain in
 * Altaira; Jira is the internal technical execution system.
 */
@Service
@Transactional
public class JiraProvisioningExecutionService {

    /** Confirmation used by Jira provisioning execution service. */
    public static final String CONFIRMATION = "CONFIRM_JIRA_ISSUES";
    /** Provider used by Jira provisioning execution service. */
    private static final String PROVIDER = "JIRA";
    /** Source run suffix used by Jira provisioning execution service. */
    private static final String SOURCE_RUN_SUFFIX = ":commercial-v1";
    /** Live run suffix used by Jira provisioning execution service. */
    private static final String LIVE_RUN_SUFFIX = ":jira-issues-live-v1";
    /** Max issues per execution ({@code 40}). */
    private static final int MAX_ISSUES_PER_EXECUTION = 40;
    /** Executable plan statuses used by Jira provisioning execution service. */
    private static final Set<String> EXECUTABLE_PLAN_STATUSES = Set.of(
            ProvisioningPlanStatus.APPROVED.value(),
            ProvisioningPlanStatus.PROVISIONED.value(),
            ProvisioningPlanStatus.PARTIALLY_COMPLETED.value()
    );
    /** Logger for this component. */
    private static final Logger logger = LoggerFactory.getLogger(JiraProvisioningExecutionService.class);

    /** Persistence access for plan. */
    private final ProvisioningPlanRepository planRepository;
    /** Persistence access for commercial flow. */
    private final CommercialFlowRepository commercialFlowRepository;
    /** Persistence access for run. */
    private final ProvisioningRunRepository runRepository;
    /** Persistence access for step. */
    private final ProvisioningStepRepository stepRepository;
    /** Persistence access for external resource. */
    private final ProvisioningExternalResourceRepository externalResourceRepository;
    /** Issue provisioner. */
    private final JiraIssueProvisioner issueProvisioner;
    /** Properties. */
    private final JiraProperties properties;
    /** Jackson mapper used to read or write JSON. */
    private final ObjectMapper objectMapper;

    /**
     * Creates the Jira provisioning execution service with its injected collaborators.
     *
     * @param planRepository persistence access for plan
     * @param commercialFlowRepository persistence access for commercial flow
     * @param runRepository persistence access for run
     * @param stepRepository persistence access for step
     * @param externalResourceRepository persistence access for external resource
     * @param issueProvisioner issue provisioner
     * @param properties properties
     * @param objectMapper Jackson mapper used to read or write JSON
     */
    public JiraProvisioningExecutionService(
            ProvisioningPlanRepository planRepository,
            CommercialFlowRepository commercialFlowRepository,
            ProvisioningRunRepository runRepository,
            ProvisioningStepRepository stepRepository,
            ProvisioningExternalResourceRepository externalResourceRepository,
            JiraIssueProvisioner issueProvisioner,
            JiraProperties properties,
            ObjectMapper objectMapper
    ) {
        this.planRepository = planRepository;
        this.commercialFlowRepository = commercialFlowRepository;
        this.runRepository = runRepository;
        this.stepRepository = stepRepository;
        this.externalResourceRepository = externalResourceRepository;
        this.issueProvisioner = issueProvisioner;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    /**
     * Executes issues. Persists changes through {@code runRepository}. Copies the list before returning it.
     *
     * @param planId identifier of the saved provisioning plan
     * @param request input carrying execute Jira issues
     * @return the Jira issue execution response
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (not found, conflict)
     */
    public JiraIssueExecutionResponse executeIssues(UUID planId, ExecuteJiraIssuesRequest request) {
        requireConfirmation(request);
        requireProviderExecutionGate();

        ProvisioningPlanEntity plan = planRepository.findByIdForUpdate(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provisioning plan not found"));
        requireApprovedPlan(plan);

        CommercialFlowEntity flow = commercialFlowRepository.findByPlan(plan)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Commercial activation must exist before external provisioning"
                ));
        requireActivatedFlow(flow);

        ProvisioningRunEntity sourceRun = runRepository.findByIdempotencyKey(planId + SOURCE_RUN_SUFFIX)
                .filter(ProvisioningRunEntity::isDryRun)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Approved dry-run tasks must exist before Jira execution"
                ));
        List<ProvisioningStepEntity> sourceSteps = stepRepository.findAllByRunOrderByCreatedAt(sourceRun).stream()
                .filter(step -> PROVIDER.equalsIgnoreCase(step.getProvider()))
                .toList();
        if (sourceSteps.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The approved plan has no Jira tasks");
        }
        if (sourceSteps.size() > MAX_ISSUES_PER_EXECUTION) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The approved plan exceeds the controlled Jira issue limit"
            );
        }

        String runKey = planId + LIVE_RUN_SUFFIX;
        ProvisioningRunEntity liveRun = runRepository.findByIdempotencyKey(runKey)
                .orElseGet(() -> createRun(plan, flow, runKey));
        liveRun.setStatus(ProvisioningStepStatus.RUNNING.name());
        liveRun.setSafeError(null);
        liveRun.setStartedAt(liveRun.getStartedAt() == null ? Instant.now() : liveRun.getStartedAt());
        liveRun.setCompletedAt(null);
        runRepository.save(liveRun);

        List<JiraIssueExecutionResponse.IssueResult> issueResults = new ArrayList<>();
        int created = 0;
        int reused = 0;
        int failed = 0;

        for (ProvisioningStepEntity sourceStep : sourceSteps) {
            IssueExecution outcome = executeSourceStep(plan, flow, liveRun, sourceStep);
            issueResults.add(outcome.response());
            created += outcome.created() ? 1 : 0;
            reused += outcome.reused() ? 1 : 0;
            failed += outcome.failed() ? 1 : 0;
        }

        Instant now = Instant.now();
        liveRun.setCompletedAt(now);
        if (failed == 0) {
            liveRun.setStatus(ProvisioningStepStatus.COMPLETED.name());
            liveRun.setSafeError(null);
        } else if (failed < issueResults.size()) {
            liveRun.setStatus("PARTIALLY_COMPLETED");
            liveRun.setSafeError(failed + " Jira issue(s) failed safely.");
        } else {
            liveRun.setStatus(ProvisioningStepStatus.FAILED.name());
            liveRun.setSafeError("All Jira issue operations failed safely.");
        }
        liveRun = runRepository.save(liveRun);

        String message = failed == 0
                ? "Approved Jira tasks are provisioned."
                : "Jira provisioning completed with " + failed + " safe failure(s).";
        return new JiraIssueExecutionResponse(
                plan.getId(),
                liveRun.getId(),
                liveRun.getStatus(),
                false,
                true,
                created,
                reused,
                failed,
                List.copyOf(issueResults),
                message
        );
    }

    /**
     * Executes source step.
     *
     * @param plan proposed or saved delivery plan
     * @param flow flow
     * @param liveRun live run
     * @param sourceStep source step
     * @return the issue execution
     */
    private IssueExecution executeSourceStep(
            ProvisioningPlanEntity plan,
            CommercialFlowEntity flow,
            ProvisioningRunEntity liveRun,
            ProvisioningStepEntity sourceStep
    ) {
        String liveStepKey = plan.getId() + ":JIRA:issue-live-v1:" + sourceStep.getId();
        String resourceKey = liveStepKey;
        String summary = sourceSummary(sourceStep);
        ProvisioningStepEntity liveStep = stepRepository.findByIdempotencyKey(liveStepKey)
                .orElseGet(() -> createStep(liveRun, sourceStep, summary, liveStepKey));
        ProvisioningExternalResourceEntity existingResource = externalResourceRepository
                .findByIdempotencyKey(resourceKey)
                .orElse(null);

        if (ProvisioningStepStatus.COMPLETED.name().equals(liveStep.getStatus()) && existingResource != null) {
            return new IssueExecution(
                    issueResponse(liveStep, summary, false, true, existingResource, "Jira issue already provisioned."),
                    false,
                    true,
                    false
            );
        }

        markRunning(liveStep);
        String idempotencyLabel = idempotencyLabel(plan.getId(), sourceStep.getId());
        try {
            JiraIssueProvisioningResult result = issueProvisioner.ensureTask(
                    summary,
                    issueDescription(plan, flow, sourceStep),
                    trackLabels(sourceStep.getTrackKey()),
                    idempotencyLabel
            );
            ProvisioningExternalResourceEntity resource = persistSuccess(
                    plan,
                    liveStep,
                    resourceKey,
                    result
            );
            String message = result.created() ? "Jira issue created." : "Existing Jira issue reused.";
            return new IssueExecution(
                    issueResponse(liveStep, summary, result.created(), result.reused(), resource, message),
                    result.created(),
                    result.reused(),
                    false
            );
        } catch (JiraIntegrationException exception) {
            return persistFailure(liveStep, summary, exception.safeMessage());
        } catch (RuntimeException exception) {
            logger.warn(
                    "Jira provisioning failed safely for planId={} sourceStepId={} type={}",
                    plan.getId(),
                    sourceStep.getId(),
                    exception.getClass().getSimpleName()
            );
            return persistFailure(liveStep, summary, "Jira issue provisioning failed safely.");
        }
    }

    /**
     * Creates run. Persists changes through {@code runRepository}.
     *
     * @param plan proposed or saved delivery plan
     * @param flow flow
     * @param idempotencyKey key used to recognize a repeated operation
     * @return the provisioning run record
     */
    private ProvisioningRunEntity createRun(
            ProvisioningPlanEntity plan,
            CommercialFlowEntity flow,
            String idempotencyKey
    ) {
        ProvisioningRunEntity run = new ProvisioningRunEntity();
        run.setPlan(plan);
        run.setClient(flow.getClient());
        run.setWorkspace(flow.getWorkspace());
        run.setStatus(ProvisioningStepStatus.PENDING.name());
        run.setDryRun(false);
        run.setIdempotencyKey(idempotencyKey);
        return runRepository.saveAndFlush(run);
    }

    /**
     * Creates step. Persists changes through {@code stepRepository}.
     *
     * @param liveRun live run
     * @param sourceStep source step
     * @param summary summary
     * @param idempotencyKey key used to recognize a repeated operation
     * @return the provisioning step record
     */
    private ProvisioningStepEntity createStep(
            ProvisioningRunEntity liveRun,
            ProvisioningStepEntity sourceStep,
            String summary,
            String idempotencyKey
    ) {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("schemaVersion", 1);
        input.put("dryRun", false);
        input.put("externalWrite", true);
        input.put("provider", PROVIDER);
        input.put("action", "CREATE_TASK_ISSUE");
        input.put("sourceStepId", sourceStep.getId().toString());
        input.put("track", sourceStep.getTrackKey());
        input.put("summary", summary);
        input.put("projectKey", properties.projectKey());

        ProvisioningStepEntity step = new ProvisioningStepEntity();
        step.setRun(liveRun);
        step.setTrackKey(sourceStep.getTrackKey());
        step.setProvider(PROVIDER);
        step.setAction(boundedAction("Create Jira task: " + summary));
        step.setStatus(ProvisioningStepStatus.PENDING.name());
        step.setInputSummaryJson(toJson(input));
        step.setManualActionRequired(false);
        step.setIdempotencyKey(idempotencyKey);
        step.setAttempts(0);
        return stepRepository.save(step);
    }

    /**
     * Marks running. Persists changes through {@code stepRepository}.
     *
     * @param step step
     */
    private void markRunning(ProvisioningStepEntity step) {
        Instant now = Instant.now();
        step.setStatus(ProvisioningStepStatus.RUNNING.name());
        step.setSafeError(null);
        step.setStartedAt(now);
        step.setCompletedAt(null);
        step.setAttempts(step.getAttempts() + 1);
        stepRepository.save(step);
    }

    /**
     * Persists success. Persists changes through {@code stepRepository}.
     *
     * @param plan proposed or saved delivery plan
     * @param step step
     * @param resourceKey key identifying the resource
     * @param result outcome carrying Jira issue provisioning
     * @return the provisioning external resource record
     */
    private ProvisioningExternalResourceEntity persistSuccess(
            ProvisioningPlanEntity plan,
            ProvisioningStepEntity step,
            String resourceKey,
            JiraIssueProvisioningResult result
    ) {
        ProvisioningExternalResourceEntity resource = externalResourceRepository.findByIdempotencyKey(resourceKey)
                .orElseGet(ProvisioningExternalResourceEntity::new);
        if (resource.getPlan() == null) {
            resource.setPlan(plan);
            resource.setProviderKey(PROVIDER);
            resource.setResourceType("issue");
            resource.setIdempotencyKey(resourceKey);
        }
        resource.setExternalResourceId(result.issueKey());
        resource.setExternalUrl(result.issueUrl());
        resource.setStatus(result.created() ? "created" : "reused");
        resource = externalResourceRepository.save(resource);

        step.setExternalResourceId(result.issueKey());
        step.setExternalUrl(result.issueUrl());
        step.setStatus(ProvisioningStepStatus.COMPLETED.name());
        step.setCompletedAt(Instant.now());
        stepRepository.save(step);
        return resource;
    }

    /**
     * Persists failure. Persists changes through {@code stepRepository}.
     *
     * @param step step
     * @param summary summary
     * @param safeMessage message suitable for display in the interface
     * @return the issue execution
     */
    private IssueExecution persistFailure(ProvisioningStepEntity step, String summary, String safeMessage) {
        String message = boundedMessage(safeMessage);
        step.setStatus(ProvisioningStepStatus.FAILED.name());
        step.setSafeError(message);
        step.setCompletedAt(Instant.now());
        stepRepository.save(step);
        return new IssueExecution(
                issueResponse(step, summary, false, false, null, message),
                false,
                false,
                true
        );
    }

    /**
     * Combines the attempted step and its provider resource into one issue-delivery result.
     *
     * @param step step
     * @param summary summary
     * @param created whether a resource was created
     * @param reused whether an existing resource was used
     * @param resource resource
     * @param message text associated with the operation
     * @return the value produced by this operation
     */
    private JiraIssueExecutionResponse.IssueResult issueResponse(
            ProvisioningStepEntity step,
            String summary,
            boolean created,
            boolean reused,
            ProvisioningExternalResourceEntity resource,
            String message
    ) {
        return new JiraIssueExecutionResponse.IssueResult(
                step.getId(),
                step.getTrackKey(),
                summary,
                step.getStatus(),
                step.getAttempts(),
                created,
                reused,
                resource == null ? step.getExternalResourceId() : resource.getExternalResourceId(),
                resource == null ? step.getExternalUrl() : resource.getExternalUrl(),
                message
        );
    }

    /**
     * Builds the short description of the plan step used as the issue source.
     *
     * @param sourceStep source step
     * @return the resulting text
     */
    private String sourceSummary(ProvisioningStepEntity sourceStep) {
        try {
            Map<String, Object> summary = objectMapper.readValue(
                    sourceStep.getInputSummaryJson(),
                    new TypeReference<>() { }
            );
            Object value = summary.get("summary");
            if (value instanceof String text && !text.isBlank()) {
                return text.substring(0, Math.min(text.length(), 255));
            }
        } catch (JsonProcessingException ignored) {
            // The source action is generated by Altaira and remains a safe fallback.
        }
        String action = sourceStep.getAction() == null ? "Project delivery task" : sourceStep.getAction();
        String normalized = action.startsWith("Create task: ") ? action.substring("Create task: ".length()) : action;
        return normalized.substring(0, Math.min(normalized.length(), 255));
    }

    /**
     * Formats the approved delivery step for a Jira issue.
     *
     * @param plan proposed or saved delivery plan
     * @param flow flow
     * @param sourceStep source step
     * @return the resulting text
     */
    private String issueDescription(
            ProvisioningPlanEntity plan,
            CommercialFlowEntity flow,
            ProvisioningStepEntity sourceStep
    ) {
        String track = sourceStep.getTrackKey() == null ? "shared" : sourceStep.getTrackKey();
        return "Altaira approved delivery task.\n"
                + "Workspace: " + flow.getWorkspace().getName() + "\n"
                + "Service track: " + track + "\n"
                + "Provisioning plan: " + plan.getId();
    }

    /**
     * Tracks labels. Uses locale-independent case normalization.
     *
     * @param track service track within the delivery plan
     * @return the string items
     */
    private List<String> trackLabels(String track) {
        if (track == null || track.isBlank()) {
            return List.of();
        }
        String normalized = track.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        return normalized.isBlank() ? List.of() : List.of(normalized);
    }

    /**
     * Builds the stable Jira label used to recognize a previously created issue.
     *
     * @param planId identifier of the saved provisioning plan
     * @param sourceStepId identifier of the source step
     * @return the resulting text
     */
    private String idempotencyLabel(UUID planId, UUID sourceStepId) {
        return "altaira-plan-" + compact(planId) + "-step-" + compact(sourceStepId);
    }

    /**
     * Builds a compact description for the provider payload.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String compact(UUID value) {
        return value.toString().replace("-", "");
    }

    /**
     * Requires confirmation.
     *
     * @param request input carrying execute Jira issues
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
     */
    private void requireConfirmation(ExecuteJiraIssuesRequest request) {
        if (request == null || !CONFIRMATION.equals(request.confirmation())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Exact confirmation is required: " + CONFIRMATION
            );
        }
    }

    /**
     * Requires provider execution gate.
     *
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (conflict, service unavailable)
     */
    private void requireProviderExecutionGate() {
        if (!properties.canMutateResources()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Jira writes are disabled. Enable integration and provisioning and disable dry-run first."
            );
        }
        if (!properties.canAttemptConnection()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Jira credentials or target project are incomplete."
            );
        }
    }

    /**
     * Requires approved plan.
     *
     * @param plan proposed or saved delivery plan
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (conflict)
     */
    private void requireApprovedPlan(ProvisioningPlanEntity plan) {
        if (!EXECUTABLE_PLAN_STATUSES.contains(plan.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Provisioning plan must be approved before Jira execution"
            );
        }
    }

    /**
     * Requires activated flow.
     *
     * @param flow flow
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (conflict)
     */
    private void requireActivatedFlow(CommercialFlowEntity flow) {
        boolean active = CommercialPaymentStatus.PAYMENT_CONFIRMED.name().equals(flow.getPaymentStatus())
                && CommercialClientStatus.CLIENT_ACTIVE.name().equals(flow.getClientStatus())
                && CommercialWorkspaceStatus.WORKSPACE_ACTIVE.name().equals(flow.getWorkspaceStatus())
                && flow.getClient() != null
                && flow.getWorkspace() != null;
        if (!active) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Payment, client and workspace must be active before external provisioning"
            );
        }
    }

    /**
     * Limits the message length retained in the response.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String boundedMessage(String value) {
        String message = value == null || value.isBlank()
                ? "Jira issue provisioning failed safely."
                : value;
        return message.substring(0, Math.min(message.length(), 1000));
    }

    /**
     * Limits the action description retained in the result.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String boundedAction(String value) {
        return value.length() <= 100 ? value : value.substring(0, 97) + "...";
    }

    /**
     * Converts the value to JSON.
     *
     * @param value stored or supplied representation
     * @return JSON
     * @throws IllegalStateException when validation reports “Jira provisioning input summary could not be stored”
     */
    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Jira provisioning input summary could not be stored", exception);
        }
    }

    /**
     * Groups the issue execution data used by Jira provisioning execution service.
     *
     * @param response response from the operation
     * @param created whether a resource was created
     * @param reused whether an existing resource was used
     * @param failed failed
     */
    private record IssueExecution(
            JiraIssueExecutionResponse.IssueResult response,
            boolean created,
            boolean reused,
            boolean failed
    ) { }
}
