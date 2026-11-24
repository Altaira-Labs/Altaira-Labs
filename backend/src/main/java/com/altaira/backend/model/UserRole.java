package com.altaira.backend.model;

import java.util.Arrays;
import java.util.Locale;

/** Distinguishes internal staff roles from client roles for authorization decisions. */
public enum UserRole {
    /** Represents admin with serialized value {@code admin}. */
    ADMIN("admin"),
    /** Represents consultant with serialized value {@code consultant}. */
    CONSULTANT("consultant"),
    /** Represents auditor with serialized value {@code auditor}. */
    AUDITOR("auditor"),
    /** Represents client user with serialized value {@code client_user}. */
    CLIENT_USER("client_user"),
    /** Represents viewer with serialized value {@code viewer}. */
    VIEWER("viewer");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code UserRole} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    UserRole(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this user role.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }

    /**
     * Resolves the supplied text to a user role value using this enum’s accepted spellings.
     *
     * @param rawValue value as supplied, before normalization
     * @return the user role
     * @throws IllegalArgumentException when validation reports “User role is required” or “Invalid user role”
     */
    public static UserRole parse(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("User role is required");
        }

        String normalized = rawValue.trim().toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(role -> role.value.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid user role"));
    }

    /**
     * Recognizes the internal admin, consultant and auditor roles.
     *
     * @return true if the value is admin role
     */
    public boolean isAdminRole() {
        return this == ADMIN || this == CONSULTANT || this == AUDITOR;
    }

    /**
     * Recognizes client users and viewers as workspace roles.
     *
     * @return true if the value is client role
     */
    public boolean isClientRole() {
        return this == CLIENT_USER || this == VIEWER;
    }
}
