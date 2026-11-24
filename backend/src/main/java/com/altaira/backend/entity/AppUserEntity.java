package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps app user records to the {@code app_users} table. */
@Entity
@Table(name = "app_users")
public class AppUserEntity {

    /** Primary identifier for this app user. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Account name used to identify the user. */
    @Column(nullable = false, unique = true, length = 120)
    private String username;

    /** Password hash. Stored in {@code password_hash}. */
    @Column(name = "password_hash", nullable = false, length = 120)
    private String passwordHash;

    /** Role used to determine allowed operations. */
    @Column(nullable = false, length = 40)
    private String role;

    /** Whether the record is currently active. */
    @Column(nullable = false)
    private boolean active = true;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Time of the most recent update. Stored in {@code updated_at}. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Time associated with last login. Stored in {@code last_login_at}. */
    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    /** Initializes now, created at, updated at before the record is inserted. */
    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    /** Refreshes the update timestamp before the persisted record changes. */
    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this app user.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return account name used to identify the user} */
    public String getUsername() { return username; }
    /**
     * Sets the account name used to identify the user on this app user.
     *
     * @param username account name used to identify the user
     */
    public void setUsername(String username) { this.username = username; }

    /** {@return password hash} */
    public String getPasswordHash() { return passwordHash; }
    /**
     * Sets the password hash on this app user.
     *
     * @param passwordHash password hash
     */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /** {@return role used to determine allowed operations} */
    public String getRole() { return role; }
    /**
     * Sets the role used to determine allowed operations on this app user.
     *
     * @param role role used to determine allowed operations
     */
    public void setRole(String role) { this.role = role; }

    /** {@return whether the record is currently active} */
    public boolean isActive() { return active; }
    /**
     * Sets the whether the record is currently active on this app user.
     *
     * @param active whether the record is currently active
     */
    public void setActive(boolean active) { this.active = active; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this app user.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this app user.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    /** {@return time associated with last login} */
    public Instant getLastLoginAt() { return lastLoginAt; }
    /**
     * Sets the time associated with last login on this app user.
     *
     * @param lastLoginAt time associated with last login
     */
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
