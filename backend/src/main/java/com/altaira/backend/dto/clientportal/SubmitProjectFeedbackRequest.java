package com.altaira.backend.dto.clientportal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for submit project feedback in the clientportal API. */
public class SubmitProjectFeedbackRequest {

    /** Feedback. The request must supply nonblank text. */
    @NotBlank(message = "Feedback is required")
    @Size(max = 2000, message = "Feedback must be at most 2000 characters")
    private String feedback;

    /** {@return feedback} */
    public String getFeedback() { return feedback; }
    /**
     * Sets the feedback on this submit project feedback.
     *
     * @param feedback feedback
     */
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
