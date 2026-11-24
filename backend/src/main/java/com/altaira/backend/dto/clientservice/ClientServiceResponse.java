package com.altaira.backend.dto.clientservice;

import com.altaira.backend.dto.service.ServiceResponse;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for client service. */
public class ClientServiceResponse {

    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the client whose workspace is in scope. */
    private UUID clientId;
    /** Service in scope. */
    private ServiceResponse service;
    /** Current state in the workflow. */
    private String status;
    /** Free-text notes associated with the item. */
    private String notes;
    /** Time the record was created. */
    private Instant createdAt;
    /** Time of the most recent update. */
    private Instant updatedAt;

    /** Constructs {@code ClientServiceResponse} with its default state. */
    public ClientServiceResponse() {}

    /**
     * Constructs {@code ClientServiceResponse} using the supplied id, client id, service and remaining fields.
     *
     * @param id unique identifier of the record
     * @param clientId identifier of the client whose workspace is in scope
     * @param service service in scope
     * @param status current state in the workflow
     * @param notes free-text notes associated with the item
     * @param createdAt time the record was created
     * @param updatedAt time of the most recent update
     */
    public ClientServiceResponse(UUID id, UUID clientId, ServiceResponse service, String status, String notes, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.clientId = clientId;
        this.service = service;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the client whose workspace is in scope} */
    public UUID getClientId() { return clientId; }
    /**
     * Sets the identifier of the client whose workspace is in scope on this client.
     *
     * @param clientId identifier of the client whose workspace is in scope
     */
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    /** {@return service in scope} */
    public ServiceResponse getService() { return service; }
    /**
     * Sets the service in scope on this client.
     *
     * @param service service in scope
     */
    public void setService(ServiceResponse service) { this.service = service; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this client.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return free-text notes associated with the item} */
    public String getNotes() { return notes; }
    /**
     * Sets the free-text notes associated with the item on this client.
     *
     * @param notes free-text notes associated with the item
     */
    public void setNotes(String notes) { this.notes = notes; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this client.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this client.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
