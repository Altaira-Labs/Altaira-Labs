package com.altaira.backend.model;

import java.util.Arrays;
import java.util.Locale;

/** Allowed client status values understood by the backend. */
public enum ClientStatus {
    /** Represents active with serialized value {@code active}. */
    ACTIVE("active"),
    /** Represents paused with serialized value {@code paused}. */
    PAUSED("paused"),
    /** Represents archived with serialized value {@code archived}. */
    ARCHIVED("archived");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code ClientStatus} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    ClientStatus(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this client status.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }

    /**
     * Resolves the supplied text to a client status value using this enum’s accepted spellings.
     *
     * @param status current state in the workflow
     * @return the client status
     * @throws IllegalArgumentException when validation reports “Invalid client status”
     */
    public static ClientStatus parse(String status) {
        if (status == null || status.isBlank()) {
            return ACTIVE;
        }

        String normalizedStatus = status.trim().toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(clientStatus -> clientStatus.value.equals(normalizedStatus))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid client status"));
    }
}
