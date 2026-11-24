package com.altaira.backend.repository;

import com.altaira.backend.entity.CommercialEmailLogEntity;
import com.altaira.backend.entity.CommercialFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data queries for commercial email log records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface CommercialEmailLogRepository extends JpaRepository<CommercialEmailLogEntity, UUID> {
    /**
     * Lists matching records by flow; orders the result by created at descending.
     *
     * @param flow flow
     * @return the commercial email log items in query order
     */
    List<CommercialEmailLogEntity> findAllByFlowOrderByCreatedAtDesc(CommercialFlowEntity flow);
    /**
     * Checks for a matching record by flow and email type and status.
     *
     * @param flow flow
     * @param emailType email type
     * @param status current state in the workflow
     * @return true when the check succeeds
     */
    boolean existsByFlowAndEmailTypeAndStatus(CommercialFlowEntity flow, String emailType, String status);
}
