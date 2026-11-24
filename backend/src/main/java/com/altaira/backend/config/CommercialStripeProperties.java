package com.altaira.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Reads checkout configuration and enforces the settings for mock and Stripe test-mode payments. */
@Component
public class CommercialStripeProperties {
    /** Whether the capability is enabled. */
    private final boolean enabled;
    /** Operating mode configured for this integration. */
    private final String mode;
    /** Key identifying the secret. */
    private final String secretKey;
    /** Webhook secret. */
    private final String webhookSecret;
    /** URL for success. */
    private final String successUrl;
    /** URL for cancel. */
    private final String cancelUrl;
    /** Whether mock confirmation is enabled. */
    private final boolean mockConfirmationEnabled;

    /**
     * Constructs {@code CommercialStripeProperties} using the supplied enabled, mode, secret key and remaining fields.
     *
     * @param enabled whether the capability is enabled
     * @param mode operating mode configured for this integration
     * @param secretKey key identifying the secret
     * @param webhookSecret webhook secret
     * @param successUrl URL for success
     * @param cancelUrl URL for cancel
     * @param mockConfirmationEnabled whether mock confirmation is enabled
     */
    public CommercialStripeProperties(
            @Value("${altaira.commercial.stripe.enabled:false}") boolean enabled,
            @Value("${altaira.commercial.stripe.mode:mock}") String mode,
            @Value("${altaira.commercial.stripe.secret-key:}") String secretKey,
            @Value("${altaira.commercial.stripe.webhook-secret:}") String webhookSecret,
            @Value("${altaira.commercial.stripe.success-url:http://localhost:3000/payment/success}") String successUrl,
            @Value("${altaira.commercial.stripe.cancel-url:http://localhost:3000/payment/cancel}") String cancelUrl,
            @Value("${altaira.commercial.stripe.mock-confirmation-enabled:false}") boolean mockConfirmationEnabled
    ) {
        this.enabled = enabled;
        this.mode = clean(mode).isBlank() ? "mock" : clean(mode).toLowerCase();
        this.secretKey = clean(secretKey);
        this.webhookSecret = clean(webhookSecret);
        this.successUrl = clean(successUrl);
        this.cancelUrl = clean(cancelUrl);
        this.mockConfirmationEnabled = mockConfirmationEnabled;
        validateSafety();
    }

    /**
     * Validates safety.
     *
     * @throws IllegalStateException when validation reports “Live Stripe keys are not allowed in this controlled commercial flow” or “STRIPE_CHECKOUT_MODE must be mock or test”
     */
    private void validateSafety() {
        if (secretKey.startsWith("sk_live_")) {
            throw new IllegalStateException("Live Stripe keys are not allowed in this controlled commercial flow");
        }
        if (!("mock".equals(mode) || "test".equals(mode))) {
            throw new IllegalStateException("STRIPE_CHECKOUT_MODE must be mock or test");
        }
        if (enabled && "test".equals(mode) && !secretKey.startsWith("sk_test_")) {
            throw new IllegalStateException("Stripe test mode requires an sk_test_ key");
        }
    }

    /**
     * Determines whether use real test API applies.
     *
     * @return true when the check succeeds
     */
    public boolean useRealTestApi() { return enabled && "test".equals(mode); }
    /**
     * Checks whether this is mock.
     *
     * @return true if the value is mock
     */
    public boolean isMock() { return !useRealTestApi(); }
    /**
     * Returns the operating mode configured for this integration for this commercial Stripe properties.
     *
     * @return operating mode configured for this integration
     */
    public String mode() { return mode; }
    /**
     * Returns the key identifying the secret for this commercial Stripe properties.
     *
     * @return key identifying the secret
     */
    public String secretKey() { return secretKey; }
    /**
     * Returns the webhook secret for this commercial Stripe properties.
     *
     * @return webhook secret
     */
    public String webhookSecret() { return webhookSecret; }
    /**
     * Returns the URL for success for this commercial Stripe properties.
     *
     * @return URL for success
     */
    public String successUrl() { return successUrl; }
    /**
     * Returns the URL for cancel for this commercial Stripe properties.
     *
     * @return URL for cancel
     */
    public String cancelUrl() { return cancelUrl; }
    /**
     * Returns the whether mock confirmation is enabled for this commercial Stripe properties.
     *
     * @return whether mock confirmation is enabled
     */
    public boolean mockConfirmationEnabled() { return mockConfirmationEnabled; }
    /**
     * Trims surrounding whitespace. Absent values use an empty string.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private static String clean(String value) { return value == null ? "" : value.trim(); }
}
