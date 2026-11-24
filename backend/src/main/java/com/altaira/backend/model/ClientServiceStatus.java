package com.altaira.backend.model;

import java.util.Arrays;
import java.util.Locale;

/** Allowed client service status values understood by the backend. */
public enum ClientServiceStatus {
    /** Represents planned with serialized value {@code planned}. */
    PLANNED("planned"),
    /** Represents in progress with serialized value {@code in_progress}. */
    IN_PROGRESS("in_progress"),
    /** Represents review with serialized value {@code review}. */
    REVIEW("review"),
    /** Represents delivered with serialized value {@code delivered}. */
    DELIVERED("delivered"),
    /** Represents cancelled with serialized value {@code cancelled}. */
    CANCELLED("cancelled");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code ClientServiceStatus} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    ClientServiceStatus(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this client service status.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }

    /**
     * Resolves the supplied text to a client service status value using this enum’s accepted spellings.
     *
     * @param status current state in the workflow
     * @return the client service status
     * @throws IllegalArgumentException when validation reports “Client service status is required” or “Invalid client service status”
     */
    public static ClientServiceStatus parse(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Client service status is required");
        }

        String normalizedStatus = status.trim().toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(serviceStatus -> serviceStatus.value.equals(normalizedStatus))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid client service status"));
    }
}
