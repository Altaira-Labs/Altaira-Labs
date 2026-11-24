package com.altaira.backend.integration.provisioning;

import com.altaira.backend.model.AutomationLevel;
import org.springframework.stereotype.Component;

/** Identifies drive provider and its automation level when preparing a provisioning preview. */
@Component
public class DriveProvider extends DryRunProvisioningProvider {
    /** Registers Google Drive under {@code DRIVE} with automation level {@code A3}. */
    public DriveProvider() {
        super("DRIVE", "Google Drive", AutomationLevel.A3);
    }
}
