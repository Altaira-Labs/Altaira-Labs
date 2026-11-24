package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Maps service records to the {@code services} table. */
@Entity
@Table(name = "services")
public class ServiceEntity {

    /** Primary identifier for this service. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Name used to identify the item. */
    @Column(nullable = false, unique = true)
    private String name;

    /** Category. */
    private String category;

    /** Human-readable description. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Whether the record is currently active. */
    @Column(nullable = false)
    private boolean active = true;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Time of the most recent update. Stored in {@code updated_at}. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Constructs {@code ServiceEntity} with its default state. */
    public ServiceEntity() {}

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
     * Sets the unique identifier of the record on this .
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return name used to identify the item} */
    public String getName() { return name; }
    /**
     * Sets the name used to identify the item on this .
     *
     * @param name name used to identify the item
     */
    public void setName(String name) { this.name = name; }

    /** {@return category} */
    public String getCategory() { return category; }
    /**
     * Sets the category on this .
     *
     * @param category category
     */
    public void setCategory(String category) { this.category = category; }

    /** {@return human-readable description} */
    public String getDescription() { return description; }
    /**
     * Sets the human-readable description on this .
     *
     * @param description human-readable description
     */
    public void setDescription(String description) { this.description = description; }

    /** {@return whether the record is currently active} */
    public boolean isActive() { return active; }
    /**
     * Sets the whether the record is currently active on this .
     *
     * @param active whether the record is currently active
     */
    public void setActive(boolean active) { this.active = active; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this .
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this .
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
