package com.altaira.backend.dto.workspacetask;

import jakarta.validation.constraints.Size;

import java.time.Instant;

/** Input for update workspace task in the workspacetask API. */
public class UpdateWorkspaceTaskRequest {

    /** Short heading shown to a reader. */
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
    /** Time associated with clear due. */
    private Boolean clearDueAt;

    /** {@return short heading shown to a reader} */
    public String getTitle() { return title; }
    /**
     * Sets the short heading shown to a reader on this update workspace task.
     *
     * @param title short heading shown to a reader
     */
    public void setTitle(String title) { this.title = title; }

    /** {@return human-readable description} */
    public String getDescription() { return description; }
    /**
     * Sets the human-readable description on this update workspace task.
     *
     * @param description human-readable description
     */
    public void setDescription(String description) { this.description = description; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this update workspace task.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return relative urgency used to order work} */
    public String getPriority() { return priority; }
    /**
     * Sets the relative urgency used to order work on this update workspace task.
     *
     * @param priority relative urgency used to order work
     */
    public void setPriority(String priority) { this.priority = priority; }

    /** {@return role responsible for the work} */
    public String getOwnerRole() { return ownerRole; }
    /**
     * Sets the role responsible for the work on this update workspace task.
     *
     * @param ownerRole role responsible for the work
     */
    public void setOwnerRole(String ownerRole) { this.ownerRole = ownerRole; }

    /** {@return audience allowed to see the item} */
    public String getVisibility() { return visibility; }
    /**
     * Sets the audience allowed to see the item on this update workspace task.
     *
     * @param visibility audience allowed to see the item
     */
    public void setVisibility(String visibility) { this.visibility = visibility; }

    /** {@return deadline for the work} */
    public Instant getDueAt() { return dueAt; }
    /**
     * Sets the deadline for the work on this update workspace task.
     *
     * @param dueAt deadline for the work
     */
    public void setDueAt(Instant dueAt) { this.dueAt = dueAt; }

    /** {@return time associated with clear due} */
    public Boolean getClearDueAt() { return clearDueAt; }
    /**
     * Sets the time associated with clear due on this update workspace task.
     *
     * @param clearDueAt time associated with clear due
     */
    public void setClearDueAt(Boolean clearDueAt) { this.clearDueAt = clearDueAt; }
}
