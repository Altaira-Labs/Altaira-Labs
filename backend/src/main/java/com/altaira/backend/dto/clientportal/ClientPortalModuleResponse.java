package com.altaira.backend.dto.clientportal;

import java.util.UUID;

/** Fields the API returns for client portal module. */
public class ClientPortalModuleResponse {
    /** Key identifying the module. */
    private String moduleKey;
    /** Short heading shown to a reader. */
    private String title;
    /** Human-readable description. */
    private String description;
    /** Whether the record is currently active. */
    private boolean active;
    /** Locked. */
    private boolean locked;
    /** Current state in the workflow. */
    private String status;
    /** Identifier of the service assignment for this client. */
    private UUID clientServiceId;
    /** Service name. */
    private String serviceName;

    /** Constructs {@code ClientPortalModuleResponse} with its default state. */
    public ClientPortalModuleResponse() {}

    /**
     * Constructs {@code ClientPortalModuleResponse} using the supplied module key, title, description and remaining fields.
     *
     * @param moduleKey key identifying the module
     * @param title short heading shown to a reader
     * @param description human-readable description
     * @param active whether the record is currently active
     * @param locked locked
     * @param status current state in the workflow
     * @param clientServiceId identifier of the service assignment for this client
     * @param serviceName service name
     */
    public ClientPortalModuleResponse(
            String moduleKey,
            String title,
            String description,
            boolean active,
            boolean locked,
            String status,
            UUID clientServiceId,
            String serviceName
    ) {
        this.moduleKey = moduleKey;
        this.title = title;
        this.description = description;
        this.active = active;
        this.locked = locked;
        this.status = status;
        this.clientServiceId = clientServiceId;
        this.serviceName = serviceName;
    }

    /** {@return key identifying the module} */
    public String getModuleKey() { return moduleKey; }
    /**
     * Sets the key identifying the module on this client portal module.
     *
     * @param moduleKey key identifying the module
     */
    public void setModuleKey(String moduleKey) { this.moduleKey = moduleKey; }

    /** {@return short heading shown to a reader} */
    public String getTitle() { return title; }
    /**
     * Sets the short heading shown to a reader on this client portal module.
     *
     * @param title short heading shown to a reader
     */
    public void setTitle(String title) { this.title = title; }

    /** {@return human-readable description} */
    public String getDescription() { return description; }
    /**
     * Sets the human-readable description on this client portal module.
     *
     * @param description human-readable description
     */
    public void setDescription(String description) { this.description = description; }

    /** {@return whether the record is currently active} */
    public boolean isActive() { return active; }
    /**
     * Sets the whether the record is currently active on this client portal module.
     *
     * @param active whether the record is currently active
     */
    public void setActive(boolean active) { this.active = active; }

    /** {@return locked} */
    public boolean isLocked() { return locked; }
    /**
     * Sets the locked on this client portal module.
     *
     * @param locked locked
     */
    public void setLocked(boolean locked) { this.locked = locked; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this client portal module.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return identifier of the service assignment for this client} */
    public UUID getClientServiceId() { return clientServiceId; }
    /**
     * Sets the identifier of the service assignment for this client on this client portal module.
     *
     * @param clientServiceId identifier of the service assignment for this client
     */
    public void setClientServiceId(UUID clientServiceId) { this.clientServiceId = clientServiceId; }

    /** {@return service name} */
    public String getServiceName() { return serviceName; }
    /**
     * Sets the service name on this client portal module.
     *
     * @param serviceName service name
     */
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
}
