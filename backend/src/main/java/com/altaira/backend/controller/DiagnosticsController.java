package com.altaira.backend.controller;

import com.altaira.backend.security.AdminAccessService;
import com.altaira.backend.service.LeadNotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** HTTP endpoints for diagnostics under {@code /api/v1/diagnostics}. */
@RestController
@RequestMapping("/api/v1/diagnostics")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class DiagnosticsController {

    /** Lead notification operations used by this component. */
    private final LeadNotificationService leadNotificationService;
    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;

    /**
     * Creates the diagnostics controller with its injected collaborators.
     *
     * @param leadNotificationService lead notification operations used by this component
     * @param adminAccessService admin access operations used by this component
     */
    public DiagnosticsController(LeadNotificationService leadNotificationService, AdminAccessService adminAccessService) {
        this.leadNotificationService = leadNotificationService;
        this.adminAccessService = adminAccessService;
    }

    /**
     * GET {@code /api/v1/diagnostics/email}. Email diagnostics. Internal access is checked before delegation.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the resulting key–value pairs
     */
    @GetMapping("/email")
    public Map<String, Object> emailDiagnostics(
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return leadNotificationService.getDiagnostics();
    }
}
