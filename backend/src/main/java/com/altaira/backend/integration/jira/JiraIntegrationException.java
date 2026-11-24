package com.altaira.backend.integration.jira;

/** Represents a failure reported by the Jira integration exception adapter. */
public class JiraIntegrationException extends RuntimeException {

    /** Max safe message length ({@code 500}). */
    private static final int MAX_SAFE_MESSAGE_LENGTH = 500;
    /** Message suitable for display in the interface. */
    private final String safeMessage;
    /** HTTP status returned by the request. */
    private final Integer statusCode;

    /**
     * Constructs {@code JiraIntegrationException} using the supplied safe message, status code.
     *
     * @param safeMessage message suitable for display in the interface
     * @param statusCode HTTP status returned by the request
     */
    private JiraIntegrationException(String safeMessage, Integer statusCode) {
        super(safeMessage);
        this.safeMessage = safeMessage;
        this.statusCode = statusCode;
    }

    /**
     * Constructs an integration error from a message already suitable for the caller.
     *
     * @param message text associated with the operation
     * @return the Jira integration exception
     */
    public static JiraIntegrationException safe(String message) {
        return new JiraIntegrationException(sanitize(message), null);
    }

    /**
     * Wraps the provider response with its HTTP status and sanitized diagnostic text.
     *
     * @param statusCode HTTP status returned by the request
     * @param message text associated with the operation
     * @return the Jira integration exception
     */
    public static JiraIntegrationException api(int statusCode, String message) {
        return new JiraIntegrationException(
                "Jira API returned " + statusCode + ": " + sanitize(message),
                statusCode
        );
    }

    /**
     * Wraps a transport failure for callers of the integration adapter.
     *
     * @return the Jira integration exception
     */
    public static JiraIntegrationException connectionFailed() {
        return new JiraIntegrationException("Jira API connection failed.", null);
    }

    /**
     * Wraps an interrupted provider call for the integration adapter.
     *
     * @return the Jira integration exception
     */
    public static JiraIntegrationException connectionInterrupted() {
        return new JiraIntegrationException("Jira API connection was interrupted.", null);
    }

    /**
     * Returns the message suitable for display in the interface for this Jira integration exception.
     *
     * @return message suitable for display in the interface
     */
    public String safeMessage() {
        return safeMessage;
    }

    /**
     * Checks whether there is status code.
     *
     * @param expected expected outcome for the scenario
     * @return true if the value has status code
     */
    public boolean hasStatusCode(int expected) {
        return statusCode != null && statusCode == expected;
    }

    /**
     * Removes credential-like fragments and control characters from the diagnostic message.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private static String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "request failed";
        }
        String sanitized = value
                .replaceAll("[\\r\\n\\t]+", " ")
                .replaceAll("(?i)Basic\\s+[^\\s,;]+", "Basic [REDACTED]")
                .replaceAll("(?i)Bearer\\s+[^\\s,;]+", "Bearer [REDACTED]")
                .replaceAll("(?i)(api[_ -]?token|token|password)\\s*[:=]\\s*[^\\s,;]+", "$1=[REDACTED]")
                .trim();
        return sanitized.substring(0, Math.min(sanitized.length(), MAX_SAFE_MESSAGE_LENGTH));
    }
}
