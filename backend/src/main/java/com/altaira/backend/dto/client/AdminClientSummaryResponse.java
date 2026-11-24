package com.altaira.backend.dto.client;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Fields the API returns for admin client summary.
 *
 * @param id unique identifier of the record
 * @param name name used to identify the item
 * @param company company
 * @param email contact email address
 * @param phone contact telephone number
 * @param sourceLeadId identifier of the enquiry that became this customer
 * @param status current state in the workflow
 * @param sectorType sector used to select the intake rules
 * @param activeServices active services
 * @param projectCount number of project items
 * @param openTaskCount number of open task items
 * @param nextAction next action
 * @param nextActionStatus next action status
 * @param nextActionOwnerRole next action owner role
 * @param nextActionDueAt time associated with next action due
 * @param overallState overall state
 * @param lastActivityAt time associated with last activity
 * @param workspaceAvailable workspace available
 * @param createdAt time the record was created
 * @param updatedAt time of the most recent update
 */
public record AdminClientSummaryResponse(
        UUID id,
        String name,
        String company,
        String email,
        String phone,
        UUID sourceLeadId,
        String status,
        String sectorType,
        List<String> activeServices,
        int projectCount,
        int openTaskCount,
        String nextAction,
        String nextActionStatus,
        String nextActionOwnerRole,
        Instant nextActionDueAt,
        String overallState,
        Instant lastActivityAt,
        boolean workspaceAvailable,
        Instant createdAt,
        Instant updatedAt
) {
}
