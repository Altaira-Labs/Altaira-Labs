package com.altaira.backend.model;

/** Allowed provisioning step status values understood by the backend. */
public enum ProvisioningStepStatus {
    /** Represents the pending option for provisioning step status. */
    PENDING,
    /** Represents the running option for provisioning step status. */
    RUNNING,
    /** Represents the completed option for provisioning step status. */
    COMPLETED,
    /** Represents the dry run option for provisioning step status. */
    DRY_RUN,
    /** Represents the blocked option for provisioning step status. */
    BLOCKED,
    /** Represents the failed option for provisioning step status. */
    FAILED,
    /** Represents the skipped option for provisioning step status. */
    SKIPPED
}
