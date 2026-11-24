package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps onboarding audit log records to the {@code onboarding_audit_logs} table. */
@Entity
@Table(name = "onboarding_audit_logs")
public class OnboardingAuditLogEntity {

    /** Primary identifier for this onboarding audit log. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Task. Joined through {@code task_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private OnboardingTaskEntity task;

    /** Client workspace in scope. Joined through {@code workspace_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private ClientWorkspaceEntity workspace;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    /** Actor user. Joined through {@code actor_user_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id")
    private AppUserEntity actorUser;

    /** Actor role. Stored in {@code actor_role}. */
    @Column(name = "actor_role", nullable = false, length = 80)
    private String actorRole;

    /** Action being recorded. */
    @Column(nullable = false, length = 120)
    private String action;

    /** Network address associated with the request. Stored in {@code ip_address}. */
    @Column(name = "ip_address", length = 80)
    private String ipAddress;

    /** Client software reported by the HTTP request. Stored in {@code user_agent}. */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /** JSON representation of metadata. Stored in {@code metadata_json}. */
    @Column(name = "metadata_json", nullable = false, columnDefinition = "TEXT")
    private String metadataJson = "{}";

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
     * Sets the unique identifier of the record on this onboarding audit log.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return task} */
    public OnboardingTaskEntity getTask() { return task; }
    /**
     * Sets the task on this onboarding audit log.
     *
     * @param task task
     */
    public void setTask(OnboardingTaskEntity task) { this.task = task; }

    /** {@return client workspace in scope} */
    public ClientWorkspaceEntity getWorkspace() { return workspace; }
    /**
     * Sets the client workspace in scope on this onboarding audit log.
     *
     * @param workspace client workspace in scope
     */
    public void setWorkspace(ClientWorkspaceEntity workspace) { this.workspace = workspace; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this onboarding audit log.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

    /** {@return actor user} */
    public AppUserEntity getActorUser() { return actorUser; }
    /**
     * Sets the actor user on this onboarding audit log.
     *
     * @param actorUser actor user
     */
    public void setActorUser(AppUserEntity actorUser) { this.actorUser = actorUser; }

    /** {@return actor role} */
    public String getActorRole() { return actorRole; }
    /**
     * Sets the actor role on this onboarding audit log.
     *
     * @param actorRole actor role
     */
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }

    /** {@return action being recorded} */
    public String getAction() { return action; }
    /**
     * Sets the action being recorded on this onboarding audit log.
     *
     * @param action action being recorded
     */
    public void setAction(String action) { this.action = action; }

    /** {@return network address associated with the request} */
    public String getIpAddress() { return ipAddress; }
    /**
     * Sets the network address associated with the request on this onboarding audit log.
     *
     * @param ipAddress network address associated with the request
     */
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    /** {@return client software reported by the HTTP request} */
    public String getUserAgent() { return userAgent; }
    /**
     * Sets the client software reported by the HTTP request on this onboarding audit log.
     *
     * @param userAgent client software reported by the HTTP request
     */
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    /** {@return JSON representation of metadata} */
    public String getMetadataJson() { return metadataJson; }
    /**
     * Sets the JSON representation of metadata on this onboarding audit log.
     *
     * @param metadataJson JSON representation of metadata
     */
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this onboarding audit log.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
