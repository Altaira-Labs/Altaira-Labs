package com.altaira.backend.dto.github;

import jakarta.validation.constraints.Size;

import java.util.List;

/** Input for GitHub provisioning dry run in the GitHub API. */
public class GitHubProvisioningDryRunRequest {

    /** Project name. */
    @Size(max = 140, message = "Project name must be at most 140 characters")
    private String projectName;

    /** Client name. */
    @Size(max = 140, message = "Client name must be at most 140 characters")
    private String clientName;

    /** Human-readable description. */
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    /** Persistence access for private. */
    private Boolean privateRepository;
    /** Catalogue keys identifying the requested deliveries. */
    private List<String> serviceKeys;

    /** {@return project name} */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets the project name on this GitHub provisioning dry run.
     *
     * @param projectName project name
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /** {@return client name} */
    public String getClientName() {
        return clientName;
    }

    /**
     * Sets the client name on this GitHub provisioning dry run.
     *
     * @param clientName client name
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /** {@return human-readable description} */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the human-readable description on this GitHub provisioning dry run.
     *
     * @param description human-readable description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** {@return persistence access for private} */
    public Boolean getPrivateRepository() {
        return privateRepository;
    }

    /**
     * Sets the persistence access for private on this GitHub provisioning dry run.
     *
     * @param privateRepository persistence access for private
     */
    public void setPrivateRepository(Boolean privateRepository) {
        this.privateRepository = privateRepository;
    }

    /** {@return catalogue keys identifying the requested deliveries} */
    public List<String> getServiceKeys() {
        return serviceKeys;
    }

    /**
     * Sets the catalogue keys identifying the requested deliveries on this GitHub provisioning dry run.
     *
     * @param serviceKeys catalogue keys identifying the requested deliveries
     */
    public void setServiceKeys(List<String> serviceKeys) {
        this.serviceKeys = serviceKeys;
    }
}
