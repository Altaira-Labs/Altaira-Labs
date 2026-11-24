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

/**
 * Opens customer accounts directly or from sales enquiries. Also builds staff delivery summaries from
 * the customer's service assignments, projects, tasks and workspace.
 */
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
     * Creates a customer account from contact details. If the source enquiry already has a customer,
     * returns that existing account.
     *
     * @param request contact and account details, with an optional source enquiry identifier
     * @return the saved customer or the customer already linked to the enquiry
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
     * Copies the enquiry's contact information into an active customer account and infers its sector.
     * Reuses the account when the enquiry was converted earlier.
     *
     * @param leadId identifier of the source sales enquiry
     * @return the customer associated with the enquiry
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
     * Lists customer accounts in reverse creation order.
     *
     * @return customer details, newest accounts first
     */
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::map)
                .toList();
    }

    /**
     * Loads the customer's contact and account details for the supplied identifier.
     *
     * @param id customer account to load
     * @return the customer's details, including status, sector and source enquiry if recorded
     */
    public ClientResponse getClientById(UUID id) {
        return map(findClientEntity(id));
    }

    /**
     * Combines service assignments, projects and open tasks for the customer directory. Test fixtures are
     * omitted from this list.
     *
     * @return one delivery overview per included customer, newest accounts first
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
     * Collects the service assignments, projects and tasks for one customer into a delivery overview.
     *
     * @param id customer whose delivery activity is being summarized
     * @return the account's delivery state, next open task if present and last activity
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
     * Loads the customer row for operations that need to attach a related record.
     *
     * @param id unique identifier of the record
     * @return the persisted customer with the supplied identifier
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    ClientEntity findClientEntity(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    /**
     * Builds the API response from the stored account.
     *
     * @param entity stored customer account
     * @return contact details, status and dates, with a nullable source enquiry identifier
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
     * Counts open tasks and projects, selects the next task and combines their activity with the
     * customer's service assignments.
     *
     * @param client customer account in scope
     * @param services services
     * @param projects projects
     * @param tasks tasks
     * @return the account's delivery overview, including workspace availability
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
     * Uses the account's own status when inactive. For an active account, blocked work takes precedence
     * over work needing attention.
     *
     * @param client customer account in scope
     * @param activeAssignments active assignments
     * @param projects projects
     * @param openTasks open tasks
     * @return the delivery status shown in the administrative directory
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
     * Selects the latest available activity time across related records.
     *
     * @param values values
     * @return the greatest non-null timestamp, or {@code null} if all inputs are absent
     */
    private Instant latestInstant(Instant... values) {
        return Arrays.stream(values)
                .filter(value -> value != null)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    /**
     * Recognizes synthetic end-to-end accounts by their name, company or email.
     *
     * @param client customer account in scope
     * @return whether to omit this account from the administrative directory
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
     * Looks for sector terms in the enquiry's industry, service interest and goals.
     *
     * @param lead enquiry whose free-text answers describe the customer's business
     * @return the matched sector key, or the custom sector when no term matches
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
     * Trims the submitted contact field before storing it.
     *
     * @param value stored or supplied representation
     * @return the trimmed value, or an empty string for missing input
     */
    private String trimRequired(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Normalizes an optional contact field before storing it.
     *
     * @param value stored or supplied representation
     * @return the trimmed value, or an empty string for missing input
     */
    private String trimOptional(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.trim();
    }
}
