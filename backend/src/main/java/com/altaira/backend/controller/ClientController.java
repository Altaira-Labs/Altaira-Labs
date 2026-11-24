package com.altaira.backend.controller;

import com.altaira.backend.dto.client.AdminClientSummaryResponse;
import com.altaira.backend.dto.client.ClientResponse;
import com.altaira.backend.dto.client.CreateClientRequest;
import com.altaira.backend.dto.clientinvitation.ClientInvitationResponse;
import com.altaira.backend.dto.clientinvitation.CreateClientInvitationRequest;
import com.altaira.backend.dto.clientservice.AssignClientServiceRequest;
import com.altaira.backend.dto.clientservice.ClientServiceResponse;
import com.altaira.backend.dto.note.CreateInternalNoteRequest;
import com.altaira.backend.dto.note.InternalNoteResponse;
import com.altaira.backend.entity.AppUserEntity;
import com.altaira.backend.security.AdminAccessService;
import com.altaira.backend.service.AuthService;
import com.altaira.backend.service.ClientInvitationService;
import com.altaira.backend.service.ClientManagementService;
import com.altaira.backend.service.ClientServiceAssignmentService;
import com.altaira.backend.service.InternalNoteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/** HTTP endpoints for client under {@code /api/v1/clients}. */
@RestController
@RequestMapping("/api/v1/clients")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class ClientController {

    /** Client management operations used by this component. */
    private final ClientManagementService clientManagementService;
    /** Client service assignment operations used by this component. */
    private final ClientServiceAssignmentService clientServiceAssignmentService;
    /** Internal note operations used by this component. */
    private final InternalNoteService internalNoteService;
    /** Client invitation operations used by this component. */
    private final ClientInvitationService clientInvitationService;
    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;
    /** Auth operations used by this component. */
    private final AuthService authService;

    /**
     * Creates the client controller with its injected collaborators.
     *
     * @param clientManagementService client management operations used by this component
     * @param clientServiceAssignmentService client service assignment operations used by this component
     * @param internalNoteService internal note operations used by this component
     * @param clientInvitationService client invitation operations used by this component
     * @param adminAccessService admin access operations used by this component
     * @param authService auth operations used by this component
     */
    public ClientController(
            ClientManagementService clientManagementService,
            ClientServiceAssignmentService clientServiceAssignmentService,
            InternalNoteService internalNoteService,
            ClientInvitationService clientInvitationService,
            AdminAccessService adminAccessService,
            AuthService authService
    ) {
        this.clientManagementService = clientManagementService;
        this.clientServiceAssignmentService = clientServiceAssignmentService;
        this.internalNoteService = internalNoteService;
        this.clientInvitationService = clientInvitationService;
        this.adminAccessService = adminAccessService;
        this.authService = authService;
    }

    /**
     * GET {@code /api/v1/clients}. Returns all clients. Internal access is checked before delegation.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client response items
     */
    @GetMapping
    public List<ClientResponse> getAllClients(
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientManagementService.getAllClients();
    }

    /**
     * GET {@code /api/v1/clients/admin-summaries}. Retrieves admin client summaries. Internal access is checked before delegation.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the admin client summary response items
     */
    @GetMapping("/admin-summaries")
    public List<AdminClientSummaryResponse> getAdminClientSummaries(
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientManagementService.getAdminClientSummaries();
    }

    /**
     * GET {@code /api/v1/clients/{id}/admin-summary}. Retrieves admin client summary. Internal access is checked before delegation.
     *
     * @param id unique identifier of the record
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return admin client summary
     */
    @GetMapping("/{id}/admin-summary")
    public AdminClientSummaryResponse getAdminClientSummary(
            @PathVariable UUID id,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientManagementService.getAdminClientSummary(id);
    }

    /**
     * GET {@code /api/v1/clients/{id}}. Retrieves client by id. Internal access is checked before delegation.
     *
     * @param id unique identifier of the record
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return identifier of the client by
     */
    @GetMapping("/{id}")
    public ClientResponse getClientById(
            @PathVariable UUID id,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientManagementService.getClientById(id);
    }

    /**
     * POST {@code /api/v1/clients}. Creates client. Internal access is checked before delegation.
     *
     * @param request input carrying create client
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the HTTP response for this operation
     */
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(
            @Valid @RequestBody CreateClientRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientManagementService.createClient(request));
    }

    /**
     * POST {@code /api/v1/clients/from-lead/{leadId}}. Creates client from lead. Internal access is checked before delegation.
     *
     * @param leadId identifier of the source sales enquiry
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client response
     */
    @PostMapping("/from-lead/{leadId}")
    public ClientResponse createClientFromLead(
            @PathVariable UUID leadId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientManagementService.createClientFromLead(leadId);
    }

    /**
     * GET {@code /api/v1/clients/{clientId}/services}. Retrieves client services. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client service response items
     */
    @GetMapping("/{clientId}/services")
    public List<ClientServiceResponse> getClientServices(
            @PathVariable UUID clientId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientServiceAssignmentService.getClientServices(clientId);
    }

    /**
     * POST {@code /api/v1/clients/{clientId}/services}. Assigns service. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param request input carrying assign client service
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the HTTP response for this operation
     */
    @PostMapping("/{clientId}/services")
    public ResponseEntity<ClientServiceResponse> assignService(
            @PathVariable UUID clientId,
            @Valid @RequestBody AssignClientServiceRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientServiceAssignmentService.assignService(clientId, request));
    }

    /**
     * GET {@code /api/v1/clients/{clientId}/notes}. Retrieves client notes. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the internal note response items
     */
    @GetMapping("/{clientId}/notes")
    public List<InternalNoteResponse> getClientNotes(
            @PathVariable UUID clientId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return internalNoteService.getClientNotes(clientId);
    }

    /**
     * POST {@code /api/v1/clients/{clientId}/notes}. Adds client note. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param request input carrying create internal note
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the HTTP response for this operation
     */
    @PostMapping("/{clientId}/notes")
    public ResponseEntity<InternalNoteResponse> addClientNote(
            @PathVariable UUID clientId,
            @Valid @RequestBody CreateInternalNoteRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(internalNoteService.addClientNote(clientId, request));
    }

    /**
     * GET {@code /api/v1/clients/{clientId}/invitations}. Retrieves client invitations. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client invitation response items
     */
    @GetMapping("/{clientId}/invitations")
    public List<ClientInvitationResponse> getClientInvitations(
            @PathVariable UUID clientId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientInvitationService.listInvitations(clientId);
    }

    /**
     * POST {@code /api/v1/clients/{clientId}/invitations}. Creates client invitation. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param request input carrying create client invitation
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @param httpRequest incoming HTTP request, including its headers
     * @return the HTTP response for this operation
     */
    @PostMapping("/{clientId}/invitations")
    public ResponseEntity<ClientInvitationResponse> createClientInvitation(
            @PathVariable UUID clientId,
            @Valid @RequestBody CreateClientInvitationRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken,
            HttpServletRequest httpRequest
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientInvitationService.createInvitation(
                clientId,
                request,
                optionalAdminUser(adminSessionToken),
                clientIp(httpRequest),
                userAgent(httpRequest)
        ));
    }

    /**
     * Resolves the signed-in internal user when staff session information is supplied.
     *
     * @param adminSessionToken session credential for the internal user
     * @return the app user record
     */
    private AppUserEntity optionalAdminUser(String adminSessionToken) {
        try {
            return authService.getCurrentUserEntity(adminSessionToken);
        } catch (ResponseStatusException ex) {
            return null;
        }
    }

    /**
     * Obtains the network address associated with the request.
     *
     * @param request input carrying http servlet
     * @return the resulting text
     */
    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * Reads the User-Agent header for the operation’s audit context.
     *
     * @param request input carrying http servlet
     * @return the resulting text
     */
    private String userAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
