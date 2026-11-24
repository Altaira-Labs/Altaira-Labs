package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Maps client CRM follow up action records to the {@code client_crm_follow_up_actions} table. */
@Entity
@Table(name = "client_crm_follow_up_actions")
public class ClientCrmFollowUpActionEntity {

    /** Primary identifier for this client CRM follow up action. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Sales enquiry in scope. Joined through {@code lead_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id", nullable = false)
    private ClientCrmLeadEntity lead;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    /** Created by user. Joined through {@code created_by_user_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private AppUserEntity createdByUser;

    /** Role responsible for the work. Stored in {@code owner_role}. */
    @Column(name = "owner_role", nullable = false)
    private String ownerRole = "admin";

    /** Whether the client can see the item. Stored in {@code visible_to_client}. */
    @Column(name = "visible_to_client", nullable = false)
    private boolean visibleToClient = false;

    /** Short heading shown to a reader. */
    @Column(nullable = false, length = 180)
    private String title;

    /** Human-readable description. */
    @Column(columnDefinition = "text")
    private String description;

    /** Current state in the workflow. */
    @Column(nullable = false)
    private String status = "open";

    /** Deadline for the work. Stored in {@code due_at}. */
    @Column(name = "due_at")
    private Instant dueAt;

    /** Time the work was completed. Stored in {@code completed_at}. */
    @Column(name = "completed_at")
    private Instant completedAt;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Time of the most recent update. Stored in {@code updated_at}. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Constructs {@code ClientCrmFollowUpActionEntity} with its default state. */
    public ClientCrmFollowUpActionEntity() {}

    /** Initializes now, created at, updated at, owner role, status before the record is inserted. */
    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;

        if (ownerRole == null || ownerRole.isBlank()) {
            ownerRole = "admin";
        }

        if (status == null || status.isBlank()) {
            status = "open";
        }
    }

    /** Refreshes the update timestamp before the persisted record changes. */
    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client CRM follow up action.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return sales enquiry in scope} */
    public ClientCrmLeadEntity getLead() { return lead; }
    /**
     * Sets the sales enquiry in scope on this client CRM follow up action.
     *
     * @param lead sales enquiry in scope
     */
    public void setLead(ClientCrmLeadEntity lead) { this.lead = lead; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this client CRM follow up action.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

    /** {@return created by user} */
    public AppUserEntity getCreatedByUser() { return createdByUser; }
    /**
     * Sets the created by user on this client CRM follow up action.
     *
     * @param createdByUser created by user
     */
    public void setCreatedByUser(AppUserEntity createdByUser) { this.createdByUser = createdByUser; }

    /** {@return role responsible for the work} */
    public String getOwnerRole() { return ownerRole; }
    /**
     * Sets the role responsible for the work on this client CRM follow up action.
     *
     * @param ownerRole role responsible for the work
     */
    public void setOwnerRole(String ownerRole) { this.ownerRole = ownerRole; }

    /** {@return whether the client can see the item} */
    public boolean isVisibleToClient() { return visibleToClient; }
    /**
     * Sets the whether the client can see the item on this client CRM follow up action.
     *
     * @param visibleToClient whether the client can see the item
     */
    public void setVisibleToClient(boolean visibleToClient) { this.visibleToClient = visibleToClient; }

    /** {@return short heading shown to a reader} */
    public String getTitle() { return title; }
    /**
     * Sets the short heading shown to a reader on this client CRM follow up action.
     *
     * @param title short heading shown to a reader
     */
    public void setTitle(String title) { this.title = title; }

    /** {@return human-readable description} */
    public String getDescription() { return description; }
    /**
     * Sets the human-readable description on this client CRM follow up action.
     *
     * @param description human-readable description
     */
    public void setDescription(String description) { this.description = description; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this client CRM follow up action.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return deadline for the work} */
    public Instant getDueAt() { return dueAt; }
    /**
     * Sets the deadline for the work on this client CRM follow up action.
     *
     * @param dueAt deadline for the work
     */
    public void setDueAt(Instant dueAt) { this.dueAt = dueAt; }

    /** {@return time the work was completed} */
    public Instant getCompletedAt() { return completedAt; }
    /**
     * Sets the time the work was completed on this client CRM follow up action.
     *
     * @param completedAt time the work was completed
     */
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this client CRM follow up action.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this client CRM follow up action.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
