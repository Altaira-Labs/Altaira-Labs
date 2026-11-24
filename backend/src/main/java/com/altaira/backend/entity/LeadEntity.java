package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Stores a prospective customer and their enquiry before the commercial team converts that lead into a client.
 */
@Entity
@Table(name = "leads")
public class LeadEntity {

    /** Primary identifier for this lead. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Name as displayed to a person. Stored in {@code full_name}. */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /** Name used by the business. Stored in {@code business_name}. */
    @Column(name = "business_name", nullable = false)
    private String businessName;

    /** Contact email address. */
    @Column(nullable = false)
    private String email;

    /** Contact telephone number. */
    private String phone;
    /** Industry. */
    private String industry;

    /** Service interest. Stored in {@code service_interest}. */
    @Column(name = "service_interest")
    private String serviceInterest;

    /** Goals. */
    private String goals;

    /** Current state in the workflow. */
    @Column(nullable = false)
    private String status;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Constructs {@code LeadEntity} with its default state. */
    public LeadEntity() {}

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this lead.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return name as displayed to a person} */
    public String getFullName() { return fullName; }
    /**
     * Sets the name as displayed to a person on this lead.
     *
     * @param fullName name as displayed to a person
     */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /** {@return name used by the business} */
    public String getBusinessName() { return businessName; }
    /**
     * Sets the name used by the business on this lead.
     *
     * @param businessName name used by the business
     */
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    /** {@return contact email address} */
    public String getEmail() { return email; }
    /**
     * Sets the contact email address on this lead.
     *
     * @param email contact email address
     */
    public void setEmail(String email) { this.email = email; }

    /** {@return contact telephone number} */
    public String getPhone() { return phone; }
    /**
     * Sets the contact telephone number on this lead.
     *
     * @param phone contact telephone number
     */
    public void setPhone(String phone) { this.phone = phone; }

    /** {@return industry} */
    public String getIndustry() { return industry; }
    /**
     * Sets the industry on this lead.
     *
     * @param industry industry
     */
    public void setIndustry(String industry) { this.industry = industry; }

    /** {@return service interest} */
    public String getServiceInterest() { return serviceInterest; }
    /**
     * Sets the service interest on this lead.
     *
     * @param serviceInterest service interest
     */
    public void setServiceInterest(String serviceInterest) { this.serviceInterest = serviceInterest; }

    /** {@return goals} */
    public String getGoals() { return goals; }
    /**
     * Sets the goals on this lead.
     *
     * @param goals goals
     */
    public void setGoals(String goals) { this.goals = goals; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this lead.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this lead.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
