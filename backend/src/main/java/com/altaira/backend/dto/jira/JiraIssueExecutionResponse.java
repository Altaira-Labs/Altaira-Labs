package com.altaira.backend.dto.jira;

import java.util.List;
import java.util.UUID;

/**
 * Fields the API returns for Jira issue execution.
 *
 * @param planId identifier of the saved provisioning plan
 * @param runId identifier of the provisioning run
 * @param status current state in the workflow
 * @param dryRun whether the operation is being simulated
 * @param executionAllowed whether the plan allows an execution step
 * @param created whether a resource was created
 * @param reused whether an existing resource was used
 * @param failed failed
 * @param issues issues
 * @param message text associated with the operation
 */
public record JiraIssueExecutionResponse(
        UUID planId,
        UUID runId,
        String status,
        boolean dryRun,
        boolean executionAllowed,
        int created,
        int reused,
        int failed,
        List<IssueResult> issues,
        String message
) {
    /**
     * Represents issue result in the Jira issue execution response payload.
     *
     * @param stepId identifier of the individual execution step
     * @param track service track within the delivery plan
     * @param summary summary
     * @param status current state in the workflow
     * @param attempts attempts
     * @param created whether a resource was created
     * @param reused whether an existing resource was used
     * @param issueKey key identifying the issue
     * @param issueUrl URL for issue
     * @param message text associated with the operation
     */
    public record IssueResult(
            UUID stepId,
            String track,
            String summary,
            String status,
            int attempts,
            boolean created,
            boolean reused,
            String issueKey,
            String issueUrl,
            String message
    ) {
    }
}
