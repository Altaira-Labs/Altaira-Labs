package com.altaira.backend.integration.provisioning;

import com.altaira.backend.model.AutomationLevel;
import org.springframework.stereotype.Component;

/** Identifies GitHub provider and its automation level when preparing a provisioning preview. */
@Component
public class GitHubProvider extends DryRunProvisioningProvider {
    /** Registers GitHub under {@code GITHUB} with automation level {@code A3}. */
    public GitHubProvider() {
        super("GITHUB", "GitHub", AutomationLevel.A3);
    }
}
