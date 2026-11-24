package com.altaira.backend.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

/** Allowed workspace task priority values understood by the backend. */
public enum WorkspaceTaskPriority {
    /** Represents the low option for workspace task priority. */
    LOW,
    /** Represents the normal option for workspace task priority. */
    NORMAL,
    /** Represents the high option for workspace task priority. */
    HIGH,
    /** Represents the urgent option for workspace task priority. */
    URGENT;

    /**
     * Resolves the supplied text to a workspace task priority value using this enum’s accepted spellings.
     *
     * @param value stored or supplied representation
     * @return the workspace task priority
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
     */
    public static WorkspaceTaskPriority parse(String value) {
        if (value == null || value.isBlank()) {
            return NORMAL;
        }

        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported task priority: " + value);
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
}
