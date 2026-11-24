package com.altaira.backend.dto.clientinvitation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/** Input for create client invitation in the clientinvitation API. */
public class CreateClientInvitationRequest {
    /** Contact email address. */
    @Email(message = "Invitation email must be valid")
    @Size(max = 160, message = "Invitation email must be at most 160 characters")
    private String email;

    /** Role used to determine allowed operations. */
    @Size(max = 40, message = "Invitation role must be at most 40 characters")
    private String role = "client_user";

    /** {@return contact email address} */
    public String getEmail() { return email; }
    /**
     * Sets the contact email address on this create client invitation.
     *
     * @param email contact email address
     */
    public void setEmail(String email) { this.email = email; }

    /** {@return role used to determine allowed operations} */
    public String getRole() { return role; }
    /**
     * Sets the role used to determine allowed operations on this create client invitation.
     *
     * @param role role used to determine allowed operations
     */
    public void setRole(String role) { this.role = role; }
}
