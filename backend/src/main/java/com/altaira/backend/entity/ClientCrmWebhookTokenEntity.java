package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Maps client CRM webhook token records to the {@code client_crm_webhook_tokens} table. */
@Entity
@Table(name = "client_crm_webhook_tokens")
public class ClientCrmWebhookTokenEntity {

    /** Primary identifier for this client CRM webhook token. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    /** Display label. */
    @Column(nullable = false)
    private String label = "Website form";

    /** Stored digest of the token. Stored in {@code token_hash}. */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    /** Token prefix. Stored in {@code token_prefix}. */
    @Column(name = "token_prefix", nullable = false, length = 24)
    private String tokenPrefix;

    /** Whether the record is currently active. */
    @Column(nullable = false)
    private boolean active = true;

    /** Time associated with last used. Stored in {@code last_used_at}. */
    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    /** Time at which access was revoked. Stored in {@code revoked_at}. */
    @Column(name = "revoked_at")
    private Instant revokedAt;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Time of the most recent update. Stored in {@code updated_at}. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Constructs {@code ClientCrmWebhookTokenEntity} with its default state. */
    public ClientCrmWebhookTokenEntity() {}

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
     * Sets the unique identifier of the record on this client CRM webhook token.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this client CRM webhook token.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

    /** {@return display label} */
    public String getLabel() { return label; }
    /**
     * Sets the display label on this client CRM webhook token.
     *
     * @param label display label
     */
    public void setLabel(String label) { this.label = label; }

    /** {@return stored digest of the token} */
    public String getTokenHash() { return tokenHash; }
    /**
     * Sets the stored digest of the token on this client CRM webhook token.
     *
     * @param tokenHash stored digest of the token
     */
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    /** {@return token prefix} */
    public String getTokenPrefix() { return tokenPrefix; }
    /**
     * Sets the token prefix on this client CRM webhook token.
     *
     * @param tokenPrefix token prefix
     */
    public void setTokenPrefix(String tokenPrefix) { this.tokenPrefix = tokenPrefix; }

    /** {@return whether the record is currently active} */
    public boolean isActive() { return active; }
    /**
     * Sets the whether the record is currently active on this client CRM webhook token.
     *
     * @param active whether the record is currently active
     */
    public void setActive(boolean active) { this.active = active; }

    /** {@return time associated with last used} */
    public Instant getLastUsedAt() { return lastUsedAt; }
    /**
     * Sets the time associated with last used on this client CRM webhook token.
     *
     * @param lastUsedAt time associated with last used
     */
    public void setLastUsedAt(Instant lastUsedAt) { this.lastUsedAt = lastUsedAt; }

    /** {@return time at which access was revoked} */
    public Instant getRevokedAt() { return revokedAt; }
    /**
     * Sets the time at which access was revoked on this client CRM webhook token.
     *
     * @param revokedAt time at which access was revoked
     */
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this client CRM webhook token.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this client CRM webhook token.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
