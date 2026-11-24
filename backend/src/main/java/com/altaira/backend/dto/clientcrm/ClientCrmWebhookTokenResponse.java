package com.altaira.backend.dto.clientcrm;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for client CRM webhook token. */
public class ClientCrmWebhookTokenResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the client whose workspace is in scope. */
    private UUID clientId;
    /** Display label. */
    private String label;
    /** Token prefix. */
    private String tokenPrefix;
    /** Whether the record is currently active. */
    private boolean active;
    /** Key identifying the api. */
    private String apiKey;
    /** URL for webhook. */
    private String webhookUrl;
    /** Time associated with last used. */
    private Instant lastUsedAt;
    /** Time at which access was revoked. */
    private Instant revokedAt;
    /** Time the record was created. */
    private Instant createdAt;
    /** Time of the most recent update. */
    private Instant updatedAt;

    /** Constructs {@code ClientCrmWebhookTokenResponse} with its default state. */
    public ClientCrmWebhookTokenResponse() {}

    /**
     * Constructs {@code ClientCrmWebhookTokenResponse} using the supplied id, client id, label and remaining fields.
     *
     * @param id unique identifier of the record
     * @param clientId identifier of the client whose workspace is in scope
     * @param label display label
     * @param tokenPrefix token prefix
     * @param active whether the record is currently active
     * @param apiKey key identifying the api
     * @param webhookUrl URL for webhook
     * @param lastUsedAt time associated with last used
     * @param revokedAt time at which access was revoked
     * @param createdAt time the record was created
     * @param updatedAt time of the most recent update
     */
    public ClientCrmWebhookTokenResponse(
            UUID id,
            UUID clientId,
            String label,
            String tokenPrefix,
            boolean active,
            String apiKey,
            String webhookUrl,
            Instant lastUsedAt,
            Instant revokedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.clientId = clientId;
        this.label = label;
        this.tokenPrefix = tokenPrefix;
        this.active = active;
        this.apiKey = apiKey;
        this.webhookUrl = webhookUrl;
        this.lastUsedAt = lastUsedAt;
        this.revokedAt = revokedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client CRM webhook token.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the client whose workspace is in scope} */
    public UUID getClientId() { return clientId; }
    /**
     * Sets the identifier of the client whose workspace is in scope on this client CRM webhook token.
     *
     * @param clientId identifier of the client whose workspace is in scope
     */
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    /** {@return display label} */
    public String getLabel() { return label; }
    /**
     * Sets the display label on this client CRM webhook token.
     *
     * @param label display label
     */
    public void setLabel(String label) { this.label = label; }

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

    /** {@return key identifying the api} */
    public String getApiKey() { return apiKey; }
    /**
     * Sets the key identifying the api on this client CRM webhook token.
     *
     * @param apiKey key identifying the api
     */
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    /** {@return URL for webhook} */
    public String getWebhookUrl() { return webhookUrl; }
    /**
     * Sets the URL for webhook on this client CRM webhook token.
     *
     * @param webhookUrl URL for webhook
     */
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }

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
