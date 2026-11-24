package com.altaira.backend.service;

import com.altaira.backend.dto.commercial.CommercialFlowResponse;
import com.altaira.backend.dto.provisioning.ProvisioningPlanResponse;
import com.altaira.backend.entity.*;
import com.altaira.backend.integration.github.GitHubAppProperties;
import com.altaira.backend.integration.provisioning.ProvisioningProviderRegistry;
import com.altaira.backend.integration.provisioning.ProvisioningRequest;
import com.altaira.backend.integration.provisioning.ProvisioningResult;
import com.altaira.backend.model.ProvisioningStepStatus;
import com.altaira.backend.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Materialize internal dry-run steps. `executionAllowed=false` remains the default. */
@Service
@Transactional
public class ProvisioningExecutionService {
    /** Track tasks used by provisioning execution service. */
    private static final Map<String, List<String>> TRACK_TASKS = Map.of(
            "WEB", List.of("Discovery", "Copy and structure", "Design", "Development", "Review", "Launch"),
            "BOOKING", List.of("Requirements", "Calendar rules", "Reminder setup", "Booking test"),
            "CRM", List.of("Pipeline", "Lead stages", "Import", "Workspace review", "Handover"),
            "AUTOMATION", List.of("Trigger map", "Action map", "Test", "Monitoring"),
            "DASHBOARD", List.of("KPI definitions", "Data sources", "Layout", "Validation")
    );
    /** Drive folders used by provisioning execution service. */
    private static final List<String> DRIVE_FOLDERS = List.of(
            "00_Admin", "01_Discovery", "02_Assets", "03_Deliverables", "04_Approvals", "05_Invoices"
    );

    /** Persistence access for run. */
    private final ProvisioningRunRepository runRepository;
    /** Persistence access for step. */
    private final ProvisioningStepRepository stepRepository;
    /** Persistence access for plan item. */
    private final ProvisioningPlanItemRepository planItemRepository;
    /** Provider registry. */
    private final ProvisioningProviderRegistry providerRegistry;
    /** GitHub properties. */
    private final GitHubAppProperties githubProperties;
    /** Jackson mapper used to read or write JSON. */
    private final ObjectMapper objectMapper;

    /**
     * Creates the provisioning execution service with its injected collaborators.
     *
     * @param runRepository persistence access for run
     * @param stepRepository persistence access for step
     * @param planItemRepository persistence access for plan item
     * @param providerRegistry provider registry
     * @param githubProperties GitHub properties
     * @param objectMapper Jackson mapper used to read or write JSON
     */
    public ProvisioningExecutionService(
            ProvisioningRunRepository runRepository,
            ProvisioningStepRepository stepRepository,
            ProvisioningPlanItemRepository planItemRepository,
            ProvisioningProviderRegistry providerRegistry,
            GitHubAppProperties githubProperties,
            ObjectMapper objectMapper
    ) {
        this.runRepository = runRepository;
        this.stepRepository = stepRepository;
        this.planItemRepository = planItemRepository;
        this.providerRegistry = providerRegistry;
        this.githubProperties = githubProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates dry run. Persists changes through {@code runRepository}.
     *
     * @param plan proposed or saved delivery plan
     * @param client customer account in scope
     * @param workspace client workspace in scope
     * @return the provisioning run record
     */
    public ProvisioningRunEntity createDryRun(ProvisioningPlanEntity plan, ClientEntity client, ClientWorkspaceEntity workspace) {
        String key = plan.getId() + ":commercial-v1";
        var existing = runRepository.findByIdempotencyKey(key);
        if (existing.isPresent()) return existing.get();

        ProvisioningRunEntity run = new ProvisioningRunEntity();
        run.setPlan(plan);
        run.setClient(client);
        run.setWorkspace(workspace);
        run.setStatus("RUNNING");
        run.setDryRun(true);
        run.setIdempotencyKey(key);
        run.setStartedAt(Instant.now());
        run = runRepository.saveAndFlush(run);

        boolean blocked = false;
        for (String track : readTrackKeys(plan.getTracksJson())) {
            for (String task : TRACK_TASKS.getOrDefault(track, List.of("Discovery", "Delivery"))) {
                ProvisioningResult result = prepare(
                        "JIRA",
                        track,
                        "Create task: " + task,
                        jiraSummary(run, track, task),
                        true,
                        false,
                        null
                );
                createStep(run, track, "JIRA", "Create task: " + task, task, result);
                blocked |= result.status() == ProvisioningStepStatus.BLOCKED;
            }
        }
        for (String folder : DRIVE_FOLDERS) {
            ProvisioningResult result = prepare(
                    "DRIVE",
                    null,
                    "Create folder: " + folder,
                    driveSummary(run, folder),
                    true,
                    false,
                    null
            );
            createStep(run, null, "GOOGLE_DRIVE", "Create folder: " + folder, folder, result);
            blocked |= result.status() == ProvisioningStepStatus.BLOCKED;
        }
        ProvisioningResult calendarResult = prepare(
                "CALENDAR",
                null,
                "Create kickoff event",
                calendarSummary(run),
                true,
                false,
                null
        );
        createStep(run, null, "GOOGLE_CALENDAR", "Create kickoff event", "kickoff", calendarResult);
        blocked |= calendarResult.status() == ProvisioningStepStatus.BLOCKED;

        for (ProvisioningPlanItemEntity item : planItemRepository.findAllByPlanOrderBySortOrder(plan)) {
            String provider = item.getProviderKey().toUpperCase();
            String action = boundedAction(item.getAction() + ": " + item.getResourceName());
            String idempotencyPart = "item:" + item.getId();
            boolean credentialsReady = !"GITHUB".equals(provider) || githubProperties.canAttemptConnection();
            ProvisioningResult result = prepare(
                    provider,
                    null,
                    action,
                    planItemSummary(run, item, provider, credentialsReady),
                    credentialsReady,
                    "GITHUB".equals(provider),
                    "GitHub App credentials are not configured."
            );
            createStep(run, null, provider, action, idempotencyPart, result);
            blocked |= result.status() == ProvisioningStepStatus.BLOCKED;
        }

        run.setStatus(blocked ? "BLOCKED" : "COMPLETED");
        run.setCompletedAt(Instant.now());
        return runRepository.save(run);
    }

    /**
     * Maps latest.
     *
     * @param plan proposed or saved delivery plan
     * @return the value produced by this operation
     */
    @Transactional(readOnly = true)
    public CommercialFlowResponse.ProvisioningRun mapLatest(ProvisioningPlanEntity plan) {
        return runRepository.findAllByPlanOrderByCreatedAtDesc(plan).stream().findFirst().map(this::map).orElse(null);
    }

    /**
     * Builds {@code CommercialFlowResponse.ProvisioningRun} from {@code ProvisioningRunEntity}, selecting the fields used by the caller.
     *
     * @param run run
     * @return the value produced by this operation
     */
    private CommercialFlowResponse.ProvisioningRun map(ProvisioningRunEntity run) {
        List<CommercialFlowResponse.ProvisioningStep> steps = stepRepository.findAllByRunOrderByCreatedAt(run).stream()
                .map(step -> new CommercialFlowResponse.ProvisioningStep(
                        step.getId(), step.getTrackKey(), step.getProvider(), step.getAction(), step.getStatus(),
                        step.isManualActionRequired(), step.getSafeError(), readSummary(step.getInputSummaryJson())
                )).toList();
        return new CommercialFlowResponse.ProvisioningRun(run.getId(), run.getStatus(), run.isDryRun(), steps);
    }

    /**
     * Creates step. Persists changes through {@code stepRepository}.
     *
     * @param run run
     * @param track service track within the delivery plan
     * @param provider integration adapter in use
     * @param action action being recorded
     * @param idempotencyPart idempotency part
     * @param result outcome carrying provisioning
     */
    private void createStep(
            ProvisioningRunEntity run,
            String track,
            String provider,
            String action,
            String idempotencyPart,
            ProvisioningResult result
    ) {
        ProvisioningStepEntity step = new ProvisioningStepEntity();
        step.setRun(run);
        step.setTrackKey(track);
        step.setProvider(provider);
        step.setAction(action);
        step.setStatus(result.status().name());
        step.setInputSummaryJson(toJson(result.inputSummary()));
        step.setManualActionRequired(result.manualActionRequired());
        step.setSafeError(result.safeError());
        step.setExternalResourceId(result.externalResourceId());
        step.setExternalUrl(result.externalUrl());
        step.setAttempts(0);
        Instant now = Instant.now();
        step.setStartedAt(now);
        step.setCompletedAt(now);
        step.setIdempotencyKey(idempotencyKey(run, provider, track, idempotencyPart));
        stepRepository.save(step);
    }

    /**
     * Builds the provider result for a requested resource.
     *
     * @param providerKey key identifying the integration provider
     * @param track service track within the delivery plan
     * @param action action being recorded
     * @param inputSummary input summary
     * @param credentialsConfigured credentials configured
     * @param blockWhenCredentialsMissing block when credentials missing
     * @param missingCredentialsMessage missing credentials message
     * @return the provisioning result
     */
    private ProvisioningResult prepare(
            String providerKey,
            String track,
            String action,
            Map<String, Object> inputSummary,
            boolean credentialsConfigured,
            boolean blockWhenCredentialsMissing,
            String missingCredentialsMessage
    ) {
        return providerRegistry.prepare(
                providerKey,
                new ProvisioningRequest(
                        track,
                        action,
                        inputSummary,
                        credentialsConfigured,
                        blockWhenCredentialsMissing,
                        missingCredentialsMessage
                )
        );
    }

    /**
     * Summarizes the planned Jira work for the execution response.
     *
     * @param run run
     * @param track service track within the delivery plan
     * @param task task
     * @return the resulting key–value pairs
     */
    private Map<String, Object> jiraSummary(ProvisioningRunEntity run, String track, String task) {
        Map<String, Object> summary = baseSummary(run, "JIRA");
        summary.put("projectName", run.getWorkspace().getName());
        summary.put("track", track);
        summary.put("issueType", "Task");
        summary.put("summary", task);
        summary.put("labels", List.of("altaira", track.toLowerCase()));
        summary.put("projectPolicy", "reuse-or-create-only-after-admin-approval");
        return summary;
    }

    /**
     * Summarizes the planned Drive resource for the execution response.
     *
     * @param run run
     * @param folder folder
     * @return the resulting key–value pairs
     */
    private Map<String, Object> driveSummary(ProvisioningRunEntity run, String folder) {
        Map<String, Object> summary = baseSummary(run, "GOOGLE_DRIVE");
        summary.put("rootFolder", run.getWorkspace().getName());
        summary.put("folderName", folder);
        summary.put("sharingPolicy", "private-until-reviewed");
        return summary;
    }

    /**
     * Summarizes the planned calendar resource for the execution response.
     *
     * @param run run
     * @return the resulting key–value pairs
     */
    private Map<String, Object> calendarSummary(ProvisioningRunEntity run) {
        Map<String, Object> summary = baseSummary(run, "GOOGLE_CALENDAR");
        summary.put("eventTitle", "Altaira onboarding - " + run.getClient().getCompany());
        summary.put("eventType", "kickoff");
        summary.put("timezone", "Europe/Brussels");
        summary.put("durationMinutes", 60);
        summary.put("attendeePolicy", "primary-contact-after-consent");
        return summary;
    }

    /**
     * Builds the readable description for an individual plan item.
     *
     * @param run run
     * @param item item
     * @param provider integration adapter in use
     * @param credentialsReady credentials ready
     * @return the resulting key–value pairs
     */
    private Map<String, Object> planItemSummary(
            ProvisioningRunEntity run,
            ProvisioningPlanItemEntity item,
            String provider,
            boolean credentialsReady
    ) {
        Map<String, Object> summary = baseSummary(run, provider);
        summary.put("resourceType", item.getResourceType());
        summary.put("resourceName", item.getResourceName());
        summary.put("requestedAction", item.getAction());
        summary.put("required", item.isRequired());
        summary.put("credentialState", credentialsReady ? "not-required-for-dry-run" : "missing");
        switch (provider) {
            case "GITHUB" -> summary.put("repositoryPolicy", "private-template-after-approval");
            case "NEON" -> {
                summary.put("tenancyStrategy", "shared-database");
                summary.put("isolationEscalation", "schema-or-project-only-after-review");
            }
            case "VERCEL" -> summary.put("deploymentPolicy", "preview-first-after-approval");
            case "RENDER" -> summary.put("deploymentPolicy", "service-and-cost-review-required");
            default -> summary.put("providerPolicy", "manual-review-before-execution");
        }
        return summary;
    }

    /**
     * Populates the fields common to each resource summary.
     *
     * @param run run
     * @param provider integration adapter in use
     * @return the resulting key–value pairs
     */
    private Map<String, Object> baseSummary(ProvisioningRunEntity run, String provider) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("schemaVersion", 1);
        summary.put("dryRun", true);
        summary.put("externalWrite", false);
        summary.put("provider", provider);
        summary.put("planId", run.getPlan().getId().toString());
        summary.put("clientId", run.getClient().getId().toString());
        summary.put("workspaceId", run.getWorkspace().getId().toString());
        summary.put("clientCompany", run.getClient().getCompany());
        summary.put("workspaceName", run.getWorkspace().getName());
        return summary;
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
     * Builds the stable key used to recognize a repeated provisioning operation.
     *
     * @param run run
     * @param provider integration adapter in use
     * @param track service track within the delivery plan
     * @param part part
     * @return the resulting text
     */
    private String idempotencyKey(ProvisioningRunEntity run, String provider, String track, String part) {
        String raw = run.getId() + ":" + provider + ":" + (track == null ? "shared" : track) + ":" + part;
        if (raw.length() <= 240) return raw;
        UUID digest = UUID.nameUUIDFromBytes(raw.getBytes(StandardCharsets.UTF_8));
        return raw.substring(0, 200) + ":" + digest;
    }

    /**
     * Reads track keys.
     *
     * @param json JSON
     * @return the string items
     * @throws IllegalStateException when validation reports “Provisioning tracks could not be read”
     */
    private List<String> readTrackKeys(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            List<ProvisioningPlanResponse.Track> tracks = objectMapper.readValue(json, new TypeReference<>() {});
            return tracks.stream().map(ProvisioningPlanResponse.Track::track).distinct().toList();
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Provisioning tracks could not be read", exception);
        }
    }

    /**
     * Reads summary.
     *
     * @param json JSON
     * @return the resulting key–value pairs
     */
    private Map<String, Object> readSummary(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException exception) {
            return Map.of("summaryUnavailable", true);
        }
    }

    /**
     * Converts the value to JSON.
     *
     * @param value stored or supplied representation
     * @return JSON
     * @throws IllegalStateException when validation reports “Provisioning step summary could not be stored”
     */
    private String toJson(Object value) {
        try { return objectMapper.writeValueAsString(value); }
        catch (JsonProcessingException exception) { throw new IllegalStateException("Provisioning step summary could not be stored", exception); }
    }
}
