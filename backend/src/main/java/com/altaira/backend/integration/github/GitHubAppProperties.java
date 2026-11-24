package com.altaira.backend.integration.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

/** Binds server-side GitHub App and allowed-organization settings. */
@Component
public class GitHubAppProperties {

    /** Whether the capability is enabled. */
    private final boolean enabled;
    /** Whether provisioning is enabled. */
    private final boolean provisioningEnabled;
    /** Whether the operation is being simulated. */
    private final boolean dryRun;
    /** Org. */
    private final String org;
    /** URL for api base. */
    private final String apiBaseUrl;
    /** Identifier of the app. */
    private final String appId;
    /** Identifier of the app client. */
    private final String appClientId;
    /** Identifier of the installation. */
    private final String installationId;
    /** Private key base64. */
    private final String privateKeyBase64;
    /** Webhook secret. */
    private final String webhookSecret;

    /**
     * Constructs {@code GitHubAppProperties} using the supplied enabled, provisioning enabled, dry run and remaining fields.
     *
     * @param enabled whether the capability is enabled
     * @param provisioningEnabled whether provisioning is enabled
     * @param dryRun whether the operation is being simulated
     * @param org org
     * @param apiBaseUrl URL for api base
     * @param appId identifier of the app
     * @param appClientId identifier of the app client
     * @param installationId identifier of the installation
     * @param privateKeyBase64 private key base64
     * @param webhookSecret webhook secret
     */
    public GitHubAppProperties(
            @Value("${altaira.github.app.enabled:false}") boolean enabled,
            @Value("${altaira.github.provisioning.enabled:false}") boolean provisioningEnabled,
            @Value("${altaira.github.dry-run:true}") boolean dryRun,
            @Value("${altaira.github.org:Altaira-Labs}") String org,
            @Value("${altaira.github.api-base-url:https://api.github.com}") String apiBaseUrl,
            @Value("${altaira.github.app.id:}") String appId,
            @Value("${altaira.github.app.client-id:}") String appClientId,
            @Value("${altaira.github.app.installation-id:}") String installationId,
            @Value("${altaira.github.app.private-key-base64:}") String privateKeyBase64,
            @Value("${altaira.github.app.webhook-secret:}") String webhookSecret
    ) {
        this.enabled = enabled;
        this.provisioningEnabled = provisioningEnabled;
        this.dryRun = dryRun;
        this.org = trimOrDefault(org, "Altaira-Labs");
        this.apiBaseUrl = trimOrDefault(apiBaseUrl, "https://api.github.com");
        this.appId = trim(appId);
        this.appClientId = trim(appClientId);
        this.installationId = trim(installationId);
        this.privateKeyBase64 = trim(privateKeyBase64);
        this.webhookSecret = trim(webhookSecret);
    }

    /**
     * Returns the whether the capability is enabled for this GitHub app properties.
     *
     * @return whether the capability is enabled
     */
    public boolean enabled() {
        return enabled;
    }

    /**
     * Returns the whether provisioning is enabled for this GitHub app properties.
     *
     * @return whether provisioning is enabled
     */
    public boolean provisioningEnabled() {
        return provisioningEnabled;
    }

    /**
     * Returns the whether the operation is being simulated for this GitHub app properties.
     *
     * @return whether the operation is being simulated
     */
    public boolean dryRun() {
        return dryRun;
    }

    /**
     * Returns the org for this GitHub app properties.
     *
     * @return org
     */
    public String org() {
        return org;
    }

    /**
     * Returns the identifier of the app for this GitHub app properties.
     *
     * @return identifier of the app
     */
    public String appId() {
        return appId;
    }

    /**
     * Returns the identifier of the app client for this GitHub app properties.
     *
     * @return identifier of the app client
     */
    public String appClientId() {
        return appClientId;
    }

    /**
     * Returns the identifier of the installation for this GitHub app properties.
     *
     * @return identifier of the installation
     */
    public String installationId() {
        return installationId;
    }

    /**
     * Returns the webhook secret for this GitHub app properties.
     *
     * @return webhook secret
     */
    public String webhookSecretConfiguredValue() {
        return webhookSecret;
    }

    /**
     * Returns the private key base64 for this GitHub app properties.
     *
     * @return private key base64
     */
    String privateKeyBase64ConfiguredValue() {
        return privateKeyBase64;
    }

    /**
     * Determines whether app configured applies.
     *
     * @return true when the check succeeds
     */
    public boolean appConfigured() {
        return !appId.isBlank() && !privateKeyBase64.isBlank();
    }

    /**
     * Determines whether installation id configured applies.
     *
     * @return true when the check succeeds
     */
    public boolean installationIdConfigured() {
        return !installationId.isBlank();
    }

    /**
     * Resolves the resource path against the configured provider API.
     *
     * @param path path used to locate the target
     * @return the uri
     */
    public URI apiUri(String path) {
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        try {
            return new URI(apiBaseUrl.replaceAll("/+$", "") + normalizedPath);
        } catch (URISyntaxException ex) {
            throw GitHubIntegrationException.safe("GitHub API base URL is invalid. Check GITHUB_API_BASE_URL.");
        }
    }

    /**
     * Checks whether the operation can attempt connection.
     *
     * @return true if the caller can attempt connection
     */
    public boolean canAttemptConnection() {
        return enabled && appConfigured() && installationIdConfigured();
    }

    /**
     * Checks whether the operation can mutate resources.
     *
     * @return true if the caller can mutate resources
     */
    public boolean canMutateResources() {
        return enabled && provisioningEnabled && !dryRun;
    }

    /**
     * Trims surrounding whitespace. Absent values use an empty string.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Trims surrounding whitespace.
     *
     * @param value stored or supplied representation
     * @param defaultValue default value
     * @return the resulting text
     */
    private static String trimOrDefault(String value, String defaultValue) {
        String trimmed = trim(value);
        return trimmed.isBlank() ? defaultValue : trimmed;
    }
}
