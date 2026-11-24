package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps workspace task records to the {@code workspace_tasks} table. */
@Entity
@Table(
        name = "workspace_tasks",
        indexes = {
                @Index(name = "idx_workspace_tasks_client_status", columnList = "client_id,status"),
                @Index(name = "idx_workspace_tasks_service_status", columnList = "service_key,status"),
                @Index(name = "idx_workspace_tasks_project", columnList = "project_id"),
                @Index(name = "idx_workspace_tasks_due_at", columnList = "due_at")
        }
)
public class WorkspaceTaskEntity {

    /** Primary identifier for this workspace task. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Client workspace in scope. Joined through {@code workspace_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private ClientWorkspaceEntity workspace;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    /** Client operations used by this component. Joined through {@code client_service_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_service_id")
    private ClientServiceEntity clientService;

    /** Project. Joined through {@code project_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ClientProjectEntity project;

    /** Created by user. Joined through {@code created_by_user_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private AppUserEntity createdByUser;

    /** Stable key for the service. Stored in {@code service_key}. */
    @Column(name = "service_key", nullable = false, length = 80)
    private String serviceKey = "general";

    /** Short heading shown to a reader. */
    @Column(nullable = false, length = 240)
    private String title;

    /** Human-readable description. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Current state in the workflow. */
    @Column(nullable = false, length = 40)
    private String status = "not_started";

    /** Relative urgency used to order work. */
    @Column(nullable = false, length = 24)
    private String priority = "normal";

    /** Role responsible for the work. Stored in {@code owner_role}. */
    @Column(name = "owner_role", nullable = false, length = 24)
    private String ownerRole = "admin";

    /** Audience allowed to see the item. */
    @Column(nullable = false, length = 32)
    private String visibility = "admin_only";

    /** Deadline for the work. Stored in {@code due_at}. */
    @Column(name = "due_at")
    private Instant dueAt;

    /** Time the work was completed. Stored in {@code completed_at}. */
    @Column(name = "completed_at")
    private Instant completedAt;

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
     * Sets the unique identifier of the record on this workspace task.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return client workspace in scope} */
    public ClientWorkspaceEntity getWorkspace() { return workspace; }
    /**
     * Sets the client workspace in scope on this workspace task.
     *
     * @param workspace client workspace in scope
     */
    public void setWorkspace(ClientWorkspaceEntity workspace) { this.workspace = workspace; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this workspace task.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

    /** {@return client operations used by this component} */
    public ClientServiceEntity getClientService() { return clientService; }
    /**
     * Sets the client operations used by this component on this workspace task.
     *
     * @param clientService client operations used by this component
     */
    public void setClientService(ClientServiceEntity clientService) { this.clientService = clientService; }

    /** {@return project} */
    public ClientProjectEntity getProject() { return project; }
    /**
     * Sets the project on this workspace task.
     *
     * @param project project
     */
    public void setProject(ClientProjectEntity project) { this.project = project; }

    /** {@return created by user} */
    public AppUserEntity getCreatedByUser() { return createdByUser; }
    /**
     * Sets the created by user on this workspace task.
     *
     * @param createdByUser created by user
     */
    public void setCreatedByUser(AppUserEntity createdByUser) { this.createdByUser = createdByUser; }

    /** {@return stable key for the service} */
    public String getServiceKey() { return serviceKey; }
    /**
     * Sets the stable key for the service on this workspace task.
     *
     * @param serviceKey stable key for the service
     */
    public void setServiceKey(String serviceKey) { this.serviceKey = serviceKey; }

    /** {@return short heading shown to a reader} */
    public String getTitle() { return title; }
    /**
     * Sets the short heading shown to a reader on this workspace task.
     *
     * @param title short heading shown to a reader
     */
    public void setTitle(String title) { this.title = title; }

    /** {@return human-readable description} */
    public String getDescription() { return description; }
    /**
     * Sets the human-readable description on this workspace task.
     *
     * @param description human-readable description
     */
    public void setDescription(String description) { this.description = description; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this workspace task.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return relative urgency used to order work} */
    public String getPriority() { return priority; }
    /**
     * Sets the relative urgency used to order work on this workspace task.
     *
     * @param priority relative urgency used to order work
     */
    public void setPriority(String priority) { this.priority = priority; }

    /** {@return role responsible for the work} */
    public String getOwnerRole() { return ownerRole; }
    /**
     * Sets the role responsible for the work on this workspace task.
     *
     * @param ownerRole role responsible for the work
     */
    public void setOwnerRole(String ownerRole) { this.ownerRole = ownerRole; }

    /** {@return audience allowed to see the item} */
    public String getVisibility() { return visibility; }
    /**
     * Sets the audience allowed to see the item on this workspace task.
     *
     * @param visibility audience allowed to see the item
     */
    public void setVisibility(String visibility) { this.visibility = visibility; }

    /** {@return deadline for the work} */
    public Instant getDueAt() { return dueAt; }
    /**
     * Sets the deadline for the work on this workspace task.
     *
     * @param dueAt deadline for the work
     */
    public void setDueAt(Instant dueAt) { this.dueAt = dueAt; }

    /** {@return time the work was completed} */
    public Instant getCompletedAt() { return completedAt; }
    /**
     * Sets the time the work was completed on this workspace task.
     *
     * @param completedAt time the work was completed
     */
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this workspace task.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this workspace task.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
