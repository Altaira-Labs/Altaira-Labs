package com.altaira.backend.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

/** Controls which audience may see a workspace task. */
public enum WorkspaceTaskVisibility {
    /** Represents the admin only option for workspace task visibility. */
    ADMIN_ONLY,
    /** Represents the client visible option for workspace task visibility. */
    CLIENT_VISIBLE;

    /**
     * Resolves the supplied text to a workspace task visibility value using this enum’s accepted spellings.
     *
     * @param value stored or supplied representation
     * @return the workspace task visibility
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
     */
    public static WorkspaceTaskVisibility parse(String value) {
        if (value == null || value.isBlank()) {
            return ADMIN_ONLY;
        }

        try {
            return valueOf(value.trim().replace('-', '_').toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported task visibility: " + value);
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
