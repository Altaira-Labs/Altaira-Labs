package com.altaira.backend.dto.clientcrm;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for client CRM follow up action. */
public class ClientCrmFollowUpActionResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the source sales enquiry. */
    private UUID leadId;
    /** Identifier of the client whose workspace is in scope. */
    private UUID clientId;
    /** Short heading shown to a reader. */
    private String title;
    /** Human-readable description. */
    private String description;
    /** Current state in the workflow. */
    private String status;
    /** Role responsible for the work. */
    private String ownerRole;
    /** Created by username. */
    private String createdByUsername;
    /** Whether the client can see the item. */
    private boolean visibleToClient;
    /** Deadline for the work. */
    private Instant dueAt;
    /** Time the work was completed. */
    private Instant completedAt;
    /** Time the record was created. */
    private Instant createdAt;
    /** Time of the most recent update. */
    private Instant updatedAt;

    /** Constructs {@code ClientCrmFollowUpActionResponse} with its default state. */
    public ClientCrmFollowUpActionResponse() {}

    /**
     * Constructs {@code ClientCrmFollowUpActionResponse} using the supplied id, lead id, client id and remaining fields.
     *
     * @param id unique identifier of the record
     * @param leadId identifier of the source sales enquiry
     * @param clientId identifier of the client whose workspace is in scope
     * @param title short heading shown to a reader
     * @param description human-readable description
     * @param status current state in the workflow
     * @param ownerRole role responsible for the work
     * @param createdByUsername created by username
     * @param visibleToClient whether the client can see the item
     * @param dueAt deadline for the work
     * @param completedAt time the work was completed
     * @param createdAt time the record was created
     * @param updatedAt time of the most recent update
     */
    public ClientCrmFollowUpActionResponse(
            UUID id,
            UUID leadId,
            UUID clientId,
            String title,
            String description,
            String status,
            String ownerRole,
            String createdByUsername,
            boolean visibleToClient,
            Instant dueAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.leadId = leadId;
        this.clientId = clientId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.ownerRole = ownerRole;
        this.createdByUsername = createdByUsername;
        this.visibleToClient = visibleToClient;
        this.dueAt = dueAt;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client CRM follow up action.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the source sales enquiry} */
    public UUID getLeadId() { return leadId; }
    /**
     * Sets the identifier of the source sales enquiry on this client CRM follow up action.
     *
     * @param leadId identifier of the source sales enquiry
     */
    public void setLeadId(UUID leadId) { this.leadId = leadId; }

    /** {@return identifier of the client whose workspace is in scope} */
    public UUID getClientId() { return clientId; }
    /**
     * Sets the identifier of the client whose workspace is in scope on this client CRM follow up action.
     *
     * @param clientId identifier of the client whose workspace is in scope
     */
    public void setClientId(UUID clientId) { this.clientId = clientId; }

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

    /** {@return role responsible for the work} */
    public String getOwnerRole() { return ownerRole; }
    /**
     * Sets the role responsible for the work on this client CRM follow up action.
     *
     * @param ownerRole role responsible for the work
     */
    public void setOwnerRole(String ownerRole) { this.ownerRole = ownerRole; }

    /** {@return created by username} */
    public String getCreatedByUsername() { return createdByUsername; }
    /**
     * Sets the created by username on this client CRM follow up action.
     *
     * @param createdByUsername created by username
     */
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    /** {@return whether the client can see the item} */
    public boolean isVisibleToClient() { return visibleToClient; }
    /**
     * Sets the whether the client can see the item on this client CRM follow up action.
     *
     * @param visibleToClient whether the client can see the item
     */
    public void setVisibleToClient(boolean visibleToClient) { this.visibleToClient = visibleToClient; }

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
