package com.altaira.backend.dto.clientportal;

import jakarta.validation.constraints.Size;

/** Input for review project asset in the clientportal API. */
public class ReviewProjectAssetRequest {

    /** Feedback. */
    @Size(max = 1000, message = "Feedback must be at most 1000 characters")
    private String feedback;

    /** {@return feedback} */
    public String getFeedback() { return feedback; }
    /**
     * Sets the feedback on this review project asset.
     *
     * @param feedback feedback
     */
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
