package com.altaira.backend.integration.github;

/** Represents a failure reported by the GitHub integration exception adapter. */
public class GitHubIntegrationException extends RuntimeException {

    /** Max safe message length ({@code 500}). */
    private static final int MAX_SAFE_MESSAGE_LENGTH = 500;

    /** Message suitable for display in the interface. */
    private final String safeMessage;
    /** HTTP status returned by the request. */
    private final Integer statusCode;

    /**
     * Constructs {@code GitHubIntegrationException} using the supplied safe message, status code.
     *
     * @param safeMessage message suitable for display in the interface
     * @param statusCode HTTP status returned by the request
     */
    private GitHubIntegrationException(String safeMessage, Integer statusCode) {
        super(safeMessage);
        this.safeMessage = safeMessage;
        this.statusCode = statusCode;
    }

    /**
     * Constructs an integration error from a message already suitable for the caller.
     *
     * @param safeMessage message suitable for display in the interface
     * @return the GitHub integration exception
     */
    public static GitHubIntegrationException safe(String safeMessage) {
        return new GitHubIntegrationException(safeMessage, null);
    }

    /**
     * Builds the integration error for incomplete server settings.
     *
     * @param safeMessage message suitable for display in the interface
     * @return the GitHub integration exception
     */
    public static GitHubIntegrationException missingConfiguration(String safeMessage) {
        return new GitHubIntegrationException(safeMessage, null);
    }

    /**
     * Wraps a GitHub API response with its status and sanitized message.
     *
     * @param statusCode HTTP status returned by the request
     * @param message text associated with the operation
     * @return the GitHub integration exception
     */
    public static GitHubIntegrationException githubApi(int statusCode, String message) {
        return new GitHubIntegrationException("GitHub API returned " + statusCode + ": " + sanitize(message), statusCode);
    }

    /**
     * Wraps a transport failure for callers of the integration adapter.
     *
     * @return the GitHub integration exception
     */
    public static GitHubIntegrationException connectionFailed() {
        return new GitHubIntegrationException("GitHub API connection failed.", null);
    }

    /**
     * Wraps an interrupted provider call for the integration adapter.
     *
     * @return the GitHub integration exception
     */
    public static GitHubIntegrationException connectionInterrupted() {
        return new GitHubIntegrationException("GitHub API connection was interrupted.", null);
    }

    /**
     * Returns the message suitable for display in the interface for this GitHub integration exception.
     *
     * @return message suitable for display in the interface
     */
    public String safeMessage() {
        return safeMessage;
    }

    /**
     * Checks whether there is status code.
     *
     * @param expectedStatusCode expected status code
     * @return true if the value has status code
     */
    public boolean hasStatusCode(int expectedStatusCode) {
        return statusCode != null && statusCode == expectedStatusCode;
    }

    /**
     * Removes credential-like fragments and control characters from the diagnostic message.
     *
     * @param message text associated with the operation
     * @return the resulting text
     */
    private static String sanitize(String message) {
        if (message == null || message.isBlank()) {
            return "request failed";
        }
        String sanitized = message
                .replaceAll("[\\r\\n\\t]+", " ")
                .replaceAll("(?i)Bearer\\s+[^\\s,;]+", "Bearer [REDACTED]")
                .replaceAll("(?i)(gh[pousr]_|github_pat_)[A-Za-z0-9_]+", "[REDACTED]")
                .trim();
        return sanitized.substring(0, Math.min(sanitized.length(), MAX_SAFE_MESSAGE_LENGTH));
    }
}
