package com.altaira.backend.model;

/** Allowed provisioning plan status values understood by the backend. */
public enum ProvisioningPlanStatus {
    /** Represents draft with serialized value {@code draft}. */
    DRAFT("draft"),
    /** Represents awaiting approval with serialized value {@code awaiting_approval}. */
    AWAITING_APPROVAL("awaiting_approval"),
    /** Represents approved with serialized value {@code approved}. */
    APPROVED("approved"),
    /** Represents provisioned with serialized value {@code provisioned}. */
    PROVISIONED("provisioned"),
    /** Represents partially completed with serialized value {@code partially_completed}. */
    PARTIALLY_COMPLETED("partially_completed"),
    /** Represents failed with serialized value {@code failed}. */
    FAILED("failed");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code ProvisioningPlanStatus} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    ProvisioningPlanStatus(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this provisioning plan status.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }

    /**
     * Resolves the supplied text to a provisioning plan status value using this enum’s accepted spellings.
     *
     * @param raw value received before parsing or normalization
     * @return the provisioning plan status
     * @throws IllegalArgumentException when validation reports “Provisioning plan status is required”
     */
    public static ProvisioningPlanStatus parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Provisioning plan status is required");
        }
        for (ProvisioningPlanStatus status : values()) {
            if (status.value.equalsIgnoreCase(raw.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unsupported provisioning plan status: " + raw);
    }
}
