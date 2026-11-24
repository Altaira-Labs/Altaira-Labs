package com.altaira.backend.dto.clientportal;

import jakarta.validation.constraints.Size;

/** Input for update client project in the clientportal API. */
public class UpdateClientProjectRequest {

    /** Name used to identify the item. */
    @Size(max = 180, message = "Project name must be at most 180 characters")
    private String name;

    /** Current phase. */
    @Size(max = 40, message = "Project phase must be at most 40 characters")
    private String currentPhase;

    /** URL for staging. */
    @Size(max = 500, message = "Staging URL must be at most 500 characters")
    private String stagingUrl;

    /** {@return name used to identify the item} */
    public String getName() { return name; }
    /**
     * Sets the name used to identify the item on this update client project.
     *
     * @param name name used to identify the item
     */
    public void setName(String name) { this.name = name; }

    /** {@return current phase} */
    public String getCurrentPhase() { return currentPhase; }
    /**
     * Sets the current phase on this update client project.
     *
     * @param currentPhase current phase
     */
    public void setCurrentPhase(String currentPhase) { this.currentPhase = currentPhase; }

    /** {@return URL for staging} */
    public String getStagingUrl() { return stagingUrl; }
    /**
     * Sets the URL for staging on this update client project.
     *
     * @param stagingUrl URL for staging
     */
    public void setStagingUrl(String stagingUrl) { this.stagingUrl = stagingUrl; }
}
