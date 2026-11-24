package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps security event records to the {@code security_events} table. */
@Entity
@Table(name = "security_events")
public class SecurityEventEntity {

    /** Primary identifier for this security event. */
    @Id
    @GeneratedValue
    private UUID id;

    /** User account in scope. Joined through {@code user_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUserEntity user;

    /** Account name used to identify the user. */
    @Column(length = 120)
    private String username;

    /** Event type. Stored in {@code event_type}. */
    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    /** Success. */
    @Column(nullable = false)
    private boolean success;

    /** Network address associated with the request. Stored in {@code ip_address}. */
    @Column(name = "ip_address", length = 80)
    private String ipAddress;

    /** Client software reported by the HTTP request. Stored in {@code user_agent}. */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /** Additional context recorded with the event. */
    @Column(columnDefinition = "text")
    private String metadata;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Initializes created at before the record is inserted. */
    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this security event.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return user account in scope} */
    public AppUserEntity getUser() { return user; }
    /**
     * Sets the user account in scope on this security event.
     *
     * @param user user account in scope
     */
    public void setUser(AppUserEntity user) { this.user = user; }

    /** {@return account name used to identify the user} */
    public String getUsername() { return username; }
    /**
     * Sets the account name used to identify the user on this security event.
     *
     * @param username account name used to identify the user
     */
    public void setUsername(String username) { this.username = username; }

    /** {@return event type} */
    public String getEventType() { return eventType; }
    /**
     * Sets the event type on this security event.
     *
     * @param eventType event type
     */
    public void setEventType(String eventType) { this.eventType = eventType; }

    /** {@return success} */
    public boolean isSuccess() { return success; }
    /**
     * Sets the success on this security event.
     *
     * @param success success
     */
    public void setSuccess(boolean success) { this.success = success; }

    /** {@return network address associated with the request} */
    public String getIpAddress() { return ipAddress; }
    /**
     * Sets the network address associated with the request on this security event.
     *
     * @param ipAddress network address associated with the request
     */
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    /** {@return client software reported by the HTTP request} */
    public String getUserAgent() { return userAgent; }
    /**
     * Sets the client software reported by the HTTP request on this security event.
     *
     * @param userAgent client software reported by the HTTP request
     */
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    /** {@return additional context recorded with the event} */
    public String getMetadata() { return metadata; }
    /**
     * Sets the additional context recorded with the event on this security event.
     *
     * @param metadata additional context recorded with the event
     */
    public void setMetadata(String metadata) { this.metadata = metadata; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this security event.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
