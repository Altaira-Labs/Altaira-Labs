package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.ClientServiceEntity;
import com.altaira.backend.entity.ServiceEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for client service records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ClientServiceRepository extends JpaRepository<ClientServiceEntity, UUID> {
    /**
     * Lists matching records by ; orders the result by updated at descending.
     *
     * @return the client service items in query order
     */
    @EntityGraph(attributePaths = {"client", "service"})
    List<ClientServiceEntity> findAllByOrderByUpdatedAtDesc();

    /**
     * Lists matching records by client; orders the result by created at descending.
     *
     * @param client customer account in scope
     * @return the client service items in query order
     */
    @EntityGraph(attributePaths = "service")
    List<ClientServiceEntity> findAllByClientOrderByCreatedAtDesc(ClientEntity client);

    /**
     * Finds matching records by client and service.
     *
     * @param client customer account in scope
     * @param service service in scope
     * @return the matching value, or an empty optional when it is absent
     */
    @EntityGraph(attributePaths = "service")
    Optional<ClientServiceEntity> findByClientAndService(ClientEntity client, ServiceEntity service);
}
