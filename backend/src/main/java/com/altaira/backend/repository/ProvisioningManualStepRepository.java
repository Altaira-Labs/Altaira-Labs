package com.altaira.backend.repository;

import com.altaira.backend.entity.ProvisioningManualStepEntity;
import com.altaira.backend.entity.ProvisioningPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data queries for provisioning manual step records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ProvisioningManualStepRepository extends JpaRepository<ProvisioningManualStepEntity, UUID> {
    /**
     * Lists matching records by plan; orders the result by sort order.
     *
     * @param plan proposed or saved delivery plan
     * @return the provisioning manual step items in query order
     */
    List<ProvisioningManualStepEntity> findAllByPlanOrderBySortOrder(ProvisioningPlanEntity plan);
    /**
     * Deletes all by plan.
     *
     * @param plan proposed or saved delivery plan
     */
    void deleteAllByPlan(ProvisioningPlanEntity plan);
}
