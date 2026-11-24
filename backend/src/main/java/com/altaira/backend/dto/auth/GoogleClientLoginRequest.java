package com.altaira.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for Google client login in the auth API. */
public class GoogleClientLoginRequest {

    /** Credential. The request must supply nonblank text. */
    @NotBlank(message = "Google credential is required")
    @Size(max = 5000, message = "Google credential must be at most 5000 characters")
    private String credential;

    /** {@return credential} */
    public String getCredential() { return credential; }
    /**
     * Sets the credential on this Google client login.
     *
     * @param credential credential
     */
    public void setCredential(String credential) { this.credential = credential; }
}
