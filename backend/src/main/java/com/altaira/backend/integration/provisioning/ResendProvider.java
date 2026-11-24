package com.altaira.backend.integration.provisioning;

import com.altaira.backend.model.AutomationLevel;
import org.springframework.stereotype.Component;

/** Identifies resend provider and its automation level when preparing a provisioning preview. */
@Component
public class ResendProvider extends DryRunProvisioningProvider {
    /** Registers Resend under {@code RESEND} with automation level {@code A1}. */
    public ResendProvider() {
        super("RESEND", "Resend", AutomationLevel.A1);
    }
}
