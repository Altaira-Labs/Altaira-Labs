package com.altaira.backend.integration.provisioning;

import com.altaira.backend.model.AutomationLevel;
import org.springframework.stereotype.Component;

/** Identifies calendar provider and its automation level when preparing a provisioning preview. */
@Component
public class CalendarProvider extends DryRunProvisioningProvider {
    /** Registers Google Calendar under {@code CALENDAR} with automation level {@code A2}. */
    public CalendarProvider() {
        super("CALENDAR", "Google Calendar", AutomationLevel.A2);
    }
}
