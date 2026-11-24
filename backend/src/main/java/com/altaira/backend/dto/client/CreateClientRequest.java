package com.altaira.backend.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/** Input for create client in the client API. */
public class CreateClientRequest {

    /** Name used to identify the item. The request must supply nonblank text. */
    @NotBlank(message = "Client name is required")
    @Size(min = 2, max = 120, message = "Client name must be between 2 and 120 characters")
    private String name;

    /** Company. The request must supply nonblank text. */
    @NotBlank(message = "Company is required")
    @Size(min = 2, max = 120, message = "Company must be between 2 and 120 characters")
    private String company;

    /** Contact email address. The request must supply nonblank text. */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /** Contact telephone number. */
    @Size(max = 40, message = "Phone must be at most 40 characters")
    private String phone;

    /** Identifier of the enquiry that became this customer. */
    private UUID sourceLeadId;

    /** Current state in the workflow. */
    @Size(max = 20, message = "Client status must be at most 20 characters")
    private String status;

    /** Sector used to select the intake rules. */
    @Size(max = 40, message = "Sector type must be at most 40 characters")
    private String sectorType;

    /** {@return name used to identify the item} */
    public String getName() { return name; }
    /**
     * Sets the name used to identify the item on this create client.
     *
     * @param name name used to identify the item
     */
    public void setName(String name) { this.name = name; }

    /** {@return company} */
    public String getCompany() { return company; }
    /**
     * Sets the company on this create client.
     *
     * @param company company
     */
    public void setCompany(String company) { this.company = company; }

    /** {@return contact email address} */
    public String getEmail() { return email; }
    /**
     * Sets the contact email address on this create client.
     *
     * @param email contact email address
     */
    public void setEmail(String email) { this.email = email; }

    /** {@return contact telephone number} */
    public String getPhone() { return phone; }
    /**
     * Sets the contact telephone number on this create client.
     *
     * @param phone contact telephone number
     */
    public void setPhone(String phone) { this.phone = phone; }

    /** {@return identifier of the enquiry that became this customer} */
    public UUID getSourceLeadId() { return sourceLeadId; }
    /**
     * Sets the identifier of the enquiry that became this customer on this create client.
     *
     * @param sourceLeadId identifier of the enquiry that became this customer
     */
    public void setSourceLeadId(UUID sourceLeadId) { this.sourceLeadId = sourceLeadId; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this create client.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return sector used to select the intake rules} */
    public String getSectorType() { return sectorType; }
    /**
     * Sets the sector used to select the intake rules on this create client.
     *
     * @param sectorType sector used to select the intake rules
     */
    public void setSectorType(String sectorType) { this.sectorType = sectorType; }
}
