package com.altaira.backend.dto.lead;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

/** Input for save lead assessment in the lead API. */
public class SaveLeadAssessmentRequest {

    /** Answers supplied in the discovery questionnaire. Required during request validation. */
    @NotNull(message = "Assessment responses are required")
    private Map<String, Object> responses;

    /** Current state in the workflow. */
    @Size(max = 20, message = "Assessment status must be at most 20 characters")
    private String status;

    /** Format version used to interpret this payload. */
    @Min(value = 1, message = "Assessment schema version must be at least 1")
    @Max(value = 2, message = "Assessment schema version is not supported")
    private Integer schemaVersion;

    /** {@return answers supplied in the discovery questionnaire} */
    public Map<String, Object> getResponses() { return responses; }
    /**
     * Sets the answers supplied in the discovery questionnaire on this save lead assessment.
     *
     * @param responses answers supplied in the discovery questionnaire
     */
    public void setResponses(Map<String, Object> responses) { this.responses = responses; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this save lead assessment.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return format version used to interpret this payload} */
    public Integer getSchemaVersion() { return schemaVersion; }
    /**
     * Sets the format version used to interpret this payload on this save lead assessment.
     *
     * @param schemaVersion format version used to interpret this payload
     */
    public void setSchemaVersion(Integer schemaVersion) { this.schemaVersion = schemaVersion; }
}
