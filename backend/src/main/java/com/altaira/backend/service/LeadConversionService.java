package com.altaira.backend.service;

import com.altaira.backend.dto.client.ClientResponse;
import com.altaira.backend.dto.clientportal.ClientPortalResponse;
import com.altaira.backend.dto.clientservice.AssignClientServiceRequest;
import com.altaira.backend.dto.clientservice.ClientServiceResponse;
import com.altaira.backend.dto.lead.LeadConversionRequest;
import com.altaira.backend.dto.lead.LeadConversionResponse;
import com.altaira.backend.dto.lead.LeadResponse;
import com.altaira.backend.entity.LeadEntity;
import com.altaira.backend.exception.LeadNotFoundException;
import com.altaira.backend.model.LeadIntakeFormKey;
import com.altaira.backend.model.LeadStatus;
import com.altaira.backend.repository.ClientRepository;
import com.altaira.backend.repository.LeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

/**
 * Turns a sales enquiry into a customer account with assigned services in a single transaction.
 * <p>The normal entry point follows payment confirmation for an approved plan. The separate
 * direct-conversion entry point is guarded by a legacy configuration switch.
 * <p>Conversion keeps the relationship to the original lead, removes duplicate service keys,
 * assigns the contracted services and marks the lead as converted.
 */
@Service
@Transactional
public class LeadConversionService {

    /** Persistence access for lead. */
    private final LeadRepository leadRepository;
    /** Persistence access for client. */
    private final ClientRepository clientRepository;
    /** Client management operations used by this component. */
    private final ClientManagementService clientManagementService;
    /** Client service assignment operations used by this component. */
    private final ClientServiceAssignmentService clientServiceAssignmentService;
    /** Service catalog operations used by this component. */
    private final ServiceCatalogService serviceCatalogService;
    /** Client portal operations used by this component. */
    private final ClientPortalService clientPortalService;
    /** Lead operations used by this component. */
    private final LeadService leadService;
    /** Whether legacy direct conversion is enabled. */
    private final boolean legacyDirectConversionEnabled;

    /**
     * Creates the lead conversion service with its injected collaborators.
     *
     * @param leadRepository persistence access for lead
     * @param clientRepository persistence access for client
     * @param clientManagementService client management operations used by this component
     * @param clientServiceAssignmentService client service assignment operations used by this component
     * @param serviceCatalogService service catalog operations used by this component
     * @param clientPortalService client portal operations used by this component
     * @param leadService lead operations used by this component
     * @param legacyDirectConversionEnabled whether legacy direct conversion is enabled
     */
    public LeadConversionService(
            LeadRepository leadRepository,
            ClientRepository clientRepository,
            ClientManagementService clientManagementService,
            ClientServiceAssignmentService clientServiceAssignmentService,
            ServiceCatalogService serviceCatalogService,
            ClientPortalService clientPortalService,
            LeadService leadService,
            @Value("${altaira.commercial.legacy-direct-conversion-enabled:false}") boolean legacyDirectConversionEnabled
    ) {
        this.leadRepository = leadRepository;
        this.clientRepository = clientRepository;
        this.clientManagementService = clientManagementService;
        this.clientServiceAssignmentService = clientServiceAssignmentService;
        this.serviceCatalogService = serviceCatalogService;
        this.clientPortalService = clientPortalService;
        this.leadService = leadService;
        this.legacyDirectConversionEnabled = legacyDirectConversionEnabled;
    }

    /**
     * Handles the legacy direct-conversion path. When direct conversion is disabled, callers must confirm payment against an approved plan.
     *
     * @param leadId identifier of the source sales enquiry
     * @param request input carrying lead conversion
     * @return the lead conversion response
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (conflict)
     */
    public LeadConversionResponse convert(UUID leadId, LeadConversionRequest request) {
        if (!legacyDirectConversionEnabled) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Direct lead conversion is disabled. Confirm payment through the approved provisioning plan."
            );
        }
        return convertInternal(leadId, request);
    }

    /**
     * Builds a confirmed conversion request from the paid plan’s service keys and carries it through the usual lead-to-client workflow.
     *
     * @param leadId identifier of the source sales enquiry
     * @param serviceKeys catalogue keys identifying the requested deliveries
     * @param notes free-text notes associated with the item
     * @return the lead conversion response
     */
    public LeadConversionResponse convertAfterConfirmedPayment(
            UUID leadId,
            List<String> serviceKeys,
            String notes
    ) {
        LeadConversionRequest request = new LeadConversionRequest();
        request.setServiceKeys(serviceKeys);
        request.setConfirmed(true);
        request.setNotes(notes);
        return convertInternal(leadId, request);
    }

    /**
     * Resolves the lead, removes duplicate service keys, assigns the selected catalogue services and marks the lead as converted. The response distinguishes an existing customer from one created during this conversion.
     *
     * @param leadId identifier of the source sales enquiry
     * @param request input carrying lead conversion
     * @return the lead conversion response
     * @throws IllegalArgumentException when validation reports “At least one launch service is required”
     * @throws com.altaira.backend.exception.LeadNotFoundException if the sales enquiry does not exist
     */
    private LeadConversionResponse convertInternal(UUID leadId, LeadConversionRequest request) {
        LeadEntity lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException(leadId));

        List<String> serviceKeys = new LinkedHashSet<>(request.getServiceKeys())
                .stream()
                .map(LeadIntakeFormKey::parse)
                .filter(formKey -> formKey != LeadIntakeFormKey.GENERAL)
                .map(LeadIntakeFormKey::value)
                .toList();

        if (serviceKeys.isEmpty()) {
            throw new IllegalArgumentException("At least one launch service is required");
        }

        boolean clientCreated = clientRepository.findBySourceLead(lead).isEmpty();
        ClientResponse client = clientManagementService.createClientFromLead(leadId);

        List<ClientServiceResponse> assignments = serviceKeys.stream()
                .map(serviceKey -> {
                    AssignClientServiceRequest assignment = new AssignClientServiceRequest();
                    assignment.setServiceId(serviceCatalogService.findServiceForModuleKey(serviceKey).getId());
                    assignment.setNotes(trimOptional(request.getNotes()));
                    return clientServiceAssignmentService.assignService(client.getId(), assignment);
                })
                .toList();

        ClientPortalResponse portal = clientPortalService.getPortalForAdmin(client.getId());
        LeadResponse convertedLead = leadService.updateStatus(leadId, LeadStatus.CONVERTED.value());

        return new LeadConversionResponse(
                convertedLead,
                client,
                clientCreated,
                assignments,
                true,
                portal.getProjects()
        );
    }

    /**
     * Trims surrounding whitespace. Absent values use an empty string.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String trimOptional(String value) {
        return value == null || value.isBlank() ? "" : value.trim();
    }
}
