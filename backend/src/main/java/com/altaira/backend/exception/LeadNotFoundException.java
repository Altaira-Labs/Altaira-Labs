package com.altaira.backend.exception;

import java.util.UUID;

/** Signals that a lead lookup did not find the requested record. */
public class LeadNotFoundException extends RuntimeException {

    /**
     * Constructs {@code LeadNotFoundException} using the supplied id.
     *
     * @param id unique identifier of the record
     */
    public LeadNotFoundException(UUID id) {
        super("Lead not found: " + id);
    }
}
