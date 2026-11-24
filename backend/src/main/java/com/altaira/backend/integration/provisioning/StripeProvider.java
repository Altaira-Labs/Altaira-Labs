package com.altaira.backend.integration.provisioning;

import com.altaira.backend.model.AutomationLevel;
import org.springframework.stereotype.Component;

/** Identifies Stripe provider and its automation level when preparing a provisioning preview. */
@Component
public class StripeProvider extends DryRunProvisioningProvider {
    /** Registers Stripe under {@code STRIPE} with automation level {@code A2}. */
    public StripeProvider() {
        super("STRIPE", "Stripe", AutomationLevel.A2);
    }
}
