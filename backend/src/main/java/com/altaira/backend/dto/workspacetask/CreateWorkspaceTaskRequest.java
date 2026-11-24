package com.altaira.backend.dto.workspacetask;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/** Input for create workspace task in the workspacetask API. */
public class CreateWorkspaceTaskRequest {

    /** Identifier of the client whose workspace is in scope. Required during request validation. */
    @NotNull(message = "Client is required")
    private UUID clientId;

    /** Identifier of the service assignment for this client. */
    private UUID clientServiceId;
    /** Identifier of the client project. */
    private UUID projectId;

    /** Short heading shown to a reader. The request must supply nonblank text. */
    @NotBlank(message = "Task title is required")
    @Size(max = 240, message = "Task title must be at most 240 characters")
    private String title;

    /** Human-readable description. */
    @Size(max = 4000, message = "Task description must be at most 4000 characters")
    private String description;

    /** Current state in the workflow. */
    @Size(max = 40)
    private String status;

    /** Relative urgency used to order work. */
    @Size(max = 24)
    private String priority;

    /** Role responsible for the work. */
    @Size(max = 24)
    private String ownerRole;

    /** Audience allowed to see the item. */
    @Size(max = 32)
    private String visibility;

    /** Deadline for the work. */
    private Instant dueAt;

    /** {@return identifier of the client whose workspace is in scope} */
    public UUID getClientId() { return clientId; }
    /**
     * Sets the identifier of the client whose workspace is in scope on this create workspace task.
     *
     * @param clientId identifier of the client whose workspace is in scope
     */
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    /** {@return identifier of the service assignment for this client} */
    public UUID getClientServiceId() { return clientServiceId; }
    /**
     * Sets the identifier of the service assignment for this client on this create workspace task.
     *
     * @param clientServiceId identifier of the service assignment for this client
     */
    public void setClientServiceId(UUID clientServiceId) { this.clientServiceId = clientServiceId; }

    /** {@return identifier of the client project} */
    public UUID getProjectId() { return projectId; }
    /**
     * Sets the identifier of the client project on this create workspace task.
     *
     * @param projectId identifier of the client project
     */
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    /** {@return short heading shown to a reader} */
    public String getTitle() { return title; }
    /**
     * Sets the short heading shown to a reader on this create workspace task.
     *
     * @param title short heading shown to a reader
     */
    public void setTitle(String title) { this.title = title; }

    /** {@return human-readable description} */
    public String getDescription() { return description; }
    /**
     * Sets the human-readable description on this create workspace task.
     *
     * @param description human-readable description
     */
    public void setDescription(String description) { this.description = description; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this create workspace task.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return relative urgency used to order work} */
    public String getPriority() { return priority; }
    /**
     * Sets the relative urgency used to order work on this create workspace task.
     *
     * @param priority relative urgency used to order work
     */
    public void setPriority(String priority) { this.priority = priority; }

    /** {@return role responsible for the work} */
    public String getOwnerRole() { return ownerRole; }
    /**
     * Sets the role responsible for the work on this create workspace task.
     *
     * @param ownerRole role responsible for the work
     */
    public void setOwnerRole(String ownerRole) { this.ownerRole = ownerRole; }

    /** {@return audience allowed to see the item} */
    public String getVisibility() { return visibility; }
    /**
     * Sets the audience allowed to see the item on this create workspace task.
     *
     * @param visibility audience allowed to see the item
     */
    public void setVisibility(String visibility) { this.visibility = visibility; }

    /** {@return deadline for the work} */
    public Instant getDueAt() { return dueAt; }
    /**
     * Sets the deadline for the work on this create workspace task.
     *
     * @param dueAt deadline for the work
     */
    public void setDueAt(Instant dueAt) { this.dueAt = dueAt; }
}
