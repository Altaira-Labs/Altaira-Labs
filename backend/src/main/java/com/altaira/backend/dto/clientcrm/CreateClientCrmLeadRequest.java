package com.altaira.backend.dto.clientcrm;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

/** Input for create client CRM lead in the clientCRM API. */
public class CreateClientCrmLeadRequest {

    /** Name as displayed to a person. The request must supply nonblank text. */
    @NotBlank(message = "Full name is required")
    @Size(max = 160, message = "Full name must be at most 160 characters")
    private String fullName;

    /** Contact email address. */
    @Email(message = "Email must be valid")
    @Size(max = 180, message = "Email must be at most 180 characters")
    private String email;

    /** Contact telephone number. */
    @Size(max = 80, message = "Phone must be at most 80 characters")
    private String phone;

    /** Original item used for the transformation. */
    @Size(max = 80, message = "Source must be at most 80 characters")
    private String source;

    /** Relative urgency used to order work. */
    @Size(max = 40, message = "Priority must be at most 40 characters")
    private String priority;

    /** Sector fields. */
    private Map<String, String> sectorFields;

    /** Initial note. */
    @Size(max = 1200, message = "Initial note must be at most 1200 characters")
    private String initialNote;

    /** {@return name as displayed to a person} */
    public String getFullName() { return fullName; }
    /**
     * Sets the name as displayed to a person on this create client CRM lead.
     *
     * @param fullName name as displayed to a person
     */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /** {@return contact email address} */
    public String getEmail() { return email; }
    /**
     * Sets the contact email address on this create client CRM lead.
     *
     * @param email contact email address
     */
    public void setEmail(String email) { this.email = email; }

    /** {@return contact telephone number} */
    public String getPhone() { return phone; }
    /**
     * Sets the contact telephone number on this create client CRM lead.
     *
     * @param phone contact telephone number
     */
    public void setPhone(String phone) { this.phone = phone; }

    /** {@return original item used for the transformation} */
    public String getSource() { return source; }
    /**
     * Sets the original item used for the transformation on this create client CRM lead.
     *
     * @param source original item used for the transformation
     */
    public void setSource(String source) { this.source = source; }

    /** {@return relative urgency used to order work} */
    public String getPriority() { return priority; }
    /**
     * Sets the relative urgency used to order work on this create client CRM lead.
     *
     * @param priority relative urgency used to order work
     */
    public void setPriority(String priority) { this.priority = priority; }

    /** {@return sector fields} */
    public Map<String, String> getSectorFields() { return sectorFields; }
    /**
     * Sets the sector fields on this create client CRM lead.
     *
     * @param sectorFields sector fields
     */
    public void setSectorFields(Map<String, String> sectorFields) { this.sectorFields = sectorFields; }

    /** {@return initial note} */
    public String getInitialNote() { return initialNote; }
    /**
     * Sets the initial note on this create client CRM lead.
     *
     * @param initialNote initial note
     */
    public void setInitialNote(String initialNote) { this.initialNote = initialNote; }
}
