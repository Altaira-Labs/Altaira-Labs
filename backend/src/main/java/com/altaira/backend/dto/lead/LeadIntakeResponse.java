package com.altaira.backend.dto.lead;

/** Fields the API returns for lead intake. */
public class LeadIntakeResponse {
    /** Sales enquiry in scope. */
    private LeadResponse lead;
    /** Assessment. */
    private LeadAssessmentResponse assessment;

    /** Constructs {@code LeadIntakeResponse} with its default state. */
    public LeadIntakeResponse() {}

    /**
     * Constructs {@code LeadIntakeResponse} using the supplied lead, assessment.
     *
     * @param lead sales enquiry in scope
     * @param assessment assessment
     */
    public LeadIntakeResponse(LeadResponse lead, LeadAssessmentResponse assessment) {
        this.lead = lead;
        this.assessment = assessment;
    }

    /** {@return sales enquiry in scope} */
    public LeadResponse getLead() { return lead; }
    /**
     * Sets the sales enquiry in scope on this lead intake.
     *
     * @param lead sales enquiry in scope
     */
    public void setLead(LeadResponse lead) { this.lead = lead; }
    /** {@return assessment} */
    public LeadAssessmentResponse getAssessment() { return assessment; }
    /**
     * Sets the assessment on this lead intake.
     *
     * @param assessment assessment
     */
    public void setAssessment(LeadAssessmentResponse assessment) { this.assessment = assessment; }
}
