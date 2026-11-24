package com.altaira.backend.provisioning;

/** Names the service track to which a set of planning decisions belongs. */
public enum ProvisioningTrack {
    /** Represents web with serialized value {@code web_seo}. */
    WEB("web_seo"),
    /** Represents booking with serialized value {@code booking}. */
    BOOKING("booking"),
    /** Represents CRM with serialized value {@code crm}. */
    CRM("crm"),
    /** Represents automation with serialized value {@code automation}. */
    AUTOMATION("automation"),
    /** Represents dashboard with serialized value {@code dashboard}. */
    DASHBOARD("dashboard");

    /** Stable key for the service. */
    private final String serviceKey;

    /**
     * Constructs {@code ProvisioningTrack} using the supplied service key.
     *
     * @param serviceKey stable key for the service
     */
    ProvisioningTrack(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    /**
     * Returns the stable key for the service for this provisioning track.
     *
     * @return stable key for the service
     */
    public String serviceKey() {
        return serviceKey;
    }
}
