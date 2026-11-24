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

/** HTTP endpoints for workspace task under {@code /api/v1/workspace-tasks}. */
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
     * GET {@code /api/v1/workspace-tasks/admin}. Lists for admin. Internal access is checked before delegation.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the workspace task response items
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
     * GET {@code /api/v1/workspace-tasks/admin/clients/{clientId}/preview}. Lists for admin client preview. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the workspace task response items
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
     * POST {@code /api/v1/workspace-tasks/admin}. Creates for admin. Internal access is checked before delegation.
     *
     * @param request input carrying create workspace task
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the workspace task response
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
     * PATCH {@code /api/v1/workspace-tasks/admin/{taskId}}. Updates for admin. Internal access is checked before delegation.
     *
     * @param taskId identifier of the task being acted on
     * @param request input carrying update workspace task
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the workspace task response
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
     * DELETE {@code /api/v1/workspace-tasks/admin/{taskId}}. Deletes for admin. Internal access is checked before delegation.
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
     * GET {@code /api/v1/workspace-tasks/client/me}. Lists for client. Membership is checked before the workspace operation.
     *
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the workspace task response items
     */
    @GetMapping("/client/me")
    public List<WorkspaceTaskResponse> listForClient(
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        return workspaceTaskService.listForClient(context);
    }

    /**
     * PATCH {@code /api/v1/workspace-tasks/client/{taskId}/status}. Updates status for client. Membership is checked before the workspace operation.
     *
     * @param taskId identifier of the task being acted on
     * @param clientSessionToken session credential supplied for the client workspace
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
