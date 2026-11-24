package com.altaira.backend.dto.clientportal;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for admin client project summary. */
public class AdminClientProjectSummaryResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the client whose workspace is in scope. */
    private UUID clientId;
    /** Client name. */
    private String clientName;
    /** Client company. */
    private String clientCompany;
    /** Client status. */
    private String clientStatus;
    /** Identifier of the service assignment for this client. */
    private UUID clientServiceId;
    /** Service name. */
    private String serviceName;
    /** Key identifying the project. */
    private String projectKey;
    /** Name used to identify the item. */
    private String name;
    /** Current phase. */
    private String currentPhase;
    /** Review pending. */
    private boolean reviewPending;
    /** Time associated with revision pending. */
    private Instant revisionPendingAt;
    /** Time of the most recent update. */
    private Instant updatedAt;

    /** Constructs {@code AdminClientProjectSummaryResponse} with its default state. */
    public AdminClientProjectSummaryResponse() {}

    /**
     * Constructs {@code AdminClientProjectSummaryResponse} using the supplied id, client id, client name and remaining fields.
     *
     * @param id unique identifier of the record
     * @param clientId identifier of the client whose workspace is in scope
     * @param clientName client name
     * @param clientCompany client company
     * @param clientStatus client status
     * @param clientServiceId identifier of the service assignment for this client
     * @param serviceName service name
     * @param projectKey key identifying the project
     * @param name name used to identify the item
     * @param currentPhase current phase
     * @param reviewPending review pending
     * @param revisionPendingAt time associated with revision pending
     * @param updatedAt time of the most recent update
     */
    public AdminClientProjectSummaryResponse(
            UUID id,
            UUID clientId,
            String clientName,
            String clientCompany,
            String clientStatus,
            UUID clientServiceId,
            String serviceName,
            String projectKey,
            String name,
            String currentPhase,
            boolean reviewPending,
            Instant revisionPendingAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientCompany = clientCompany;
        this.clientStatus = clientStatus;
        this.clientServiceId = clientServiceId;
        this.serviceName = serviceName;
        this.projectKey = projectKey;
        this.name = name;
        this.currentPhase = currentPhase;
        this.reviewPending = reviewPending;
        this.revisionPendingAt = revisionPendingAt;
        this.updatedAt = updatedAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this admin client project summary.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the client whose workspace is in scope} */
    public UUID getClientId() { return clientId; }
    /**
     * Sets the identifier of the client whose workspace is in scope on this admin client project summary.
     *
     * @param clientId identifier of the client whose workspace is in scope
     */
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    /** {@return client name} */
    public String getClientName() { return clientName; }
    /**
     * Sets the client name on this admin client project summary.
     *
     * @param clientName client name
     */
    public void setClientName(String clientName) { this.clientName = clientName; }

    /** {@return client company} */
    public String getClientCompany() { return clientCompany; }
    /**
     * Sets the client company on this admin client project summary.
     *
     * @param clientCompany client company
     */
    public void setClientCompany(String clientCompany) { this.clientCompany = clientCompany; }

    /** {@return client status} */
    public String getClientStatus() { return clientStatus; }
    /**
     * Sets the client status on this admin client project summary.
     *
     * @param clientStatus client status
     */
    public void setClientStatus(String clientStatus) { this.clientStatus = clientStatus; }

    /** {@return identifier of the service assignment for this client} */
    public UUID getClientServiceId() { return clientServiceId; }
    /**
     * Sets the identifier of the service assignment for this client on this admin client project summary.
     *
     * @param clientServiceId identifier of the service assignment for this client
     */
    public void setClientServiceId(UUID clientServiceId) { this.clientServiceId = clientServiceId; }

    /** {@return service name} */
    public String getServiceName() { return serviceName; }
    /**
     * Sets the service name on this admin client project summary.
     *
     * @param serviceName service name
     */
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    /** {@return key identifying the project} */
    public String getProjectKey() { return projectKey; }
    /**
     * Sets the key identifying the project on this admin client project summary.
     *
     * @param projectKey key identifying the project
     */
    public void setProjectKey(String projectKey) { this.projectKey = projectKey; }

    /** {@return name used to identify the item} */
    public String getName() { return name; }
    /**
     * Sets the name used to identify the item on this admin client project summary.
     *
     * @param name name used to identify the item
     */
    public void setName(String name) { this.name = name; }

    /** {@return current phase} */
    public String getCurrentPhase() { return currentPhase; }
    /**
     * Sets the current phase on this admin client project summary.
     *
     * @param currentPhase current phase
     */
    public void setCurrentPhase(String currentPhase) { this.currentPhase = currentPhase; }

    /** {@return review pending} */
    public boolean isReviewPending() { return reviewPending; }
    /**
     * Sets the review pending on this admin client project summary.
     *
     * @param reviewPending review pending
     */
    public void setReviewPending(boolean reviewPending) { this.reviewPending = reviewPending; }

    /** {@return time associated with revision pending} */
    public Instant getRevisionPendingAt() { return revisionPendingAt; }
    /**
     * Sets the time associated with revision pending on this admin client project summary.
     *
     * @param revisionPendingAt time associated with revision pending
     */
    public void setRevisionPendingAt(Instant revisionPendingAt) { this.revisionPendingAt = revisionPendingAt; }

    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this admin client project summary.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
