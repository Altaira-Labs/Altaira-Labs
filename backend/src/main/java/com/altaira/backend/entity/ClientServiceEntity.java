package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Maps client service records to the {@code client_services} table. */
@Entity
@Table(name = "client_services")
public class ClientServiceEntity {

    /** Primary identifier for this client service. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    /** Service in scope. Joined through {@code service_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    /** Current state in the workflow. */
    @Column(nullable = false)
    private String status;

    /** Free-text notes associated with the item. */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Time of the most recent update. Stored in {@code updated_at}. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Constructs {@code ClientServiceEntity} with its default state. */
    public ClientServiceEntity() {}

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
     * Sets the unique identifier of the record on this client.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this client.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

    /** {@return service in scope} */
    public ServiceEntity getService() { return service; }
    /**
     * Sets the service in scope on this client.
     *
     * @param service service in scope
     */
    public void setService(ServiceEntity service) { this.service = service; }

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
