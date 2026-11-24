package com.altaira.backend.dto.lead;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

/** Input for create admin lead intake in the lead API. */
public class CreateAdminLeadIntakeRequest {

    /** Name as displayed to a person. The request must supply nonblank text. */
    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must be at most 120 characters")
    private String fullName;

    /** Name used by the business. The request must supply nonblank text. */
    @NotBlank(message = "Business name is required")
    @Size(max = 160, message = "Business name must be at most 160 characters")
    private String businessName;

    /** Contact email address. The request must supply nonblank text. */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 180, message = "Email must be at most 180 characters")
    private String email;

    /** Contact telephone number. */
    @Size(max = 50, message = "Phone must be at most 50 characters")
    private String phone;

    /** Industry. */
    @Size(max = 120, message = "Industry must be at most 120 characters")
    private String industry;

    /** Goals. */
    @Size(max = 2000, message = "Goals must be at most 2000 characters")
    private String goals;

    /** Key selecting the discovery questionnaire. The request must supply nonblank text. */
    @NotBlank(message = "Form key is required")
    @Size(max = 40, message = "Form key must be at most 40 characters")
    private String formKey;

    /** Answers supplied in the discovery questionnaire. Required during request validation. */
    @NotNull(message = "Assessment responses are required")
    private Map<String, Object> responses;

    /** Format version used to interpret this payload. */
    @Min(value = 1, message = "Assessment schema version must be at least 1")
    @Max(value = 2, message = "Assessment schema version is not supported")
    private Integer schemaVersion;

    /** {@return name as displayed to a person} */
    public String getFullName() { return fullName; }
    /**
     * Sets the name as displayed to a person on this create admin lead intake.
     *
     * @param fullName name as displayed to a person
     */
    public void setFullName(String fullName) { this.fullName = fullName; }
    /** {@return name used by the business} */
    public String getBusinessName() { return businessName; }
    /**
     * Sets the name used by the business on this create admin lead intake.
     *
     * @param businessName name used by the business
     */
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    /** {@return contact email address} */
    public String getEmail() { return email; }
    /**
     * Sets the contact email address on this create admin lead intake.
     *
     * @param email contact email address
     */
    public void setEmail(String email) { this.email = email; }
    /** {@return contact telephone number} */
    public String getPhone() { return phone; }
    /**
     * Sets the contact telephone number on this create admin lead intake.
     *
     * @param phone contact telephone number
     */
    public void setPhone(String phone) { this.phone = phone; }
    /** {@return industry} */
    public String getIndustry() { return industry; }
    /**
     * Sets the industry on this create admin lead intake.
     *
     * @param industry industry
     */
    public void setIndustry(String industry) { this.industry = industry; }
    /** {@return goals} */
    public String getGoals() { return goals; }
    /**
     * Sets the goals on this create admin lead intake.
     *
     * @param goals goals
     */
    public void setGoals(String goals) { this.goals = goals; }
    /** {@return key selecting the discovery questionnaire} */
    public String getFormKey() { return formKey; }
    /**
     * Sets the key selecting the discovery questionnaire on this create admin lead intake.
     *
     * @param formKey key selecting the discovery questionnaire
     */
    public void setFormKey(String formKey) { this.formKey = formKey; }
    /** {@return answers supplied in the discovery questionnaire} */
    public Map<String, Object> getResponses() { return responses; }
    /**
     * Sets the answers supplied in the discovery questionnaire on this create admin lead intake.
     *
     * @param responses answers supplied in the discovery questionnaire
     */
    public void setResponses(Map<String, Object> responses) { this.responses = responses; }
    /** {@return format version used to interpret this payload} */
    public Integer getSchemaVersion() { return schemaVersion; }
    /**
     * Sets the format version used to interpret this payload on this create admin lead intake.
     *
     * @param schemaVersion format version used to interpret this payload
     */
    public void setSchemaVersion(Integer schemaVersion) { this.schemaVersion = schemaVersion; }
}
