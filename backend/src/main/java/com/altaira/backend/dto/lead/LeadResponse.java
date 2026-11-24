package com.altaira.backend.dto.lead;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for lead. */
public class LeadResponse {

    /** Unique identifier of the record. */
    private UUID id;
    /** Name as displayed to a person. */
    private String fullName;
    /** Name used by the business. */
    private String businessName;
    /** Contact email address. */
    private String email;
    /** Contact telephone number. */
    private String phone;
    /** Industry. */
    private String industry;
    /** Service interest. */
    private String serviceInterest;
    /** Goals. */
    private String goals;
    /** Current state in the workflow. */
    private String status;
    /** Time the record was created. */
    private Instant createdAt;
    /** Email notification sent. */
    private Boolean emailNotificationSent;
    /** Email notification message. */
    private String emailNotificationMessage;

    /** Constructs {@code LeadResponse} with its default state. */
    public LeadResponse() {
    }

    /**
     * Constructs {@code LeadResponse} using the supplied id, full name, business name and remaining fields.
     *
     * @param id unique identifier of the record
     * @param fullName name as displayed to a person
     * @param businessName name used by the business
     * @param email contact email address
     * @param industry industry
     * @param goals goals
     * @param status current state in the workflow
     * @param createdAt time the record was created
     */
    public LeadResponse(UUID id, String fullName, String businessName, String email, String industry, String goals, String status, Instant createdAt) {
        this(id, fullName, businessName, email, null, industry, null, goals, status, createdAt, null, null);
    }

    /**
     * Constructs {@code LeadResponse} using the supplied id, full name, business name and remaining fields.
     *
     * @param id unique identifier of the record
     * @param fullName name as displayed to a person
     * @param businessName name used by the business
     * @param email contact email address
     * @param phone contact telephone number
     * @param industry industry
     * @param serviceInterest service interest
     * @param goals goals
     * @param status current state in the workflow
     * @param createdAt time the record was created
     */
    public LeadResponse(UUID id, String fullName, String businessName, String email, String phone, String industry, String serviceInterest, String goals, String status, Instant createdAt) {
        this(id, fullName, businessName, email, phone, industry, serviceInterest, goals, status, createdAt, null, null);
    }

    /**
     * Constructs {@code LeadResponse} using the supplied id, full name, business name and remaining fields.
     *
     * @param id unique identifier of the record
     * @param fullName name as displayed to a person
     * @param businessName name used by the business
     * @param email contact email address
     * @param industry industry
     * @param goals goals
     * @param status current state in the workflow
     * @param createdAt time the record was created
     * @param emailNotificationSent email notification sent
     * @param emailNotificationMessage email notification message
     */
    public LeadResponse(UUID id, String fullName, String businessName, String email, String industry, String goals, String status, Instant createdAt, Boolean emailNotificationSent, String emailNotificationMessage) {
        this(id, fullName, businessName, email, null, industry, null, goals, status, createdAt, emailNotificationSent, emailNotificationMessage);
    }

    /**
     * Constructs {@code LeadResponse} using the supplied id, full name, business name and remaining fields.
     *
     * @param id unique identifier of the record
     * @param fullName name as displayed to a person
     * @param businessName name used by the business
     * @param email contact email address
     * @param phone contact telephone number
     * @param industry industry
     * @param serviceInterest service interest
     * @param goals goals
     * @param status current state in the workflow
     * @param createdAt time the record was created
     * @param emailNotificationSent email notification sent
     * @param emailNotificationMessage email notification message
     */
    public LeadResponse(UUID id, String fullName, String businessName, String email, String phone, String industry, String serviceInterest, String goals, String status, Instant createdAt, Boolean emailNotificationSent, String emailNotificationMessage) {
        this.id = id;
        this.fullName = fullName;
        this.businessName = businessName;
        this.email = email;
        this.phone = phone;
        this.industry = industry;
        this.serviceInterest = serviceInterest;
        this.goals = goals;
        this.status = status;
        this.createdAt = createdAt;
        this.emailNotificationSent = emailNotificationSent;
        this.emailNotificationMessage = emailNotificationMessage;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the record on this lead.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /** {@return name as displayed to a person} */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the name as displayed to a person on this lead.
     *
     * @param fullName name as displayed to a person
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /** {@return name used by the business} */
    public String getBusinessName() {
        return businessName;
    }

    /**
     * Sets the name used by the business on this lead.
     *
     * @param businessName name used by the business
     */
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    /** {@return contact email address} */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the contact email address on this lead.
     *
     * @param email contact email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /** {@return contact telephone number} */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the contact telephone number on this lead.
     *
     * @param phone contact telephone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** {@return industry} */
    public String getIndustry() {
        return industry;
    }

    /**
     * Sets the industry on this lead.
     *
     * @param industry industry
     */
    public void setIndustry(String industry) {
        this.industry = industry;
    }

    /** {@return service interest} */
    public String getServiceInterest() {
        return serviceInterest;
    }

    /**
     * Sets the service interest on this lead.
     *
     * @param serviceInterest service interest
     */
    public void setServiceInterest(String serviceInterest) {
        this.serviceInterest = serviceInterest;
    }

    /** {@return goals} */
    public String getGoals() {
        return goals;
    }

    /**
     * Sets the goals on this lead.
     *
     * @param goals goals
     */
    public void setGoals(String goals) {
        this.goals = goals;
    }

    /** {@return current state in the workflow} */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current state in the workflow on this lead.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /** {@return time the record was created} */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the time the record was created on this lead.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /** {@return email notification sent} */
    public Boolean getEmailNotificationSent() {
        return emailNotificationSent;
    }

    /**
     * Sets the email notification sent on this lead.
     *
     * @param emailNotificationSent email notification sent
     */
    public void setEmailNotificationSent(Boolean emailNotificationSent) {
        this.emailNotificationSent = emailNotificationSent;
    }

    /** {@return email notification message} */
    public String getEmailNotificationMessage() {
        return emailNotificationMessage;
    }

    /**
     * Sets the email notification message on this lead.
     *
     * @param emailNotificationMessage email notification message
     */
    public void setEmailNotificationMessage(String emailNotificationMessage) {
        this.emailNotificationMessage = emailNotificationMessage;
    }
}
