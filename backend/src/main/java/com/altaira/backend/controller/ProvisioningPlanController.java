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

/**
 * Staff API for reviewing delivery plans and requesting provider operations. Drafting a plan records a
 * preview. Confirmed GitHub and Jira execution use separate routes and are subject to the provider
 * services' activation checks.
 */
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
     * POST {@code /api/v1/leads/{leadId}/provisioning-plans/dry-run}.
     * Creates or refreshes a draft delivery plan from a saved assessment for internal review.
     *
     * @param leadId identifier of the source sales enquiry
     * @param request optional assessment to use when generating the draft
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the saved preview, including the proposed delivery tracks, tools and manual steps
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
     * GET {@code /api/v1/leads/{leadId}/provisioning-plans}.
     * Lists the saved plans associated with one sales enquiry.
     *
     * @param leadId identifier of the source sales enquiry
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return plans for that lead, most recently updated first
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
     * GET {@code /api/v1/provisioning-plans/{planId}}.
     * Loads the saved delivery preview and its decisions for staff review.
     *
     * @param planId identifier of the saved provisioning plan
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the detailed plan for the requested identifier
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
     * PATCH {@code /api/v1/provisioning-plans/{planId}/status}.
     * Sets the review state of a dry-run plan to draft, awaiting approval or approved.
     *
     * @param planId identifier of the saved provisioning plan
     * @param request the requested dry-run review state
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the plan with its saved status
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
     * POST {@code /api/v1/provisioning-plans/{planId}/providers/github/repository}.
     * Requests the confirmed GitHub repository operation for an approved plan with an activated commercial
     * flow.
     *
     * @param planId identifier of the saved provisioning plan
     * @param request explicit staff confirmation for the repository operation
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the execution status, whether a repository was created or reused, and its reference when available
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
     * POST {@code /api/v1/provisioning-plans/{planId}/providers/jira/issues}.
     * Requests confirmed creation of Jira issues from the approved plan's dry-run tasks after commercial
     * activation.
     *
     * @param planId identifier of the saved provisioning plan
     * @param request explicit staff confirmation for the issue operation
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the created, reused and failed counts with the outcome for each reported issue
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
