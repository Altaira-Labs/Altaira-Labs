package com.altaira.backend.integration.provisioning;

import com.altaira.backend.model.AutomationLevel;
import org.springframework.stereotype.Component;

/** Identifies Jira provider and its automation level when preparing a provisioning preview. */
@Component
public class JiraProvider extends DryRunProvisioningProvider {
    /** Registers Jira under {@code JIRA} with automation level {@code A3}. */
    public JiraProvider() {
        super("JIRA", "Jira", AutomationLevel.A3);
    }
}
