package com.altaira.backend.model;

import java.util.Arrays;
import java.util.Locale;

/** Allowed lead assessment status values understood by the backend. */
public enum LeadAssessmentStatus {
    /** Represents draft with serialized value {@code draft}. */
    DRAFT("draft"),
    /** Represents submitted with serialized value {@code submitted}. */
    SUBMITTED("submitted"),
    /** Represents reviewed with serialized value {@code reviewed}. */
    REVIEWED("reviewed");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code LeadAssessmentStatus} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    LeadAssessmentStatus(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this lead assessment status.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }

    /**
     * Parses or submitted. Uses locale-independent case normalization.
     *
     * @param value stored or supplied representation
     * @return the lead assessment status
     * @throws IllegalArgumentException when validation reports “Unsupported lead assessment status”
     */
    public static LeadAssessmentStatus parseOrSubmitted(String value) {
        if (value == null || value.isBlank()) {
            return SUBMITTED;
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(status -> status.value.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported lead assessment status"));
    }
}
