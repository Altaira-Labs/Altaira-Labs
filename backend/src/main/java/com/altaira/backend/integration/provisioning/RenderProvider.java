package com.altaira.backend.integration.provisioning;

import com.altaira.backend.model.AutomationLevel;
import org.springframework.stereotype.Component;

/** Identifies render provider and its automation level when preparing a provisioning preview. */
@Component
public class RenderProvider extends DryRunProvisioningProvider {
    /** Registers Render under {@code RENDER} with automation level {@code A3}. */
    public RenderProvider() {
        super("RENDER", "Render", AutomationLevel.A3);
    }
}
