package com.altaira.backend.dto.lead;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for update lead status in the lead API. */
public class UpdateLeadStatusRequest {

    /** Current state in the workflow. The request must supply nonblank text. */
    @NotBlank(message = "Lead status is required")
    @Size(max = 20, message = "Lead status must be at most 20 characters")
    private String status;

    /** {@return current state in the workflow} */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current state in the workflow on this update lead status.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
