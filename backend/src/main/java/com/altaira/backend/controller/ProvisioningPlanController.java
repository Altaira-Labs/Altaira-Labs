package com.altaira.backend.controller;

import com.altaira.backend.dto.provisioning.CreateProvisioningDryRunRequest;
import com.altaira.backend.dto.provisioning.ProvisioningPlanResponse;
import com.altaira.backend.dto.provisioning.UpdateProvisioningPlanStatusRequest;
import com.altaira.backend.dto.github.ExecuteGitHubRepositoryRequest;
import com.altaira.backend.dto.github.GitHubRepositoryExecutionResponse;
import com.altaira.backend.dto.jira.ExecuteJiraIssuesRequest;
import com.altaira.backend.dto.jira.JiraIssueExecutionResponse;
import com.altaira.backend.security.AdminAccessService;
import com.altaira.backend.service.GitHubProvisioningExecutionService;
import com.altaira.backend.service.JiraProvisioningExecutionService;
import com.altaira.backend.service.ProvisioningPlanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** HTTP endpoints for provisioning plan under {@code /api/v1}. */
@RestController
@RequestMapping("/api/v1")
public class ProvisioningPlanController {

    /** Provisioning plan operations used by this component. */
    private final ProvisioningPlanService provisioningPlanService;
    /** GitHub provisioning execution operations used by this component. */
    private final GitHubProvisioningExecutionService githubProvisioningExecutionService;
    /** Jira provisioning execution operations used by this component. */
    private final JiraProvisioningExecutionService jiraProvisioningExecutionService;
    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;

    /**
     * Creates the provisioning plan controller with its injected collaborators.
     *
     * @param provisioningPlanService provisioning plan operations used by this component
     * @param githubProvisioningExecutionService GitHub provisioning execution operations used by this component
     * @param jiraProvisioningExecutionService Jira provisioning execution operations used by this component
     * @param adminAccessService admin access operations used by this component
     */
    public ProvisioningPlanController(
            ProvisioningPlanService provisioningPlanService,
            GitHubProvisioningExecutionService githubProvisioningExecutionService,
            JiraProvisioningExecutionService jiraProvisioningExecutionService,
            AdminAccessService adminAccessService
    ) {
        this.provisioningPlanService = provisioningPlanService;
        this.githubProvisioningExecutionService = githubProvisioningExecutionService;
        this.jiraProvisioningExecutionService = jiraProvisioningExecutionService;
        this.adminAccessService = adminAccessService;
    }

    /**
     * POST {@code /api/v1/leads/{leadId}/provisioning-plans/dry-run}. Generates dry run. Internal access is checked before delegation.
     *
     * @param leadId identifier of the source sales enquiry
     * @param request input carrying create provisioning dry run
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the provisioning plan response
     */
    @PostMapping("/leads/{leadId}/provisioning-plans/dry-run")
    public ProvisioningPlanResponse generateDryRun(
            @PathVariable UUID leadId,
            @RequestBody(required = false) CreateProvisioningDryRunRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return provisioningPlanService.generateDryRun(leadId, request);
    }

    /**
     * GET {@code /api/v1/leads/{leadId}/provisioning-plans}. Retrieves plans. Internal access is checked before delegation.
     *
     * @param leadId identifier of the source sales enquiry
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the provisioning plan response items
     */
    @GetMapping("/leads/{leadId}/provisioning-plans")
    public List<ProvisioningPlanResponse> getPlans(
            @PathVariable UUID leadId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return provisioningPlanService.getPlansForLead(leadId);
    }

    /**
     * GET {@code /api/v1/provisioning-plans/{planId}}. Retrieves plan. Internal access is checked before delegation.
     *
     * @param planId identifier of the saved provisioning plan
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return proposed or saved delivery plan
     */
    @GetMapping("/provisioning-plans/{planId}")
    public ProvisioningPlanResponse getPlan(
            @PathVariable UUID planId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return provisioningPlanService.getPlan(planId);
    }

    /**
     * PATCH {@code /api/v1/provisioning-plans/{planId}/status}. Updates status. Internal access is checked before delegation.
     *
     * @param planId identifier of the saved provisioning plan
     * @param request input carrying update provisioning plan status
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the provisioning plan response
     */
    @PatchMapping("/provisioning-plans/{planId}/status")
    public ProvisioningPlanResponse updateStatus(
            @PathVariable UUID planId,
            @Valid @RequestBody UpdateProvisioningPlanStatusRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return provisioningPlanService.updateStatus(planId, request.getStatus());
    }

    /**
     * POST {@code /api/v1/provisioning-plans/{planId}/providers/github/repository}. Executes GitHub repository. Internal access is checked before delegation.
     *
     * @param planId identifier of the saved provisioning plan
     * @param request input carrying execute GitHub repository
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the GitHub repository execution response
     */
    @PostMapping("/provisioning-plans/{planId}/providers/github/repository")
    public GitHubRepositoryExecutionResponse executeGitHubRepository(
            @PathVariable UUID planId,
            @Valid @RequestBody ExecuteGitHubRepositoryRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return githubProvisioningExecutionService.executePrivateRepository(planId, request);
    }

    /**
     * POST {@code /api/v1/provisioning-plans/{planId}/providers/jira/issues}. Executes Jira issues. Internal access is checked before delegation.
     *
     * @param planId identifier of the saved provisioning plan
     * @param request input carrying execute Jira issues
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the Jira issue execution response
     */
    @PostMapping("/provisioning-plans/{planId}/providers/jira/issues")
    public JiraIssueExecutionResponse executeJiraIssues(
            @PathVariable UUID planId,
            @Valid @RequestBody ExecuteJiraIssuesRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return jiraProvisioningExecutionService.executeIssues(planId, request);
    }
}
