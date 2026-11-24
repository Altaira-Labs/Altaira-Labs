package com.altaira.backend.dto.jira;

import jakarta.validation.constraints.NotBlank;

/**
 * Input for execute Jira issues in the Jira API.
 *
 * @param confirmation confirmation
 */
public record ExecuteJiraIssuesRequest(@NotBlank String confirmation) {
}
