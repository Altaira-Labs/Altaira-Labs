package com.altaira.backend.model;

import java.util.Arrays;
import java.util.Locale;

/** Allowed client CRM lead priority values understood by the backend. */
public enum ClientCrmLeadPriority {
    /** Represents low with serialized value {@code low}. */
    LOW("low"),
    /** Represents normal with serialized value {@code normal}. */
    NORMAL("normal"),
    /** Represents high with serialized value {@code high}. */
    HIGH("high"),
    /** Represents urgent with serialized value {@code urgent}. */
    URGENT("urgent");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code ClientCrmLeadPriority} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    ClientCrmLeadPriority(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this client CRM lead priority.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }

    /**
     * Resolves the supplied text to a client CRM lead priority value using this enum’s accepted spellings.
     *
     * @param priority relative urgency used to order work
     * @return the client CRM lead priority
     * @throws IllegalArgumentException when validation reports “Invalid client CRM lead priority”
     */
    public static ClientCrmLeadPriority parse(String priority) {
        if (priority == null || priority.isBlank()) {
            return NORMAL;
        }

        String normalized = priority.trim().toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(leadPriority -> leadPriority.value.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid client CRM lead priority"));
    }
}
