package com.altaira.backend.repository;

import com.altaira.backend.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for service records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {
    /**
     * Lists matching records by ; orders the result by name ascending.
     *
     * @return the service items in query order
     */
    List<ServiceEntity> findAllByOrderByNameAsc();
    /**
     * Finds matching records by name matches without case sensitivity.
     *
     * @param name name used to identify the item
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<ServiceEntity> findByNameIgnoreCase(String name);
}
