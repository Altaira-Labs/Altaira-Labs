package com.altaira.backend.dto.clientinvitation;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for client invitation. */
public class ClientInvitationResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the client whose workspace is in scope. */
    private UUID clientId;
    /** Contact email address. */
    private String email;
    /** Role used to determine allowed operations. */
    private String role;
    /** Current state in the workflow. */
    private String status;
    /** Time after which this item can no longer be used. */
    private Instant expiresAt;
    /** Time associated with accepted. */
    private Instant acceptedAt;
    /** Time at which access was revoked. */
    private Instant revokedAt;
    /** Email sent. */
    private boolean emailSent;
    /** Email message. */
    private String emailMessage;
    /** URL for invitation. */
    private String invitationUrl;
    /** Time the record was created. */
    private Instant createdAt;
    /** Time of the most recent update. */
    private Instant updatedAt;

    /** Constructs {@code ClientInvitationResponse} with its default state. */
    public ClientInvitationResponse() {}

    /**
     * Constructs {@code ClientInvitationResponse} using the supplied id, client id, email and remaining fields.
     *
     * @param id unique identifier of the record
     * @param clientId identifier of the client whose workspace is in scope
     * @param email contact email address
     * @param role role used to determine allowed operations
     * @param status current state in the workflow
     * @param expiresAt time after which this item can no longer be used
     * @param acceptedAt time associated with accepted
     * @param revokedAt time at which access was revoked
     * @param emailSent email sent
     * @param emailMessage email message
     * @param invitationUrl URL for invitation
     * @param createdAt time the record was created
     * @param updatedAt time of the most recent update
     */
    public ClientInvitationResponse(
            UUID id,
            UUID clientId,
            String email,
            String role,
            String status,
            Instant expiresAt,
            Instant acceptedAt,
            Instant revokedAt,
            boolean emailSent,
            String emailMessage,
            String invitationUrl,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.clientId = clientId;
        this.email = email;
        this.role = role;
        this.status = status;
        this.expiresAt = expiresAt;
        this.acceptedAt = acceptedAt;
        this.revokedAt = revokedAt;
        this.emailSent = emailSent;
        this.emailMessage = emailMessage;
        this.invitationUrl = invitationUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client invitation.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the client whose workspace is in scope} */
    public UUID getClientId() { return clientId; }
    /**
     * Sets the identifier of the client whose workspace is in scope on this client invitation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     */
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    /** {@return contact email address} */
    public String getEmail() { return email; }
    /**
     * Sets the contact email address on this client invitation.
     *
     * @param email contact email address
     */
    public void setEmail(String email) { this.email = email; }

    /** {@return role used to determine allowed operations} */
    public String getRole() { return role; }
    /**
     * Sets the role used to determine allowed operations on this client invitation.
     *
     * @param role role used to determine allowed operations
     */
    public void setRole(String role) { this.role = role; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this client invitation.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return time after which this item can no longer be used} */
    public Instant getExpiresAt() { return expiresAt; }
    /**
     * Sets the time after which this item can no longer be used on this client invitation.
     *
     * @param expiresAt time after which this item can no longer be used
     */
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    /** {@return time associated with accepted} */
    public Instant getAcceptedAt() { return acceptedAt; }
    /**
     * Sets the time associated with accepted on this client invitation.
     *
     * @param acceptedAt time associated with accepted
     */
    public void setAcceptedAt(Instant acceptedAt) { this.acceptedAt = acceptedAt; }

    /** {@return time at which access was revoked} */
    public Instant getRevokedAt() { return revokedAt; }
    /**
     * Sets the time at which access was revoked on this client invitation.
     *
     * @param revokedAt time at which access was revoked
     */
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }

    /** {@return email sent} */
    public boolean isEmailSent() { return emailSent; }
    /**
     * Sets the email sent on this client invitation.
     *
     * @param emailSent email sent
     */
    public void setEmailSent(boolean emailSent) { this.emailSent = emailSent; }

    /** {@return email message} */
    public String getEmailMessage() { return emailMessage; }
    /**
     * Sets the email message on this client invitation.
     *
     * @param emailMessage email message
     */
    public void setEmailMessage(String emailMessage) { this.emailMessage = emailMessage; }

    /** {@return URL for invitation} */
    public String getInvitationUrl() { return invitationUrl; }
    /**
     * Sets the URL for invitation on this client invitation.
     *
     * @param invitationUrl URL for invitation
     */
    public void setInvitationUrl(String invitationUrl) { this.invitationUrl = invitationUrl; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this client invitation.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this client invitation.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
