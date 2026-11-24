package com.altaira.backend.integration.provisioning;

import com.altaira.backend.model.AutomationLevel;
import org.springframework.stereotype.Component;

/** Identifies neon provider and its automation level when preparing a provisioning preview. */
@Component
public class NeonProvider extends DryRunProvisioningProvider {
    /** Registers Neon PostgreSQL under {@code NEON} with automation level {@code A3}. */
    public NeonProvider() {
        super("NEON", "Neon PostgreSQL", AutomationLevel.A3);
    }
}
