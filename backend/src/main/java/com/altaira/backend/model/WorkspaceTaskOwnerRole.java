package com.altaira.backend.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

/** Allowed workspace task owner role values understood by the backend. */
public enum WorkspaceTaskOwnerRole {
    /** Represents the admin option for workspace task owner role. */
    ADMIN,
    /** Represents the client option for workspace task owner role. */
    CLIENT;

    /**
     * Resolves the supplied text to a workspace task owner role value using this enum’s accepted spellings.
     *
     * @param value stored or supplied representation
     * @return the workspace task owner role
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
     */
    public static WorkspaceTaskOwnerRole parse(String value) {
        if (value == null || value.isBlank()) {
            return ADMIN;
        }

        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported task owner: " + value);
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
