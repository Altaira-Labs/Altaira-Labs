package com.altaira.backend.dto.clientcrm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for update client CRM follow up action status in the clientCRM API. */
public class UpdateClientCrmFollowUpActionStatusRequest {

    /** Current state in the workflow. The request must supply nonblank text. */
    @NotBlank(message = "Follow-up action status is required")
    @Size(max = 40, message = "Follow-up action status must be at most 40 characters")
    private String status;

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this update client CRM follow up action status.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
}
