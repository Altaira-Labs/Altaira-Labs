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
 * Admin/client task views and status transitions. Altaira tasks are the client-visible source of truth.
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
     * Lists for admin.
     *
     * @return the workspace task response items
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
     * Lists the tasks the signed-in customer may see in their selected workspace.
     *
     * @param context context carrying client access
     * @return the workspace task response items
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
     * Lists for admin client preview. Looks up the referenced record by identifier.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @return the workspace task response items
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
     * Creates for admin. Persists changes through {@code workspaceTaskRepository}.
     *
     * @param request input carrying create workspace task
     * @param adminUser internal user performing the action
     * @return the workspace task response
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
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
     * Updates for admin. Persists changes through {@code workspaceTaskRepository}.
     *
     * @param taskId identifier of the task being acted on
     * @param request input carrying update workspace task
     * @return the workspace task response
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
     * Deletes for admin. Persists changes through {@code workspaceTaskRepository}.
     *
     * @param taskId identifier of the task being acted on
     */
    @Transactional
    public void deleteForAdmin(UUID taskId) {
        workspaceTaskRepository.delete(findTask(taskId));
    }

    /**
     * Creates workspace. Persists changes through {@code clientWorkspaceRepository}.
     *
     * @param client customer account in scope
     * @return the client workspace record
     */
    private ClientWorkspaceEntity createWorkspace(ClientEntity client) {
        ClientWorkspaceEntity workspace = new ClientWorkspaceEntity();
        workspace.setClient(client);
        workspace.setName(client.getCompany() + " Workspace");
        return clientWorkspaceRepository.save(workspace);
    }

    /**
     * Resolves client service. Looks up the referenced record by identifier.
     *
     * @param client customer account in scope
     * @param clientServiceId identifier of the service assignment for this client
     * @return the client service record
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
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
     * Resolves project. Looks up the referenced record by identifier.
     *
     * @param client customer account in scope
     * @param projectId identifier of the client project
     * @return the client project record
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
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
     * Finds task. Looks up the referenced record by identifier.
     *
     * @param taskId identifier of the task being acted on
     * @return the workspace task record
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    private WorkspaceTaskEntity findTask(UUID taskId) {
        return workspaceTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace task", taskId));
    }

    /**
     * Resolves service key. Uses locale-independent case normalization.
     *
     * @param clientService client operations used by this component
     * @param project project
     * @return the resulting text
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
     * Synchronizes completed at.
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
     * Defines the ordering of tasks shown in a workspace.
     *
     * @return the value produced by this operation
     */
    private Comparator<WorkspaceTaskEntity> taskOrder() {
        return Comparator
                .comparing(WorkspaceTaskEntity::getDueAt, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(WorkspaceTaskEntity::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
    }

    /**
     * Converts the value to response.
     *
     * @param task task
     * @return response
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
     * Checks whether this is technical fixture.
     *
     * @param client customer account in scope
     * @return true if the value is technical fixture
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
     * Requires text.
     *
     * @param value stored or supplied representation
     * @param message text associated with the operation
     * @return the resulting text
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
     */
    private String requireText(String value, String message) {
        String cleaned = cleanNullable(value);
        if (cleaned == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return cleaned;
    }

    /**
     * Trims surrounding whitespace. Absent values return null.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String cleanNullable(String value) {
        if (value == null) {
            return null;
        }
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }
}
