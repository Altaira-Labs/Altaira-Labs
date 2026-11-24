package com.altaira.backend.repository;

import com.altaira.backend.entity.CommercialPaymentEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for commercial payment event records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface CommercialPaymentEventRepository extends JpaRepository<CommercialPaymentEventEntity, UUID> {
    /**
     * Finds matching records by provider event id.
     *
     * @param providerEventId identifier of the provider event
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<CommercialPaymentEventEntity> findByProviderEventId(String providerEventId);
}
