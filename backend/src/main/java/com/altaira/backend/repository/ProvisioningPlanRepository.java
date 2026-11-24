package com.altaira.backend.repository;

import com.altaira.backend.entity.LeadAssessmentEntity;
import com.altaira.backend.entity.LeadEntity;
import com.altaira.backend.entity.ProvisioningPlanEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for provisioning plan records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ProvisioningPlanRepository extends JpaRepository<ProvisioningPlanEntity, UUID> {
    /**
     * Finds matching records by assessment.
     *
     * @param assessment assessment
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<ProvisioningPlanEntity> findByAssessment(LeadAssessmentEntity assessment);
    /**
     * Lists matching records by lead; orders the result by updated at descending.
     *
     * @param lead sales enquiry in scope
     * @return the provisioning plan items in query order
     */
    List<ProvisioningPlanEntity> findAllByLeadOrderByUpdatedAtDesc(LeadEntity lead);

    /**
     * Finds matching records by id for update. A pessimistic lock reserves the selected rows for this transaction.
     *
     * @param id unique identifier of the record
     * @return the matching value, or an empty optional when it is absent
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select plan from ProvisioningPlanEntity plan where plan.id = :id")
    Optional<ProvisioningPlanEntity> findByIdForUpdate(@Param("id") UUID id);
}
