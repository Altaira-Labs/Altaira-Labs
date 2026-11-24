package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps provisioning plan records to the {@code provisioning_plans} table. */
@Entity
@Table(
        name = "provisioning_plans",
        uniqueConstraints = @UniqueConstraint(
                name = "provisioning_plans_assessment_unique",
                columnNames = {"assessment_id"}
        )
)
public class ProvisioningPlanEntity {

    /** Primary identifier for this provisioning plan. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Sales enquiry in scope. Joined through {@code lead_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id", nullable = false)
    private LeadEntity lead;

    /** Assessment. Joined through {@code assessment_id}. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private LeadAssessmentEntity assessment;

    /** Key identifying the route. Stored in {@code route_key}. */
    @Column(name = "route_key", nullable = false, length = 60)
    private String routeKey;

    /** Degree of automation associated with the step. Stored in {@code automation_level}. */
    @Column(name = "automation_level", nullable = false, length = 4)
    private String automationLevel;

    /** Automation scope. Stored in {@code automation_scope}. */
    @Column(name = "automation_scope", nullable = false, length = 20)
    private String automationScope;

    /** Current state in the workflow. */
    @Column(nullable = false, length = 30)
    private String status;

    /** Whether the operation is being simulated. Stored in {@code dry_run}. */
    @Column(name = "dry_run", nullable = false)
    private boolean dryRun;

    /** JSON representation of normalized requirements. Stored in {@code normalized_requirements_json}. */
    @Column(name = "normalized_requirements_json", nullable = false, columnDefinition = "TEXT")
    private String normalizedRequirementsJson;

    /** Decision reason. Stored in {@code decision_reason}. */
    @Column(name = "decision_reason", nullable = false, columnDefinition = "TEXT")
    private String decisionReason;

    /** JSON representation of risks. Stored in {@code risks_json}. */
    @Column(name = "risks_json", nullable = false, columnDefinition = "TEXT")
    private String risksJson;

    /** Cost estimate. Stored in {@code cost_estimate}. */
    @Column(name = "cost_estimate", nullable = false, columnDefinition = "TEXT")
    private String costEstimate;

    /** JSON representation of tracks. Stored in {@code tracks_json}. */
    @Column(name = "tracks_json", nullable = false, columnDefinition = "TEXT")
    private String tracksJson = "[]";

    /** JSON representation of shared resources. Stored in {@code shared_resources_json}. */
    @Column(name = "shared_resources_json", nullable = false, columnDefinition = "TEXT")
    private String sharedResourcesJson = "[]";

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
     * Sets the unique identifier of the record on this provisioning plan.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }
    /** {@return sales enquiry in scope} */
    public LeadEntity getLead() { return lead; }
    /**
     * Sets the sales enquiry in scope on this provisioning plan.
     *
     * @param lead sales enquiry in scope
     */
    public void setLead(LeadEntity lead) { this.lead = lead; }
    /** {@return assessment} */
    public LeadAssessmentEntity getAssessment() { return assessment; }
    /**
     * Sets the assessment on this provisioning plan.
     *
     * @param assessment assessment
     */
    public void setAssessment(LeadAssessmentEntity assessment) { this.assessment = assessment; }
    /** {@return key identifying the route} */
    public String getRouteKey() { return routeKey; }
    /**
     * Sets the key identifying the route on this provisioning plan.
     *
     * @param routeKey key identifying the route
     */
    public void setRouteKey(String routeKey) { this.routeKey = routeKey; }
    /** {@return degree of automation associated with the step} */
    public String getAutomationLevel() { return automationLevel; }
    /**
     * Sets the degree of automation associated with the step on this provisioning plan.
     *
     * @param automationLevel degree of automation associated with the step
     */
    public void setAutomationLevel(String automationLevel) { this.automationLevel = automationLevel; }
    /** {@return automation scope} */
    public String getAutomationScope() { return automationScope; }
    /**
     * Sets the automation scope on this provisioning plan.
     *
     * @param automationScope automation scope
     */
    public void setAutomationScope(String automationScope) { this.automationScope = automationScope; }
    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this provisioning plan.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
    /** {@return whether the operation is being simulated} */
    public boolean isDryRun() { return dryRun; }
    /**
     * Sets the whether the operation is being simulated on this provisioning plan.
     *
     * @param dryRun whether the operation is being simulated
     */
    public void setDryRun(boolean dryRun) { this.dryRun = dryRun; }
    /** {@return JSON representation of normalized requirements} */
    public String getNormalizedRequirementsJson() { return normalizedRequirementsJson; }
    /**
     * Sets the JSON representation of normalized requirements on this provisioning plan.
     *
     * @param normalizedRequirementsJson JSON representation of normalized requirements
     */
    public void setNormalizedRequirementsJson(String normalizedRequirementsJson) { this.normalizedRequirementsJson = normalizedRequirementsJson; }
    /** {@return decision reason} */
    public String getDecisionReason() { return decisionReason; }
    /**
     * Sets the decision reason on this provisioning plan.
     *
     * @param decisionReason decision reason
     */
    public void setDecisionReason(String decisionReason) { this.decisionReason = decisionReason; }
    /** {@return JSON representation of risks} */
    public String getRisksJson() { return risksJson; }
    /**
     * Sets the JSON representation of risks on this provisioning plan.
     *
     * @param risksJson JSON representation of risks
     */
    public void setRisksJson(String risksJson) { this.risksJson = risksJson; }
    /** {@return cost estimate} */
    public String getCostEstimate() { return costEstimate; }
    /**
     * Sets the cost estimate on this provisioning plan.
     *
     * @param costEstimate cost estimate
     */
    public void setCostEstimate(String costEstimate) { this.costEstimate = costEstimate; }
    /** {@return JSON representation of tracks} */
    public String getTracksJson() { return tracksJson; }
    /**
     * Sets the JSON representation of tracks on this provisioning plan.
     *
     * @param tracksJson JSON representation of tracks
     */
    public void setTracksJson(String tracksJson) { this.tracksJson = tracksJson; }
    /** {@return JSON representation of shared resources} */
    public String getSharedResourcesJson() { return sharedResourcesJson; }
    /**
     * Sets the JSON representation of shared resources on this provisioning plan.
     *
     * @param sharedResourcesJson JSON representation of shared resources
     */
    public void setSharedResourcesJson(String sharedResourcesJson) { this.sharedResourcesJson = sharedResourcesJson; }
    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this provisioning plan.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this provisioning plan.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
