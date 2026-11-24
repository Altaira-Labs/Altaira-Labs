package com.altaira.backend.model;

import java.util.Arrays;
import java.util.Locale;

/** Allowed client CRM lead status values understood by the backend. */
public enum ClientCrmLeadStatus {
    /** Represents new lead with serialized value {@code new_lead}. */
    NEW_LEAD("new_lead"),
    /** Represents contacted with serialized value {@code contacted}. */
    CONTACTED("contacted"),
    /** Represents appointment scheduled with serialized value {@code appointment_scheduled}. */
    APPOINTMENT_SCHEDULED("appointment_scheduled"),
    /** Represents proposal sent with serialized value {@code proposal_sent}. */
    PROPOSAL_SENT("proposal_sent"),
    /** Represents won with serialized value {@code won}. */
    WON("won"),
    /** Represents lost with serialized value {@code lost}. */
    LOST("lost");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code ClientCrmLeadStatus} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    ClientCrmLeadStatus(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this client CRM lead status.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }

    /**
     * Resolves the supplied text to a client CRM lead status value using this enum’s accepted spellings.
     *
     * @param status current state in the workflow
     * @return the client CRM lead status
     * @throws IllegalArgumentException when validation reports “Invalid client CRM lead status”
     */
    public static ClientCrmLeadStatus parse(String status) {
        if (status == null || status.isBlank()) {
            return NEW_LEAD;
        }

        String normalized = status.trim().toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(leadStatus -> leadStatus.value.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid client CRM lead status"));
    }
}
