package com.altaira.backend.model;

import java.util.Arrays;
import java.util.Locale;

/** Identifies the sector-specific discovery form whose answers are being interpreted. */
public enum LeadIntakeFormKey {
    /** Represents general with serialized value {@code general}. */
    GENERAL("general"),
    /** Represents web seo with serialized value {@code web_seo}. */
    WEB_SEO("web_seo"),
    /** Represents booking with serialized value {@code booking}. */
    BOOKING("booking"),
    /** Represents CRM with serialized value {@code crm}. */
    CRM("crm"),
    /** Represents automation with serialized value {@code automation}. */
    AUTOMATION("automation"),
    /** Represents dashboard with serialized value {@code dashboard}. */
    DASHBOARD("dashboard");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code LeadIntakeFormKey} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    LeadIntakeFormKey(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this lead intake form key.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }

    /**
     * Resolves the supplied text to a lead intake form key value using this enum’s accepted spellings.
     *
     * @param value stored or supplied representation
     * @return the lead intake form key
     * @throws IllegalArgumentException when validation reports “Lead intake form key is required” or “Unsupported lead intake form key”
     */
    public static LeadIntakeFormKey parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Lead intake form key is required");
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(formKey -> formKey.value.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported lead intake form key"));
    }
}
