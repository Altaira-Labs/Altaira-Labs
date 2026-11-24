package com.altaira.backend.model;

import java.util.Arrays;
import java.util.Locale;

/** Allowed onboarding task status values understood by the backend. */
public enum OnboardingTaskStatus {
    /** Represents pending with serialized value {@code pending}. */
    PENDING("pending"),
    /** Represents submitted with serialized value {@code submitted}. */
    SUBMITTED("submitted"),
    /** Represents approved with serialized value {@code approved}. */
    APPROVED("approved"),
    /** Represents rejected with serialized value {@code rejected}. */
    REJECTED("rejected");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code OnboardingTaskStatus} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    OnboardingTaskStatus(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this onboarding task status.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }

    /**
     * Resolves the supplied text to a onboarding task status value using this enum’s accepted spellings.
     *
     * @param rawValue value as supplied, before normalization
     * @return the onboarding task status
     * @throws IllegalArgumentException when validation reports “Invalid onboarding task status”
     */
    public static OnboardingTaskStatus parse(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return PENDING;
        }

        String normalized = rawValue.trim().toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(status -> status.value.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid onboarding task status"));
    }
}
