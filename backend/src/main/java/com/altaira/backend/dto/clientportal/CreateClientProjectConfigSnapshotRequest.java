package com.altaira.backend.dto.clientportal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for create client project config snapshot in the clientportal API. */
public class CreateClientProjectConfigSnapshotRequest {

    /** Display label. The request must supply nonblank text. */
    @NotBlank(message = "Snapshot label is required")
    @Size(max = 180, message = "Snapshot label must be at most 180 characters")
    private String label;

    /** Artifact type. The request must supply nonblank text. */
    @NotBlank(message = "Artifact type is required")
    @Size(max = 120, message = "Artifact type must be at most 120 characters")
    private String artifactType;

    /** JSON representation of config. The request must supply nonblank text. */
    @NotBlank(message = "Configuration JSON is required")
    @Size(max = 50000, message = "Configuration JSON must be at most 50000 characters")
    private String configJson;

    /** {@return display label} */
    public String getLabel() { return label; }
    /**
     * Sets the display label on this create client project config snapshot.
     *
     * @param label display label
     */
    public void setLabel(String label) { this.label = label; }

    /** {@return artifact type} */
    public String getArtifactType() { return artifactType; }
    /**
     * Sets the artifact type on this create client project config snapshot.
     *
     * @param artifactType artifact type
     */
    public void setArtifactType(String artifactType) { this.artifactType = artifactType; }

    /** {@return JSON representation of config} */
    public String getConfigJson() { return configJson; }
    /**
     * Sets the JSON representation of config on this create client project config snapshot.
     *
     * @param configJson JSON representation of config
     */
    public void setConfigJson(String configJson) { this.configJson = configJson; }
}
