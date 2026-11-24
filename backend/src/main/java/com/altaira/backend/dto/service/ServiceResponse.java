package com.altaira.backend.dto.service;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for service. */
public class ServiceResponse {

    /** Unique identifier of the record. */
    private UUID id;
    /** Name used to identify the item. */
    private String name;
    /** Category. */
    private String category;
    /** Human-readable description. */
    private String description;
    /** Whether the record is currently active. */
    private boolean active;
    /** Time the record was created. */
    private Instant createdAt;
    /** Time of the most recent update. */
    private Instant updatedAt;

    /** Constructs {@code ServiceResponse} with its default state. */
    public ServiceResponse() {}

    /**
     * Constructs {@code ServiceResponse} using the supplied id, name, category and remaining fields.
     *
     * @param id unique identifier of the record
     * @param name name used to identify the item
     * @param category category
     * @param description human-readable description
     * @param active whether the record is currently active
     * @param createdAt time the record was created
     * @param updatedAt time of the most recent update
     */
    public ServiceResponse(UUID id, String name, String category, String description, boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
