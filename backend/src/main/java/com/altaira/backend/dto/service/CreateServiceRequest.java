package com.altaira.backend.dto.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for create service in the service API. */
public class CreateServiceRequest {

    /** Name used to identify the item. The request must supply nonblank text. */
    @NotBlank(message = "Service name is required")
    @Size(min = 2, max = 120, message = "Service name must be between 2 and 120 characters")
    private String name;

    /** Category. */
    @Size(max = 80, message = "Category must be at most 80 characters")
    private String category;

    /** Human-readable description. */
    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    /** Whether the record is currently active. */
    private Boolean active;

    /** {@return name used to identify the item} */
    public String getName() { return name; }
    /**
     * Sets the name used to identify the item on this create.
     *
     * @param name name used to identify the item
     */
    public void setName(String name) { this.name = name; }

    /** {@return category} */
    public String getCategory() { return category; }
    /**
     * Sets the category on this create.
     *
     * @param category category
     */
    public void setCategory(String category) { this.category = category; }

    /** {@return human-readable description} */
    public String getDescription() { return description; }
    /**
     * Sets the human-readable description on this create.
     *
     * @param description human-readable description
     */
    public void setDescription(String description) { this.description = description; }

    /** {@return whether the record is currently active} */
    public Boolean getActive() { return active; }
    /**
     * Sets the whether the record is currently active on this create.
     *
     * @param active whether the record is currently active
     */
    public void setActive(Boolean active) { this.active = active; }
}
