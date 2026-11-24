package com.altaira.backend.dto.provisioning;

import java.util.UUID;

/** Input for create provisioning dry run in the provisioning API. */
public class CreateProvisioningDryRunRequest {
    /** Identifier of the assessment attached to the lead. */
    private UUID assessmentId;

    /** {@return identifier of the assessment attached to the lead} */
    public UUID getAssessmentId() {
        return assessmentId;
    }

    /**
     * Sets the identifier of the assessment attached to the lead on this create provisioning dry run.
     *
     * @param assessmentId identifier of the assessment attached to the lead
     */
    public void setAssessmentId(UUID assessmentId) {
        this.assessmentId = assessmentId;
    }
}
