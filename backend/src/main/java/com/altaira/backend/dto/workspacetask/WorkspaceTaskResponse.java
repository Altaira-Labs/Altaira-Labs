package com.altaira.backend.dto.workspacetask;

import java.time.Instant;
import java.util.UUID;

/**
 * Fields the API returns for workspace task.
 *
 * @param id unique identifier of the record
 * @param workspaceId identifier of the workspace
 * @param clientId identifier of the client whose workspace is in scope
 * @param clientName client name
 * @param clientCompany client company
 * @param clientServiceId identifier of the service assignment for this client
 * @param serviceKey stable key for the service
 * @param serviceName service name
 * @param projectId identifier of the client project
 * @param projectName project name
 * @param title short heading shown to a reader
 * @param description human-readable description
 * @param status current state in the workflow
 * @param priority relative urgency used to order work
 * @param ownerRole role responsible for the work
 * @param visibility audience allowed to see the item
 * @param createdByUsername created by username
 * @param dueAt deadline for the work
 * @param completedAt time the work was completed
 * @param createdAt time the record was created
 * @param updatedAt time of the most recent update
 */
public record WorkspaceTaskResponse(
        UUID id,
        UUID workspaceId,
        UUID clientId,
        String clientName,
        String clientCompany,
        UUID clientServiceId,
        String serviceKey,
        String serviceName,
        UUID projectId,
        String projectName,
        String title,
        String description,
        String status,
        String priority,
        String ownerRole,
        String visibility,
        String createdByUsername,
        Instant dueAt,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
