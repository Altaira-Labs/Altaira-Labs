package com.altaira.backend.service;

import com.altaira.backend.dto.clientservice.AssignClientServiceRequest;
import com.altaira.backend.dto.clientservice.ClientServiceResponse;
import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.ClientServiceEntity;
import com.altaira.backend.entity.ServiceEntity;
import com.altaira.backend.exception.ResourceNotFoundException;
import com.altaira.backend.model.ClientServiceStatus;
import com.altaira.backend.repository.ClientServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Connects a customer to a service from the consultancy's catalogue.
 * <p>A catalogue entry describes an offering such as a booking system. The assignment
 * records whether that particular customer has the service and carries its delivery
 * status and notes. Assigning an existing customer–service pair reuses the same record.
 */
@Service
@Transactional
public class ClientServiceAssignmentService {

    /** Persistence access for client service. */
    private final ClientServiceRepository clientServiceRepository;
    /** Client management operations used by this component. */
    private final ClientManagementService clientManagementService;
    /** Service catalog operations used by this component. */
    private final ServiceCatalogService serviceCatalogService;

    /**
     * Creates the client service assignment service with its injected collaborators.
     *
     * @param clientServiceRepository persistence access for client service
     * @param clientManagementService client management operations used by this component
     * @param serviceCatalogService service catalog operations used by this component
     */
    public ClientServiceAssignmentService(
            ClientServiceRepository clientServiceRepository,
            ClientManagementService clientManagementService,
            ServiceCatalogService serviceCatalogService
    ) {
        this.clientServiceRepository = clientServiceRepository;
        this.clientManagementService = clientManagementService;
        this.serviceCatalogService = serviceCatalogService;
    }

    /**
     * Assigns a catalogue service to a customer. When the pairing already exists, nonblank status or notes
     * update the existing assignment.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param request catalogue service to assign, with optional status and notes
     * @return the saved assignment for the customer and service
     */
    public ClientServiceResponse assignService(UUID clientId, AssignClientServiceRequest request) {
        ClientEntity client = clientManagementService.findClientEntity(clientId);
        ServiceEntity service = serviceCatalogService.findServiceEntity(request.getServiceId());

        var existingAssignment = clientServiceRepository.findByClientAndService(client, service);
        if (existingAssignment.isPresent()) {
            ClientServiceEntity entity = existingAssignment.get();

            if (request.getStatus() != null && !request.getStatus().isBlank()) {
                entity.setStatus(ClientServiceStatus.parse(request.getStatus()).value());
            }

            if (request.getNotes() != null && !request.getNotes().isBlank()) {
                entity.setNotes(trimOptional(request.getNotes()));
            }

            return map(clientServiceRepository.save(entity));
        }

        ClientServiceEntity entity = new ClientServiceEntity();
        entity.setClient(client);
        entity.setService(service);
        entity.setStatus(normalizeStatusOrDefault(request.getStatus()));
        entity.setNotes(trimOptional(request.getNotes()));

        return map(clientServiceRepository.save(entity));
    }

    /**
     * Lists the catalogue services assigned to an existing customer.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @return the customer's assignments, newest first
     */
    public List<ClientServiceResponse> getClientServices(UUID clientId) {
        ClientEntity client = clientManagementService.findClientEntity(clientId);

        return clientServiceRepository.findAllByClientOrderByCreatedAtDesc(client)
                .stream()
                .map(this::map)
                .toList();
    }

    /**
     * Changes the status of a single customer service assignment.
     *
     * @param id unique identifier of the record
     * @param status current state in the workflow
     * @return the saved assignment with the parsed status
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    public ClientServiceResponse updateStatus(UUID id, String status) {
        ClientServiceEntity entity = clientServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client service", id));

        entity.setStatus(ClientServiceStatus.parse(status).value());
        return map(clientServiceRepository.save(entity));
    }

    /**
     * Normalizes status or default.
     *
     * @param status current state in the workflow
     * @return the resulting text
     */
    private String normalizeStatusOrDefault(String status) {
        if (status == null || status.isBlank()) {
            return ClientServiceStatus.PLANNED.value();
        }

        return ClientServiceStatus.parse(status).value();
    }

    /**
     * Builds {@code ClientServiceResponse} from {@code ClientServiceEntity}, selecting the fields used by the caller.
     *
     * @param entity record carrying client service
     * @return the client service response
     */
    private ClientServiceResponse map(ClientServiceEntity entity) {
        return new ClientServiceResponse(
                entity.getId(),
                entity.getClient().getId(),
                serviceCatalogService.map(entity.getService()),
                entity.getStatus(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Trims surrounding whitespace. Absent values use an empty string.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String trimOptional(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.trim();
    }
}
