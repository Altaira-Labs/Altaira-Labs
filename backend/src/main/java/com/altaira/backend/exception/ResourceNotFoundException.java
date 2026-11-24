package com.altaira.backend.exception;

import java.util.UUID;

/** Identifies the type and identifier of a resource that could not be found. */
public class ResourceNotFoundException extends RuntimeException {

    /** Name assigned to the resource. */
    private final String resourceName;

    /**
     * Constructs {@code ResourceNotFoundException} using the supplied resource name, id.
     *
     * @param resourceName name assigned to the resource
     * @param id unique identifier of the record
     */
    public ResourceNotFoundException(String resourceName, UUID id) {
        super(resourceName + " not found: " + id);
        this.resourceName = resourceName;
    }

    /** {@return name assigned to the resource} */
    public String getResourceName() {
        return resourceName;
    }
}
