package com.altaira.backend.dto.onboarding;

import jakarta.validation.constraints.Size;

/** Input for review onboarding task in the onboarding API. */
public class ReviewOnboardingTaskRequest {

    /** Feedback. */
    @Size(max = 1000, message = "Feedback must be at most 1000 characters")
    private String feedback;

    /** {@return feedback} */
    public String getFeedback() { return feedback; }
    /**
     * Sets the feedback on this review onboarding task.
     *
     * @param feedback feedback
     */
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
