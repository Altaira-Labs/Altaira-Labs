package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Persists the account, token hash, expiry and revocation state needed to validate a login session. */
@Entity
@Table(name = "app_user_sessions")
public class AppUserSessionEntity {

    /** Primary identifier for this app user session. */
    @Id
    @GeneratedValue
    private UUID id;

    /** User account in scope. Joined through {@code user_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUserEntity user;

    /** Session token hash. Stored in {@code session_token_hash}. */
    @Column(name = "session_token_hash", nullable = false, unique = true, length = 64)
    private String sessionTokenHash;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Time after which this item can no longer be used. Stored in {@code expires_at}. */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /** Time at which access was revoked. Stored in {@code revoked_at}. */
    @Column(name = "revoked_at")
    private Instant revokedAt;

    /** Initializes created at before the record is inserted. */
    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    /**
     * Checks whether this is usable.
     *
     * @param now now
     * @return true if the value is usable
     */
    public boolean isUsable(Instant now) {
        return revokedAt == null && expiresAt.isAfter(now);
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this app user session.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return user account in scope} */
    public AppUserEntity getUser() { return user; }
    /**
     * Sets the user account in scope on this app user session.
     *
     * @param user user account in scope
     */
    public void setUser(AppUserEntity user) { this.user = user; }

    /** {@return session token hash} */
    public String getSessionTokenHash() { return sessionTokenHash; }
    /**
     * Sets the session token hash on this app user session.
     *
     * @param sessionTokenHash session token hash
     */
    public void setSessionTokenHash(String sessionTokenHash) { this.sessionTokenHash = sessionTokenHash; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this app user session.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** {@return time after which this item can no longer be used} */
    public Instant getExpiresAt() { return expiresAt; }
    /**
     * Sets the time after which this item can no longer be used on this app user session.
     *
     * @param expiresAt time after which this item can no longer be used
     */
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    /** {@return time at which access was revoked} */
    public Instant getRevokedAt() { return revokedAt; }
    /**
     * Sets the time at which access was revoked on this app user session.
     *
     * @param revokedAt time at which access was revoked
     */
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }
}
