package com.altaira.backend.dto.workspacetask;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for update workspace task status in the workspacetask API. */
public class UpdateWorkspaceTaskStatusRequest {

    /** Current state in the workflow. The request must supply nonblank text. */
    @NotBlank(message = "Task status is required")
    @Size(max = 40)
    private String status;

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this update workspace task status.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
}
