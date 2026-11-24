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

/**
 * Staff endpoints for the customer directory under {@code /api/v1/clients}. They expose account
 * details, service assignments, notes and invitations. Directory summaries combine the activity across
 * these records for the staff dashboard.
 */
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
     * GET {@code /api/v1/clients}.
     * Lists customer accounts for an authorized internal caller.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return customer accounts in reverse creation order
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
     * GET {@code /api/v1/clients/admin-summaries}.
     * Displays the staff delivery directory with synthetic test accounts omitted.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return customer overviews with service, project and task indicators
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
     * GET {@code /api/v1/clients/{id}/admin-summary}.
     * Shows staff the delivery overview for one customer.
     *
     * @param id identifier of the customer whose delivery activity is requested
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the account's delivery state, next task when present and last activity
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
     * GET {@code /api/v1/clients/{id}}.
     * Loads one customer's account for an authorized internal caller.
     *
     * @param id identifier of the customer to load
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the customer's contact details, sector, status and source enquiry when present
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
     * POST {@code /api/v1/clients}.
     * Opens a customer account or retrieves the account already associated with the same source enquiry.
     *
     * @param request contact and account details, with an optional source enquiry identifier
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return HTTP 201 with the resulting customer account
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
     * POST {@code /api/v1/clients/from-lead/{leadId}}.
     * Converts the enquiry's contact details into a customer account, reusing an earlier conversion when
     * present.
     *
     * @param leadId identifier of the source sales enquiry
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the customer linked to that enquiry
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
     * GET {@code /api/v1/clients/{clientId}/services}.
     * Lists the services assigned to the selected customer.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return customer service assignments, newest first
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
     * POST {@code /api/v1/clients/{clientId}/services}.
     * Assigns a catalogue service to a customer or updates the existing pairing.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param request catalogue service to assign, with optional status and notes
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return HTTP 201 with the resulting customer service assignment
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
     * GET {@code /api/v1/clients/{clientId}/notes}.
     * Lists staff notes on a customer account.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return internal notes in reverse creation order
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
     * POST {@code /api/v1/clients/{clientId}/notes}.
     * Attaches a staff note to the selected account.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param request note content and optional author
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return HTTP 201 with the saved note
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
     * GET {@code /api/v1/clients/{clientId}/invitations}.
     * Shows staff the invitation history for a customer.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return invitations in reverse creation order
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
     * POST {@code /api/v1/clients/{clientId}/invitations}.
     * Invites a person to the customer's portal and revokes earlier unused invitations for the same
     * customer and email.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param request invitation email and role for the selected customer
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @param httpRequest incoming HTTP request, including its headers
     * @return HTTP 201 with the new invitation details
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
     * Looks up the staff user for invitation attribution when an administrative session is available.
     *
     * @param adminSessionToken session credential for the internal user
     * @return the session user, or {@code null} if the lookup rejects the session
     */
    private AppUserEntity optionalAdminUser(String adminSessionToken) {
        try {
            return authService.getCurrentUserEntity(adminSessionToken);
        } catch (ResponseStatusException ex) {
            return null;
        }
    }

    /**
     * Selects the first forwarded address when present, otherwise the servlet peer address.
     *
     * @param request request that initiated the invitation
     * @return the address passed to the invitation operation for its audit context
     */
    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * Reads the browser or client identification header for invitation attribution.
     *
     * @param request request that initiated the invitation
     * @return the User-Agent header, or {@code null} if absent
     */
    private String userAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
