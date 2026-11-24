package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps lead assessment records to the {@code lead_assessments} table. */
@Entity
@Table(
        name = "lead_assessments",
        uniqueConstraints = @UniqueConstraint(
                name = "lead_assessments_lead_form_unique",
                columnNames = {"lead_id", "form_key"}
        )
)
public class LeadAssessmentEntity {

    /** Primary identifier for this lead assessment. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Sales enquiry in scope. Joined through {@code lead_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id", nullable = false)
    private LeadEntity lead;

    /** Key selecting the discovery questionnaire. Stored in {@code form_key}. */
    @Column(name = "form_key", nullable = false, length = 40)
    private String formKey;

    /** Format version used to interpret this payload. Stored in {@code schema_version}. */
    @Column(name = "schema_version", nullable = false)
    private int schemaVersion;

    /** Current state in the workflow. */
    @Column(nullable = false, length = 20)
    private String status;

    /** Serialized discovery answers. Stored in {@code responses_json}. */
    @Column(name = "responses_json", nullable = false, columnDefinition = "TEXT")
    private String responsesJson;

    /** JSON representation of recommended services. Stored in {@code recommended_services_json}. */
    @Column(name = "recommended_services_json", nullable = false, columnDefinition = "TEXT")
    private String recommendedServicesJson;

    /** Qualification summary. Stored in {@code qualification_summary}. */
    @Column(name = "qualification_summary", nullable = false, columnDefinition = "TEXT")
    private String qualificationSummary;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Time of the most recent update. Stored in {@code updated_at}. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Initializes now, created at, updated at before the record is inserted. */
    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    /** Refreshes the update timestamp before the persisted record changes. */
    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this lead assessment.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return sales enquiry in scope} */
    public LeadEntity getLead() { return lead; }
    /**
     * Sets the sales enquiry in scope on this lead assessment.
     *
     * @param lead sales enquiry in scope
     */
    public void setLead(LeadEntity lead) { this.lead = lead; }

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

    /** {@return serialized discovery answers} */
    public String getResponsesJson() { return responsesJson; }
    /**
     * Sets the serialized discovery answers on this lead assessment.
     *
     * @param responsesJson serialized discovery answers
     */
    public void setResponsesJson(String responsesJson) { this.responsesJson = responsesJson; }

    /** {@return JSON representation of recommended services} */
    public String getRecommendedServicesJson() { return recommendedServicesJson; }
    /**
     * Sets the JSON representation of recommended services on this lead assessment.
     *
     * @param recommendedServicesJson JSON representation of recommended services
     */
    public void setRecommendedServicesJson(String recommendedServicesJson) { this.recommendedServicesJson = recommendedServicesJson; }

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
