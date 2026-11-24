package com.altaira.backend.dto.clientcrm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/** Input for create client CRM follow up action in the clientCRM API. */
public class CreateClientCrmFollowUpActionRequest {

    /** Short heading shown to a reader. The request must supply nonblank text. */
    @NotBlank(message = "Follow-up action title is required")
    @Size(max = 180, message = "Follow-up action title must be at most 180 characters")
    private String title;

    /** Human-readable description. */
    @Size(max = 1200, message = "Follow-up action description must be at most 1200 characters")
    private String description;

    /** Whether the client can see the item. */
    private Boolean visibleToClient;

    /** Deadline for the work. */
    private Instant dueAt;

    /** {@return short heading shown to a reader} */
    public String getTitle() { return title; }
    /**
     * Sets the short heading shown to a reader on this create client CRM follow up action.
     *
     * @param title short heading shown to a reader
     */
    public void setTitle(String title) { this.title = title; }

    /** {@return human-readable description} */
    public String getDescription() { return description; }
    /**
     * Sets the human-readable description on this create client CRM follow up action.
     *
     * @param description human-readable description
     */
    public void setDescription(String description) { this.description = description; }

    /** {@return whether the client can see the item} */
    public Boolean getVisibleToClient() { return visibleToClient; }
    /**
     * Sets the whether the client can see the item on this create client CRM follow up action.
     *
     * @param visibleToClient whether the client can see the item
     */
    public void setVisibleToClient(Boolean visibleToClient) { this.visibleToClient = visibleToClient; }

    /** {@return deadline for the work} */
    public Instant getDueAt() { return dueAt; }
    /**
     * Sets the deadline for the work on this create client CRM follow up action.
     *
     * @param dueAt deadline for the work
     */
    public void setDueAt(Instant dueAt) { this.dueAt = dueAt; }
}
