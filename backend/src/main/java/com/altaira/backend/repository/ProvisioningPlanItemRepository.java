package com.altaira.backend.repository;

import com.altaira.backend.entity.ProvisioningPlanEntity;
import com.altaira.backend.entity.ProvisioningPlanItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data queries for provisioning plan item records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ProvisioningPlanItemRepository extends JpaRepository<ProvisioningPlanItemEntity, UUID> {
    /**
     * Lists matching records by plan; orders the result by sort order.
     *
     * @param plan proposed or saved delivery plan
     * @return the provisioning plan item items in query order
     */
    List<ProvisioningPlanItemEntity> findAllByPlanOrderBySortOrder(ProvisioningPlanEntity plan);
    /**
     * Deletes all by plan.
     *
     * @param plan proposed or saved delivery plan
     */
    void deleteAllByPlan(ProvisioningPlanEntity plan);
}
