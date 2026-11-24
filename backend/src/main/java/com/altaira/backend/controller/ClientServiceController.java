package com.altaira.backend.controller;

import com.altaira.backend.dto.clientservice.ClientServiceResponse;
import com.altaira.backend.dto.clientservice.UpdateClientServiceStatusRequest;
import com.altaira.backend.security.AdminAccessService;
import com.altaira.backend.service.ClientServiceAssignmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/** HTTP endpoints for client service under {@code /api/v1/client-services}. */
@RestController
@RequestMapping("/api/v1/client-services")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class ClientServiceController {

    /** Client service assignment operations used by this component. */
    private final ClientServiceAssignmentService clientServiceAssignmentService;
    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;

    /**
     * Creates the client service controller with its injected collaborators.
     *
     * @param clientServiceAssignmentService client service assignment operations used by this component
     * @param adminAccessService admin access operations used by this component
     */
    public ClientServiceController(
            ClientServiceAssignmentService clientServiceAssignmentService,
            AdminAccessService adminAccessService
    ) {
        this.clientServiceAssignmentService = clientServiceAssignmentService;
        this.adminAccessService = adminAccessService;
    }

    /**
     * PATCH {@code /api/v1/client-services/{id}/status}. Updates status. Internal access is checked before delegation.
     *
     * @param id unique identifier of the record
     * @param request input carrying update client service status
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client service response
     */
    @PatchMapping("/{id}/status")
    public ClientServiceResponse updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateClientServiceStatusRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientServiceAssignmentService.updateStatus(id, request.getStatus());
    }
}
