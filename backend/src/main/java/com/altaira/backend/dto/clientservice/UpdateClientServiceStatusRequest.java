package com.altaira.backend.dto.clientservice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for update client service status in the clientservice API. */
public class UpdateClientServiceStatusRequest {

    /** Current state in the workflow. The request must supply nonblank text. */
    @NotBlank(message = "Client service status is required")
    @Size(max = 20, message = "Client service status must be at most 20 characters")
    private String status;

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this update client service status.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
}
