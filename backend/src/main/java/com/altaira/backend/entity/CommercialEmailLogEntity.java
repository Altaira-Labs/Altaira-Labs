package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Maps commercial email log records to the {@code commercial_email_logs} table. */
@Entity
@Table(name = "commercial_email_logs")
public class CommercialEmailLogEntity {
    /** Primary identifier for this commercial email log. */
    @Id @GeneratedValue private UUID id;
    /** Flow. Joined through {@code flow_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "flow_id", nullable = false) private CommercialFlowEntity flow;
    /** Email type. Stored in {@code email_type}. */
    @Column(name = "email_type", nullable = false, length = 50) private String emailType;
    /** Recipient. */
    @Column(nullable = false, length = 200) private String recipient;
    /** Current state in the workflow. */
    @Column(nullable = false, length = 30) private String status;
    /** Identifier of the provider message. Stored in {@code provider_message_id}. */
    @Column(name = "provider_message_id", length = 240) private String providerMessageId;
    /** Error detail suitable for showing to the caller. Stored in {@code safe_error}. */
    @Column(name = "safe_error", columnDefinition = "TEXT") private String safeError;
    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    /** Time associated with sent. Stored in {@code sent_at}. */
    @Column(name = "sent_at") private Instant sentAt;
    /** Initializes created at before the record is inserted. */
    @PrePersist void onCreate() { createdAt = Instant.now(); }
    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /** {@return flow} */
    public CommercialFlowEntity getFlow() { return flow; }
    /**
     * Sets the flow on this commercial email log.
     *
     * @param flow flow
     */
    public void setFlow(CommercialFlowEntity flow) { this.flow = flow; }
    /** {@return email type} */
    public String getEmailType() { return emailType; }
    /**
     * Sets the email type on this commercial email log.
     *
     * @param emailType email type
     */
    public void setEmailType(String emailType) { this.emailType = emailType; }
    /** {@return recipient} */
    public String getRecipient() { return recipient; }
    /**
     * Sets the recipient on this commercial email log.
     *
     * @param recipient recipient
     */
    public void setRecipient(String recipient) { this.recipient = recipient; }
    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this commercial email log.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
    /** {@return identifier of the provider message} */
    public String getProviderMessageId() { return providerMessageId; }
    /**
     * Sets the identifier of the provider message on this commercial email log.
     *
     * @param providerMessageId identifier of the provider message
     */
    public void setProviderMessageId(String providerMessageId) { this.providerMessageId = providerMessageId; }
    /** {@return error detail suitable for showing to the caller} */
    public String getSafeError() { return safeError; }
    /**
     * Sets the error detail suitable for showing to the caller on this commercial email log.
     *
     * @param safeError error detail suitable for showing to the caller
     */
    public void setSafeError(String safeError) { this.safeError = safeError; }
    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /** {@return time associated with sent} */
    public Instant getSentAt() { return sentAt; }
    /**
     * Sets the time associated with sent on this commercial email log.
     *
     * @param sentAt time associated with sent
     */
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
}
