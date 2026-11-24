package com.altaira.backend.model;

import java.util.Arrays;
import java.util.Locale;

/** Allowed project phase values understood by the backend. */
public enum ProjectPhase {
    /** Represents requirements with serialized value {@code requirements}. */
    REQUIREMENTS("requirements"),
    /** Represents design with serialized value {@code design}. */
    DESIGN("design"),
    /** Represents development with serialized value {@code development}. */
    DEVELOPMENT("development"),
    /** Represents review with serialized value {@code review}. */
    REVIEW("review"),
    /** Represents launch with serialized value {@code launch}. */
    LAUNCH("launch");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code ProjectPhase} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    ProjectPhase(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this project phase.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }

    /**
     * Resolves the supplied text to a project phase value using this enum’s accepted spellings.
     *
     * @param rawValue value as supplied, before normalization
     * @return the project phase
     * @throws IllegalArgumentException when validation reports “Invalid project phase”
     */
    public static ProjectPhase parse(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return REQUIREMENTS;
        }

        String normalized = rawValue.trim().toLowerCase(Locale.ROOT)
                .replace("-", "_")
                .replace(" ", "_");

        return Arrays.stream(values())
                .filter(phase -> phase.value.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid project phase"));
    }
}
