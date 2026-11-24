package com.altaira.backend.dto.auth;

import java.time.Instant;

/** Fields the API returns for login. */
public class LoginResponse {
    /** Credential used to identify the current session. */
    private String sessionToken;
    /** Time after which this item can no longer be used. */
    private Instant expiresAt;
    /** User account in scope. */
    private AuthUserResponse user;

    /**
     * Constructs {@code LoginResponse} using the supplied session token, expires at, user.
     *
     * @param sessionToken credential used to identify the current session
     * @param expiresAt time after which this item can no longer be used
     * @param user user account in scope
     */
    public LoginResponse(String sessionToken, Instant expiresAt, AuthUserResponse user) {
        this.sessionToken = sessionToken;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    /** {@return credential used to identify the current session} */
    public String getSessionToken() { return sessionToken; }
    /**
     * Sets the credential used to identify the current session on this login.
     *
     * @param sessionToken credential used to identify the current session
     */
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }

    /** {@return time after which this item can no longer be used} */
    public Instant getExpiresAt() { return expiresAt; }
    /**
     * Sets the time after which this item can no longer be used on this login.
     *
     * @param expiresAt time after which this item can no longer be used
     */
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    /** {@return user account in scope} */
    public AuthUserResponse getUser() { return user; }
    /**
     * Sets the user account in scope on this login.
     *
     * @param user user account in scope
     */
    public void setUser(AuthUserResponse user) { this.user = user; }
}
