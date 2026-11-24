package com.altaira.backend.dto.clientcrm;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Fields the API returns for client CRM lead. */
public class ClientCrmLeadResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the client whose workspace is in scope. */
    private UUID clientId;
    /** Name as displayed to a person. */
    private String fullName;
    /** Contact email address. */
    private String email;
    /** Contact telephone number. */
    private String phone;
    /** Original item used for the transformation. */
    private String source;
    /** Current state in the workflow. */
    private String status;
    /** Relative urgency used to order work. */
    private String priority;
    /** Sector used to select the intake rules. */
    private String sectorType;
    /** Sector fields. */
    private Map<String, String> sectorFields;
    /** Follow up actions. */
    private List<ClientCrmFollowUpActionResponse> followUpActions;
    /** Free-text notes associated with the item. */
    private List<ClientCrmLeadNoteResponse> notes;
    /** Events. */
    private List<ClientCrmLeadEventResponse> events;
    /** Time the record was created. */
    private Instant createdAt;
    /** Time of the most recent update. */
    private Instant updatedAt;

    /** Constructs {@code ClientCrmLeadResponse} with its default state. */
    public ClientCrmLeadResponse() {}

    /**
     * Constructs {@code ClientCrmLeadResponse} using the supplied id, client id, full name and remaining fields.
     *
     * @param id unique identifier of the record
     * @param clientId identifier of the client whose workspace is in scope
     * @param fullName name as displayed to a person
     * @param email contact email address
     * @param phone contact telephone number
     * @param source original item used for the transformation
     * @param status current state in the workflow
     * @param priority relative urgency used to order work
     * @param sectorType sector used to select the intake rules
     * @param sectorFields sector fields
     * @param followUpActions follow up actions
     * @param notes free-text notes associated with the item
     * @param events events
     * @param createdAt time the record was created
     * @param updatedAt time of the most recent update
     */
    public ClientCrmLeadResponse(
            UUID id,
            UUID clientId,
            String fullName,
            String email,
            String phone,
            String source,
            String status,
            String priority,
            String sectorType,
            Map<String, String> sectorFields,
            List<ClientCrmFollowUpActionResponse> followUpActions,
            List<ClientCrmLeadNoteResponse> notes,
            List<ClientCrmLeadEventResponse> events,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.clientId = clientId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.source = source;
        this.status = status;
        this.priority = priority;
        this.sectorType = sectorType;
        this.sectorFields = sectorFields;
        this.followUpActions = followUpActions;
        this.notes = notes;
        this.events = events;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client CRM lead.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the client whose workspace is in scope} */
    public UUID getClientId() { return clientId; }
    /**
     * Sets the identifier of the client whose workspace is in scope on this client CRM lead.
     *
     * @param clientId identifier of the client whose workspace is in scope
     */
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    /** {@return name as displayed to a person} */
    public String getFullName() { return fullName; }
    /**
     * Sets the name as displayed to a person on this client CRM lead.
     *
     * @param fullName name as displayed to a person
     */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /** {@return contact email address} */
    public String getEmail() { return email; }
    /**
     * Sets the contact email address on this client CRM lead.
     *
     * @param email contact email address
     */
    public void setEmail(String email) { this.email = email; }

    /** {@return contact telephone number} */
    public String getPhone() { return phone; }
    /**
     * Sets the contact telephone number on this client CRM lead.
     *
     * @param phone contact telephone number
     */
    public void setPhone(String phone) { this.phone = phone; }

    /** {@return original item used for the transformation} */
    public String getSource() { return source; }
    /**
     * Sets the original item used for the transformation on this client CRM lead.
     *
     * @param source original item used for the transformation
     */
    public void setSource(String source) { this.source = source; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this client CRM lead.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return relative urgency used to order work} */
    public String getPriority() { return priority; }
    /**
     * Sets the relative urgency used to order work on this client CRM lead.
     *
     * @param priority relative urgency used to order work
     */
    public void setPriority(String priority) { this.priority = priority; }

    /** {@return sector used to select the intake rules} */
    public String getSectorType() { return sectorType; }
    /**
     * Sets the sector used to select the intake rules on this client CRM lead.
     *
     * @param sectorType sector used to select the intake rules
     */
    public void setSectorType(String sectorType) { this.sectorType = sectorType; }

    /** {@return sector fields} */
    public Map<String, String> getSectorFields() { return sectorFields; }
    /**
     * Sets the sector fields on this client CRM lead.
     *
     * @param sectorFields sector fields
     */
    public void setSectorFields(Map<String, String> sectorFields) { this.sectorFields = sectorFields; }

    /** {@return follow up actions} */
    public List<ClientCrmFollowUpActionResponse> getFollowUpActions() { return followUpActions; }
    /**
     * Sets the follow up actions on this client CRM lead.
     *
     * @param followUpActions follow up actions
     */
    public void setFollowUpActions(List<ClientCrmFollowUpActionResponse> followUpActions) { this.followUpActions = followUpActions; }

    /** {@return free-text notes associated with the item} */
    public List<ClientCrmLeadNoteResponse> getNotes() { return notes; }
    /**
     * Sets the free-text notes associated with the item on this client CRM lead.
     *
     * @param notes free-text notes associated with the item
     */
    public void setNotes(List<ClientCrmLeadNoteResponse> notes) { this.notes = notes; }

    /** {@return events} */
    public List<ClientCrmLeadEventResponse> getEvents() { return events; }
    /**
     * Sets the events on this client CRM lead.
     *
     * @param events events
     */
    public void setEvents(List<ClientCrmLeadEventResponse> events) { this.events = events; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this client CRM lead.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this client CRM lead.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
