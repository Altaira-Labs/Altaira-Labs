package com.altaira.backend.repository;

import com.altaira.backend.entity.CommercialFlowEntity;
import com.altaira.backend.entity.ProvisioningPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for commercial flow records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface CommercialFlowRepository extends JpaRepository<CommercialFlowEntity, UUID> {
    /**
     * Finds matching records by plan.
     *
     * @param plan proposed or saved delivery plan
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<CommercialFlowEntity> findByPlan(ProvisioningPlanEntity plan);
}
