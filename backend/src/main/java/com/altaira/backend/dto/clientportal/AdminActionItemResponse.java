package com.altaira.backend.dto.clientportal;

import java.time.Instant;
import java.util.UUID;

/**
 * Fields the API returns for admin action item.
 *
 * @param sourceId identifier of the source
 * @param actionType kind of action recorded
 * @param title short heading shown to a reader
 * @param clientId identifier of the client whose workspace is in scope
 * @param clientName client name
 * @param clientCompany client company
 * @param projectId identifier of the client project
 * @param projectName project name
 * @param serviceKey stable key for the service
 * @param serviceName service name
 * @param critical critical
 * @param actionAt time associated with action
 */
public record AdminActionItemResponse(
        UUID sourceId,
        String actionType,
        String title,
        UUID clientId,
        String clientName,
        String clientCompany,
        UUID projectId,
        String projectName,
        String serviceKey,
        String serviceName,
        boolean critical,
        Instant actionAt
) {
}
