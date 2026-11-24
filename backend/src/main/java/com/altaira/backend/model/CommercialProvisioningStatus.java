package com.altaira.backend.model;

/** Allowed commercial provisioning status values understood by the backend. */
public enum CommercialProvisioningStatus {
    /** Represents the provisioning pending option for commercial provisioning status. */
    PROVISIONING_PENDING,
    /** Represents the provisioning running option for commercial provisioning status. */
    PROVISIONING_RUNNING,
    /** Represents the provisioning completed option for commercial provisioning status. */
    PROVISIONING_COMPLETED,
    /** Represents the provisioning blocked option for commercial provisioning status. */
    PROVISIONING_BLOCKED,
    /** Represents the provisioning failed option for commercial provisioning status. */
    PROVISIONING_FAILED
}
