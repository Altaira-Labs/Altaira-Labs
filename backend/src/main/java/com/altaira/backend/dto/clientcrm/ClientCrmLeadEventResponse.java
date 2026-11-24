package com.altaira.backend.dto.clientcrm;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for client CRM lead event. */
public class ClientCrmLeadEventResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the source sales enquiry. */
    private UUID leadId;
    /** Event type. */
    private String eventType;
    /** Actor role. */
    private String actorRole;
    /** Actor username. */
    private String actorUsername;
    /** From status. */
    private String fromStatus;
    /** To status. */
    private String toStatus;
    /** Summary. */
    private String summary;
    /** Time the record was created. */
    private Instant createdAt;

    /** Constructs {@code ClientCrmLeadEventResponse} with its default state. */
    public ClientCrmLeadEventResponse() {}

    /**
     * Constructs {@code ClientCrmLeadEventResponse} using the supplied id, lead id, event type and remaining fields.
     *
     * @param id unique identifier of the record
     * @param leadId identifier of the source sales enquiry
     * @param eventType event type
     * @param actorRole actor role
     * @param actorUsername actor username
     * @param fromStatus from status
     * @param toStatus to status
     * @param summary summary
     * @param createdAt time the record was created
     */
    public ClientCrmLeadEventResponse(
            UUID id,
            UUID leadId,
            String eventType,
            String actorRole,
            String actorUsername,
            String fromStatus,
            String toStatus,
            String summary,
            Instant createdAt
    ) {
        this.id = id;
        this.leadId = leadId;
        this.eventType = eventType;
        this.actorRole = actorRole;
        this.actorUsername = actorUsername;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.summary = summary;
        this.createdAt = createdAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client CRM lead event.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the source sales enquiry} */
    public UUID getLeadId() { return leadId; }
    /**
     * Sets the identifier of the source sales enquiry on this client CRM lead event.
     *
     * @param leadId identifier of the source sales enquiry
     */
    public void setLeadId(UUID leadId) { this.leadId = leadId; }

    /** {@return event type} */
    public String getEventType() { return eventType; }
    /**
     * Sets the event type on this client CRM lead event.
     *
     * @param eventType event type
     */
    public void setEventType(String eventType) { this.eventType = eventType; }

    /** {@return actor role} */
    public String getActorRole() { return actorRole; }
    /**
     * Sets the actor role on this client CRM lead event.
     *
     * @param actorRole actor role
     */
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }

    /** {@return actor username} */
    public String getActorUsername() { return actorUsername; }
    /**
     * Sets the actor username on this client CRM lead event.
     *
     * @param actorUsername actor username
     */
    public void setActorUsername(String actorUsername) { this.actorUsername = actorUsername; }

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
