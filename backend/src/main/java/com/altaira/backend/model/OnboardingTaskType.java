package com.altaira.backend.model;

import java.util.Arrays;
import java.util.Locale;

/** Allowed onboarding task type values understood by the backend. */
public enum OnboardingTaskType {
    /** Represents signature with serialized value {@code signature}. */
    SIGNATURE("signature"),
    /** Represents file upload with serialized value {@code file_upload}. */
    FILE_UPLOAD("file_upload"),
    /** Represents preferences form with serialized value {@code preferences_form}. */
    PREFERENCES_FORM("preferences_form");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code OnboardingTaskType} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    OnboardingTaskType(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this onboarding task type.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }

    /**
     * Resolves the supplied text to a onboarding task type value using this enum’s accepted spellings.
     *
     * @param rawValue value as supplied, before normalization
     * @return the onboarding task type
     * @throws IllegalArgumentException when validation reports “Onboarding task type is required” or “Invalid onboarding task type”
     */
    public static OnboardingTaskType parse(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("Onboarding task type is required");
        }

        String normalized = rawValue.trim().toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(type -> type.value.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid onboarding task type"));
    }
}
