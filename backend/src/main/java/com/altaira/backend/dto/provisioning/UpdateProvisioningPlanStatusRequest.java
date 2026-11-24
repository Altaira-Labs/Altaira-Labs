package com.altaira.backend.dto.provisioning;

import jakarta.validation.constraints.NotBlank;

/** Input for update provisioning plan status in the provisioning API. */
public class UpdateProvisioningPlanStatusRequest {

    /** Current state in the workflow. The request must supply nonblank text. */
    @NotBlank
    private String status;

    /** {@return current state in the workflow} */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current state in the workflow on this update provisioning plan status.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
