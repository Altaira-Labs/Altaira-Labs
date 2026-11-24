package com.altaira.backend.dto.clientinvitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for accept client invitation in the clientinvitation API. */
public class AcceptClientInvitationRequest {
    /** Credential supplied for this operation. The request must supply nonblank text. */
    @NotBlank(message = "Invitation token is required")
    private String token;

    /** Credential supplied for the account. The request must supply nonblank text. */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 200, message = "Password must be between 8 and 200 characters")
    private String password;

    /** Confirm password. The request must supply nonblank text. */
    @NotBlank(message = "Password confirmation is required")
    @Size(min = 8, max = 200, message = "Password confirmation must be between 8 and 200 characters")
    private String confirmPassword;

    /** {@return credential supplied for this operation} */
    public String getToken() { return token; }
    /**
     * Sets the credential supplied for this operation on this accept client invitation.
     *
     * @param token credential supplied for this operation
     */
    public void setToken(String token) { this.token = token; }

    /** {@return credential supplied for the account} */
    public String getPassword() { return password; }
    /**
     * Sets the credential supplied for the account on this accept client invitation.
     *
     * @param password credential supplied for the account
     */
    public void setPassword(String password) { this.password = password; }

    /** {@return confirm password} */
    public String getConfirmPassword() { return confirmPassword; }
    /**
     * Sets the confirm password on this accept client invitation.
     *
     * @param confirmPassword confirm password
     */
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
