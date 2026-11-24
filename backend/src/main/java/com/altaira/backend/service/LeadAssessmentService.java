package com.altaira.backend.service;

import com.altaira.backend.dto.lead.CreateAdminLeadIntakeRequest;
import com.altaira.backend.dto.lead.LeadAssessmentResponse;
import com.altaira.backend.dto.lead.LeadIntakeResponse;
import com.altaira.backend.dto.lead.LeadResponse;
import com.altaira.backend.dto.lead.SaveLeadAssessmentRequest;
import com.altaira.backend.entity.LeadAssessmentEntity;
import com.altaira.backend.entity.LeadEntity;
import com.altaira.backend.exception.LeadNotFoundException;
import com.altaira.backend.model.LeadAssessmentStatus;
import com.altaira.backend.model.LeadIntakeFormKey;
import com.altaira.backend.repository.LeadAssessmentRepository;
import com.altaira.backend.repository.LeadRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Save diagnostic answers and recommend services. Recommendation is advisory until admin approval. */
@Service
@Transactional
public class LeadAssessmentService {

    /** Legacy schema version ({@code 1}). */
    private static final int LEGACY_SCHEMA_VERSION = 1;
    /** Current schema version ({@code 2}). */
    private static final int CURRENT_SCHEMA_VERSION = 2;
    /** Max response entries ({@code 50}). */
    private static final int MAX_RESPONSE_ENTRIES = 50;
    /** Max responses JSON length ({@code 20000}). */
    private static final int MAX_RESPONSES_JSON_LENGTH = 20_000;

    /** Persistence access for assessment. */
    private final LeadAssessmentRepository assessmentRepository;
    /** Persistence access for lead. */
    private final LeadRepository leadRepository;
    /** Lead operations used by this component. */
    private final LeadService leadService;
    /** Schema v2 validator. */
    private final LeadIntakeSchemaV2Validator schemaV2Validator;
    /** Jackson mapper used to read or write JSON. */
    private final ObjectMapper objectMapper;

    /**
     * Creates the lead assessment service with its injected collaborators.
     *
     * @param assessmentRepository persistence access for assessment
     * @param leadRepository persistence access for lead
     * @param leadService lead operations used by this component
     * @param schemaV2Validator schema v2 validator
     * @param objectMapper Jackson mapper used to read or write JSON
     */
    public LeadAssessmentService(
            LeadAssessmentRepository assessmentRepository,
            LeadRepository leadRepository,
            LeadService leadService,
            LeadIntakeSchemaV2Validator schemaV2Validator,
            ObjectMapper objectMapper
    ) {
        this.assessmentRepository = assessmentRepository;
        this.leadRepository = leadRepository;
        this.leadService = leadService;
        this.schemaV2Validator = schemaV2Validator;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates admin intake.
     *
     * @param request input carrying create admin lead intake
     * @return the lead intake response
     */
    public LeadIntakeResponse createAdminIntake(CreateAdminLeadIntakeRequest request) {
        LeadIntakeFormKey formKey = LeadIntakeFormKey.parse(request.getFormKey());
        LeadResponse lead = leadService.createAdminLead(request, formKey.value());

        SaveLeadAssessmentRequest assessmentRequest = new SaveLeadAssessmentRequest();
        assessmentRequest.setResponses(request.getResponses());
        assessmentRequest.setStatus(LeadAssessmentStatus.SUBMITTED.value());
        assessmentRequest.setSchemaVersion(request.getSchemaVersion());

        return new LeadIntakeResponse(
                lead,
                saveAssessment(lead.getId(), formKey.value(), assessmentRequest)
        );
    }

    /**
     * Retrieves assessments.
     *
     * @param leadId identifier of the source sales enquiry
     * @return the lead assessment response items
     */
    @Transactional(readOnly = true)
    public List<LeadAssessmentResponse> getAssessments(UUID leadId) {
        LeadEntity lead = findLead(leadId);
        return assessmentRepository.findAllByLeadOrderByUpdatedAtDesc(lead)
                .stream()
                .map(this::map)
                .toList();
    }

    /**
     * Saves assessment. Persists changes through {@code assessmentRepository}.
     *
     * @param leadId identifier of the source sales enquiry
     * @param rawFormKey key identifying the raw form
     * @param request input carrying save lead assessment
     * @return the lead assessment response
     */
    public LeadAssessmentResponse saveAssessment(
            UUID leadId,
            String rawFormKey,
            SaveLeadAssessmentRequest request
    ) {
        LeadEntity lead = findLead(leadId);
        LeadIntakeFormKey formKey = LeadIntakeFormKey.parse(rawFormKey);
        LeadAssessmentStatus status = LeadAssessmentStatus.parseOrSubmitted(request.getStatus());
        Map<String, Object> responses = validateResponses(request.getResponses());

        LeadAssessmentEntity entity = assessmentRepository.findByLeadAndFormKey(lead, formKey.value())
                .orElseGet(LeadAssessmentEntity::new);
        int schemaVersion = resolveSchemaVersion(entity, request.getSchemaVersion());
        if (schemaVersion == CURRENT_SCHEMA_VERSION) {
            schemaV2Validator.validate(formKey, responses);
        }
        Recommendation recommendation = recommend(formKey, responses);

        entity.setLead(lead);
        entity.setFormKey(formKey.value());
        entity.setSchemaVersion(schemaVersion);
        entity.setStatus(status.value());
        entity.setResponsesJson(writeJson(responses));
        entity.setRecommendedServicesJson(writeJson(recommendation.serviceKeys()));
        entity.setQualificationSummary(recommendation.summary());

        return map(assessmentRepository.save(entity));
    }

    /**
     * Resolves schema version.
     *
     * @param entity record carrying lead assessment
     * @param requestedVersion requested version
     * @return the value produced by this operation
     * @throws IllegalArgumentException when validation reports “Unsupported assessment schema version”
     */
    private int resolveSchemaVersion(LeadAssessmentEntity entity, Integer requestedVersion) {
        if (requestedVersion != null) {
            if (requestedVersion < LEGACY_SCHEMA_VERSION || requestedVersion > CURRENT_SCHEMA_VERSION) {
                throw new IllegalArgumentException("Unsupported assessment schema version");
            }
            return requestedVersion;
        }
        return entity.getId() == null
                ? LEGACY_SCHEMA_VERSION
                : Math.max(LEGACY_SCHEMA_VERSION, entity.getSchemaVersion());
    }

    /**
     * Validates responses.
     *
     * @param responses answers supplied in the discovery questionnaire
     * @return the resulting key–value pairs
     * @throws IllegalArgumentException when validation reports “Assessment responses are required” or “Assessment contains too many response fields”
     */
    private Map<String, Object> validateResponses(Map<String, Object> responses) {
        if (responses == null) {
            throw new IllegalArgumentException("Assessment responses are required");
        }
        if (responses.size() > MAX_RESPONSE_ENTRIES) {
            throw new IllegalArgumentException("Assessment contains too many response fields");
        }

        Map<String, Object> safeCopy = new LinkedHashMap<>();
        responses.forEach((key, value) -> {
            if (key == null || key.isBlank() || key.length() > 80) {
                throw new IllegalArgumentException("Assessment response keys must be 1 to 80 characters");
            }
            safeCopy.put(key.trim(), value);
        });

        String json = writeJson(safeCopy);
        if (json.length() > MAX_RESPONSES_JSON_LENGTH) {
            throw new IllegalArgumentException("Assessment responses are too large");
        }
        return safeCopy;
    }

    /**
     * Evaluates the assessment answers to recommend a set of services.
     *
     * @param formKey key selecting the discovery questionnaire
     * @param responses answers supplied in the discovery questionnaire
     * @return the recommendation
     */
    private Recommendation recommend(LeadIntakeFormKey formKey, Map<String, Object> responses) {
        if (formKey != LeadIntakeFormKey.GENERAL) {
            return new Recommendation(
                    List.of(formKey.value()),
                    "The selected service intake directly supports " + serviceLabel(formKey.value()) + "."
            );
        }

        Map<String, Integer> scores = new LinkedHashMap<>();
        Map<String, List<String>> reasons = new LinkedHashMap<>();
        List.of("web_seo", "booking", "crm", "automation", "dashboard")
                .forEach(key -> {
                    scores.put(key, 0);
                    reasons.put(key, new ArrayList<>());
                });

        String primaryGoal = value(responses, "primaryGoal");
        switch (primaryGoal) {
            case "online_presence" -> addSignal(scores, reasons, "web_seo", 3, "improve online presence");
            case "bookings" -> addSignal(scores, reasons, "booking", 3, "organise bookings");
            case "lead_management" -> addSignal(scores, reasons, "crm", 3, "control leads and follow-up");
            case "automation" -> addSignal(scores, reasons, "automation", 3, "reduce repetitive work");
            case "visibility" -> addSignal(scores, reasons, "dashboard", 3, "improve operational visibility");
            default -> {
                // No automatic primary recommendation without an explicit goal.
            }
        }

        if (List.of("none", "basic", "outdated").contains(value(responses, "onlinePresence"))) {
            addSignal(scores, reasons, "web_seo", 2, "the current online presence needs work");
        }
        if (List.of("calls_messages", "spreadsheet_calendar").contains(value(responses, "bookingProcess"))) {
            addSignal(scores, reasons, "booking", 2, "bookings are handled manually");
        }
        if (List.of("messages_email", "spreadsheet").contains(value(responses, "leadProcess"))) {
            addSignal(scores, reasons, "crm", 2, "lead follow-up is fragmented");
        }
        if (List.of("medium", "high").contains(value(responses, "repetitiveWork"))) {
            addSignal(scores, reasons, "automation", 2, "repetitive work is material");
        }
        if (List.of("none", "manual").contains(value(responses, "reporting"))) {
            addSignal(scores, reasons, "dashboard", 2, "reporting lacks a shared operational view");
        }

        List<String> recommended = scores.entrySet().stream()
                .filter(entry -> entry.getValue() >= 2)
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry::getKey))
                .limit(2)
                .map(Map.Entry::getKey)
                .toList();

        if (recommended.isEmpty()) {
            return new Recommendation(
                    List.of(),
                    "No automatic service recommendation was made. A discovery review is required."
            );
        }

        String summary = recommended.stream()
                .map(key -> serviceLabel(key) + ": " + String.join(" and ", reasons.get(key)))
                .reduce((left, right) -> left + ". " + right)
                .orElse("");

        return new Recommendation(recommended, summary + ".");
    }

    /**
     * Adds signal.
     *
     * @param scores scores
     * @param reasons reasons
     * @param key key used to identify the item
     * @param weight weight
     * @param reason explanation of the decision
     */
    private void addSignal(
            Map<String, Integer> scores,
            Map<String, List<String>> reasons,
            String key,
            int weight,
            String reason
    ) {
        scores.computeIfPresent(key, (ignored, current) -> current + weight);
        reasons.get(key).add(reason);
    }

    /**
     * Reads a discovery answer as trimmed lowercase text, with an empty string for a missing response.
     *
     * @param responses answers supplied in the discovery questionnaire
     * @param key key used to identify the item
     * @return the resulting text
     */
    private String value(Map<String, Object> responses, String key) {
        Object value = responses.get(key);
        return value == null ? "" : String.valueOf(value).trim().toLowerCase();
    }

    /**
     * Chooses the display label for the recommended service.
     *
     * @param key key used to identify the item
     * @return the resulting text
     */
    private String serviceLabel(String key) {
        return switch (key) {
            case "web_seo" -> "Web & SEO";
            case "booking" -> "Booking System";
            case "crm" -> "CRM / Lead Management";
            case "automation" -> "Workflow Automation";
            case "dashboard" -> "Management Dashboard";
            default -> key;
        };
    }

    /**
     * Finds lead. Looks up the referenced record by identifier.
     *
     * @param id unique identifier of the record
     * @return the lead record
     * @throws com.altaira.backend.exception.LeadNotFoundException if the sales enquiry does not exist
     */
    private LeadEntity findLead(UUID id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new LeadNotFoundException(id));
    }

    /**
     * Builds {@code LeadAssessmentResponse} from {@code LeadAssessmentEntity}, selecting the fields used by the caller.
     *
     * @param entity record carrying lead assessment
     * @return the lead assessment response
     */
    private LeadAssessmentResponse map(LeadAssessmentEntity entity) {
        return new LeadAssessmentResponse(
                entity.getId(),
                entity.getLead().getId(),
                entity.getFormKey(),
                entity.getSchemaVersion(),
                entity.getStatus(),
                readMap(entity.getResponsesJson()),
                readList(entity.getRecommendedServicesJson()),
                entity.getQualificationSummary(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Writes JSON.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     * @throws IllegalArgumentException when validation reports “Assessment data could not be serialized”
     */
    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Assessment data could not be serialized", exception);
        }
    }

    /**
     * Reads map.
     *
     * @param json JSON
     * @return the resulting key–value pairs
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
     * Reads list.
     *
     * @param json JSON
     * @return the string items
     * @throws IllegalStateException when validation reports “Stored assessment recommendation is invalid”
     */
    private List<String> readList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Stored assessment recommendation is invalid", exception);
        }
    }

    /**
     * Groups the recommendation data used by lead assessment service.
     *
     * @param serviceKeys catalogue keys identifying the requested deliveries
     * @param summary summary
     */
    private record Recommendation(List<String> serviceKeys, String summary) {}
}
