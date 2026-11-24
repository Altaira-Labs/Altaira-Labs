package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps client invitation records to the {@code client_invitations} table. */
@Entity
@Table(name = "client_invitations")
public class ClientInvitationEntity {

    /** Primary identifier for this client invitation. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    /** Contact email address. */
    @Column(nullable = false, length = 160)
    private String email;

    /** Role used to determine allowed operations. */
    @Column(nullable = false, length = 40)
    private String role = "client_user";

    /** Stored digest of the token. Stored in {@code token_hash}. */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    /** Time after which this item can no longer be used. Stored in {@code expires_at}. */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /** Time associated with accepted. Stored in {@code accepted_at}. */
    @Column(name = "accepted_at")
    private Instant acceptedAt;

    /** Time at which access was revoked. Stored in {@code revoked_at}. */
    @Column(name = "revoked_at")
    private Instant revokedAt;

    /** Email sent. Stored in {@code email_sent}. */
    @Column(name = "email_sent", nullable = false)
    private boolean emailSent = false;

    /** Email message. Stored in {@code email_message}. */
    @Column(name = "email_message", length = 300)
    private String emailMessage;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Time of the most recent update. Stored in {@code updated_at}. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

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
     * Sets the unique identifier of the record on this client invitation.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this client invitation.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

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

    /** {@return stored digest of the token} */
    public String getTokenHash() { return tokenHash; }
    /**
     * Sets the stored digest of the token on this client invitation.
     *
     * @param tokenHash stored digest of the token
     */
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

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
