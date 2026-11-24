package com.altaira.backend.dto.lead;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for create lead in the lead API. */
public class CreateLeadRequest {

    /** Name as displayed to a person. The request must supply nonblank text. */
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    /** Name used by the business. The request must supply nonblank text. */
    @NotBlank(message = "Business name is required")
    @Size(min = 2, max = 120, message = "Business name must be between 2 and 120 characters")
    private String businessName;

    /** Contact email address. The request must supply nonblank text. */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /** Contact telephone number. */
    @Size(max = 50, message = "Phone must be at most 50 characters")
    private String phone;

    /** Industry. */
    @Size(max = 50, message = "Industry must be at most 50 characters")
    private String industry;

    /** Service interest. */
    @Size(max = 120, message = "Service interest must be at most 120 characters")
    private String serviceInterest;

    /** Goals. */
    @Size(max = 1000, message = "Goals must be at most 1000 characters")
    private String goals;

    /** Website. */
    private String website;

    /** {@return name as displayed to a person} */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the name as displayed to a person on this create lead.
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
     * Sets the name used by the business on this create lead.
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
     * Sets the contact email address on this create lead.
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
     * Sets the contact telephone number on this create lead.
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
     * Sets the industry on this create lead.
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
     * Sets the service interest on this create lead.
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
     * Sets the goals on this create lead.
     *
     * @param goals goals
     */
    public void setGoals(String goals) {
        this.goals = goals;
    }

    /** {@return website} */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the website on this create lead.
     *
     * @param website website
     */
    public void setWebsite(String website) {
        this.website = website;
    }
}
