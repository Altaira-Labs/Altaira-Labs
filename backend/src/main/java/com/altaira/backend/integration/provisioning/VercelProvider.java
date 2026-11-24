package com.altaira.backend.integration.provisioning;

import com.altaira.backend.model.AutomationLevel;
import org.springframework.stereotype.Component;

/** Identifies vercel provider and its automation level when preparing a provisioning preview. */
@Component
public class VercelProvider extends DryRunProvisioningProvider {
    /** Registers Vercel under {@code VERCEL} with automation level {@code A3}. */
    public VercelProvider() {
        super("VERCEL", "Vercel", AutomationLevel.A3);
    }
}
