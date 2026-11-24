package com.altaira.backend.controller;

import com.altaira.backend.dto.clientcrm.ClientCrmWebhookTokenResponse;
import com.altaira.backend.dto.clientcrm.CreateClientCrmWebhookTokenRequest;
import com.altaira.backend.security.AdminAccessService;
import com.altaira.backend.service.ClientCrmWebhookTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * HTTP endpoints for client CRM webhook admin under {@code /api/v1/client-crm/admin/clients/{clientId}/webhook-tokens}.
 */
@RestController
@RequestMapping("/api/v1/client-crm/admin/clients/{clientId}/webhook-tokens")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class ClientCrmWebhookAdminController {

    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;
    /** Token operations used by this component. */
    private final ClientCrmWebhookTokenService tokenService;

    /**
     * Creates the client CRM webhook admin controller with its injected collaborators.
     *
     * @param adminAccessService admin access operations used by this component
     * @param tokenService token operations used by this component
     */
    public ClientCrmWebhookAdminController(
            AdminAccessService adminAccessService,
            ClientCrmWebhookTokenService tokenService
    ) {
        this.adminAccessService = adminAccessService;
        this.tokenService = tokenService;
    }

    /**
     * GET {@code /api/v1/client-crm/admin/clients/{clientId}/webhook-tokens}. Lists tokens. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client CRM webhook token response items
     */
    @GetMapping
    public List<ClientCrmWebhookTokenResponse> listTokens(
            @PathVariable UUID clientId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return tokenService.listTokens(clientId);
    }

    /**
     * POST {@code /api/v1/client-crm/admin/clients/{clientId}/webhook-tokens}. Creates token. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param request input carrying create client CRM webhook token
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client CRM webhook token response
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientCrmWebhookTokenResponse createToken(
            @PathVariable UUID clientId,
            @Valid @RequestBody(required = false) CreateClientCrmWebhookTokenRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return tokenService.createToken(clientId, request);
    }

    /**
     * PATCH {@code /api/v1/client-crm/admin/clients/{clientId}/webhook-tokens/{tokenId}/revoke}. Revokes token. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param tokenId identifier of the token
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client CRM webhook token response
     */
    @PatchMapping("/{tokenId}/revoke")
    public ClientCrmWebhookTokenResponse revokeToken(
            @PathVariable UUID clientId,
            @PathVariable UUID tokenId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return tokenService.revokeToken(clientId, tokenId);
    }
}
