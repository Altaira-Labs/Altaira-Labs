package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Maps client CRM lead records to the {@code client_crm_leads} table. */
@Entity
@Table(name = "client_crm_leads")
public class ClientCrmLeadEntity {

    /** Primary identifier for this client CRM lead. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    /** Created by user. Joined through {@code created_by_user_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private AppUserEntity createdByUser;

    /** Name as displayed to a person. Stored in {@code full_name}. */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /** Contact email address. */
    private String email;
    /** Contact telephone number. */
    private String phone;
    /** Original item used for the transformation. */
    private String source;

    /** Current state in the workflow. */
    @Column(nullable = false)
    private String status = "new_lead";

    /** Relative urgency used to order work. */
    @Column(nullable = false)
    private String priority = "normal";

    /** Sector used to select the intake rules. Stored in {@code sector_type}. */
    @Column(name = "sector_type", nullable = false)
    private String sectorType = "custom";

    /** JSON representation of sector fields. Stored in {@code sector_fields_json}. */
    @Column(name = "sector_fields_json", nullable = false, columnDefinition = "text")
    private String sectorFieldsJson = "{}";

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Time of the most recent update. Stored in {@code updated_at}. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Constructs {@code ClientCrmLeadEntity} with its default state. */
    public ClientCrmLeadEntity() {}

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
     * Sets the unique identifier of the record on this client CRM lead.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this client CRM lead.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

    /** {@return created by user} */
    public AppUserEntity getCreatedByUser() { return createdByUser; }
    /**
     * Sets the created by user on this client CRM lead.
     *
     * @param createdByUser created by user
     */
    public void setCreatedByUser(AppUserEntity createdByUser) { this.createdByUser = createdByUser; }

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

    /** {@return JSON representation of sector fields} */
    public String getSectorFieldsJson() { return sectorFieldsJson; }
    /**
     * Sets the JSON representation of sector fields on this client CRM lead.
     *
     * @param sectorFieldsJson JSON representation of sector fields
     */
    public void setSectorFieldsJson(String sectorFieldsJson) { this.sectorFieldsJson = sectorFieldsJson; }

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
