package com.altaira.backend.provisioning;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Translates answers from discovery forms into the small set of requirements understood by the planning engine.
 */
@Component
public class ProvisioningRequirementNormalizer {

    /**
     * Converts discovery answers and selected services into the requirements consumed by the planning rules.
     *
     * @param formKey key selecting the discovery questionnaire
     * @param responses answers supplied in the discovery questionnaire
     * @param recommendedServices catalogue services proposed after discovery
     * @return the requirement set
     */
    public RequirementSet normalize(
            String formKey,
            Map<String, Object> responses,
            List<String> recommendedServices
    ) {
        RequirementSet requirements = new RequirementSet();
        String normalizedFormKey = normalize(formKey);

        switch (normalizedFormKey) {
            case "web_seo" -> applyWebRequirements(requirements, responses);
            case "booking" -> applyBookingRequirements(requirements, responses);
            case "crm" -> applyCrmRequirements(requirements, responses);
            case "automation" -> applyAutomationRequirements(requirements, responses);
            case "dashboard" -> applyDashboardRequirements(requirements, responses);
            default -> applyGeneralRequirements(requirements, responses, recommendedServices);
        }

        applyExplicitSignals(requirements, responses);
        deriveTechnicalDependencies(requirements);
        return requirements;
    }

    /**
     * Applies web requirements.
     *
     * @param requirements requirements
     * @param responses answers supplied in the discovery questionnaire
     */
    private void applyWebRequirements(RequirementSet requirements, Map<String, Object> responses) {
        requirements.enable(RequirementSet.FRONTEND);

        String shape = value(responses, "solutionShape", "websiteType", "siteType");
        if (matches(shape, "custom_app", "web_application", "custom")) {
            requirements.enable(RequirementSet.BACKEND);
            requirements.enable(RequirementSet.DATABASE);
        }
        if (matches(shape, "ecommerce", "online_store")) {
            requirements.enable(RequirementSet.PAYMENTS);
            if (isYes(responses, "dataPersistence", "authentication")
                    || matches(value(responses, "externalIntegrations"), "custom")) {
                requirements.enable(RequirementSet.DATABASE);
                requirements.enable(RequirementSet.BACKEND);
            }
        }
        if (isYes(responses, "contentManagement", "requiresCms")) {
            requirements.enable(RequirementSet.CMS);
        }
    }

    /**
     * Applies booking requirements.
     *
     * @param requirements requirements
     * @param responses answers supplied in the discovery questionnaire
     */
    private void applyBookingRequirements(RequirementSet requirements, Map<String, Object> responses) {
        requirements.enable(RequirementSet.BOOKING);
        requirements.enable(RequirementSet.CALENDAR);
        requirements.enable(RequirementSet.FRONTEND);
        requirements.enable(RequirementSet.AUTH);

        if (isCustomBooking(responses)) {
            requirements.enable(RequirementSet.BACKEND);
            requirements.enable(RequirementSet.DATABASE);
        }

        if (isYes(responses, "depositRequired", "paymentsRequired")) {
            requirements.enable(RequirementSet.PAYMENTS);
        }
        if (matches(value(responses, "currentBookingProcess"), "existing_tool")
                || hasUsefulValue(responses, "calendarIntegrations", "integrations")) {
            requirements.enable(RequirementSet.EXTERNAL_INTEGRATIONS);
        }
    }

    /**
     * Applies CRM requirements.
     *
     * @param requirements requirements
     * @param responses answers supplied in the discovery questionnaire
     */
    private void applyCrmRequirements(RequirementSet requirements, Map<String, Object> responses) {
        requirements.enable(RequirementSet.CRM);
        requirements.enable(RequirementSet.TRAINING);

        boolean externalCrm = matches(value(responses, "currentLeadProcess"), "existing_crm");
        if (!externalCrm) {
            requirements.enable(RequirementSet.FRONTEND);
            requirements.enable(RequirementSet.BACKEND);
            requirements.enable(RequirementSet.DATABASE);
            requirements.enable(RequirementSet.AUTH);
        }

        if (isYes(responses, "importRequired", "dataMigration")) {
            requirements.enable(RequirementSet.DATA_MIGRATION);
            requirements.enable(RequirementSet.FILE_STORAGE);
        }
        if (externalCrm || containsAny(responses, "leadSources", "multiple website forms")) {
            requirements.enable(RequirementSet.EXTERNAL_INTEGRATIONS);
        }
        if (containsAny(responses, "pipelineStages", "follow-up")
                && containsAny(responses, "requiredFields", "last contact")) {
            requirements.enable(RequirementSet.AUTOMATION);
            requirements.enable(RequirementSet.EXTERNAL_INTEGRATIONS);
        }
    }

    /**
     * Applies automation requirements.
     *
     * @param requirements requirements
     * @param responses answers supplied in the discovery questionnaire
     */
    private void applyAutomationRequirements(RequirementSet requirements, Map<String, Object> responses) {
        requirements.enable(RequirementSet.AUTOMATION);

        String channels = value(responses, "channels");
        if (!channels.equals("altaira crm")) {
            requirements.enable(RequirementSet.EXTERNAL_INTEGRATIONS);
        }
        if (channels.contains("crm")) {
            requirements.enable(RequirementSet.CRM);
        }
        if (channels.contains("calendar")) {
            requirements.enable(RequirementSet.CALENDAR);
        }
        if (channels.contains("stripe")) {
            requirements.enable(RequirementSet.PAYMENTS);
        }
        if (isCustomAutomation(responses)) {
            requirements.enable(RequirementSet.BACKEND);
            requirements.enable(RequirementSet.DATABASE);
        }
    }

    /**
     * Applies dashboard requirements.
     *
     * @param requirements requirements
     * @param responses answers supplied in the discovery questionnaire
     */
    private void applyDashboardRequirements(RequirementSet requirements, Map<String, Object> responses) {
        requirements.enable(RequirementSet.DASHBOARD);

        if (matches(value(responses, "updateFrequency"), "realtime")) {
            requirements.enable(RequirementSet.REALTIME);
            requirements.enable(RequirementSet.FRONTEND);
            requirements.enable(RequirementSet.BACKEND);
            requirements.enable(RequirementSet.DATABASE);
            requirements.enable(RequirementSet.AUTH);
        } else if (requiresDashboardAuthentication(responses)) {
            requirements.enable(RequirementSet.AUTH);
        }
        if (isYes(responses, "financialData", "sensitiveData")) {
            requirements.enable(RequirementSet.AUTH);
        }
        if (matches(value(responses, "currentReporting"), "spreadsheets", "manual_reports")) {
            requirements.enable(RequirementSet.DATA_MIGRATION);
        }
        if (hasUsefulValue(responses, "dataSources")) {
            requirements.enable(RequirementSet.EXTERNAL_INTEGRATIONS);
        }
    }

    /**
     * Applies general requirements.
     *
     * @param requirements requirements
     * @param responses answers supplied in the discovery questionnaire
     * @param recommendedServices catalogue services proposed after discovery
     */
    private void applyGeneralRequirements(
            RequirementSet requirements,
            Map<String, Object> responses,
            List<String> recommendedServices
    ) {
        for (String service : recommendedServices) {
            switch (normalize(service)) {
                case "web_seo" -> requirements.enable(RequirementSet.FRONTEND);
                case "booking" -> applyBookingRequirements(requirements, responses);
                case "crm" -> applyCrmRequirements(requirements, responses);
                case "automation" -> applyAutomationRequirements(requirements, responses);
                case "dashboard" -> applyDashboardRequirements(requirements, responses);
                default -> {
                    // Unknown recommendations stay visible in the assessment but do not create capabilities.
                }
            }
        }

        boolean hasWeb = recommendedServices.stream().map(this::normalize).anyMatch("web_seo"::equals);
        boolean hasBooking = recommendedServices.stream().map(this::normalize).anyMatch("booking"::equals);
        if (hasWeb && matches(value(responses, "onlinePresence"), "outdated", "basic")) {
            requirements.enable(RequirementSet.CMS);
        }
        if (hasBooking) {
            requirements.enable(RequirementSet.BACKEND);
            requirements.enable(RequirementSet.DATABASE);
        }
        if (matches(value(responses, "primaryGoal"), "automation")
                && recommendedServices.stream().map(this::normalize).anyMatch("dashboard"::equals)) {
            requirements.enable(RequirementSet.FILE_STORAGE);
        }
    }

    /**
     * Applies explicit signals.
     *
     * @param requirements requirements
     * @param responses answers supplied in the discovery questionnaire
     */
    private void applyExplicitSignals(RequirementSet requirements, Map<String, Object> responses) {
        enableIfYes(requirements, RequirementSet.DATABASE, responses, "dataPersistence", "requiresDatabase");
        enableIfYes(requirements, RequirementSet.BACKEND, responses, "backendRequired", "requiresBackend");
        enableIfYes(requirements, RequirementSet.AUTH, responses, "authentication", "requiresAuth");
        enableIfYes(requirements, RequirementSet.CMS, responses, "contentManagement", "requiresCms");
        enableIfYes(requirements, RequirementSet.PAYMENTS, responses, "payments", "depositRequired");
        enableIfYes(requirements, RequirementSet.FILE_STORAGE, responses, "fileUploads", "requiresFileStorage");
        enableIfYes(requirements, RequirementSet.DATA_MIGRATION, responses, "dataMigration", "importRequired");
        enableIfYes(requirements, RequirementSet.TRAINING, responses, "trainingRequired");

        String integrations = value(responses, "externalIntegrations", "integrations");
        if (!integrations.isBlank() && !matches(integrations, "none", "no", "unknown")) {
            requirements.enable(RequirementSet.EXTERNAL_INTEGRATIONS);
        }
    }

    /**
     * Adds the supporting technical requirements implied by the requested capabilities.
     *
     * @param requirements requirements
     */
    private void deriveTechnicalDependencies(RequirementSet requirements) {
        if (requirements.has(RequirementSet.REALTIME)) {
            requirements.enable(RequirementSet.BACKEND);
        }
        if (requirements.has(RequirementSet.REALTIME)) {
            requirements.enable(RequirementSet.DATABASE);
        }

        boolean staticSite = requirements.has(RequirementSet.FRONTEND)
                && !requirements.has(RequirementSet.BACKEND)
                && !requirements.has(RequirementSet.DATABASE)
                && !requirements.has(RequirementSet.AUTH)
                && !requirements.has(RequirementSet.CMS)
                && !requirements.has(RequirementSet.PAYMENTS)
                && !requirements.has(RequirementSet.BOOKING)
                && !requirements.has(RequirementSet.CRM)
                && !requirements.has(RequirementSet.DASHBOARD);
        if (staticSite) {
            requirements.enable(RequirementSet.STATIC_SITE);
        } else {
            requirements.disable(RequirementSet.STATIC_SITE);
        }
    }

    /**
     * Enables a requirement when the corresponding discovery response is affirmative.
     *
     * @param requirements requirements
     * @param requirement requirement
     * @param responses answers supplied in the discovery questionnaire
     * @param keys keys
     */
    private void enableIfYes(
            RequirementSet requirements,
            String requirement,
            Map<String, Object> responses,
            String... keys
    ) {
        if (isYes(responses, keys)) {
            requirements.enable(requirement);
        }
    }

    /**
     * Checks whether this is yes.
     *
     * @param responses answers supplied in the discovery questionnaire
     * @param keys keys
     * @return true if the value is yes
     */
    private boolean isYes(Map<String, Object> responses, String... keys) {
        String raw = value(responses, keys);
        return matches(raw, "yes", "true", "required", "enabled");
    }

    /**
     * Checks whether there is useful value.
     *
     * @param responses answers supplied in the discovery questionnaire
     * @param keys keys
     * @return true if the value has useful value
     */
    private boolean hasUsefulValue(Map<String, Object> responses, String... keys) {
        String raw = value(responses, keys);
        return !raw.isBlank() && !matches(raw, "none", "no", "not_applicable", "unknown");
    }

    /**
     * Checks whether this is custom booking.
     *
     * @param responses answers supplied in the discovery questionnaire
     * @return true if the value is custom booking
     */
    private boolean isCustomBooking(Map<String, Object> responses) {
        String bookingType = value(responses, "bookingType");
        String resources = value(responses, "resources");
        String slotDuration = value(responses, "slotDuration");
        return bookingType.contains("resource and capacity")
                || resources.contains("dependent")
                || slotDuration.equals("variable")
                || integerValue(responses, "resourceCount") > 10
                || integerValue(responses, "locationCount") > 1;
    }

    /**
     * Checks whether this is custom automation.
     *
     * @param responses answers supplied in the discovery questionnaire
     * @return true if the value is custom automation
     */
    private boolean isCustomAutomation(Map<String, Object> responses) {
        int monthlyVolume = integerValue(responses, "monthlyVolume");
        return monthlyVolume >= 5000
                || matches(value(responses, "criticality"), "high")
                || containsAny(responses, "currentProcess", "critical")
                || containsAny(responses, "failureHandling", "compensate");
    }

    /**
     * Determines whether requires dashboard authentication applies.
     *
     * @param responses answers supplied in the discovery questionnaire
     * @return true when the check succeeds
     */
    private boolean requiresDashboardAuthentication(Map<String, Object> responses) {
        String source = value(responses, "dataSources");
        String users = value(responses, "users");
        String viewerType = value(responses, "viewerType");
        return source.contains("postgresql")
                || source.contains("finance")
                || users.contains("sales team")
                || users.contains("finance and operations")
                || matches(viewerType, "owner", "internal_team", "clients", "mixed");
    }

    /**
     * Checks for any.
     *
     * @param responses answers supplied in the discovery questionnaire
     * @param key key used to identify the item
     * @param fragments fragments
     * @return true if the value contains any
     */
    private boolean containsAny(Map<String, Object> responses, String key, String... fragments) {
        String raw = value(responses, key);
        for (String fragment : fragments) {
            if (raw.contains(normalize(fragment))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts digits from the discovery field, using zero when there are none.
     *
     * @param responses answers supplied in the discovery questionnaire
     * @param key key used to identify the item
     * @return the value produced by this operation
     */
    private int integerValue(Map<String, Object> responses, String key) {
        String raw = value(responses, key).replaceAll("[^0-9]", "");
        return raw.isBlank() ? 0 : Integer.parseInt(raw);
    }

    /**
     * Reads the first available discovery answer across the supplied keys and handles both scalar and collection responses.
     *
     * @param responses answers supplied in the discovery questionnaire
     * @param keys keys
     * @return the resulting text
     */
    private String value(Map<String, Object> responses, String... keys) {
        for (String key : keys) {
            Object raw = responses.get(key);
            if (raw == null) {
                continue;
            }
            if (raw instanceof Collection<?> collection) {
                String joined = collection.stream()
                        .map(String::valueOf)
                        .map(this::normalize)
                        .filter(item -> !item.isBlank())
                        .reduce((left, right) -> left + "," + right)
                        .orElse("");
                if (!joined.isBlank()) {
                    return joined;
                }
            } else {
                String normalized = normalize(String.valueOf(raw));
                if (!normalized.isBlank()) {
                    return normalized;
                }
            }
        }
        return "";
    }

    /**
     * Determines whether matches applies.
     *
     * @param value stored or supplied representation
     * @param candidates candidates
     * @return true when the check succeeds
     */
    private boolean matches(String value, String... candidates) {
        for (String candidate : candidates) {
            if (value.equals(normalize(candidate))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts discovery answers and selected services into the requirements consumed by the planning rules.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
