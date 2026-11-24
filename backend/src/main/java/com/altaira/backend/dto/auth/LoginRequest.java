package com.altaira.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for login in the auth API. */
public class LoginRequest {

    /** Account name used to identify the user. The request must supply nonblank text. */
    @NotBlank(message = "Username is required")
    @Size(max = 120, message = "Username must be at most 120 characters")
    private String username;

    /** Credential supplied for the account. The request must supply nonblank text. */
    @NotBlank(message = "Password is required")
    @Size(max = 200, message = "Password must be at most 200 characters")
    private String password;

    /** {@return account name used to identify the user} */
    public String getUsername() { return username; }
    /**
     * Sets the account name used to identify the user on this login.
     *
     * @param username account name used to identify the user
     */
    public void setUsername(String username) { this.username = username; }

    /** {@return credential supplied for the account} */
    public String getPassword() { return password; }
    /**
     * Sets the credential supplied for the account on this login.
     *
     * @param password credential supplied for the account
     */
    public void setPassword(String password) { this.password = password; }
}
