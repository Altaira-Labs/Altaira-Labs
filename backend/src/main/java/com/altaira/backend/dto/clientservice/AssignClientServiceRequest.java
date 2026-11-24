package com.altaira.backend.dto.clientservice;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/** Input for assign client service in the clientservice API. */
public class AssignClientServiceRequest {

    /** Identifier of the catalogue entry. Required during request validation. */
    @NotNull(message = "Service id is required")
    private UUID serviceId;

    /** Current state in the workflow. */
    @Size(max = 20, message = "Client service status must be at most 20 characters")
    private String status;

    /** Free-text notes associated with the item. */
    @Size(max = 1000, message = "Notes must be at most 1000 characters")
    private String notes;

    /** {@return identifier of the catalogue entry} */
    public UUID getServiceId() { return serviceId; }
    /**
     * Sets the identifier of the catalogue entry on this assign client.
     *
     * @param serviceId identifier of the catalogue entry
     */
    public void setServiceId(UUID serviceId) { this.serviceId = serviceId; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this assign client.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return free-text notes associated with the item} */
    public String getNotes() { return notes; }
    /**
     * Sets the free-text notes associated with the item on this assign client.
     *
     * @param notes free-text notes associated with the item
     */
    public void setNotes(String notes) { this.notes = notes; }
}
