package com.altaira.backend.integration.provisioning;

import com.altaira.backend.model.ProvisioningStepStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** Exercises provisioning provider registry behavior with JUnit. */
class ProvisioningProviderRegistryTests {

    /** Regression case: returns typed dry run blocked and manual results without external resources. */
    @Test
    void returnsTypedDryRunBlockedAndManualResultsWithoutExternalResources() {
        ProvisioningProviderRegistry registry = new ProvisioningProviderRegistry(List.of(
                new JiraProvider(),
                new DriveProvider(),
                new CalendarProvider(),
                new GitHubProvider()
        ));
        ProvisioningRequest ready = request(true, false, null);
        ProvisioningRequest missingGitHub = request(
                false,
                true,
                "GitHub App credentials are not configured."
        );

        ProvisioningResult jira = registry.prepare("JIRA", ready);
        ProvisioningResult drive = registry.prepare("DRIVE", ready);
        ProvisioningResult calendar = registry.prepare("CALENDAR", ready);
        ProvisioningResult github = registry.prepare("GITHUB", missingGitHub);
        ProvisioningResult unknown = registry.prepare("UNREGISTERED_TOOL", ready);

        assertThat(List.of(jira, drive, calendar))
                .allMatch(result -> result.status() == ProvisioningStepStatus.DRY_RUN)
                .allMatch(result -> !result.manualActionRequired());
        assertThat(github.status()).isEqualTo(ProvisioningStepStatus.BLOCKED);
        assertThat(github.manualActionRequired()).isTrue();
        assertThat(github.safeError()).isEqualTo("GitHub App credentials are not configured.");
        assertThat(unknown.status()).isEqualTo(ProvisioningStepStatus.DRY_RUN);
        assertThat(unknown.manualActionRequired()).isTrue();
        assertThat(unknown.safeError()).contains("No execution adapter");
        assertThat(List.of(jira, drive, calendar, github, unknown))
                .allMatch(result -> result.externalResourceId() == null && result.externalUrl() == null)
                .allMatch(result -> Boolean.TRUE.equals(result.inputSummary().get("dryRun")));
    }

    /**
     * Constructs the example request accepted by the component under test.
     *
     * @param credentialsConfigured credentials configured
     * @param blockWhenCredentialsMissing block when credentials missing
     * @param missingCredentialsMessage missing credentials message
     * @return the provisioning request
     */
    private ProvisioningRequest request(
            boolean credentialsConfigured,
            boolean blockWhenCredentialsMissing,
            String missingCredentialsMessage
    ) {
        return new ProvisioningRequest(
                "WEB",
                "prepare",
                Map.of("dryRun", true, "externalWrite", false),
                credentialsConfigured,
                blockWhenCredentialsMissing,
                missingCredentialsMessage
        );
    }
}
