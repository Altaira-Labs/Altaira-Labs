package com.altaira.backend.service;

import com.altaira.backend.dto.workspacetask.CreateWorkspaceTaskRequest;
import com.altaira.backend.dto.workspacetask.UpdateWorkspaceTaskRequest;
import com.altaira.backend.dto.workspacetask.WorkspaceTaskResponse;
import com.altaira.backend.entity.AppUserEntity;
import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.ClientProjectEntity;
import com.altaira.backend.entity.ClientServiceEntity;
import com.altaira.backend.entity.ClientWorkspaceEntity;
import com.altaira.backend.entity.WorkspaceTaskEntity;
import com.altaira.backend.exception.ResourceNotFoundException;
import com.altaira.backend.model.WorkspaceTaskOwnerRole;
import com.altaira.backend.model.WorkspaceTaskPriority;
import com.altaira.backend.model.WorkspaceTaskStatus;
import com.altaira.backend.model.WorkspaceTaskVisibility;
import com.altaira.backend.repository.ClientProjectRepository;
import com.altaira.backend.repository.ClientRepository;
import com.altaira.backend.repository.ClientServiceRepository;
import com.altaira.backend.repository.ClientWorkspaceRepository;
import com.altaira.backend.repository.WorkspaceTaskRepository;
import com.altaira.backend.security.ClientAccessContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Manages delivery tasks associated with a customer's workspace, service and project. Staff can change
 * ownership, visibility and deadlines. Customer views include only tasks marked client-visible.
 */
@Service
public class WorkspaceTaskService {

    /** Persistence access for workspace task. */
    private final WorkspaceTaskRepository workspaceTaskRepository;
    /** Persistence access for client. */
    private final ClientRepository clientRepository;
    /** Persistence access for client workspace. */
    private final ClientWorkspaceRepository clientWorkspaceRepository;
    /** Persistence access for client service. */
    private final ClientServiceRepository clientServiceRepository;
    /** Persistence access for client project. */
    private final ClientProjectRepository clientProjectRepository;

    /**
     * Creates the workspace task service with its injected collaborators.
     *
     * @param workspaceTaskRepository persistence access for workspace task
     * @param clientRepository persistence access for client
     * @param clientWorkspaceRepository persistence access for client workspace
     * @param clientServiceRepository persistence access for client service
     * @param clientProjectRepository persistence access for client project
     */
    public WorkspaceTaskService(
            WorkspaceTaskRepository workspaceTaskRepository,
            ClientRepository clientRepository,
            ClientWorkspaceRepository clientWorkspaceRepository,
            ClientServiceRepository clientServiceRepository,
            ClientProjectRepository clientProjectRepository
    ) {
        this.workspaceTaskRepository = workspaceTaskRepository;
        this.clientRepository = clientRepository;
        this.clientWorkspaceRepository = clientWorkspaceRepository;
        this.clientServiceRepository = clientServiceRepository;
        this.clientProjectRepository = clientProjectRepository;
    }

    /**
     * Lists tasks across customers for the staff workspace, excluding recognised test accounts.
     *
     * @return tasks ordered by due date, then newest creation time for ties
     */
    @Transactional(readOnly = true)
    public List<WorkspaceTaskResponse> listForAdmin() {
        return workspaceTaskRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(task -> !isTechnicalFixture(task.getClient()))
                .sorted(taskOrder())
                .map(this::toResponse)
                .toList();
    }

    /**
     * Lists tasks marked client-visible for the customer in the authenticated context.
     *
     * @param context context carrying client access
     * @return that customer's visible tasks in due-date order, with undated tasks last
     */
    @Transactional(readOnly = true)
    public List<WorkspaceTaskResponse> listForClient(ClientAccessContext context) {
        return workspaceTaskRepository
                .findAllByClientAndVisibilityOrderByCreatedAtDesc(
                        context.client(),
                        WorkspaceTaskVisibility.CLIENT_VISIBLE.value()
                )
                .stream()
                .sorted(taskOrder())
                .map(this::toResponse)
                .toList();
    }

    /**
     * Shows staff the client-visible tasks for the selected customer.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @return the same task ordering and visibility filter used by the client list
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    @Transactional(readOnly = true)
    public List<WorkspaceTaskResponse> listForAdminClientPreview(UUID clientId) {
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));

        return workspaceTaskRepository
                .findAllByClientAndVisibilityOrderByCreatedAtDesc(
                        client,
                        WorkspaceTaskVisibility.CLIENT_VISIBLE.value()
                )
                .stream()
                .sorted(taskOrder())
                .map(this::toResponse)
                .toList();
    }

    /**
     * Creates a task in the customer's workspace, creating the workspace if needed. An optional project
     * and service must belong to the selected customer and agree with each other.
     *
     * @param request customer, optional project and service, and initial task details
     * @param adminUser staff account to record as the task creator, when available
     * @return the saved task with its ownership, visibility, status and due date
     * @throws org.springframework.web.server.ResponseStatusException if the title is blank or the service, project and customer do not agree
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    @Transactional
    public WorkspaceTaskResponse createForAdmin(CreateWorkspaceTaskRequest request, AppUserEntity adminUser) {
        ClientEntity client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", request.getClientId()));
        ClientWorkspaceEntity workspace = clientWorkspaceRepository.findByClient(client)
                .orElseGet(() -> createWorkspace(client));
        ClientServiceEntity clientService = resolveClientService(client, request.getClientServiceId());
        ClientProjectEntity project = resolveProject(client, request.getProjectId());

        if (project != null && clientService != null &&
                (project.getClientService() == null ||
                        !Objects.equals(project.getClientService().getId(), clientService.getId()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project does not belong to the selected service");
        }

        if (clientService == null && project != null) {
            clientService = project.getClientService();
        }

        WorkspaceTaskEntity task = new WorkspaceTaskEntity();
        task.setWorkspace(workspace);
        task.setClient(client);
        task.setClientService(clientService);
        task.setProject(project);
        task.setCreatedByUser(adminUser);
        task.setServiceKey(resolveServiceKey(clientService, project));
        task.setTitle(requireText(request.getTitle(), "Task title is required"));
        task.setDescription(cleanNullable(request.getDescription()));
        task.setStatus(WorkspaceTaskStatus.parse(request.getStatus()).value());
        task.setPriority(WorkspaceTaskPriority.parse(request.getPriority()).value());
        task.setOwnerRole(WorkspaceTaskOwnerRole.parse(request.getOwnerRole()).value());
        task.setVisibility(WorkspaceTaskVisibility.parse(request.getVisibility()).value());
        task.setDueAt(request.getDueAt());
        syncCompletedAt(task);

        return toResponse(workspaceTaskRepository.save(task));
    }

    /**
     * Applies supplied changes and updates the completion time to match the resulting status. An explicit
     * request to clear the deadline takes precedence over a replacement date.
     *
     * @param taskId identifier of the task being acted on
     * @param request fields to change and an optional instruction to clear the due date
     * @return the task after the changes have been saved
     */
    @Transactional
    public WorkspaceTaskResponse updateForAdmin(UUID taskId, UpdateWorkspaceTaskRequest request) {
        WorkspaceTaskEntity task = findTask(taskId);

        if (request.getTitle() != null) {
            task.setTitle(requireText(request.getTitle(), "Task title cannot be blank"));
        }
        if (request.getDescription() != null) {
            task.setDescription(cleanNullable(request.getDescription()));
        }
        if (request.getStatus() != null) {
            task.setStatus(WorkspaceTaskStatus.parse(request.getStatus()).value());
        }
        if (request.getPriority() != null) {
            task.setPriority(WorkspaceTaskPriority.parse(request.getPriority()).value());
        }
        if (request.getOwnerRole() != null) {
            task.setOwnerRole(WorkspaceTaskOwnerRole.parse(request.getOwnerRole()).value());
        }
        if (request.getVisibility() != null) {
            task.setVisibility(WorkspaceTaskVisibility.parse(request.getVisibility()).value());
        }
        if (Boolean.TRUE.equals(request.getClearDueAt())) {
            task.setDueAt(null);
        } else if (request.getDueAt() != null) {
            task.setDueAt(request.getDueAt());
        }

        syncCompletedAt(task);
        return toResponse(workspaceTaskRepository.save(task));
    }

    /**
     * Removes the task identified by the administrator.
     *
     * @param taskId identifier of the task being acted on
     */
    @Transactional
    public void deleteForAdmin(UUID taskId) {
        workspaceTaskRepository.delete(findTask(taskId));
    }

    /**
     * Creates the customer's workspace when creating their first task requires one.
     *
     * @param client customer account in scope
     * @return the saved workspace named after the customer's company
     */
    private ClientWorkspaceEntity createWorkspace(ClientEntity client) {
        ClientWorkspaceEntity workspace = new ClientWorkspaceEntity();
        workspace.setClient(client);
        workspace.setName(client.getCompany() + " Workspace");
        return clientWorkspaceRepository.save(workspace);
    }

    /**
     * Resolves an optional service assignment and checks that it belongs to the selected customer.
     *
     * @param client customer account in scope
     * @param clientServiceId identifier of the service assignment for this client
     * @return the customer's assignment, or {@code null} when no assignment ID was supplied
     * @throws org.springframework.web.server.ResponseStatusException if the assignment belongs to another customer
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    private ClientServiceEntity resolveClientService(ClientEntity client, UUID clientServiceId) {
        if (clientServiceId == null) {
            return null;
        }

        ClientServiceEntity clientService = clientServiceRepository.findById(clientServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Client service", clientServiceId));

        if (!Objects.equals(clientService.getClient().getId(), client.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service does not belong to the selected client");
        }

        return clientService;
    }

    /**
     * Resolves an optional project and checks that it belongs to the selected customer.
     *
     * @param client customer account in scope
     * @param projectId identifier of the client project
     * @return the customer's project, or {@code null} when no project ID was supplied
     * @throws org.springframework.web.server.ResponseStatusException if the project belongs to another customer
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    private ClientProjectEntity resolveProject(ClientEntity client, UUID projectId) {
        if (projectId == null) {
            return null;
        }

        ClientProjectEntity project = clientProjectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Client project", projectId));

        if (!Objects.equals(project.getClient().getId(), client.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project does not belong to the selected client");
        }

        return project;
    }

    /**
     * Loads the stored task before it is updated or removed.
     *
     * @param taskId identifier of the task being acted on
     * @return the task with the requested identifier
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    private WorkspaceTaskEntity findTask(UUID taskId) {
        return workspaceTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace task", taskId));
    }

    /**
     * Uses the project key when present, otherwise classifies the service by its catalogue name.
     *
     * @param clientService client operations used by this component
     * @param project project
     * @return the task's service key, with {@code general} as the fallback
     */
    private String resolveServiceKey(ClientServiceEntity clientService, ClientProjectEntity project) {
        if (project != null && project.getProjectKey() != null && !project.getProjectKey().isBlank()) {
            return project.getProjectKey().trim().toLowerCase(Locale.ROOT);
        }
        if (clientService == null || clientService.getService() == null) {
            return "general";
        }

        String name = clientService.getService().getName().toLowerCase(Locale.ROOT);
        if (name.contains("booking")) return "booking";
        if (name.contains("crm")) return "crm";
        if (name.contains("automation")) return "automation";
        if (name.contains("dashboard")) return "dashboard";
        if (name.contains("web") || name.contains("seo")) return "web_seo";
        return "general";
    }

    /**
     * Records when a task first reaches a finished status and clears that timestamp when it is reopened.
     *
     * @param task task
     */
    private void syncCompletedAt(WorkspaceTaskEntity task) {
        WorkspaceTaskStatus status = WorkspaceTaskStatus.parse(task.getStatus());
        if (status.isFinished() && task.getCompletedAt() == null) {
            task.setCompletedAt(Instant.now());
        } else if (!status.isFinished()) {
            task.setCompletedAt(null);
        }
    }

    /**
     * Orders tasks by earliest due date, leaving undated tasks at the end.
     *
     * @return a comparator that breaks equal due dates by newest creation time
     */
    private Comparator<WorkspaceTaskEntity> taskOrder() {
        return Comparator
                .comparing(WorkspaceTaskEntity::getDueAt, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(WorkspaceTaskEntity::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
    }

    /**
     * Builds the task displayed in the workspace, including its customer, optional project and optional
     * service assignment.
     *
     * @param task task
     * @return the task details and identifiers needed to locate the related work
     */
    private WorkspaceTaskResponse toResponse(WorkspaceTaskEntity task) {
        ClientServiceEntity clientService = task.getClientService();
        ClientProjectEntity project = task.getProject();
        String serviceName = clientService != null && clientService.getService() != null
                ? clientService.getService().getName()
                : null;

        return new WorkspaceTaskResponse(
                task.getId(),
                task.getWorkspace().getId(),
                task.getClient().getId(),
                task.getClient().getName(),
                task.getClient().getCompany(),
                clientService == null ? null : clientService.getId(),
                task.getServiceKey(),
                serviceName,
                project == null ? null : project.getId(),
                project == null ? null : project.getName(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getOwnerRole(),
                task.getVisibility(),
                task.getCreatedByUser() == null ? null : task.getCreatedByUser().getUsername(),
                task.getDueAt(),
                task.getCompletedAt(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    /**
     * Checks the customer name, company and email for markers used by synthetic test accounts.
     *
     * @param client customer account in scope
     * @return whether to omit the customer's tasks from the staff list
     */
    private boolean isTechnicalFixture(ClientEntity client) {
        String name = normalize(client.getName());
        String company = normalize(client.getCompany());
        String email = normalize(client.getEmail());

        return name.startsWith("e2e ") ||
                name.equals("e2e") ||
                company.startsWith("e2e ") ||
                company.startsWith("altaira e2e") ||
                email.contains("fixture") ||
                email.contains("e2e");
    }

    /**
     * Trims whitespace and lowercases the value for comparison, using an empty string for missing input. Uses locale-independent case normalization.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Trims a required task field and rejects missing or blank content.
     *
     * @param value stored or supplied representation
     * @param message error message returned when the trimmed value is empty
     * @return the non-empty trimmed text
     * @throws org.springframework.web.server.ResponseStatusException if the submitted field is missing or blank
     */
    private String requireText(String value, String message) {
        String cleaned = cleanNullable(value);
        if (cleaned == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return cleaned;
    }

    /**
     * Trims an optional text field and represents blank content as missing.
     *
     * @param value stored or supplied representation
     * @return the trimmed text, or {@code null} for blank or missing input
     */
    private String cleanNullable(String value) {
        if (value == null) {
            return null;
        }
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }
}
