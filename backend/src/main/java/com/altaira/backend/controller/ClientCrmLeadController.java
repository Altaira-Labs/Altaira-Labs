package com.altaira.backend.controller;

import com.altaira.backend.dto.clientcrm.AddClientCrmLeadNoteRequest;
import com.altaira.backend.dto.clientcrm.ClientCrmLeadResponse;
import com.altaira.backend.dto.clientcrm.CreateClientCrmFollowUpActionRequest;
import com.altaira.backend.dto.clientcrm.CreateClientCrmLeadRequest;
import com.altaira.backend.dto.clientcrm.UpdateClientCrmFollowUpActionStatusRequest;
import com.altaira.backend.dto.clientcrm.UpdateClientCrmLeadStatusRequest;
import com.altaira.backend.security.AdminAccessService;
import com.altaira.backend.security.ClientAccessService;
import com.altaira.backend.service.ClientCrmLeadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** HTTP endpoints for client CRM lead under {@code /api/v1/client-crm}. */
@RestController
@RequestMapping("/api/v1/client-crm")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class ClientCrmLeadController {

    /** Client access operations used by this component. */
    private final ClientAccessService clientAccessService;
    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;
    /** Client CRM lead operations used by this component. */
    private final ClientCrmLeadService clientCrmLeadService;

    /**
     * Creates the client CRM lead controller with its injected collaborators.
     *
     * @param clientAccessService client access operations used by this component
     * @param adminAccessService admin access operations used by this component
     * @param clientCrmLeadService client CRM lead operations used by this component
     */
    public ClientCrmLeadController(
            ClientAccessService clientAccessService,
            AdminAccessService adminAccessService,
            ClientCrmLeadService clientCrmLeadService
    ) {
        this.clientAccessService = clientAccessService;
        this.adminAccessService = adminAccessService;
        this.clientCrmLeadService = clientCrmLeadService;
    }

    /**
     * GET {@code /api/v1/client-crm/client/leads}. Lists client leads. Membership is checked before the workspace operation.
     *
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the client CRM lead response items
     */
    @GetMapping("/client/leads")
    public List<ClientCrmLeadResponse> listClientLeads(
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        return clientCrmLeadService.listClientLeads(context);
    }

    /**
     * GET {@code /api/v1/client-crm/client/leads/{leadId}}. Retrieves client lead. Membership is checked before the workspace operation.
     *
     * @param leadId identifier of the source sales enquiry
     * @param clientSessionToken session credential supplied for the client workspace
     * @return client lead
     */
    @GetMapping("/client/leads/{leadId}")
    public ClientCrmLeadResponse getClientLead(
            @PathVariable UUID leadId,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        return clientCrmLeadService.getClientLead(context, leadId);
    }

    /**
     * POST {@code /api/v1/client-crm/client/leads}. Creates client lead. Membership is checked before the workspace operation.
     *
     * @param request input carrying create client CRM lead
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the client CRM lead response
     */
    @PostMapping("/client/leads")
    @ResponseStatus(HttpStatus.CREATED)
    public ClientCrmLeadResponse createClientLead(
            @Valid @RequestBody CreateClientCrmLeadRequest request,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return clientCrmLeadService.createClientLead(context, request);
    }

    /**
     * PATCH {@code /api/v1/client-crm/client/leads/{leadId}/status}. Updates client lead status. Membership is checked before the workspace operation.
     *
     * @param leadId identifier of the source sales enquiry
     * @param request input carrying update client CRM lead status
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the client CRM lead response
     */
    @PatchMapping("/client/leads/{leadId}/status")
    public ClientCrmLeadResponse updateClientLeadStatus(
            @PathVariable UUID leadId,
            @Valid @RequestBody UpdateClientCrmLeadStatusRequest request,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return clientCrmLeadService.updateClientLeadStatus(context, leadId, request);
    }

    /**
     * POST {@code /api/v1/client-crm/client/leads/{leadId}/notes}. Adds client lead note. Membership is checked before the workspace operation.
     *
     * @param leadId identifier of the source sales enquiry
     * @param request input carrying add client CRM lead note
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the client CRM lead response
     */
    @PostMapping("/client/leads/{leadId}/notes")
    public ClientCrmLeadResponse addClientLeadNote(
            @PathVariable UUID leadId,
            @Valid @RequestBody AddClientCrmLeadNoteRequest request,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return clientCrmLeadService.addClientLeadNote(context, leadId, request);
    }

    /**
     * POST {@code /api/v1/client-crm/client/leads/{leadId}/follow-up-actions}. Creates client follow up action. Membership is checked before the workspace operation.
     *
     * @param leadId identifier of the source sales enquiry
     * @param request input carrying create client CRM follow up action
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the client CRM lead response
     */
    @PostMapping("/client/leads/{leadId}/follow-up-actions")
    public ClientCrmLeadResponse createClientFollowUpAction(
            @PathVariable UUID leadId,
            @Valid @RequestBody CreateClientCrmFollowUpActionRequest request,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return clientCrmLeadService.createClientFollowUpAction(context, leadId, request);
    }

    /**
     * PATCH {@code /api/v1/client-crm/client/leads/{leadId}/follow-up-actions/{actionId}/status}. Updates client follow up action status. Membership is checked before the workspace operation.
     *
     * @param leadId identifier of the source sales enquiry
     * @param actionId identifier of the action
     * @param request input carrying update client CRM follow up action status
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the client CRM lead response
     */
    @PatchMapping("/client/leads/{leadId}/follow-up-actions/{actionId}/status")
    public ClientCrmLeadResponse updateClientFollowUpActionStatus(
            @PathVariable UUID leadId,
            @PathVariable UUID actionId,
            @Valid @RequestBody UpdateClientCrmFollowUpActionStatusRequest request,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return clientCrmLeadService.updateClientFollowUpActionStatus(context, leadId, actionId, request);
    }

    /**
     * GET {@code /api/v1/client-crm/admin/clients/{clientId}/leads}. Lists client leads as admin. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client CRM lead response items
     */
    @GetMapping("/admin/clients/{clientId}/leads")
    public List<ClientCrmLeadResponse> listClientLeadsAsAdmin(
            @PathVariable UUID clientId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientCrmLeadService.listClientLeadsAsAdmin(clientId);
    }

    /**
     * PATCH {@code /api/v1/client-crm/admin/clients/{clientId}/leads/{leadId}/status}. Updates client lead status as admin. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param leadId identifier of the source sales enquiry
     * @param request input carrying update client CRM lead status
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client CRM lead response
     */
    @PatchMapping("/admin/clients/{clientId}/leads/{leadId}/status")
    public ClientCrmLeadResponse updateClientLeadStatusAsAdmin(
            @PathVariable UUID clientId,
            @PathVariable UUID leadId,
            @Valid @RequestBody UpdateClientCrmLeadStatusRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        var adminUser = adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientCrmLeadService.updateClientLeadStatusAsAdmin(clientId, leadId, adminUser, request);
    }

    /**
     * POST {@code /api/v1/client-crm/admin/clients/{clientId}/leads/{leadId}/notes}. Adds client lead note as admin. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param leadId identifier of the source sales enquiry
     * @param request input carrying add client CRM lead note
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client CRM lead response
     */
    @PostMapping("/admin/clients/{clientId}/leads/{leadId}/notes")
    public ClientCrmLeadResponse addClientLeadNoteAsAdmin(
            @PathVariable UUID clientId,
            @PathVariable UUID leadId,
            @Valid @RequestBody AddClientCrmLeadNoteRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        var adminUser = adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientCrmLeadService.addClientLeadNoteAsAdmin(clientId, leadId, adminUser, request);
    }

    /**
     * POST {@code /api/v1/client-crm/admin/clients/{clientId}/leads/{leadId}/follow-up-actions}. Creates follow up action as admin. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param leadId identifier of the source sales enquiry
     * @param request input carrying create client CRM follow up action
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client CRM lead response
     */
    @PostMapping("/admin/clients/{clientId}/leads/{leadId}/follow-up-actions")
    public ClientCrmLeadResponse createFollowUpActionAsAdmin(
            @PathVariable UUID clientId,
            @PathVariable UUID leadId,
            @Valid @RequestBody CreateClientCrmFollowUpActionRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        var adminUser = adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientCrmLeadService.createFollowUpActionAsAdmin(clientId, leadId, adminUser, request);
    }

    /**
     * PATCH {@code /api/v1/client-crm/admin/clients/{clientId}/leads/{leadId}/follow-up-actions/{actionId}/status}. Updates follow up action status as admin. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param leadId identifier of the source sales enquiry
     * @param actionId identifier of the action
     * @param request input carrying update client CRM follow up action status
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client CRM lead response
     */
    @PatchMapping("/admin/clients/{clientId}/leads/{leadId}/follow-up-actions/{actionId}/status")
    public ClientCrmLeadResponse updateFollowUpActionStatusAsAdmin(
            @PathVariable UUID clientId,
            @PathVariable UUID leadId,
            @PathVariable UUID actionId,
            @Valid @RequestBody UpdateClientCrmFollowUpActionStatusRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        var adminUser = adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientCrmLeadService.updateFollowUpActionStatusAsAdmin(clientId, leadId, actionId, adminUser, request);
    }
}
