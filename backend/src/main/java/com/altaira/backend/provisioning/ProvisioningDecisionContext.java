package com.altaira.backend.provisioning;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Bundles the normalized requirements, discovery answers and commercial selections considered by a planning rule.
 *
 * @param requirements requirements
 * @param formKey key selecting the discovery questionnaire
 * @param responses answers supplied in the discovery questionnaire
 * @param recommendedServices catalogue services proposed after discovery
 * @param businessName name used by the business
 */
public record ProvisioningDecisionContext(
        RequirementSet requirements,
        String formKey,
        Map<String, Object> responses,
        List<String> recommendedServices,
        String businessName
) {
    /**
     * Constructs {@code ProvisioningDecisionContext} using the supplied requirements, form key, responses and remaining fields.
     *
     * @param requirements requirements
     * @param formKey key selecting the discovery questionnaire
     * @param responses answers supplied in the discovery questionnaire
     * @param recommendedServices catalogue services proposed after discovery
     * @param businessName name used by the business
     */
    public ProvisioningDecisionContext {
        formKey = normalize(formKey);
        responses = responses == null ? Map.of() : Map.copyOf(responses);
        recommendedServices = recommendedServices == null
                ? List.of()
                : recommendedServices.stream().map(ProvisioningDecisionContext::normalize).toList();
        businessName = businessName == null || businessName.isBlank() ? "Client" : businessName.trim();
    }

    /**
     * Checks whether this is general.
     *
     * @return true if the value is general
     */
    public boolean isGeneral() {
        return "general".equals(formKey);
    }

    /**
     * Determines whether recommends applies.
     *
     * @param track service track within the delivery plan
     * @return true when the check succeeds
     */
    public boolean recommends(ProvisioningTrack track) {
        return recommendedServices.contains(track.serviceKey());
    }

    /**
     * Looks up alternative discovery keys in order, joining and normalizing collection answers when present.
     *
     * @param keys keys
     * @return the resulting text
     */
    public String value(String... keys) {
        for (String key : keys) {
            Object raw = responses.get(key);
            if (raw instanceof Collection<?> values) {
                String joined = values.stream()
                        .map(String::valueOf)
                        .map(ProvisioningDecisionContext::normalize)
                        .filter(value -> !value.isBlank())
                        .reduce((left, right) -> left + "," + right)
                        .orElse("");
                if (!joined.isBlank()) {
                    return joined;
                }
            } else if (raw != null) {
                String value = normalize(String.valueOf(raw));
                if (!value.isBlank()) {
                    return value;
                }
            }
        }
        return "";
    }

    /**
     * Determines whether equals any applies.
     *
     * @param key key used to identify the item
     * @param candidates candidates
     * @return true when the check succeeds
     */
    public boolean equalsAny(String key, String... candidates) {
        String actual = value(key);
        for (String candidate : candidates) {
            if (actual.equals(normalize(candidate))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether contains applies.
     *
     * @param key key used to identify the item
     * @param fragments fragments
     * @return true when the check succeeds
     */
    public boolean contains(String key, String... fragments) {
        String actual = value(key);
        for (String fragment : fragments) {
            if (actual.contains(normalize(fragment))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether yes applies.
     *
     * @param keys keys
     * @return true when the check succeeds
     */
    public boolean yes(String... keys) {
        for (String key : keys) {
            if (equalsAny(key, "yes", "true", "required", "enabled")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reads digits from the discovery value and uses zero when it contains none.
     *
     * @param key key used to identify the item
     * @return the value produced by this operation
     */
    public int integer(String key) {
        String digits = value(key).replaceAll("[^0-9]", "");
        return digits.isBlank() ? 0 : Integer.parseInt(digits);
    }

    /**
     * Trims whitespace and lowercases the value for comparison, using an empty string for missing input. Uses locale-independent case normalization.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
