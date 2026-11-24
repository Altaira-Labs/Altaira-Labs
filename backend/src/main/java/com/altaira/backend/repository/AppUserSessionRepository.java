package com.altaira.backend.repository;

import com.altaira.backend.entity.AppUserEntity;
import com.altaira.backend.entity.AppUserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for app user session records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface AppUserSessionRepository extends JpaRepository<AppUserSessionEntity, UUID> {
    /**
     * Finds matching records by session token hash.
     *
     * @param sessionTokenHash session token hash
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<AppUserSessionEntity> findBySessionTokenHash(String sessionTokenHash);
    /**
     * Lists matching records by user and revoked at is absent.
     *
     * @param user user account in scope
     * @return the app user session items
     */
    List<AppUserSessionEntity> findAllByUserAndRevokedAtIsNull(AppUserEntity user);
}
