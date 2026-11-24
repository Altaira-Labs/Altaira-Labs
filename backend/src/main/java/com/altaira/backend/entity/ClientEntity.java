package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Stores the customer account around which engagements, people and workspace data are grouped. */
@Entity
@Table(name = "clients")
public class ClientEntity {

    /** Primary identifier for this client. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Name used to identify the item. */
    @Column(nullable = false)
    private String name;

    /** Company. */
    @Column(nullable = false)
    private String company;

    /** Contact email address. */
    @Column(nullable = false)
    private String email;

    /** Contact telephone number. */
    private String phone;

    /** Enquiry from which the client was created. Joined through {@code source_lead_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_lead_id", unique = true)
    private LeadEntity sourceLead;

    /** Current state in the workflow. */
    @Column(nullable = false)
    private String status;

    /** Sector used to select the intake rules. Stored in {@code sector_type}. */
    @Column(name = "sector_type", nullable = false)
    private String sectorType = "custom";

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Time of the most recent update. Stored in {@code updated_at}. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Constructs {@code ClientEntity} with its default state. */
    public ClientEntity() {}

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
     * Sets the unique identifier of the record on this client.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return name used to identify the item} */
    public String getName() { return name; }
    /**
     * Sets the name used to identify the item on this client.
     *
     * @param name name used to identify the item
     */
    public void setName(String name) { this.name = name; }

    /** {@return company} */
    public String getCompany() { return company; }
    /**
     * Sets the company on this client.
     *
     * @param company company
     */
    public void setCompany(String company) { this.company = company; }

    /** {@return contact email address} */
    public String getEmail() { return email; }
    /**
     * Sets the contact email address on this client.
     *
     * @param email contact email address
     */
    public void setEmail(String email) { this.email = email; }

    /** {@return contact telephone number} */
    public String getPhone() { return phone; }
    /**
     * Sets the contact telephone number on this client.
     *
     * @param phone contact telephone number
     */
    public void setPhone(String phone) { this.phone = phone; }

    /** {@return enquiry from which the client was created} */
    public LeadEntity getSourceLead() { return sourceLead; }
    /**
     * Sets the enquiry from which the client was created on this client.
     *
     * @param sourceLead enquiry from which the client was created
     */
    public void setSourceLead(LeadEntity sourceLead) { this.sourceLead = sourceLead; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this client.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return sector used to select the intake rules} */
    public String getSectorType() { return sectorType; }
    /**
     * Sets the sector used to select the intake rules on this client.
     *
     * @param sectorType sector used to select the intake rules
     */
    public void setSectorType(String sectorType) { this.sectorType = sectorType; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this client.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this client.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
