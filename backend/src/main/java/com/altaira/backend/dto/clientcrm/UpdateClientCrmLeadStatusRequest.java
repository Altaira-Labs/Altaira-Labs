package com.altaira.backend.dto.clientcrm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for update client CRM lead status in the clientCRM API. */
public class UpdateClientCrmLeadStatusRequest {

    /** Current state in the workflow. The request must supply nonblank text. */
    @NotBlank(message = "Status is required")
    @Size(max = 40, message = "Status must be at most 40 characters")
    private String status;

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this update client CRM lead status.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
}
