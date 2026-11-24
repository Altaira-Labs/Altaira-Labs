package com.altaira.backend.model;

import com.altaira.backend.exception.InvalidLeadStatusException;

import java.util.Arrays;
import java.util.Locale;

/** Allowed lead status values understood by the backend. */
public enum LeadStatus {
    /** Represents new with serialized value {@code new}. */
    NEW("new"),
    /** Represents contacted with serialized value {@code contacted}. */
    CONTACTED("contacted"),
    /** Represents qualified with serialized value {@code qualified}. */
    QUALIFIED("qualified"),
    /** Represents converted with serialized value {@code converted}. */
    CONVERTED("converted"),
    /** Represents lost with serialized value {@code lost}. */
    LOST("lost"),
    /** Represents closed with serialized value {@code closed}. */
    CLOSED("closed");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code LeadStatus} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    LeadStatus(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this lead status.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }

    /**
     * Resolves the supplied text to a lead status value using this enum’s accepted spellings.
     *
     * @param status current state in the workflow
     * @return the lead status
     */
    public static LeadStatus parse(String status) {
        if (status == null || status.isBlank()) {
            throw new InvalidLeadStatusException("Lead status is required");
        }

        String normalizedStatus = status.trim().toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(leadStatus -> leadStatus.value.equals(normalizedStatus))
                .findFirst()
                .orElseThrow(() -> new InvalidLeadStatusException("Invalid lead status"));
    }
}
