package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Maps commercial payment event records to the {@code commercial_payment_events} table. */
@Entity
@Table(name = "commercial_payment_events")
public class CommercialPaymentEventEntity {
    /** Primary identifier for this commercial payment event. */
    @Id @GeneratedValue private UUID id;
    /** Checkout-session information for the plan. Joined through {@code payment_session_id}. */
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "payment_session_id") private CommercialPaymentSessionEntity paymentSession;
    /** Identifier of the provider event. Stored in {@code provider_event_id}. */
    @Column(name = "provider_event_id", nullable = false, unique = true, length = 240) private String providerEventId;
    /** Event type. Stored in {@code event_type}. */
    @Column(name = "event_type", nullable = false, length = 100) private String eventType;
    /** Current state in the workflow. */
    @Column(nullable = false, length = 30) private String status = "RECEIVED";
    /** Error detail suitable for showing to the caller. Stored in {@code safe_error}. */
    @Column(name = "safe_error", columnDefinition = "TEXT") private String safeError;
    /** Time associated with received. Stored in {@code received_at}. */
    @Column(name = "received_at", nullable = false, updatable = false) private Instant receivedAt;
    /** Time associated with processed. Stored in {@code processed_at}. */
    @Column(name = "processed_at") private Instant processedAt;
    /** Initializes received at before the record is inserted. */
    @PrePersist void onCreate() { receivedAt = Instant.now(); }
    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /** {@return checkout-session information for the plan} */
    public CommercialPaymentSessionEntity getPaymentSession() { return paymentSession; }
    /**
     * Sets the checkout-session information for the plan on this commercial payment event.
     *
     * @param paymentSession checkout-session information for the plan
     */
    public void setPaymentSession(CommercialPaymentSessionEntity paymentSession) { this.paymentSession = paymentSession; }
    /** {@return identifier of the provider event} */
    public String getProviderEventId() { return providerEventId; }
    /**
     * Sets the identifier of the provider event on this commercial payment event.
     *
     * @param providerEventId identifier of the provider event
     */
    public void setProviderEventId(String providerEventId) { this.providerEventId = providerEventId; }
    /** {@return event type} */
    public String getEventType() { return eventType; }
    /**
     * Sets the event type on this commercial payment event.
     *
     * @param eventType event type
     */
    public void setEventType(String eventType) { this.eventType = eventType; }
    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this commercial payment event.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
    /** {@return error detail suitable for showing to the caller} */
    public String getSafeError() { return safeError; }
    /**
     * Sets the error detail suitable for showing to the caller on this commercial payment event.
     *
     * @param safeError error detail suitable for showing to the caller
     */
    public void setSafeError(String safeError) { this.safeError = safeError; }
    /** {@return time associated with received} */
    public Instant getReceivedAt() { return receivedAt; }
    /** {@return time associated with processed} */
    public Instant getProcessedAt() { return processedAt; }
    /**
     * Sets the time associated with processed on this commercial payment event.
     *
     * @param processedAt time associated with processed
     */
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }
}
