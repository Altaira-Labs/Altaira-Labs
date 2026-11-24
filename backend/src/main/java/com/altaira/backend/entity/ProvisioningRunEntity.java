package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Maps provisioning run records to the {@code provisioning_runs} table. */
@Entity
@Table(name = "provisioning_runs")
public class ProvisioningRunEntity {
    /** Primary identifier for this provisioning run. */
    @Id @GeneratedValue private UUID id;
    /** Proposed or saved delivery plan. Joined through {@code plan_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "plan_id", nullable = false) private ProvisioningPlanEntity plan;
    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "client_id", nullable = false) private ClientEntity client;
    /** Client workspace in scope. Joined through {@code workspace_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "workspace_id", nullable = false) private ClientWorkspaceEntity workspace;
    /** Current state in the workflow. */
    @Column(nullable = false, length = 30) private String status;
    /** Whether the operation is being simulated. Stored in {@code dry_run}. */
    @Column(name = "dry_run", nullable = false) private boolean dryRun = true;
    /** Key used to recognize a repeated operation. Stored in {@code idempotency_key}. */
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 200) private String idempotencyKey;
    /** Error detail suitable for showing to the caller. Stored in {@code safe_error}. */
    @Column(name = "safe_error", columnDefinition = "TEXT") private String safeError;
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
    /** {@return proposed or saved delivery plan} */
    public ProvisioningPlanEntity getPlan() { return plan; }
    /**
     * Sets the proposed or saved delivery plan on this provisioning run.
     *
     * @param plan proposed or saved delivery plan
     */
    public void setPlan(ProvisioningPlanEntity plan) { this.plan = plan; }
    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this provisioning run.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }
    /** {@return client workspace in scope} */
    public ClientWorkspaceEntity getWorkspace() { return workspace; }
    /**
     * Sets the client workspace in scope on this provisioning run.
     *
     * @param workspace client workspace in scope
     */
    public void setWorkspace(ClientWorkspaceEntity workspace) { this.workspace = workspace; }
    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this provisioning run.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
    /** {@return whether the operation is being simulated} */
    public boolean isDryRun() { return dryRun; }
    /**
     * Sets the whether the operation is being simulated on this provisioning run.
     *
     * @param dryRun whether the operation is being simulated
     */
    public void setDryRun(boolean dryRun) { this.dryRun = dryRun; }
    /** {@return key used to recognize a repeated operation} */
    public String getIdempotencyKey() { return idempotencyKey; }
    /**
     * Sets the key used to recognize a repeated operation on this provisioning run.
     *
     * @param idempotencyKey key used to recognize a repeated operation
     */
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    /** {@return error detail suitable for showing to the caller} */
    public String getSafeError() { return safeError; }
    /**
     * Sets the error detail suitable for showing to the caller on this provisioning run.
     *
     * @param safeError error detail suitable for showing to the caller
     */
    public void setSafeError(String safeError) { this.safeError = safeError; }
    /** {@return time associated with started} */
    public Instant getStartedAt() { return startedAt; }
    /**
     * Sets the time associated with started on this provisioning run.
     *
     * @param startedAt time associated with started
     */
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    /** {@return time the work was completed} */
    public Instant getCompletedAt() { return completedAt; }
    /**
     * Sets the time the work was completed on this provisioning run.
     *
     * @param completedAt time the work was completed
     */
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
}
