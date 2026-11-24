package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Maps client CRM lead event records to the {@code client_crm_lead_events} table. */
@Entity
@Table(name = "client_crm_lead_events")
public class ClientCrmLeadEventEntity {

    /** Primary identifier for this client CRM lead event. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Sales enquiry in scope. Joined through {@code lead_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id", nullable = false)
    private ClientCrmLeadEntity lead;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    /** Actor user. Joined through {@code actor_user_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id")
    private AppUserEntity actorUser;

    /** Actor role. Stored in {@code actor_role}. */
    @Column(name = "actor_role", nullable = false)
    private String actorRole;

    /** Event type. Stored in {@code event_type}. */
    @Column(name = "event_type", nullable = false)
    private String eventType;

    /** From status. Stored in {@code from_status}. */
    @Column(name = "from_status")
    private String fromStatus;

    /** To status. Stored in {@code to_status}. */
    @Column(name = "to_status")
    private String toStatus;

    /** Summary. */
    @Column(columnDefinition = "text")
    private String summary;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Constructs {@code ClientCrmLeadEventEntity} with its default state. */
    public ClientCrmLeadEventEntity() {}

    /** Initializes created at before the record is inserted. */
    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client CRM lead event.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return sales enquiry in scope} */
    public ClientCrmLeadEntity getLead() { return lead; }
    /**
     * Sets the sales enquiry in scope on this client CRM lead event.
     *
     * @param lead sales enquiry in scope
     */
    public void setLead(ClientCrmLeadEntity lead) { this.lead = lead; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this client CRM lead event.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

    /** {@return actor user} */
    public AppUserEntity getActorUser() { return actorUser; }
    /**
     * Sets the actor user on this client CRM lead event.
     *
     * @param actorUser actor user
     */
    public void setActorUser(AppUserEntity actorUser) { this.actorUser = actorUser; }

    /** {@return actor role} */
    public String getActorRole() { return actorRole; }
    /**
     * Sets the actor role on this client CRM lead event.
     *
     * @param actorRole actor role
     */
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }

    /** {@return event type} */
    public String getEventType() { return eventType; }
    /**
     * Sets the event type on this client CRM lead event.
     *
     * @param eventType event type
     */
    public void setEventType(String eventType) { this.eventType = eventType; }

    /** {@return from status} */
    public String getFromStatus() { return fromStatus; }
    /**
     * Sets the from status on this client CRM lead event.
     *
     * @param fromStatus from status
     */
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }

    /** {@return to status} */
    public String getToStatus() { return toStatus; }
    /**
     * Sets the to status on this client CRM lead event.
     *
     * @param toStatus to status
     */
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }

    /** {@return summary} */
    public String getSummary() { return summary; }
    /**
     * Sets the summary on this client CRM lead event.
     *
     * @param summary summary
     */
    public void setSummary(String summary) { this.summary = summary; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this client CRM lead event.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
