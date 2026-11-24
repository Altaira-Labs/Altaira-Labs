package com.altaira.backend.service;

/**
 * Outcome of email notification, shared with the component that requested the operation.
 *
 * @param sent sent
 * @param message text associated with the operation
 */
public record EmailNotificationResult(boolean sent, String message) {
    /**
     * Builds the successful email delivery result.
     *
     * @return the email notification result
     */
    public static EmailNotificationResult success() {
        return new EmailNotificationResult(true, "Email notification sent.");
    }

    /**
     * Builds an outcome describing an undelivered notification.
     *
     * @param message text associated with the operation
     * @return the email notification result
     */
    public static EmailNotificationResult notSent(String message) {
        return new EmailNotificationResult(false, message);
    }
}
