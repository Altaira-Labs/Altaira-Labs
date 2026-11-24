package com.altaira.backend.dto.clientportal;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for client project config snapshot. */
public class ClientProjectConfigSnapshotResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the client project. */
    private UUID projectId;
    /** Identifier of the client whose workspace is in scope. */
    private UUID clientId;
    /** Stable key for the service. */
    private String serviceKey;
    /** Artifact type. */
    private String artifactType;
    /** Display label. */
    private String label;
    /** JSON representation of config. */
    private String configJson;
    /** Created by username. */
    private String createdByUsername;
    /** Time the record was created. */
    private Instant createdAt;

    /** Constructs {@code ClientProjectConfigSnapshotResponse} with its default state. */
    public ClientProjectConfigSnapshotResponse() {}

    /**
     * Constructs {@code ClientProjectConfigSnapshotResponse} using the supplied id, project id, client id and remaining fields.
     *
     * @param id unique identifier of the record
     * @param projectId identifier of the client project
     * @param clientId identifier of the client whose workspace is in scope
     * @param serviceKey stable key for the service
     * @param artifactType artifact type
     * @param label display label
     * @param configJson JSON representation of config
     * @param createdByUsername created by username
     * @param createdAt time the record was created
     */
    public ClientProjectConfigSnapshotResponse(
            UUID id,
            UUID projectId,
            UUID clientId,
            String serviceKey,
            String artifactType,
            String label,
            String configJson,
            String createdByUsername,
            Instant createdAt
    ) {
        this.id = id;
        this.projectId = projectId;
        this.clientId = clientId;
        this.serviceKey = serviceKey;
        this.artifactType = artifactType;
        this.label = label;
        this.configJson = configJson;
        this.createdByUsername = createdByUsername;
        this.createdAt = createdAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client project config snapshot.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the client project} */
    public UUID getProjectId() { return projectId; }
    /**
     * Sets the identifier of the client project on this client project config snapshot.
     *
     * @param projectId identifier of the client project
     */
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    /** {@return identifier of the client whose workspace is in scope} */
    public UUID getClientId() { return clientId; }
    /**
     * Sets the identifier of the client whose workspace is in scope on this client project config snapshot.
     *
     * @param clientId identifier of the client whose workspace is in scope
     */
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    /** {@return stable key for the service} */
    public String getServiceKey() { return serviceKey; }
    /**
     * Sets the stable key for the service on this client project config snapshot.
     *
     * @param serviceKey stable key for the service
     */
    public void setServiceKey(String serviceKey) { this.serviceKey = serviceKey; }

    /** {@return artifact type} */
    public String getArtifactType() { return artifactType; }
    /**
     * Sets the artifact type on this client project config snapshot.
     *
     * @param artifactType artifact type
     */
    public void setArtifactType(String artifactType) { this.artifactType = artifactType; }

    /** {@return display label} */
    public String getLabel() { return label; }
    /**
     * Sets the display label on this client project config snapshot.
     *
     * @param label display label
     */
    public void setLabel(String label) { this.label = label; }

    /** {@return JSON representation of config} */
    public String getConfigJson() { return configJson; }
    /**
     * Sets the JSON representation of config on this client project config snapshot.
     *
     * @param configJson JSON representation of config
     */
    public void setConfigJson(String configJson) { this.configJson = configJson; }

    /** {@return created by username} */
    public String getCreatedByUsername() { return createdByUsername; }
    /**
     * Sets the created by username on this client project config snapshot.
     *
     * @param createdByUsername created by username
     */
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this client project config snapshot.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
