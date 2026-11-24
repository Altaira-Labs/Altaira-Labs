package com.altaira.backend.dto.auth;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for auth user. */
public class AuthUserResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Account name used to identify the user. */
    private String username;
    /** Role used to determine allowed operations. */
    private String role;
    /** Whether the record is currently active. */
    private boolean active;
    /** Time associated with last login. */
    private Instant lastLoginAt;

    /**
     * Constructs {@code AuthUserResponse} using the supplied id, username, role and remaining fields.
     *
     * @param id unique identifier of the record
     * @param username account name used to identify the user
     * @param role role used to determine allowed operations
     * @param active whether the record is currently active
     * @param lastLoginAt time associated with last login
     */
    public AuthUserResponse(UUID id, String username, String role, boolean active, Instant lastLoginAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.active = active;
        this.lastLoginAt = lastLoginAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this auth user.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return account name used to identify the user} */
    public String getUsername() { return username; }
    /**
     * Sets the account name used to identify the user on this auth user.
     *
     * @param username account name used to identify the user
     */
    public void setUsername(String username) { this.username = username; }

    /** {@return role used to determine allowed operations} */
    public String getRole() { return role; }
    /**
     * Sets the role used to determine allowed operations on this auth user.
     *
     * @param role role used to determine allowed operations
     */
    public void setRole(String role) { this.role = role; }

    /** {@return whether the record is currently active} */
    public boolean isActive() { return active; }
    /**
     * Sets the whether the record is currently active on this auth user.
     *
     * @param active whether the record is currently active
     */
    public void setActive(boolean active) { this.active = active; }

    /** {@return time associated with last login} */
    public Instant getLastLoginAt() { return lastLoginAt; }
    /**
     * Sets the time associated with last login on this auth user.
     *
     * @param lastLoginAt time associated with last login
     */
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
