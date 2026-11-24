package com.altaira.backend.service;

import com.altaira.backend.dto.github.ExecuteGitHubRepositoryRequest;
import com.altaira.backend.dto.github.GitHubRepositoryExecutionResponse;
import com.altaira.backend.entity.CommercialFlowEntity;
import com.altaira.backend.entity.ProvisioningExternalResourceEntity;
import com.altaira.backend.entity.ProvisioningPlanEntity;
import com.altaira.backend.entity.ProvisioningPlanItemEntity;
import com.altaira.backend.entity.ProvisioningRunEntity;
import com.altaira.backend.entity.ProvisioningStepEntity;
import com.altaira.backend.integration.github.GitHubAppProperties;
import com.altaira.backend.integration.github.GitHubIntegrationException;
import com.altaira.backend.integration.github.GitHubRepositoryNameNormalizer;
import com.altaira.backend.integration.github.GitHubRepositoryProvisioner;
import com.altaira.backend.integration.github.GitHubRepositoryProvisioningResult;
import com.altaira.backend.model.CommercialClientStatus;
import com.altaira.backend.model.CommercialPaymentStatus;
import com.altaira.backend.model.CommercialWorkspaceStatus;
import com.altaira.backend.model.ProvisioningPlanStatus;
import com.altaira.backend.model.ProvisioningStepStatus;
import com.altaira.backend.repository.CommercialFlowRepository;
import com.altaira.backend.repository.ProvisioningExternalResourceRepository;
import com.altaira.backend.repository.ProvisioningPlanItemRepository;
import com.altaira.backend.repository.ProvisioningPlanRepository;
import com.altaira.backend.repository.ProvisioningRunRepository;
import com.altaira.backend.repository.ProvisioningStepRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Executes the explicitly approved GitHub repository step. The service requires an
 * activated commercial flow, a provider gate and an exact confirmation phrase, and
 * records idempotent runs, steps and external resource identifiers for auditability.
 */
@Service
@Transactional
public class GitHubProvisioningExecutionService {

    /** Confirmation used by GitHub provisioning execution service. */
    public static final String CONFIRMATION = "CONFIRM_PRIVATE_REPOSITORY";
    /** Provider used by GitHub provisioning execution service. */
    private static final String PROVIDER = "GITHUB";
    /** Action used by GitHub provisioning execution service. */
    private static final String ACTION = "CREATE_PRIVATE_REPOSITORY";
    /** Executable plan statuses used by GitHub provisioning execution service. */
    private static final Set<String> EXECUTABLE_PLAN_STATUSES = Set.of(
            ProvisioningPlanStatus.APPROVED.value(),
            ProvisioningPlanStatus.PROVISIONED.value(),
            ProvisioningPlanStatus.PARTIALLY_COMPLETED.value()
    );
    /** Logger for this component. */
    private static final Logger logger = LoggerFactory.getLogger(GitHubProvisioningExecutionService.class);

    /** Persistence access for plan. */
    private final ProvisioningPlanRepository planRepository;
    /** Persistence access for plan item. */
    private final ProvisioningPlanItemRepository planItemRepository;
    /** Persistence access for commercial flow. */
    private final CommercialFlowRepository commercialFlowRepository;
    /** Persistence access for run. */
    private final ProvisioningRunRepository runRepository;
    /** Persistence access for step. */
    private final ProvisioningStepRepository stepRepository;
    /** Persistence access for external resource. */
    private final ProvisioningExternalResourceRepository externalResourceRepository;
    /** Repository provisioner. */
    private final GitHubRepositoryProvisioner repositoryProvisioner;
    /** Properties. */
    private final GitHubAppProperties properties;
    /** Jackson mapper used to read or write JSON. */
    private final ObjectMapper objectMapper;

    /**
     * Creates the GitHub provisioning execution service with its injected collaborators.
     *
     * @param planRepository persistence access for plan
     * @param planItemRepository persistence access for plan item
     * @param commercialFlowRepository persistence access for commercial flow
     * @param runRepository persistence access for run
     * @param stepRepository persistence access for step
     * @param externalResourceRepository persistence access for external resource
     * @param repositoryProvisioner repository provisioner
     * @param properties properties
     * @param objectMapper Jackson mapper used to read or write JSON
     */
    public GitHubProvisioningExecutionService(
            ProvisioningPlanRepository planRepository,
            ProvisioningPlanItemRepository planItemRepository,
            CommercialFlowRepository commercialFlowRepository,
            ProvisioningRunRepository runRepository,
            ProvisioningStepRepository stepRepository,
            ProvisioningExternalResourceRepository externalResourceRepository,
            GitHubRepositoryProvisioner repositoryProvisioner,
            GitHubAppProperties properties,
            ObjectMapper objectMapper
    ) {
        this.planRepository = planRepository;
        this.planItemRepository = planItemRepository;
        this.commercialFlowRepository = commercialFlowRepository;
        this.runRepository = runRepository;
        this.stepRepository = stepRepository;
        this.externalResourceRepository = externalResourceRepository;
        this.repositoryProvisioner = repositoryProvisioner;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    /**
     * Executes private repository.
     *
     * @param planId identifier of the saved provisioning plan
     * @param request input carrying execute GitHub repository
     * @return the GitHub repository execution response
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (not found, conflict)
     */
    public GitHubRepositoryExecutionResponse executePrivateRepository(
            UUID planId,
            ExecuteGitHubRepositoryRequest request
    ) {
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

        List<ProvisioningPlanItemEntity> githubItems = planItemRepository.findAllByPlanOrderBySortOrder(plan).stream()
                .filter(item -> PROVIDER.equalsIgnoreCase(item.getProviderKey()))
                .toList();
        if (githubItems.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This approved plan does not include a GitHub resource"
            );
        }

        String runKey = plan.getId() + ":github-repository-live-v1";
        String stepKey = plan.getId() + ":GITHUB:repository-live-v1";
        ProvisioningRunEntity run = runRepository.findByIdempotencyKey(runKey)
                .orElseGet(() -> createRun(plan, flow, runKey));
        ProvisioningStepEntity step = stepRepository.findByIdempotencyKey(stepKey)
                .orElseGet(() -> createStep(run, flow, stepKey));

        ProvisioningExternalResourceEntity completedResource = findRepositoryResource(plan, step.getExternalResourceId());
        if (ProvisioningStepStatus.COMPLETED.name().equals(step.getStatus()) && completedResource != null) {
            return response(plan, run, step, completedResource, "Private GitHub repository already provisioned.");
        }

        markRunning(run, step);
        try {
            String repositoryName = GitHubRepositoryNameNormalizer.normalize(
                    flow.getClient().getCompany() + " " + flow.getWorkspace().getName()
            );
            GitHubRepositoryProvisioningResult result = repositoryProvisioner.ensurePrivateRepository(
                    repositoryName,
                    "Private Altaira Labs workspace repository for " + flow.getClient().getCompany() + "."
            );
            ProvisioningExternalResourceEntity resource = persistSuccess(plan, githubItems, run, step, result);
            String message = result.created()
                    ? "Private GitHub repository created."
                    : "Existing private GitHub repository reused.";
            return response(plan, run, step, resource, message);
        } catch (GitHubIntegrationException exception) {
            return persistFailure(plan, run, step, exception.safeMessage());
        } catch (RuntimeException exception) {
            logger.warn(
                    "GitHub provisioning failed safely for planId={} type={}",
                    plan.getId(),
                    exception.getClass().getSimpleName()
            );
            return persistFailure(plan, run, step, "GitHub repository provisioning failed safely.");
        }
    }

    /**
     * Requires confirmation.
     *
     * @param request input carrying execute GitHub repository
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
     */
    private void requireConfirmation(ExecuteGitHubRepositoryRequest request) {
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
                    "GitHub writes are disabled. Enable the app and provisioning flags and disable dry-run first."
            );
        }
        if (!properties.canAttemptConnection()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "GitHub App credentials are incomplete."
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
                    "Provisioning plan must be approved before GitHub execution"
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
     * @param run run
     * @param flow flow
     * @param idempotencyKey key used to recognize a repeated operation
     * @return the provisioning step record
     */
    private ProvisioningStepEntity createStep(
            ProvisioningRunEntity run,
            CommercialFlowEntity flow,
            String idempotencyKey
    ) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("schemaVersion", 1);
        summary.put("dryRun", false);
        summary.put("externalWrite", true);
        summary.put("provider", PROVIDER);
        summary.put("action", ACTION);
        summary.put("planId", run.getPlan().getId().toString());
        summary.put("clientId", flow.getClient().getId().toString());
        summary.put("workspaceId", flow.getWorkspace().getId().toString());
        summary.put("visibility", "private");

        ProvisioningStepEntity step = new ProvisioningStepEntity();
        step.setRun(run);
        step.setProvider(PROVIDER);
        step.setAction(ACTION);
        step.setStatus(ProvisioningStepStatus.PENDING.name());
        step.setInputSummaryJson(toJson(summary));
        step.setManualActionRequired(false);
        step.setIdempotencyKey(idempotencyKey);
        step.setAttempts(0);
        return stepRepository.save(step);
    }

    /**
     * Marks running. Persists changes through {@code stepRepository}.
     *
     * @param run run
     * @param step step
     */
    private void markRunning(ProvisioningRunEntity run, ProvisioningStepEntity step) {
        Instant now = Instant.now();
        run.setStatus(ProvisioningStepStatus.RUNNING.name());
        run.setSafeError(null);
        run.setStartedAt(now);
        run.setCompletedAt(null);
        step.setStatus(ProvisioningStepStatus.RUNNING.name());
        step.setSafeError(null);
        step.setStartedAt(now);
        step.setCompletedAt(null);
        step.setAttempts(step.getAttempts() + 1);
        runRepository.save(run);
        stepRepository.save(step);
    }

    /**
     * Persists success. Persists changes through {@code runRepository}.
     *
     * @param plan proposed or saved delivery plan
     * @param githubItems GitHub items
     * @param run run
     * @param step step
     * @param result outcome carrying GitHub repository provisioning
     * @return the provisioning external resource record
     */
    private ProvisioningExternalResourceEntity persistSuccess(
            ProvisioningPlanEntity plan,
            List<ProvisioningPlanItemEntity> githubItems,
            ProvisioningRunEntity run,
            ProvisioningStepEntity step,
            GitHubRepositoryProvisioningResult result
    ) {
        String status = result.created() ? "created" : "reused";
        ProvisioningExternalResourceEntity resource = findRepositoryResource(plan, result.externalResourceId());
        if (resource == null) {
            resource = externalResourceRepository.findAllByPlanOrderByProviderKey(plan).stream()
                    .filter(candidate -> PROVIDER.equalsIgnoreCase(candidate.getProviderKey()))
                    .findFirst()
                    .orElseGet(ProvisioningExternalResourceEntity::new);
        }
        if (resource.getPlan() == null) {
            resource.setPlan(plan);
            resource.setProviderKey(PROVIDER);
            resource.setResourceType("repository");
            resource.setIdempotencyKey(plan.getId() + ":GITHUB:repository-live-v1");
        }
        resource.setExternalResourceId(result.externalResourceId());
        resource.setExternalUrl(result.externalUrl());
        resource.setStatus(status);
        resource = externalResourceRepository.save(resource);

        githubItems.forEach(item -> item.setStatus(status));
        planItemRepository.saveAll(githubItems);

        Instant now = Instant.now();
        step.setExternalResourceId(result.externalResourceId());
        step.setExternalUrl(result.externalUrl());
        step.setStatus(ProvisioningStepStatus.COMPLETED.name());
        step.setCompletedAt(now);
        run.setStatus(ProvisioningStepStatus.COMPLETED.name());
        run.setCompletedAt(now);
        stepRepository.save(step);
        runRepository.save(run);
        return resource;
    }

    /**
     * Persists failure. Persists changes through {@code runRepository}.
     *
     * @param plan proposed or saved delivery plan
     * @param run run
     * @param step step
     * @param safeMessage message suitable for display in the interface
     * @return the GitHub repository execution response
     */
    private GitHubRepositoryExecutionResponse persistFailure(
            ProvisioningPlanEntity plan,
            ProvisioningRunEntity run,
            ProvisioningStepEntity step,
            String safeMessage
    ) {
        String boundedMessage = safeMessage == null || safeMessage.isBlank()
                ? "GitHub repository provisioning failed safely."
                : safeMessage.substring(0, Math.min(safeMessage.length(), 1000));
        Instant now = Instant.now();
        step.setStatus(ProvisioningStepStatus.FAILED.name());
        step.setSafeError(boundedMessage);
        step.setCompletedAt(now);
        run.setStatus(ProvisioningStepStatus.FAILED.name());
        run.setSafeError(boundedMessage);
        run.setCompletedAt(now);
        stepRepository.save(step);
        runRepository.save(run);
        return response(plan, run, step, null, boundedMessage);
    }

    /**
     * Finds repository resource.
     *
     * @param plan proposed or saved delivery plan
     * @param externalResourceId identifier assigned by the external provider
     * @return the provisioning external resource record
     */
    private ProvisioningExternalResourceEntity findRepositoryResource(
            ProvisioningPlanEntity plan,
            String externalResourceId
    ) {
        return externalResourceRepository.findAllByPlanOrderByProviderKey(plan).stream()
                .filter(resource -> PROVIDER.equalsIgnoreCase(resource.getProviderKey()))
                .filter(resource -> externalResourceId == null
                        || externalResourceId.equals(resource.getExternalResourceId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Builds {@code GitHubRepositoryExecutionResponse} from {@code ProvisioningPlanEntity}, selecting the fields used by the caller.
     *
     * @param plan proposed or saved delivery plan
     * @param run run
     * @param step step
     * @param resource resource
     * @param message text associated with the operation
     * @return the GitHub repository execution response
     */
    private GitHubRepositoryExecutionResponse response(
            ProvisioningPlanEntity plan,
            ProvisioningRunEntity run,
            ProvisioningStepEntity step,
            ProvisioningExternalResourceEntity resource,
            String message
    ) {
        boolean created = resource != null && "created".equals(resource.getStatus());
        boolean reused = resource != null && "reused".equals(resource.getStatus());
        return new GitHubRepositoryExecutionResponse(
                plan.getId(),
                run.getId(),
                step.getId(),
                PROVIDER,
                step.getStatus(),
                step.getAttempts(),
                false,
                true,
                created,
                reused,
                step.getExternalResourceId(),
                step.getExternalUrl(),
                message
        );
    }

    /**
     * Converts the value to JSON.
     *
     * @param value stored or supplied representation
     * @return JSON
     * @throws IllegalStateException when validation reports “GitHub provisioning input summary could not be stored”
     */
    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("GitHub provisioning input summary could not be stored", exception);
        }
    }
}
