package com.altaira.backend.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

/** Allowed workspace task status values understood by the backend. */
public enum WorkspaceTaskStatus {
    /** Represents the not started option for workspace task status. */
    NOT_STARTED,
    /** Represents the in progress option for workspace task status. */
    IN_PROGRESS,
    /** Represents the submitted option for workspace task status. */
    SUBMITTED,
    /** Represents the needs review option for workspace task status. */
    NEEDS_REVIEW,
    /** Represents the approved option for workspace task status. */
    APPROVED,
    /** Represents the rejected option for workspace task status. */
    REJECTED,
    /** Represents the blocked option for workspace task status. */
    BLOCKED,
    /** Represents the completed option for workspace task status. */
    COMPLETED;

    /**
     * Resolves the supplied text to a workspace task status value using this enum’s accepted spellings.
     *
     * @param value stored or supplied representation
     * @return the workspace task status
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
     */
    public static WorkspaceTaskStatus parse(String value) {
        if (value == null || value.isBlank()) {
            return NOT_STARTED;
        }

        try {
            return valueOf(value.trim().replace('-', '_').toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported task status: " + value);
        }
    }

    /**
     * Returns the lowercase enum name used in API and stored values.
     *
     * @return the resulting text
     */
    public String value() {
        return name().toLowerCase(Locale.ROOT);
    }

    /**
     * Checks whether this is finished.
     *
     * @return true if the value is finished
     */
    public boolean isFinished() {
        return this == APPROVED || this == COMPLETED;
    }
}
