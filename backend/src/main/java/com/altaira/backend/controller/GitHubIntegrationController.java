package com.altaira.backend.controller;

import com.altaira.backend.dto.github.GitHubIntegrationStatusResponse;
import com.altaira.backend.dto.github.GitHubProvisioningDryRunRequest;
import com.altaira.backend.dto.github.GitHubProvisioningPlanResponse;
import com.altaira.backend.integration.github.GitHubIntegrationStatusService;
import com.altaira.backend.integration.github.GitHubProvisioningService;
import com.altaira.backend.security.AdminAccessService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** HTTP endpoints for GitHub integration under {@code /api/v1/integrations/github}. */
@RestController
@RequestMapping("/api/v1/integrations/github")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class GitHubIntegrationController {

    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;
    /** Status operations used by this component. */
    private final GitHubIntegrationStatusService statusService;
    /** Provisioning operations used by this component. */
    private final GitHubProvisioningService provisioningService;

    /**
     * Creates the GitHub integration controller with its injected collaborators.
     *
     * @param adminAccessService admin access operations used by this component
     * @param statusService status operations used by this component
     * @param provisioningService provisioning operations used by this component
     */
    public GitHubIntegrationController(
            AdminAccessService adminAccessService,
            GitHubIntegrationStatusService statusService,
            GitHubProvisioningService provisioningService
    ) {
        this.adminAccessService = adminAccessService;
        this.statusService = statusService;
        this.provisioningService = provisioningService;
    }

    /**
     * GET {@code /api/v1/integrations/github/status}. Status. Internal access is checked before delegation.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the GitHub integration status response
     */
    @GetMapping("/status")
    public GitHubIntegrationStatusResponse status(
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return statusService.getStatus();
    }

    /**
     * POST {@code /api/v1/integrations/github/provisioning/dry-run}. Dry run. Internal access is checked before delegation.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @param request input carrying GitHub provisioning dry run
     * @return the GitHub provisioning plan response
     */
    @PostMapping("/provisioning/dry-run")
    public GitHubProvisioningPlanResponse dryRun(
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken,
            @Valid @RequestBody(required = false) GitHubProvisioningDryRunRequest request
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return provisioningService.dryRun(request);
    }
}
