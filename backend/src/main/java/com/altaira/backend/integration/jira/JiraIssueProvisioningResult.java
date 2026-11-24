package com.altaira.backend.integration.jira;

/**
 * Outcome of Jira issue provisioning, shared with the component that requested the operation.
 *
 * @param created whether a resource was created
 * @param reused whether an existing resource was used
 * @param issueId identifier of the issue
 * @param issueKey key identifying the issue
 * @param issueUrl URL for issue
 */
public record JiraIssueProvisioningResult(
        boolean created,
        boolean reused,
        String issueId,
        String issueKey,
        String issueUrl
) {
}
