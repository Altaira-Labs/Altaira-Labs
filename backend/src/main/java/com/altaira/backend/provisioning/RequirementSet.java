package com.altaira.backend.provisioning;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the normalized requirements derived from a commercial assessment before a delivery route is selected.
 */
public class RequirementSet {

    /** Database used by requirement set. */
    public static final String DATABASE = "requires_database";
    /** Backend used by requirement set. */
    public static final String BACKEND = "requires_backend";
    /** Frontend used by requirement set. */
    public static final String FRONTEND = "requires_frontend";
    /** Static site used by requirement set. */
    public static final String STATIC_SITE = "requires_static_site";
    /** Auth used by requirement set. */
    public static final String AUTH = "requires_auth";
    /** Cms used by requirement set. */
    public static final String CMS = "requires_cms";
    /** Payments used by requirement set. */
    public static final String PAYMENTS = "requires_payments";
    /** Booking used by requirement set. */
    public static final String BOOKING = "requires_booking";
    /** CRM used by requirement set. */
    public static final String CRM = "requires_crm";
    /** Calendar used by requirement set. */
    public static final String CALENDAR = "requires_calendar";
    /** Automation used by requirement set. */
    public static final String AUTOMATION = "requires_automation";
    /** File storage used by requirement set. */
    public static final String FILE_STORAGE = "requires_file_storage";
    /** Dashboard used by requirement set. */
    public static final String DASHBOARD = "requires_dashboard";
    /** Realtime used by requirement set. */
    public static final String REALTIME = "requires_realtime";
    /** Data migration used by requirement set. */
    public static final String DATA_MIGRATION = "requires_data_migration";
    /** Training used by requirement set. */
    public static final String TRAINING = "requires_training";
    /** External integrations used by requirement set. */
    public static final String EXTERNAL_INTEGRATIONS = "requires_external_integrations";

    /** Ordered keys used by requirement set. */
    private static final List<String> ORDERED_KEYS = List.of(
            DATABASE,
            BACKEND,
            FRONTEND,
            STATIC_SITE,
            AUTH,
            CMS,
            PAYMENTS,
            BOOKING,
            CRM,
            CALENDAR,
            AUTOMATION,
            FILE_STORAGE,
            DASHBOARD,
            REALTIME,
            DATA_MIGRATION,
            TRAINING,
            EXTERNAL_INTEGRATIONS
    );

    /** Values. */
    private final Map<String, Boolean> values = new LinkedHashMap<>();

    /** Constructs {@code RequirementSet} with its default state. */
    public RequirementSet() {
        ORDERED_KEYS.forEach(key -> values.put(key, false));
    }

    /**
     * Sets a known requirement key to true.
     *
     * @param key key used to identify the item
     */
    public void enable(String key) {
        requireKnownKey(key);
        values.put(key, true);
    }

    /**
     * Sets a known requirement key to false.
     *
     * @param key key used to identify the item
     */
    public void disable(String key) {
        requireKnownKey(key);
        values.put(key, false);
    }

    /**
     * Determines whether has applies.
     *
     * @param key key used to identify the item
     * @return true when the check succeeds
     */
    public boolean has(String key) {
        requireKnownKey(key);
        return values.get(key);
    }

    /**
     * Exposes the named requirements and their enabled flags for serialization or comparison.
     *
     * @return the resulting key–value pairs
     */
    public Map<String, Boolean> asMap() {
        return Map.copyOf(values);
    }

    /**
     * Requires known key.
     *
     * @param key key used to identify the item
     */
    private void requireKnownKey(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("Unknown provisioning requirement: " + key);
        }
    }
}
