package com.altaira.backend.dto.jira;

import jakarta.validation.constraints.NotBlank;

/**
 * Explicit confirmation from staff requesting Jira issue creation for the approved delivery plan.
 *
 * @param confirmation confirmation text checked by the execution service before issuing work
 */
public record ExecuteJiraIssuesRequest(@NotBlank String confirmation) {
}
