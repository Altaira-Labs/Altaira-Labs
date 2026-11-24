package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps client project records to the {@code client_projects} table. */
@Entity
@Table(name = "client_projects")
public class ClientProjectEntity {

    /** Primary identifier for this client project. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    /** Client operations used by this component. Joined through {@code client_service_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_service_id")
    private ClientServiceEntity clientService;

    /** Key identifying the project. Stored in {@code project_key}. */
    @Column(name = "project_key", nullable = false, length = 80)
    private String projectKey = "web_seo";

    /** Name used to identify the item. */
    @Column(nullable = false)
    private String name;

    /** Current phase. Stored in {@code current_phase}. */
    @Column(name = "current_phase", nullable = false, length = 40)
    private String currentPhase = "requirements";

    /** URL for staging. Stored in {@code staging_url}. */
    @Column(name = "staging_url")
    private String stagingUrl;

    /** Latest client feedback. Stored in {@code latest_client_feedback}. */
    @Column(name = "latest_client_feedback", columnDefinition = "TEXT")
    private String latestClientFeedback;

    /** Time associated with revision pending. Stored in {@code revision_pending_at}. */
    @Column(name = "revision_pending_at")
    private Instant revisionPendingAt;

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
     * Sets the unique identifier of the record on this client project.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this client project.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

    /** {@return client operations used by this component} */
    public ClientServiceEntity getClientService() { return clientService; }
    /**
     * Sets the client operations used by this component on this client project.
     *
     * @param clientService client operations used by this component
     */
    public void setClientService(ClientServiceEntity clientService) { this.clientService = clientService; }

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
