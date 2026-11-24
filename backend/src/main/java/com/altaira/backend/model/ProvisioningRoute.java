package com.altaira.backend.model;

/** Allowed provisioning route values understood by the backend. */
public enum ProvisioningRoute {
    /** Represents the composite option for provisioning route. */
    COMPOSITE,
    /** Represents the provision web static option for provisioning route. */
    PROVISION_WEB_STATIC,
    /** Represents the provision web cms option for provisioning route. */
    PROVISION_WEB_CMS,
    /** Represents the provision web custom option for provisioning route. */
    PROVISION_WEB_CUSTOM,
    /** Represents the provision ecommerce platform option for provisioning route. */
    PROVISION_ECOMMERCE_PLATFORM,
    /** Represents the provision ecommerce custom option for provisioning route. */
    PROVISION_ECOMMERCE_CUSTOM,
    /** Represents the provision booking saas option for provisioning route. */
    PROVISION_BOOKING_SAAS,
    /** Represents the provision booking custom option for provisioning route. */
    PROVISION_BOOKING_CUSTOM,
    /** Represents the provision CRM altaira option for provisioning route. */
    PROVISION_CRM_ALTAIRA,
    /** Represents the provision CRM external option for provisioning route. */
    PROVISION_CRM_EXTERNAL,
    /** Represents the provision automation managed option for provisioning route. */
    PROVISION_AUTOMATION_MANAGED,
    /** Represents the provision automation custom option for provisioning route. */
    PROVISION_AUTOMATION_CUSTOM,
    /** Represents the provision dashboard bi option for provisioning route. */
    PROVISION_DASHBOARD_BI,
    /** Represents the provision dashboard custom option for provisioning route. */
    PROVISION_DASHBOARD_CUSTOM
}
