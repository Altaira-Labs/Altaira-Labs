package com.altaira.backend.dto.lead;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Fields the API returns for lead assessment. */
public class LeadAssessmentResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the source sales enquiry. */
    private UUID leadId;
    /** Key selecting the discovery questionnaire. */
    private String formKey;
    /** Format version used to interpret this payload. */
    private int schemaVersion;
    /** Current state in the workflow. */
    private String status;
    /** Answers supplied in the discovery questionnaire. */
    private Map<String, Object> responses;
    /** Recommended service keys. */
    private List<String> recommendedServiceKeys;
    /** Qualification summary. */
    private String qualificationSummary;
    /** Time the record was created. */
    private Instant createdAt;
    /** Time of the most recent update. */
    private Instant updatedAt;

    /** Constructs {@code LeadAssessmentResponse} with its default state. */
    public LeadAssessmentResponse() {}

    /**
     * Constructs {@code LeadAssessmentResponse} using the supplied id, lead id, form key and remaining fields.
     *
     * @param id unique identifier of the record
     * @param leadId identifier of the source sales enquiry
     * @param formKey key selecting the discovery questionnaire
     * @param schemaVersion format version used to interpret this payload
     * @param status current state in the workflow
     * @param responses answers supplied in the discovery questionnaire
     * @param recommendedServiceKeys recommended service keys
     * @param qualificationSummary qualification summary
     * @param createdAt time the record was created
     * @param updatedAt time of the most recent update
     */
    public LeadAssessmentResponse(
            UUID id,
            UUID leadId,
            String formKey,
            int schemaVersion,
            String status,
            Map<String, Object> responses,
            List<String> recommendedServiceKeys,
            String qualificationSummary,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.leadId = leadId;
        this.formKey = formKey;
        this.schemaVersion = schemaVersion;
        this.status = status;
        this.responses = responses;
        this.recommendedServiceKeys = recommendedServiceKeys;
        this.qualificationSummary = qualificationSummary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this lead assessment.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }
    /** {@return identifier of the source sales enquiry} */
    public UUID getLeadId() { return leadId; }
    /**
     * Sets the identifier of the source sales enquiry on this lead assessment.
     *
     * @param leadId identifier of the source sales enquiry
     */
    public void setLeadId(UUID leadId) { this.leadId = leadId; }
    /** {@return key selecting the discovery questionnaire} */
    public String getFormKey() { return formKey; }
    /**
     * Sets the key selecting the discovery questionnaire on this lead assessment.
     *
     * @param formKey key selecting the discovery questionnaire
     */
    public void setFormKey(String formKey) { this.formKey = formKey; }
    /** {@return format version used to interpret this payload} */
    public int getSchemaVersion() { return schemaVersion; }
    /**
     * Sets the format version used to interpret this payload on this lead assessment.
     *
     * @param schemaVersion format version used to interpret this payload
     */
    public void setSchemaVersion(int schemaVersion) { this.schemaVersion = schemaVersion; }
    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this lead assessment.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
    /** {@return answers supplied in the discovery questionnaire} */
    public Map<String, Object> getResponses() { return responses; }
    /**
     * Sets the answers supplied in the discovery questionnaire on this lead assessment.
     *
     * @param responses answers supplied in the discovery questionnaire
     */
    public void setResponses(Map<String, Object> responses) { this.responses = responses; }
    /** {@return recommended service keys} */
    public List<String> getRecommendedServiceKeys() { return recommendedServiceKeys; }
    /**
     * Sets the recommended service keys on this lead assessment.
     *
     * @param recommendedServiceKeys recommended service keys
     */
    public void setRecommendedServiceKeys(List<String> recommendedServiceKeys) { this.recommendedServiceKeys = recommendedServiceKeys; }
    /** {@return qualification summary} */
    public String getQualificationSummary() { return qualificationSummary; }
    /**
     * Sets the qualification summary on this lead assessment.
     *
     * @param qualificationSummary qualification summary
     */
    public void setQualificationSummary(String qualificationSummary) { this.qualificationSummary = qualificationSummary; }
    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this lead assessment.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this lead assessment.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
