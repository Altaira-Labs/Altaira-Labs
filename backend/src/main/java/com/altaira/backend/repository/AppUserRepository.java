package com.altaira.backend.repository;

import com.altaira.backend.entity.AppUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for app user records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface AppUserRepository extends JpaRepository<AppUserEntity, UUID> {
    /**
     * Finds matching records by username matches without case sensitivity.
     *
     * @param username account name used to identify the user
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<AppUserEntity> findByUsernameIgnoreCase(String username);
    /**
     * Checks for a matching record by username matches without case sensitivity.
     *
     * @param username account name used to identify the user
     * @return true when the check succeeds
     */
    boolean existsByUsernameIgnoreCase(String username);
}
