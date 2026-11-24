package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps client workspace records to the {@code client_workspaces} table. */
@Entity
@Table(name = "client_workspaces")
public class ClientWorkspaceEntity {

    /** Primary identifier for this client workspace. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Customer account in scope. Joined through {@code client_id}. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private ClientEntity client;

    /** Name used to identify the item. */
    @Column(nullable = false)
    private String name;

    /** Current state in the workflow. */
    @Column(nullable = false, length = 40)
    private String status = "onboarding";

    /** Onboarding completed. Stored in {@code onboarding_completed}. */
    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted = false;

    /** Time associated with contract submitted. Stored in {@code contract_submitted_at}. */
    @Column(name = "contract_submitted_at")
    private Instant contractSubmittedAt;

    /** Time associated with contract approved. Stored in {@code contract_approved_at}. */
    @Column(name = "contract_approved_at")
    private Instant contractApprovedAt;

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
     * Sets the unique identifier of the record on this client workspace.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this client workspace.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

    /** {@return name used to identify the item} */
    public String getName() { return name; }
    /**
     * Sets the name used to identify the item on this client workspace.
     *
     * @param name name used to identify the item
     */
    public void setName(String name) { this.name = name; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this client workspace.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return onboarding completed} */
    public boolean isOnboardingCompleted() { return onboardingCompleted; }
    /**
     * Sets the onboarding completed on this client workspace.
     *
     * @param onboardingCompleted onboarding completed
     */
    public void setOnboardingCompleted(boolean onboardingCompleted) { this.onboardingCompleted = onboardingCompleted; }

    /** {@return time associated with contract submitted} */
    public Instant getContractSubmittedAt() { return contractSubmittedAt; }
    /**
     * Sets the time associated with contract submitted on this client workspace.
     *
     * @param contractSubmittedAt time associated with contract submitted
     */
    public void setContractSubmittedAt(Instant contractSubmittedAt) { this.contractSubmittedAt = contractSubmittedAt; }

    /** {@return time associated with contract approved} */
    public Instant getContractApprovedAt() { return contractApprovedAt; }
    /**
     * Sets the time associated with contract approved on this client workspace.
     *
     * @param contractApprovedAt time associated with contract approved
     */
    public void setContractApprovedAt(Instant contractApprovedAt) { this.contractApprovedAt = contractApprovedAt; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this client workspace.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this client workspace.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
