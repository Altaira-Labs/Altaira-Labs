package com.altaira.backend.repository;

import com.altaira.backend.entity.CommercialFlowEntity;
import com.altaira.backend.entity.CommercialPaymentSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for commercial payment session records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface CommercialPaymentSessionRepository extends JpaRepository<CommercialPaymentSessionEntity, UUID> {
    /**
     * Finds matching records by idempotency key.
     *
     * @param idempotencyKey key used to recognize a repeated operation
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<CommercialPaymentSessionEntity> findByIdempotencyKey(String idempotencyKey);
    /**
     * Finds matching records by provider session id.
     *
     * @param providerSessionId identifier of the provider session
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<CommercialPaymentSessionEntity> findByProviderSessionId(String providerSessionId);
    /**
     * Lists matching records by flow; orders the result by created at descending.
     *
     * @param flow flow
     * @return the commercial payment session items in query order
     */
    List<CommercialPaymentSessionEntity> findAllByFlowOrderByCreatedAtDesc(CommercialFlowEntity flow);
}
