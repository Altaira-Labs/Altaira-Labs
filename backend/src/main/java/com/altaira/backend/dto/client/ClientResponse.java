package com.altaira.backend.dto.client;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for client. */
public class ClientResponse {

    /** Unique identifier of the record. */
    private UUID id;
    /** Name used to identify the item. */
    private String name;
    /** Company. */
    private String company;
    /** Contact email address. */
    private String email;
    /** Contact telephone number. */
    private String phone;
    /** Identifier of the enquiry that became this customer. */
    private UUID sourceLeadId;
    /** Current state in the workflow. */
    private String status;
    /** Sector used to select the intake rules. */
    private String sectorType;
    /** Time the record was created. */
    private Instant createdAt;
    /** Time of the most recent update. */
    private Instant updatedAt;

    /** Constructs {@code ClientResponse} with its default state. */
    public ClientResponse() {}

    /**
     * Constructs {@code ClientResponse} using the supplied id, name, company and remaining fields.
     *
     * @param id unique identifier of the record
     * @param name name used to identify the item
     * @param company company
     * @param email contact email address
     * @param phone contact telephone number
     * @param sourceLeadId identifier of the enquiry that became this customer
     * @param status current state in the workflow
     * @param sectorType sector used to select the intake rules
     * @param createdAt time the record was created
     * @param updatedAt time of the most recent update
     */
    public ClientResponse(UUID id, String name, String company, String email, String phone, UUID sourceLeadId, String status, String sectorType, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.email = email;
        this.phone = phone;
        this.sourceLeadId = sourceLeadId;
        this.status = status;
        this.sectorType = sectorType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    /** {@return identifier of the enquiry that became this customer} */
    public UUID getSourceLeadId() { return sourceLeadId; }
    /**
     * Sets the identifier of the enquiry that became this customer on this client.
     *
     * @param sourceLeadId identifier of the enquiry that became this customer
     */
    public void setSourceLeadId(UUID sourceLeadId) { this.sourceLeadId = sourceLeadId; }

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
