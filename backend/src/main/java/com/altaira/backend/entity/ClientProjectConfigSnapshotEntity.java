package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps client project config snapshot records to the {@code client_project_config_snapshots} table. */
@Entity
@Table(name = "client_project_config_snapshots")
public class ClientProjectConfigSnapshotEntity {

    /** Primary identifier for this client project config snapshot. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Project. Joined through {@code project_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private ClientProjectEntity project;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    /** Stable key for the service. Stored in {@code service_key}. */
    @Column(name = "service_key", nullable = false, length = 80)
    private String serviceKey;

    /** Artifact type. Stored in {@code artifact_type}. */
    @Column(name = "artifact_type", nullable = false, length = 120)
    private String artifactType;

    /** Display label. */
    @Column(nullable = false, length = 180)
    private String label;

    /** JSON representation of config. Stored in {@code config_json}. */
    @Column(name = "config_json", nullable = false, columnDefinition = "TEXT")
    private String configJson;

    /** Created by username. Stored in {@code created_by_username}. */
    @Column(name = "created_by_username", length = 180)
    private String createdByUsername;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Initializes created at before the record is inserted. */
    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client project config snapshot.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return project} */
    public ClientProjectEntity getProject() { return project; }
    /**
     * Sets the project on this client project config snapshot.
     *
     * @param project project
     */
    public void setProject(ClientProjectEntity project) { this.project = project; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this client project config snapshot.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

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
