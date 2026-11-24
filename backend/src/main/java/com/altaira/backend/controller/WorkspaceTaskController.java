package com.altaira.backend.controller;

import com.altaira.backend.dto.workspacetask.CreateWorkspaceTaskRequest;
import com.altaira.backend.dto.workspacetask.UpdateWorkspaceTaskRequest;
import com.altaira.backend.dto.workspacetask.WorkspaceTaskResponse;
import com.altaira.backend.security.AdminAccessService;
import com.altaira.backend.security.ClientAccessService;
import com.altaira.backend.service.WorkspaceTaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Exposes the staff task board and the customer's read-only task list under {@code
 * /api/v1/workspace-tasks}. Staff can manage tasks and preview what a specific customer can see.
 */
@RestController
@RequestMapping("/api/v1/workspace-tasks")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:3001",
        "https://altairalabs.vercel.app"
})
public class WorkspaceTaskController {

    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;
    /** Client access operations used by this component. */
    private final ClientAccessService clientAccessService;
    /** Workspace task operations used by this component. */
    private final WorkspaceTaskService workspaceTaskService;

    /**
     * Creates the workspace task controller with its injected collaborators.
     *
     * @param adminAccessService admin access operations used by this component
     * @param clientAccessService client access operations used by this component
     * @param workspaceTaskService workspace task operations used by this component
     */
    public WorkspaceTaskController(
            AdminAccessService adminAccessService,
            ClientAccessService clientAccessService,
            WorkspaceTaskService workspaceTaskService
    ) {
        this.adminAccessService = adminAccessService;
        this.clientAccessService = clientAccessService;
        this.workspaceTaskService = workspaceTaskService;
    }

    /**
     * GET {@code /api/v1/workspace-tasks/admin}.
     * Lists work across customer accounts for the staff dashboard.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return tasks ordered by due date, with synthetic test accounts omitted
     */
    @GetMapping("/admin")
    public List<WorkspaceTaskResponse> listForAdmin(
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return workspaceTaskService.listForAdmin();
    }

    /**
     * GET {@code /api/v1/workspace-tasks/admin/clients/{clientId}/preview}.
     * Lets staff preview the task list visible to one customer.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return only client-visible tasks belonging to the selected customer
     */
    @GetMapping("/admin/clients/{clientId}/preview")
    public List<WorkspaceTaskResponse> listForAdminClientPreview(
            @PathVariable UUID clientId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return workspaceTaskService.listForAdminClientPreview(clientId);
    }

    /**
     * POST {@code /api/v1/workspace-tasks/admin}.
     * Adds a task to a customer's workspace on behalf of an authorized administrator.
     *
     * @param request target customer and initial task details
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the saved task with its visibility, owner role and deadline
     */
    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkspaceTaskResponse createForAdmin(
            @Valid @RequestBody CreateWorkspaceTaskRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        var adminUser = adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return workspaceTaskService.createForAdmin(request, adminUser);
    }

    /**
     * PATCH {@code /api/v1/workspace-tasks/admin/{taskId}}.
     * Applies the submitted changes to a task in the staff workspace.
     *
     * @param taskId identifier of the task being acted on
     * @param request task changes, including optional deadline removal
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the task after its changes have been saved
     */
    @PatchMapping("/admin/{taskId}")
    public WorkspaceTaskResponse updateForAdmin(
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateWorkspaceTaskRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return workspaceTaskService.updateForAdmin(taskId, request);
    }

    /**
     * DELETE {@code /api/v1/workspace-tasks/admin/{taskId}}.
     * Deletes a task for an authorized administrator and returns HTTP 204.
     *
     * @param taskId identifier of the task being acted on
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     */
    @DeleteMapping("/admin/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteForAdmin(
            @PathVariable UUID taskId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        workspaceTaskService.deleteForAdmin(taskId);
    }

    /**
     * GET {@code /api/v1/workspace-tasks/client/me}.
     * Loads the task list associated with the authenticated customer's workspace.
     *
     * @param clientSessionToken session credential supplied for the client workspace
     * @return client-visible tasks, with undated tasks last
     */
    @GetMapping("/client/me")
    public List<WorkspaceTaskResponse> listForClient(
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        return workspaceTaskService.listForClient(context);
    }

    /**
     * PATCH {@code /api/v1/workspace-tasks/client/{taskId}/status}.
     * Authenticates the client session and rejects direct task status changes with HTTP 403. Clients can
     * submit a service-track suggestion for review.
     *
     * @param taskId task named in the rejected update
     * @param clientSessionToken session credential supplied for the client workspace
     * @throws org.springframework.web.server.ResponseStatusException if the client session is rejected or direct modification is forbidden
     */
    @PatchMapping("/client/{taskId}/status")
    public void updateStatusForClient(
            @PathVariable UUID taskId,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        clientAccessService.requireClientAccess(clientSessionToken);
        throw new org.springframework.web.server.ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Client task management is read-only. Submit a service-track suggestion for Altaira review."
        );
    }
}
