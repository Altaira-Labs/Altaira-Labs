package com.altaira.backend.service;

import com.altaira.backend.dto.client.AdminClientSummaryResponse;
import com.altaira.backend.dto.client.ClientResponse;
import com.altaira.backend.dto.client.CreateClientRequest;
import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.ClientProjectEntity;
import com.altaira.backend.entity.ClientServiceEntity;
import com.altaira.backend.entity.LeadEntity;
import com.altaira.backend.entity.WorkspaceTaskEntity;
import com.altaira.backend.exception.LeadNotFoundException;
import com.altaira.backend.exception.ResourceNotFoundException;
import com.altaira.backend.model.ClientStatus;
import com.altaira.backend.model.SectorType;
import com.altaira.backend.repository.ClientProjectRepository;
import com.altaira.backend.repository.ClientRepository;
import com.altaira.backend.repository.ClientServiceRepository;
import com.altaira.backend.repository.ClientWorkspaceRepository;
import com.altaira.backend.repository.LeadRepository;
import com.altaira.backend.repository.WorkspaceTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Client list/detail and administrative changes. Admin operations use explicit DTOs and state rules. */
@Service
@Transactional
public class ClientManagementService {

    /** Persistence access for client. */
    private final ClientRepository clientRepository;
    /** Persistence access for lead. */
    private final LeadRepository leadRepository;
    /** Persistence access for client service. */
    private final ClientServiceRepository clientServiceRepository;
    /** Persistence access for client project. */
    private final ClientProjectRepository clientProjectRepository;
    /** Persistence access for workspace task. */
    private final WorkspaceTaskRepository workspaceTaskRepository;
    /** Persistence access for client workspace. */
    private final ClientWorkspaceRepository clientWorkspaceRepository;

    /**
     * Creates the client management service with its injected collaborators.
     *
     * @param clientRepository persistence access for client
     * @param leadRepository persistence access for lead
     * @param clientServiceRepository persistence access for client service
     * @param clientProjectRepository persistence access for client project
     * @param workspaceTaskRepository persistence access for workspace task
     * @param clientWorkspaceRepository persistence access for client workspace
     */
    public ClientManagementService(
            ClientRepository clientRepository,
            LeadRepository leadRepository,
            ClientServiceRepository clientServiceRepository,
            ClientProjectRepository clientProjectRepository,
            WorkspaceTaskRepository workspaceTaskRepository,
            ClientWorkspaceRepository clientWorkspaceRepository
    ) {
        this.clientRepository = clientRepository;
        this.leadRepository = leadRepository;
        this.clientServiceRepository = clientServiceRepository;
        this.clientProjectRepository = clientProjectRepository;
        this.workspaceTaskRepository = workspaceTaskRepository;
        this.clientWorkspaceRepository = clientWorkspaceRepository;
    }

    /**
     * Creates client. Persists changes through {@code clientRepository}. Uses locale-independent case normalization.
     *
     * @param request input carrying create client
     * @return the client response
     * @throws com.altaira.backend.exception.LeadNotFoundException if the sales enquiry does not exist
     */
    public ClientResponse createClient(CreateClientRequest request) {
        LeadEntity sourceLead = null;

        if (request.getSourceLeadId() != null) {
            sourceLead = leadRepository.findById(request.getSourceLeadId())
                    .orElseThrow(() -> new LeadNotFoundException(request.getSourceLeadId()));

            var existingClient = clientRepository.findBySourceLead(sourceLead);
            if (existingClient.isPresent()) {
                return map(existingClient.get());
            }
        }

        ClientEntity entity = new ClientEntity();
        entity.setName(trimRequired(request.getName()));
        entity.setCompany(trimRequired(request.getCompany()));
        entity.setEmail(trimRequired(request.getEmail()).toLowerCase(Locale.ROOT));
        entity.setPhone(trimOptional(request.getPhone()));
        entity.setSourceLead(sourceLead);
        entity.setStatus(ClientStatus.parse(request.getStatus()).value());
        entity.setSectorType(SectorType.parse(request.getSectorType()).value());

        return map(clientRepository.save(entity));
    }

    /**
     * Creates client from lead. Persists changes through {@code clientRepository}.
     *
     * @param leadId identifier of the source sales enquiry
     * @return the client response
     * @throws com.altaira.backend.exception.LeadNotFoundException if the sales enquiry does not exist
     */
    public ClientResponse createClientFromLead(UUID leadId) {
        LeadEntity lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException(leadId));

        var existingClient = clientRepository.findBySourceLead(lead);
        if (existingClient.isPresent()) {
            return map(existingClient.get());
        }

        ClientEntity entity = new ClientEntity();
        entity.setName(lead.getFullName());
        entity.setCompany(lead.getBusinessName());
        entity.setEmail(lead.getEmail());
        entity.setPhone(trimOptional(lead.getPhone()));
        entity.setSourceLead(lead);
        entity.setStatus(ClientStatus.ACTIVE.value());
        entity.setSectorType(inferSectorFromLead(lead));

        return map(clientRepository.save(entity));
    }

    /**
     * Returns all clients.
     *
     * @return the client response items
     */
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::map)
                .toList();
    }

    /**
     * Retrieves client by id.
     *
     * @param id unique identifier of the record
     * @return identifier of the client by
     */
    public ClientResponse getClientById(UUID id) {
        return map(findClientEntity(id));
    }

    /**
     * Retrieves admin client summaries.
     *
     * @return the admin client summary response items
     */
    public List<AdminClientSummaryResponse> getAdminClientSummaries() {
        List<ClientEntity> clients = clientRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(client -> !isTechnicalFixture(client))
                .toList();
        Map<UUID, List<ClientServiceEntity>> servicesByClient = clientServiceRepository
                .findAllByOrderByUpdatedAtDesc()
                .stream()
                .collect(Collectors.groupingBy(assignment -> assignment.getClient().getId()));
        Map<UUID, List<ClientProjectEntity>> projectsByClient = clientProjectRepository
                .findAllByOrderByUpdatedAtDesc()
                .stream()
                .collect(Collectors.groupingBy(project -> project.getClient().getId()));
        Map<UUID, List<WorkspaceTaskEntity>> tasksByClient = workspaceTaskRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .collect(Collectors.groupingBy(task -> task.getClient().getId()));

        return clients.stream()
                .map(client -> mapAdminSummary(
                        client,
                        servicesByClient.getOrDefault(client.getId(), List.of()),
                        projectsByClient.getOrDefault(client.getId(), List.of()),
                        tasksByClient.getOrDefault(client.getId(), List.of())
                ))
                .toList();
    }

    /**
     * Retrieves admin client summary.
     *
     * @param id unique identifier of the record
     * @return admin client summary
     */
    public AdminClientSummaryResponse getAdminClientSummary(UUID id) {
        ClientEntity client = findClientEntity(id);
        return mapAdminSummary(
                client,
                clientServiceRepository.findAllByClientOrderByCreatedAtDesc(client),
                clientProjectRepository.findAllByClientOrderByCreatedAtAsc(client),
                workspaceTaskRepository.findAllByClientOrderByCreatedAtDesc(client)
        );
    }

    /**
     * Finds client entity. Looks up the referenced record by identifier.
     *
     * @param id unique identifier of the record
     * @return the client record
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    ClientEntity findClientEntity(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    /**
     * Builds {@code ClientResponse} from {@code ClientEntity}, selecting the fields used by the caller.
     *
     * @param entity record carrying client
     * @return the client response
     */
    ClientResponse map(ClientEntity entity) {
        UUID sourceLeadId = entity.getSourceLead() == null ? null : entity.getSourceLead().getId();

        return new ClientResponse(
                entity.getId(),
                entity.getName(),
                entity.getCompany(),
                entity.getEmail(),
                entity.getPhone(),
                sourceLeadId,
                entity.getStatus(),
                entity.getSectorType(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Maps admin summary.
     *
     * @param client customer account in scope
     * @param services services
     * @param projects projects
     * @param tasks tasks
     * @return the admin client summary response
     */
    private AdminClientSummaryResponse mapAdminSummary(
            ClientEntity client,
            List<ClientServiceEntity> services,
            List<ClientProjectEntity> projects,
            List<WorkspaceTaskEntity> tasks
    ) {
        List<ClientServiceEntity> activeAssignments = services.stream()
                .filter(assignment -> !"cancelled".equals(assignment.getStatus()))
                .toList();
        List<String> activeServices = activeAssignments.stream()
                .map(assignment -> assignment.getService().getName())
                .distinct()
                .sorted()
                .toList();
        Set<String> closedStatuses = Set.of("completed", "approved");
        List<WorkspaceTaskEntity> openTasks = tasks.stream()
                .filter(task -> !closedStatuses.contains(task.getStatus()))
                .toList();
        WorkspaceTaskEntity nextTask = openTasks.stream()
                .min(Comparator
                        .comparing((WorkspaceTaskEntity task) -> task.getDueAt() == null)
                        .thenComparing(task -> task.getDueAt() == null ? task.getCreatedAt() : task.getDueAt()))
                .orElse(null);
        Instant lastActivityAt = latestInstant(
                client.getUpdatedAt(),
                services.stream().map(ClientServiceEntity::getUpdatedAt).max(Comparator.naturalOrder()).orElse(null),
                projects.stream().map(ClientProjectEntity::getUpdatedAt).max(Comparator.naturalOrder()).orElse(null),
                tasks.stream().map(WorkspaceTaskEntity::getUpdatedAt).max(Comparator.naturalOrder()).orElse(null)
        );

        return new AdminClientSummaryResponse(
                client.getId(),
                client.getName(),
                client.getCompany(),
                client.getEmail(),
                client.getPhone(),
                client.getSourceLead() == null ? null : client.getSourceLead().getId(),
                client.getStatus(),
                client.getSectorType(),
                activeServices,
                projects.size(),
                openTasks.size(),
                nextTask == null ? null : nextTask.getTitle(),
                nextTask == null ? null : nextTask.getStatus(),
                nextTask == null ? null : nextTask.getOwnerRole(),
                nextTask == null ? null : nextTask.getDueAt(),
                overallState(client, activeAssignments, projects, openTasks),
                lastActivityAt,
                clientWorkspaceRepository.findByClient(client).isPresent(),
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }

    /**
     * Combines delivery indicators into the status shown on the administrative client summary.
     *
     * @param client customer account in scope
     * @param activeAssignments active assignments
     * @param projects projects
     * @param openTasks open tasks
     * @return the resulting text
     */
    private String overallState(
            ClientEntity client,
            List<ClientServiceEntity> activeAssignments,
            List<ClientProjectEntity> projects,
            List<WorkspaceTaskEntity> openTasks
    ) {
        if (!ClientStatus.ACTIVE.value().equals(client.getStatus())) {
            return client.getStatus();
        }

        if (openTasks.stream().anyMatch(task -> "blocked".equals(task.getStatus()))) {
            return "blocked";
        }

        if (openTasks.stream().anyMatch(task -> Set.of("submitted", "needs_review", "rejected").contains(task.getStatus()))) {
            return "needs_attention";
        }

        if (!projects.isEmpty() || !openTasks.isEmpty()) {
            return "in_progress";
        }

        if (!activeAssignments.isEmpty()) {
            return "ready";
        }

        return "no_services";
    }

    /**
     * Chooses the most recent available timestamp for the customer summary.
     *
     * @param values values
     * @return the resulting instant in time
     */
    private Instant latestInstant(Instant... values) {
        return Arrays.stream(values)
                .filter(value -> value != null)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    /**
     * Checks whether this is technical fixture. Uses locale-independent case normalization.
     *
     * @param client customer account in scope
     * @return true if the value is technical fixture
     */
    private boolean isTechnicalFixture(ClientEntity client) {
        String name = client.getName() == null ? "" : client.getName().trim().toLowerCase(Locale.ROOT);
        String company = client.getCompany() == null ? "" : client.getCompany().trim().toLowerCase(Locale.ROOT);
        String email = client.getEmail() == null ? "" : client.getEmail().trim().toLowerCase(Locale.ROOT);

        return name.startsWith("e2e ") ||
                name.equals("e2e") ||
                company.startsWith("e2e ") ||
                company.startsWith("altaira e2e") ||
                email.contains("e2e");
    }

    /**
     * Infers the customer sector from its source enquiry.
     *
     * @param lead sales enquiry in scope
     * @return the resulting text
     */
    private String inferSectorFromLead(LeadEntity lead) {
        String combined = ((lead.getIndustry() == null ? "" : lead.getIndustry()) + " " +
                (lead.getServiceInterest() == null ? "" : lead.getServiceInterest()) + " " +
                (lead.getGoals() == null ? "" : lead.getGoals())).toLowerCase(Locale.ROOT);

        if (combined.contains("clinic") || combined.contains("dental") || combined.contains("patient") || combined.contains("clinica")) {
            return SectorType.CLINICS.value();
        }

        if (combined.contains("restaurant") || combined.contains("reservation") || combined.contains("restaurante") || combined.contains("menu")) {
            return SectorType.RESTAURANTS.value();
        }

        if (combined.contains("car") || combined.contains("dealer") || combined.contains("vehicle") || combined.contains("concesionario")) {
            return SectorType.CAR_DEALERS.value();
        }

        return SectorType.CUSTOM.value();
    }

    /**
     * Trims surrounding whitespace. Absent values use an empty string.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String trimRequired(String value) {
        return value == null ? "" : value.trim();
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
