package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps commercial payment session records to the {@code commercial_payment_sessions} table. */
@Entity
@Table(name = "commercial_payment_sessions")
public class CommercialPaymentSessionEntity {
    /** Primary identifier for this commercial payment session. */
    @Id @GeneratedValue private UUID id;
    /** Flow. Joined through {@code flow_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "flow_id", nullable = false) private CommercialFlowEntity flow;
    /** Integration adapter in use. */
    @Column(nullable = false, length = 30) private String provider = "STRIPE";
    /** Provider mode. Stored in {@code provider_mode}. */
    @Column(name = "provider_mode", nullable = false, length = 20) private String providerMode;
    /** Current state in the workflow. */
    @Column(nullable = false, length = 40) private String status;
    /** Checkout amount in the currency’s smallest unit. Stored in {@code amount_minor}. */
    @Column(name = "amount_minor", nullable = false) private long amountMinor;
    /** Currency code for the payment. */
    @Column(nullable = false, length = 3) private String currency;
    /** Customer email. Stored in {@code customer_email}. */
    @Column(name = "customer_email", nullable = false, length = 200) private String customerEmail;
    /** Human-readable description. */
    @Column(nullable = false, length = 500) private String description;
    /** Identifier of the provider session. Stored in {@code provider_session_id}. */
    @Column(name = "provider_session_id", unique = true, length = 240) private String providerSessionId;
    /** URL for checkout. Stored in {@code checkout_url}. */
    @Column(name = "checkout_url", columnDefinition = "TEXT") private String checkoutUrl;
    /** Identifier of the payment intent. Stored in {@code payment_intent_id}. */
    @Column(name = "payment_intent_id", length = 240) private String paymentIntentId;
    /** Key used to recognize a repeated operation. Stored in {@code idempotency_key}. */
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 200) private String idempotencyKey;
    /** Error detail suitable for showing to the caller. Stored in {@code safe_error}. */
    @Column(name = "safe_error", columnDefinition = "TEXT") private String safeError;
    /** Time after which this item can no longer be used. Stored in {@code expires_at}. */
    @Column(name = "expires_at") private Instant expiresAt;
    /** Time confirmation was recorded. Stored in {@code confirmed_at}. */
    @Column(name = "confirmed_at") private Instant confirmedAt;
    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    /** Time of the most recent update. Stored in {@code updated_at}. */
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    /** Initializes now, created at, updated at before the record is inserted. */
    @PrePersist void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
    /** Refreshes the update timestamp before the persisted record changes. */
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /** {@return flow} */
    public CommercialFlowEntity getFlow() { return flow; }
    /**
     * Sets the flow on this commercial payment session.
     *
     * @param flow flow
     */
    public void setFlow(CommercialFlowEntity flow) { this.flow = flow; }
    /** {@return integration adapter in use} */
    public String getProvider() { return provider; }
    /**
     * Sets the integration adapter in use on this commercial payment session.
     *
     * @param provider integration adapter in use
     */
    public void setProvider(String provider) { this.provider = provider; }
    /** {@return provider mode} */
    public String getProviderMode() { return providerMode; }
    /**
     * Sets the provider mode on this commercial payment session.
     *
     * @param providerMode provider mode
     */
    public void setProviderMode(String providerMode) { this.providerMode = providerMode; }
    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this commercial payment session.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
    /** {@return checkout amount in the currency’s smallest unit} */
    public long getAmountMinor() { return amountMinor; }
    /**
     * Sets the checkout amount in the currency’s smallest unit on this commercial payment session.
     *
     * @param amountMinor checkout amount in the currency’s smallest unit
     */
    public void setAmountMinor(long amountMinor) { this.amountMinor = amountMinor; }
    /** {@return currency code for the payment} */
    public String getCurrency() { return currency; }
    /**
     * Sets the currency code for the payment on this commercial payment session.
     *
     * @param currency currency code for the payment
     */
    public void setCurrency(String currency) { this.currency = currency; }
    /** {@return customer email} */
    public String getCustomerEmail() { return customerEmail; }
    /**
     * Sets the customer email on this commercial payment session.
     *
     * @param customerEmail customer email
     */
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    /** {@return human-readable description} */
    public String getDescription() { return description; }
    /**
     * Sets the human-readable description on this commercial payment session.
     *
     * @param description human-readable description
     */
    public void setDescription(String description) { this.description = description; }
    /** {@return identifier of the provider session} */
    public String getProviderSessionId() { return providerSessionId; }
    /**
     * Sets the identifier of the provider session on this commercial payment session.
     *
     * @param providerSessionId identifier of the provider session
     */
    public void setProviderSessionId(String providerSessionId) { this.providerSessionId = providerSessionId; }
    /** {@return URL for checkout} */
    public String getCheckoutUrl() { return checkoutUrl; }
    /**
     * Sets the URL for checkout on this commercial payment session.
     *
     * @param checkoutUrl URL for checkout
     */
    public void setCheckoutUrl(String checkoutUrl) { this.checkoutUrl = checkoutUrl; }
    /** {@return identifier of the payment intent} */
    public String getPaymentIntentId() { return paymentIntentId; }
    /**
     * Sets the identifier of the payment intent on this commercial payment session.
     *
     * @param paymentIntentId identifier of the payment intent
     */
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }
    /** {@return key used to recognize a repeated operation} */
    public String getIdempotencyKey() { return idempotencyKey; }
    /**
     * Sets the key used to recognize a repeated operation on this commercial payment session.
     *
     * @param idempotencyKey key used to recognize a repeated operation
     */
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    /** {@return error detail suitable for showing to the caller} */
    public String getSafeError() { return safeError; }
    /**
     * Sets the error detail suitable for showing to the caller on this commercial payment session.
     *
     * @param safeError error detail suitable for showing to the caller
     */
    public void setSafeError(String safeError) { this.safeError = safeError; }
    /** {@return time after which this item can no longer be used} */
    public Instant getExpiresAt() { return expiresAt; }
    /**
     * Sets the time after which this item can no longer be used on this commercial payment session.
     *
     * @param expiresAt time after which this item can no longer be used
     */
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    /** {@return time confirmation was recorded} */
    public Instant getConfirmedAt() { return confirmedAt; }
    /**
     * Sets the time confirmation was recorded on this commercial payment session.
     *
     * @param confirmedAt time confirmation was recorded
     */
    public void setConfirmedAt(Instant confirmedAt) { this.confirmedAt = confirmedAt; }
    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
}
