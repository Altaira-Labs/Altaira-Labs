package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Maps provisioning step records to the {@code provisioning_steps} table. */
@Entity
@Table(name = "provisioning_steps")
public class ProvisioningStepEntity {
    /** Primary identifier for this provisioning step. */
    @Id @GeneratedValue private UUID id;
    /** Run. Joined through {@code run_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "run_id", nullable = false) private ProvisioningRunEntity run;
    /** Key identifying the track. Stored in {@code track_key}. */
    @Column(name = "track_key", length = 40) private String trackKey;
    /** Integration adapter in use. */
    @Column(nullable = false, length = 40) private String provider;
    /** Action being recorded. */
    @Column(nullable = false, length = 100) private String action;
    /** Current state in the workflow. */
    @Column(nullable = false, length = 30) private String status;
    /** JSON representation of input summary. Stored in {@code input_summary_json}. */
    @Column(name = "input_summary_json", nullable = false, columnDefinition = "TEXT") private String inputSummaryJson = "{}";
    /** Identifier assigned by the external provider. Stored in {@code external_resource_id}. */
    @Column(name = "external_resource_id", length = 240) private String externalResourceId;
    /** Link to the resource in the external system. Stored in {@code external_url}. */
    @Column(name = "external_url", columnDefinition = "TEXT") private String externalUrl;
    /** Error detail suitable for showing to the caller. Stored in {@code safe_error}. */
    @Column(name = "safe_error", columnDefinition = "TEXT") private String safeError;
    /** Manual action required. Stored in {@code manual_action_required}. */
    @Column(name = "manual_action_required", nullable = false) private boolean manualActionRequired;
    /** Key used to recognize a repeated operation. Stored in {@code idempotency_key}. */
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 240) private String idempotencyKey;
    /** Attempts. */
    @Column(nullable = false) private int attempts;
    /** Time associated with started. Stored in {@code started_at}. */
    @Column(name = "started_at") private Instant startedAt;
    /** Time the work was completed. Stored in {@code completed_at}. */
    @Column(name = "completed_at") private Instant completedAt;
    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    /** Time of the most recent update. Stored in {@code updated_at}. */
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    /** Initializes now, created at, updated at before the record is inserted. */
    @PrePersist void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
    /** Refreshes the update timestamp before the persisted record changes. */
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /** {@return run} */
    public ProvisioningRunEntity getRun() { return run; }
    /**
     * Sets the run on this provisioning step.
     *
     * @param run run
     */
    public void setRun(ProvisioningRunEntity run) { this.run = run; }
    /** {@return key identifying the track} */
    public String getTrackKey() { return trackKey; }
    /**
     * Sets the key identifying the track on this provisioning step.
     *
     * @param trackKey key identifying the track
     */
    public void setTrackKey(String trackKey) { this.trackKey = trackKey; }
    /** {@return integration adapter in use} */
    public String getProvider() { return provider; }
    /**
     * Sets the integration adapter in use on this provisioning step.
     *
     * @param provider integration adapter in use
     */
    public void setProvider(String provider) { this.provider = provider; }
    /** {@return action being recorded} */
    public String getAction() { return action; }
    /**
     * Sets the action being recorded on this provisioning step.
     *
     * @param action action being recorded
     */
    public void setAction(String action) { this.action = action; }
    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this provisioning step.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
    /** {@return JSON representation of input summary} */
    public String getInputSummaryJson() { return inputSummaryJson; }
    /**
     * Sets the JSON representation of input summary on this provisioning step.
     *
     * @param inputSummaryJson JSON representation of input summary
     */
    public void setInputSummaryJson(String inputSummaryJson) { this.inputSummaryJson = inputSummaryJson; }
    /** {@return identifier assigned by the external provider} */
    public String getExternalResourceId() { return externalResourceId; }
    /**
     * Sets the identifier assigned by the external provider on this provisioning step.
     *
     * @param externalResourceId identifier assigned by the external provider
     */
    public void setExternalResourceId(String externalResourceId) { this.externalResourceId = externalResourceId; }
    /** {@return link to the resource in the external system} */
    public String getExternalUrl() { return externalUrl; }
    /**
     * Sets the link to the resource in the external system on this provisioning step.
     *
     * @param externalUrl link to the resource in the external system
     */
    public void setExternalUrl(String externalUrl) { this.externalUrl = externalUrl; }
    /** {@return error detail suitable for showing to the caller} */
    public String getSafeError() { return safeError; }
    /**
     * Sets the error detail suitable for showing to the caller on this provisioning step.
     *
     * @param safeError error detail suitable for showing to the caller
     */
    public void setSafeError(String safeError) { this.safeError = safeError; }
    /** {@return manual action required} */
    public boolean isManualActionRequired() { return manualActionRequired; }
    /**
     * Sets the manual action required on this provisioning step.
     *
     * @param manualActionRequired manual action required
     */
    public void setManualActionRequired(boolean manualActionRequired) { this.manualActionRequired = manualActionRequired; }
    /** {@return key used to recognize a repeated operation} */
    public String getIdempotencyKey() { return idempotencyKey; }
    /**
     * Sets the key used to recognize a repeated operation on this provisioning step.
     *
     * @param idempotencyKey key used to recognize a repeated operation
     */
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    /** {@return attempts} */
    public int getAttempts() { return attempts; }
    /**
     * Sets the attempts on this provisioning step.
     *
     * @param attempts attempts
     */
    public void setAttempts(int attempts) { this.attempts = attempts; }
    /** {@return time associated with started} */
    public Instant getStartedAt() { return startedAt; }
    /**
     * Sets the time associated with started on this provisioning step.
     *
     * @param startedAt time associated with started
     */
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    /** {@return time the work was completed} */
    public Instant getCompletedAt() { return completedAt; }
    /**
     * Sets the time the work was completed on this provisioning step.
     *
     * @param completedAt time the work was completed
     */
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
}
