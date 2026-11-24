package com.altaira.backend.dto.clientportal;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Fields the API returns for client project. */
public class ClientProjectResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the service assignment for this client. */
    private UUID clientServiceId;
    /** Key identifying the project. */
    private String projectKey;
    /** Name used to identify the item. */
    private String name;
    /** Current phase. */
    private String currentPhase;
    /** URL for staging. */
    private String stagingUrl;
    /** Latest client feedback. */
    private String latestClientFeedback;
    /** Time associated with revision pending. */
    private Instant revisionPendingAt;
    /** Assets. */
    private List<ClientProjectAssetResponse> assets;
    /** Time the record was created. */
    private Instant createdAt;
    /** Time of the most recent update. */
    private Instant updatedAt;

    /** Constructs {@code ClientProjectResponse} with its default state. */
    public ClientProjectResponse() {}

    /**
     * Constructs {@code ClientProjectResponse} using the supplied id, client service id, project key and remaining fields.
     *
     * @param id unique identifier of the record
     * @param clientServiceId identifier of the service assignment for this client
     * @param projectKey key identifying the project
     * @param name name used to identify the item
     * @param currentPhase current phase
     * @param stagingUrl URL for staging
     * @param latestClientFeedback latest client feedback
     * @param revisionPendingAt time associated with revision pending
     * @param assets assets
     * @param createdAt time the record was created
     * @param updatedAt time of the most recent update
     */
    public ClientProjectResponse(
            UUID id,
            UUID clientServiceId,
            String projectKey,
            String name,
            String currentPhase,
            String stagingUrl,
            String latestClientFeedback,
            Instant revisionPendingAt,
            List<ClientProjectAssetResponse> assets,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.clientServiceId = clientServiceId;
        this.projectKey = projectKey;
        this.name = name;
        this.currentPhase = currentPhase;
        this.stagingUrl = stagingUrl;
        this.latestClientFeedback = latestClientFeedback;
        this.revisionPendingAt = revisionPendingAt;
        this.assets = assets;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client project.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the service assignment for this client} */
    public UUID getClientServiceId() { return clientServiceId; }
    /**
     * Sets the identifier of the service assignment for this client on this client project.
     *
     * @param clientServiceId identifier of the service assignment for this client
     */
    public void setClientServiceId(UUID clientServiceId) { this.clientServiceId = clientServiceId; }

    /** {@return key identifying the project} */
    public String getProjectKey() { return projectKey; }
    /**
     * Sets the key identifying the project on this client project.
     *
     * @param projectKey key identifying the project
     */
    public void setProjectKey(String projectKey) { this.projectKey = projectKey; }

    /** {@return name used to identify the item} */
    public String getName() { return name; }
    /**
     * Sets the name used to identify the item on this client project.
     *
     * @param name name used to identify the item
     */
    public void setName(String name) { this.name = name; }

    /** {@return current phase} */
    public String getCurrentPhase() { return currentPhase; }
    /**
     * Sets the current phase on this client project.
     *
     * @param currentPhase current phase
     */
    public void setCurrentPhase(String currentPhase) { this.currentPhase = currentPhase; }

    /** {@return URL for staging} */
    public String getStagingUrl() { return stagingUrl; }
    /**
     * Sets the URL for staging on this client project.
     *
     * @param stagingUrl URL for staging
     */
    public void setStagingUrl(String stagingUrl) { this.stagingUrl = stagingUrl; }

    /** {@return latest client feedback} */
    public String getLatestClientFeedback() { return latestClientFeedback; }
    /**
     * Sets the latest client feedback on this client project.
     *
     * @param latestClientFeedback latest client feedback
     */
    public void setLatestClientFeedback(String latestClientFeedback) { this.latestClientFeedback = latestClientFeedback; }

    /** {@return time associated with revision pending} */
    public Instant getRevisionPendingAt() { return revisionPendingAt; }
    /**
     * Sets the time associated with revision pending on this client project.
     *
     * @param revisionPendingAt time associated with revision pending
     */
    public void setRevisionPendingAt(Instant revisionPendingAt) { this.revisionPendingAt = revisionPendingAt; }

    /** {@return assets} */
    public List<ClientProjectAssetResponse> getAssets() { return assets; }
    /**
     * Sets the assets on this client project.
     *
     * @param assets assets
     */
    public void setAssets(List<ClientProjectAssetResponse> assets) { this.assets = assets; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this client project.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this client project.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
