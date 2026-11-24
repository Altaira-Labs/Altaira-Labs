package com.altaira.backend.service;

import com.altaira.backend.dto.clientportal.AdminActionItemResponse;
import com.altaira.backend.dto.clientportal.AdminClientProjectSummaryResponse;
import com.altaira.backend.dto.clientportal.ClientPortalModuleResponse;
import com.altaira.backend.dto.clientportal.ClientPortalResponse;
import com.altaira.backend.dto.clientportal.ClientProjectAssetResponse;
import com.altaira.backend.dto.clientportal.ClientProjectConfigSnapshotResponse;
import com.altaira.backend.dto.clientportal.ClientProjectResponse;
import com.altaira.backend.dto.clientportal.CreateClientProjectConfigSnapshotRequest;
import com.altaira.backend.dto.clientportal.CreateProjectLinkRequest;
import com.altaira.backend.dto.clientportal.ReviewProjectAssetRequest;
import com.altaira.backend.dto.clientportal.SubmitProjectFeedbackRequest;
import com.altaira.backend.dto.clientportal.UpdateClientProjectRequest;
import com.altaira.backend.dto.onboarding.OnboardingDashboardResponse;
import com.altaira.backend.dto.media.CompletePrivateUploadRequest;
import com.altaira.backend.dto.media.PreparePrivateUploadRequest;
import com.altaira.backend.dto.media.UploadUrlResponse;
import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.ClientProjectAssetEntity;
import com.altaira.backend.entity.ClientProjectConfigSnapshotEntity;
import com.altaira.backend.entity.ClientProjectEntity;
import com.altaira.backend.entity.ClientServiceEntity;
import com.altaira.backend.entity.OnboardingTaskEntity;
import com.altaira.backend.exception.ResourceNotFoundException;
import com.altaira.backend.model.ClientServiceStatus;
import com.altaira.backend.model.ProjectPhase;
import com.altaira.backend.repository.ClientProjectAssetRepository;
import com.altaira.backend.repository.ClientProjectConfigSnapshotRepository;
import com.altaira.backend.repository.ClientProjectRepository;
import com.altaira.backend.repository.ClientServiceRepository;
import com.altaira.backend.repository.OnboardingTaskRepository;
import com.altaira.backend.security.ClientAccessContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builds the project, onboarding and resource views that belong to a customer's workspace.
 * <p>Customer operations resolve workspace membership before selecting records. Staff
 * can request an administrative preview for a named customer. Keeping these paths separate
 * lets a consultant review delivery while client operations remain scoped to one account.
 * <p>Asset operations also verify that the referenced project belongs to the customer
 * before exposing a download or changing its content.
 */
@Service
@Transactional
public class ClientPortalService {

    /** External link storage prefix used by client portal service. */
    private static final String EXTERNAL_LINK_STORAGE_PREFIX = "external-link:";

    /** Modules used by client portal service. */
    private static final List<ModuleDefinition> MODULES = List.of(
            new ModuleDefinition("web_seo", "Web & SEO", "Project status, content handoff, staging and launch readiness."),
            new ModuleDefinition("crm", "CRM / Lead Management", "Lead pipeline, follow-up records and sector-specific client fields."),
            new ModuleDefinition("booking", "Booking System", "Appointments, reservations, resources and daily agenda operations."),
            new ModuleDefinition("automation", "Workflow Automation", "Reusable trigger/action recipes for repetitive follow-up work."),
            new ModuleDefinition("dashboard", "Management Dashboard", "Business KPIs and operational analytics for decisions.")
    );

    /** Onboarding operations used by this component. */
    private final OnboardingService onboardingService;
    /** Onboarding template operations used by this component. */
    private final OnboardingTemplateService onboardingTemplateService;
    /** Client management operations used by this component. */
    private final ClientManagementService clientManagementService;
    /** Persistence access for client service. */
    private final ClientServiceRepository clientServiceRepository;
    /** Persistence access for client project. */
    private final ClientProjectRepository clientProjectRepository;
    /** Persistence access for client project asset. */
    private final ClientProjectAssetRepository clientProjectAssetRepository;
    /** Persistence access for client project config snapshot. */
    private final ClientProjectConfigSnapshotRepository clientProjectConfigSnapshotRepository;
    /** Persistence access for onboarding task. */
    private final OnboardingTaskRepository onboardingTaskRepository;
    /** Onboarding file storage operations used by this component. */
    private final OnboardingFileStorageService onboardingFileStorageService;
    /** Media upload URL operations used by this component. */
    private final MediaUploadUrlService mediaUploadUrlService;
    /** Jackson mapper used to read or write JSON. */
    private final ObjectMapper objectMapper;

    /**
     * Creates the client portal service with its injected collaborators.
     *
     * @param onboardingService onboarding operations used by this component
     * @param onboardingTemplateService onboarding template operations used by this component
     * @param clientManagementService client management operations used by this component
     * @param clientServiceRepository persistence access for client service
     * @param clientProjectRepository persistence access for client project
     * @param clientProjectAssetRepository persistence access for client project asset
     * @param clientProjectConfigSnapshotRepository persistence access for client project config snapshot
     * @param onboardingTaskRepository persistence access for onboarding task
     * @param onboardingFileStorageService onboarding file storage operations used by this component
     * @param mediaUploadUrlService media upload URL operations used by this component
     * @param objectMapper Jackson mapper used to read or write JSON
     */
    public ClientPortalService(
            OnboardingService onboardingService,
            OnboardingTemplateService onboardingTemplateService,
            ClientManagementService clientManagementService,
            ClientServiceRepository clientServiceRepository,
            ClientProjectRepository clientProjectRepository,
            ClientProjectAssetRepository clientProjectAssetRepository,
            ClientProjectConfigSnapshotRepository clientProjectConfigSnapshotRepository,
            OnboardingTaskRepository onboardingTaskRepository,
            OnboardingFileStorageService onboardingFileStorageService,
            MediaUploadUrlService mediaUploadUrlService,
            ObjectMapper objectMapper
    ) {
        this.onboardingService = onboardingService;
        this.onboardingTemplateService = onboardingTemplateService;
        this.clientManagementService = clientManagementService;
        this.clientServiceRepository = clientServiceRepository;
        this.clientProjectRepository = clientProjectRepository;
        this.clientProjectAssetRepository = clientProjectAssetRepository;
        this.clientProjectConfigSnapshotRepository = clientProjectConfigSnapshotRepository;
        this.onboardingTaskRepository = onboardingTaskRepository;
        this.onboardingFileStorageService = onboardingFileStorageService;
        this.mediaUploadUrlService = mediaUploadUrlService;
        this.objectMapper = objectMapper;
    }

    /**
     * Resolves the signed-in client and assembles the workspace information visible to that customer.
     *
     * @param context context carrying client access
     * @return portal
     */
    public ClientPortalResponse getPortal(ClientAccessContext context) {
        OnboardingDashboardResponse onboarding = onboardingService.getClientDashboard(context);
        return buildPortal(context.client(), onboarding);
    }

    /**
     * Builds a staff-facing view of the selected customer’s portal for review.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @return portal for admin
     */
    public ClientPortalResponse getPortalForAdmin(UUID clientId) {
        OnboardingDashboardResponse onboarding = onboardingService.getAdminDashboard(clientId);
        ClientEntity client = clientManagementService.findClientEntity(clientId);
        return buildPortal(client, onboarding);
    }

    /**
     * Lists projects as admin.
     *
     * @return the admin client project summary response items
     */
    @Transactional(readOnly = true)
    public List<AdminClientProjectSummaryResponse> listProjectsAsAdmin() {
        return clientProjectRepository.findAllByOrderByUpdatedAtDesc()
                .stream()
                .filter(project -> !isTechnicalFixture(project.getClient()))
                .map(this::mapAdminProjectSummary)
                .toList();
    }

    /**
     * Lists action items as admin.
     *
     * @return the admin action item response items
     */
    @Transactional(readOnly = true)
    public List<AdminActionItemResponse> listActionItemsAsAdmin() {
        List<AdminActionItemResponse> actions = new ArrayList<>();

        onboardingTaskRepository.findAllByStatusOrderBySubmittedAtAsc("submitted")
                .stream()
                .filter(task -> !isTechnicalFixture(task.getClient()))
                .map(this::mapOnboardingAction)
                .forEach(actions::add);

        clientProjectAssetRepository.findAllByStatusOrderByUploadedAtAsc("uploaded")
                .stream()
                .filter(asset -> !isTechnicalFixture(asset.getClient()))
                .map(this::mapResourceAction)
                .forEach(actions::add);

        clientProjectRepository.findAllByOrderByUpdatedAtDesc()
                .stream()
                .filter(project -> project.getRevisionPendingAt() != null)
                .filter(project -> !isTechnicalFixture(project.getClient()))
                .map(this::mapFeedbackAction)
                .forEach(actions::add);

        return actions.stream()
                .sorted(
                        Comparator.comparing(AdminActionItemResponse::critical)
                                .reversed()
                                .thenComparing(
                                        AdminActionItemResponse::actionAt,
                                        Comparator.nullsLast(Comparator.naturalOrder())
                                )
                )
                .toList();
    }

    /**
     * Updates project as admin. Persists changes through {@code clientProjectRepository}.
     *
     * @param projectId identifier of the client project
     * @param request input carrying update client project
     * @return the client project response
     */
    public ClientProjectResponse updateProjectAsAdmin(UUID projectId, UpdateClientProjectRequest request) {
        ClientProjectEntity project = findProject(projectId);

        if (request.getName() != null && !request.getName().isBlank()) {
            project.setName(trimToNull(request.getName()));
        }

        if (request.getCurrentPhase() != null && !request.getCurrentPhase().isBlank()) {
            ProjectPhase phase = ProjectPhase.parse(request.getCurrentPhase());
            project.setCurrentPhase(phase.value());

            if (phase != ProjectPhase.REVIEW) {
                project.setRevisionPendingAt(null);
            }
        }

        if (request.getStagingUrl() != null) {
            String stagingUrl = trimToNull(request.getStagingUrl());
            project.setStagingUrl(stagingUrl == null ? null : normalizeExternalUrl(stagingUrl));
        }

        return mapProject(clientProjectRepository.save(project));
    }

    /**
     * Lists project assets.
     *
     * @param context context carrying client access
     * @param projectId identifier of the client project
     * @return the client project asset response items
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (locked)
     */
    public List<ClientProjectAssetResponse> listProjectAssets(
            ClientAccessContext context,
            UUID projectId
    ) {
        OnboardingDashboardResponse onboarding = onboardingService.getClientDashboard(context);

        if (!onboarding.isContractApproved()) {
            throw new ResponseStatusException(
                    HttpStatus.LOCKED,
                    "Client dashboard is locked until the service contract is approved"
            );
        }

        ClientProjectEntity project = findProject(projectId);
        ensureProjectBelongsToClient(project, context.client());

        return clientProjectAssetRepository.findAllByProjectOrderByUploadedAtAsc(project)
                .stream()
                .map(this::mapAsset)
                .toList();
    }

    /**
     * Uploads project assets. Persists changes through {@code clientProjectAssetRepository}.
     *
     * @param context context carrying client access
     * @param projectId identifier of the client project
     * @param files files
     * @param assetType asset type
     * @param notes free-text notes associated with the item
     * @return the client project asset response items
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (locked)
     * @throws IllegalArgumentException when validation reports “At least one project asset file is required”
     */
    public List<ClientProjectAssetResponse> uploadProjectAssets(
            ClientAccessContext context,
            UUID projectId,
            List<MultipartFile> files,
            String assetType,
            String notes
    ) {
        OnboardingDashboardResponse onboarding = onboardingService.getClientDashboard(context);

        if (!onboarding.isContractApproved()) {
            throw new ResponseStatusException(
                    HttpStatus.LOCKED,
                    "Client dashboard is locked until the service contract is approved"
            );
        }

        ClientProjectEntity project = findProject(projectId);
        ensureProjectBelongsToClient(project, context.client());

        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            throw new IllegalArgumentException("At least one project asset file is required");
        }

        List<ClientProjectAssetResponse> uploadedAssets = new ArrayList<>();
        String normalizedAssetType = normalizeAssetType(assetType);
        String safeNotes = trimToLimit(notes, 2000);

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            var storedFile = onboardingFileStorageService.storeProjectAsset(context.client().getId(), project.getId(), file);

            ClientProjectAssetEntity asset = new ClientProjectAssetEntity();
            asset.setProject(project);
            asset.setClient(context.client());
            asset.setUploadedByUser(context.user());
            asset.setAssetType(normalizedAssetType);
            asset.setNotes(safeNotes);
            asset.setOriginalFilename(storedFile.originalFilename());
            asset.setStoredFilename(storedFile.storedFilename());
            asset.setStorageKey(storedFile.storageKey());
            asset.setContentType(storedFile.contentType());
            asset.setSizeBytes(storedFile.sizeBytes());
            asset.setChecksumSha256(storedFile.checksumSha256());
            asset.setStatus("uploaded");

            uploadedAssets.add(mapAsset(clientProjectAssetRepository.save(asset)));
        }

        return uploadedAssets;
    }

    /**
     * Prepares project asset upload.
     *
     * @param context context carrying client access
     * @param projectId identifier of the client project
     * @param request input carrying prepare private upload
     * @return the upload URL response
     */
    public UploadUrlResponse prepareProjectAssetUpload(
            ClientAccessContext context,
            UUID projectId,
            PreparePrivateUploadRequest request
    ) {
        requireUnlockedPortal(context);
        ClientProjectEntity project = findProject(projectId);
        ensureProjectBelongsToClient(project, context.client());
        String assetType = normalizeAssetType(request.getAssetType());

        return mediaUploadUrlService.createContextUploadUrl(
                context.client().getId(),
                storageFolderForAssetType(assetType),
                project.getProjectKey(),
                "project",
                project.getId(),
                request
        );
    }

    /**
     * Validates the uploaded project asset and completes its registration in the workspace.
     *
     * @param context context carrying client access
     * @param projectId identifier of the client project
     * @param request input carrying complete private upload
     * @return the client project asset response
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (conflict)
     */
    public ClientProjectAssetResponse completeProjectAssetUpload(
            ClientAccessContext context,
            UUID projectId,
            CompletePrivateUploadRequest request
    ) {
        requireUnlockedPortal(context);
        ClientProjectEntity project = findProject(projectId);
        ensureProjectBelongsToClient(project, context.client());
        String assetType = normalizeAssetType(request.getAssetType());

        var verified = mediaUploadUrlService.verifyCompletedUpload(
                context.client().getId(),
                storageFolderForAssetType(assetType),
                project.getProjectKey(),
                "project",
                project.getId(),
                request.getBucket(),
                request.getObjectKey(),
                request.getFilename(),
                request.getContentType(),
                request.getSizeBytes()
        );

        var existing = clientProjectAssetRepository.findByStorageKey(verified.storageKey());
        if (existing.isPresent()) {
            ClientProjectAssetEntity existingAsset = existing.get();
            if (!existingAsset.getClient().getId().equals(context.client().getId()) ||
                    !existingAsset.getProject().getId().equals(project.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Uploaded object is already assigned to another context");
            }
            return mapAsset(existingAsset);
        }

        ClientProjectAssetEntity asset = new ClientProjectAssetEntity();
        asset.setProject(project);
        asset.setClient(context.client());
        asset.setUploadedByUser(context.user());
        asset.setAssetType(assetType);
        asset.setNotes(trimToLimit(request.getNotes(), 2000));
        asset.setOriginalFilename(verified.originalFilename());
        asset.setStoredFilename(verified.storedFilename());
        asset.setStorageKey(verified.storageKey());
        asset.setContentType(verified.contentType());
        asset.setSizeBytes(verified.sizeBytes());
        asset.setChecksumSha256(verified.checksumSha256());
        asset.setStatus("uploaded");

        try {
            return mapAsset(clientProjectAssetRepository.save(asset));
        } catch (RuntimeException ex) {
            mediaUploadUrlService.deleteStoredObjectQuietly(verified.storageKey());
            throw ex;
        }
    }

    /**
     * Creates project link. Persists changes through {@code clientProjectAssetRepository}.
     *
     * @param context context carrying client access
     * @param projectId identifier of the client project
     * @param request input carrying create project link
     * @return the client project asset response
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (locked)
     */
    public ClientProjectAssetResponse createProjectLink(
            ClientAccessContext context,
            UUID projectId,
            CreateProjectLinkRequest request
    ) {
        OnboardingDashboardResponse onboarding = onboardingService.getClientDashboard(context);

        if (!onboarding.isContractApproved()) {
            throw new ResponseStatusException(
                    HttpStatus.LOCKED,
                    "Client dashboard is locked until the service contract is approved"
            );
        }

        ClientProjectEntity project = findProject(projectId);
        ensureProjectBelongsToClient(project, context.client());

        String safeUrl = normalizeExternalUrl(request.getUrl());
        String label = trimToLimit(request.getLabel(), 160);

        if (label == null || label.isBlank()) {
            label = URI.create(safeUrl).getHost();
        }

        ClientProjectAssetEntity asset = new ClientProjectAssetEntity();
        asset.setProject(project);
        asset.setClient(context.client());
        asset.setUploadedByUser(context.user());
        asset.setAssetType(normalizeAssetType(request.getAssetType() == null ? "external_link" : request.getAssetType()));
        asset.setNotes(trimToLimit(request.getNotes(), 2000));
        asset.setOriginalFilename(label);
        asset.setStoredFilename("external-link");
        asset.setStorageKey(EXTERNAL_LINK_STORAGE_PREFIX + UUID.randomUUID() + ":" + safeUrl);
        asset.setContentType("text/uri-list");
        asset.setSizeBytes(0);
        asset.setChecksumSha256(null);
        asset.setStatus("uploaded");

        return mapAsset(clientProjectAssetRepository.save(asset));
    }

    /**
     * Lists project assets as admin.
     *
     * @param projectId identifier of the client project
     * @return the client project asset response items
     */
    @Transactional(readOnly = true)
    public List<ClientProjectAssetResponse> listProjectAssetsAsAdmin(UUID projectId) {
        ClientProjectEntity project = findProject(projectId);
        return clientProjectAssetRepository.findAllByProjectOrderByUploadedAtAsc(project)
                .stream()
                .map(this::mapAsset)
                .toList();
    }

    /**
     * Lists project config snapshots as admin.
     *
     * @param projectId identifier of the client project
     * @return the client project config snapshot response items
     */
    @Transactional(readOnly = true)
    public List<ClientProjectConfigSnapshotResponse> listProjectConfigSnapshotsAsAdmin(UUID projectId) {
        ClientProjectEntity project = findProject(projectId);
        return clientProjectConfigSnapshotRepository.findAllByProjectOrderByCreatedAtDesc(project)
                .stream()
                .map(this::mapConfigSnapshot)
                .toList();
    }

    /**
     * Creates project config snapshot as admin. Persists changes through {@code clientProjectConfigSnapshotRepository}.
     *
     * @param projectId identifier of the client project
     * @param request input carrying create client project config snapshot
     * @param createdByUsername created by username
     * @return the client project config snapshot response
     */
    public ClientProjectConfigSnapshotResponse createProjectConfigSnapshotAsAdmin(
            UUID projectId,
            CreateClientProjectConfigSnapshotRequest request,
            String createdByUsername
    ) {
        ClientProjectEntity project = findProject(projectId);
        var jsonNode = parseConfigSnapshotJson(request.getConfigJson());

        ClientProjectConfigSnapshotEntity snapshot = new ClientProjectConfigSnapshotEntity();
        snapshot.setProject(project);
        snapshot.setClient(project.getClient());
        snapshot.setServiceKey(normalizeAssetType(project.getProjectKey()));
        snapshot.setArtifactType(normalizeAssetType(request.getArtifactType()));
        snapshot.setLabel(trimToLimit(request.getLabel(), 180));
        snapshot.setConfigJson(jsonNode.toPrettyString());
        snapshot.setCreatedByUsername(trimToLimit(createdByUsername, 180));

        return mapConfigSnapshot(clientProjectConfigSnapshotRepository.save(snapshot));
    }

    /**
     * Retrieves project asset download as admin.
     *
     * @param assetId identifier of the asset
     * @return project asset download as admin
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    @Transactional(readOnly = true)
    public ClientProjectAssetDownload getProjectAssetDownloadAsAdmin(UUID assetId) {
        ClientProjectAssetEntity asset = findAsset(assetId);

        if (isExternalLink(asset)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "External project links cannot be downloaded");
        }

        try {
            return new ClientProjectAssetDownload(asset, onboardingFileStorageService.open(asset.getStorageKey()));
        } catch (IOException ex) {
            throw new ResourceNotFoundException("Stored project asset", assetId);
        }
    }

    /**
     * Retrieves project asset download.
     *
     * @param context context carrying client access
     * @param assetId identifier of the asset
     * @return project asset download
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (forbidden, bad request)
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    @Transactional(readOnly = true)
    public ClientProjectAssetDownload getProjectAssetDownload(
            ClientAccessContext context,
            UUID assetId
    ) {
        ClientProjectAssetEntity asset = findAsset(assetId);

        if (!asset.getClient().getId().equals(context.client().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access to this client project asset");
        }

        if (isExternalLink(asset)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "External project links cannot be downloaded");
        }

        try {
            return new ClientProjectAssetDownload(asset, onboardingFileStorageService.open(asset.getStorageKey()));
        } catch (IOException ex) {
            throw new ResourceNotFoundException("Stored project asset", assetId);
        }
    }

    /**
     * Requires unlocked portal.
     *
     * @param context context carrying client access
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (locked)
     */
    private void requireUnlockedPortal(ClientAccessContext context) {
        OnboardingDashboardResponse onboarding = onboardingService.getClientDashboard(context);
        if (!onboarding.isContractApproved()) {
            throw new ResponseStatusException(
                    HttpStatus.LOCKED,
                    "Client dashboard is locked until the service contract is approved"
            );
        }
    }

    /**
     * Selects the storage folder used by this category of project asset.
     *
     * @param assetType asset type
     * @return the resulting text
     */
    private String storageFolderForAssetType(String assetType) {
        String normalized = normalizeAssetType(assetType);
        if (normalized.matches(".*(brand|logo|website|content|seo).*")) {
            return "branding";
        }
        if (normalized.matches(".*(contract|agreement|legal|privacy|nda|identity|document).*")) {
            return "legal";
        }
        return "multimedia";
    }

    /**
     * Approves project asset as admin. Persists changes through {@code clientProjectAssetRepository}.
     *
     * @param assetId identifier of the asset
     * @return the client project asset response
     */
    public ClientProjectAssetResponse approveProjectAssetAsAdmin(UUID assetId) {
        ClientProjectAssetEntity asset = findAsset(assetId);
        asset.setStatus("approved");
        asset.setAdminFeedback(null);
        asset.setReviewedAt(Instant.now());
        return mapAsset(clientProjectAssetRepository.save(asset));
    }

    /**
     * Rejects project asset as admin. Persists changes through {@code clientProjectAssetRepository}.
     *
     * @param assetId identifier of the asset
     * @param request input carrying review project asset
     * @return the client project asset response
     */
    public ClientProjectAssetResponse rejectProjectAssetAsAdmin(UUID assetId, ReviewProjectAssetRequest request) {
        ClientProjectAssetEntity asset = findAsset(assetId);
        asset.setStatus("rejected");
        asset.setAdminFeedback(trimToLimit(request == null ? null : request.getFeedback(), 1000));
        asset.setReviewedAt(Instant.now());
        return mapAsset(clientProjectAssetRepository.save(asset));
    }

    /**
     * Submits project feedback. Persists changes through {@code clientProjectRepository}.
     *
     * @param context context carrying client access
     * @param projectId identifier of the client project
     * @param request input carrying submit project feedback
     * @return the client project response
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (locked, forbidden)
     */
    public ClientProjectResponse submitProjectFeedback(
            ClientAccessContext context,
            UUID projectId,
            SubmitProjectFeedbackRequest request
    ) {
        OnboardingDashboardResponse onboarding = onboardingService.getClientDashboard(context);

        if (!onboarding.isContractApproved()) {
            throw new ResponseStatusException(
                    HttpStatus.LOCKED,
                    "Client dashboard is locked until the service contract is approved"
            );
        }

        ClientProjectEntity project = findProject(projectId);

        if (!project.getClient().getId().equals(context.client().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access to this client project");
        }

        project.setLatestClientFeedback(trimToLimit(request.getFeedback(), 2000));
        project.setRevisionPendingAt(Instant.now());
        project.setCurrentPhase(ProjectPhase.REVIEW.value());

        return mapProject(clientProjectRepository.save(project));
    }

    /**
     * Builds portal.
     *
     * @param client customer account in scope
     * @param onboarding onboarding
     * @return the client portal response
     */
    private ClientPortalResponse buildPortal(ClientEntity client, OnboardingDashboardResponse onboarding) {
        List<ClientServiceEntity> activeAssignments = clientServiceRepository.findAllByClientOrderByCreatedAtDesc(client)
                .stream()
                .filter(this::isVisibleClientService)
                .toList();

        Map<String, ClientServiceEntity> assignmentByModule = activeAssignments.stream()
                .collect(Collectors.toMap(
                        onboardingTemplateService::serviceKey,
                        Function.identity(),
                        this::preferMostRelevantAssignment
                ));

        ensureModuleProjects(client, assignmentByModule);

        List<ClientPortalModuleResponse> modules = MODULES.stream()
                .map(module -> mapModule(module, assignmentByModule.get(module.moduleKey())))
                .toList();

        List<ClientProjectResponse> projects = clientProjectRepository.findAllByClientOrderByCreatedAtAsc(client)
                .stream()
                .sorted(Comparator.comparing(ClientProjectEntity::getCreatedAt))
                .map(this::mapProject)
                .toList();

        return new ClientPortalResponse(
                onboarding.getWorkspaceId(),
                onboarding.getWorkspaceName(),
                onboarding.getStatus(),
                clientManagementService.map(client),
                onboarding.isOnboardingCompleted(),
                onboarding.isContractApproved(),
                modules,
                projects,
                onboarding.getAccessRole(),
                onboarding.isCanEdit()
        );
    }

    /**
     * Checks whether this is visible client service.
     *
     * @param assignment assignment
     * @return true if the value is visible client service
     */
    private boolean isVisibleClientService(ClientServiceEntity assignment) {
        return assignment.getStatus() != null &&
                !ClientServiceStatus.CANCELLED.value().equals(assignment.getStatus());
    }

    /**
     * Chooses which service assignment to display when several refer to the same module.
     *
     * @param current current
     * @param candidate candidate
     * @return the client service record
     */
    private ClientServiceEntity preferMostRelevantAssignment(ClientServiceEntity current, ClientServiceEntity candidate) {
        if (ClientServiceStatus.IN_PROGRESS.value().equals(candidate.getStatus())) {
            return candidate;
        }

        if (ClientServiceStatus.IN_PROGRESS.value().equals(current.getStatus())) {
            return current;
        }

        return current.getCreatedAt().isAfter(candidate.getCreatedAt()) ? current : candidate;
    }

    /**
     * Ensures module projects. Persists changes through {@code clientProjectRepository}.
     *
     * @param client customer account in scope
     * @param assignmentByModule assignment by module
     */
    private void ensureModuleProjects(ClientEntity client, Map<String, ClientServiceEntity> assignmentByModule) {
        MODULES.forEach(module -> {
            ClientServiceEntity assignment = assignmentByModule.get(module.moduleKey());
            if (assignment == null) {
                return;
            }

            Optional<ClientProjectEntity> existingProject = clientProjectRepository.findByClientAndClientServiceAndProjectKey(
                    client,
                    assignment,
                    module.moduleKey()
            );

            if (existingProject.isPresent()) {
                return;
            }

            ClientProjectEntity project = new ClientProjectEntity();
            project.setClient(client);
            project.setClientService(assignment);
            project.setProjectKey(module.moduleKey());
            project.setName(module.title() + " project");
            project.setCurrentPhase(ProjectPhase.REQUIREMENTS.value());
            clientProjectRepository.save(project);
        });
    }

    /**
     * Maps module.
     *
     * @param module module
     * @param assignment assignment
     * @return the client portal module response
     */
    private ClientPortalModuleResponse mapModule(ModuleDefinition module, ClientServiceEntity assignment) {
        boolean active = assignment != null;

        return new ClientPortalModuleResponse(
                module.moduleKey(),
                module.title(),
                module.description(),
                active,
                !active,
                active ? assignment.getStatus() : "locked",
                active ? assignment.getId() : null,
                active ? assignment.getService().getName() : null
        );
    }

    /**
     * Maps project.
     *
     * @param project project
     * @return the client project response
     */
    private ClientProjectResponse mapProject(ClientProjectEntity project) {
        return new ClientProjectResponse(
                project.getId(),
                project.getClientService() == null ? null : project.getClientService().getId(),
                project.getProjectKey(),
                project.getName(),
                project.getCurrentPhase(),
                project.getStagingUrl(),
                project.getLatestClientFeedback(),
                project.getRevisionPendingAt(),
                clientProjectAssetRepository.findAllByProjectOrderByUploadedAtAsc(project)
                        .stream()
                        .map(this::mapAsset)
                        .toList(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    /**
     * Maps admin project summary.
     *
     * @param project project
     * @return the admin client project summary response
     */
    private AdminClientProjectSummaryResponse mapAdminProjectSummary(ClientProjectEntity project) {
        ClientEntity client = project.getClient();
        ClientServiceEntity assignment = project.getClientService();

        return new AdminClientProjectSummaryResponse(
                project.getId(),
                client.getId(),
                client.getName(),
                client.getCompany(),
                client.getStatus(),
                assignment == null ? null : assignment.getId(),
                assignment == null || assignment.getService() == null
                        ? null
                        : assignment.getService().getName(),
                project.getProjectKey(),
                project.getName(),
                project.getCurrentPhase(),
                project.getRevisionPendingAt() != null,
                project.getRevisionPendingAt(),
                project.getUpdatedAt()
        );
    }

    /**
     * Maps onboarding action.
     *
     * @param task task
     * @return the admin action item response
     */
    private AdminActionItemResponse mapOnboardingAction(OnboardingTaskEntity task) {
        ClientServiceEntity assignment = task.getClientService();

        return new AdminActionItemResponse(
                task.getId(),
                "onboarding_review",
                task.getTitle(),
                task.getClient().getId(),
                task.getClient().getName(),
                task.getClient().getCompany(),
                null,
                null,
                task.getServiceKey(),
                assignment == null || assignment.getService() == null
                        ? null
                        : assignment.getService().getName(),
                task.isCritical(),
                task.getSubmittedAt()
        );
    }

    /**
     * Maps resource action.
     *
     * @param asset asset
     * @return the admin action item response
     */
    private AdminActionItemResponse mapResourceAction(ClientProjectAssetEntity asset) {
        ClientProjectEntity project = asset.getProject();
        ClientServiceEntity assignment = project.getClientService();

        return new AdminActionItemResponse(
                asset.getId(),
                "resource_review",
                "Review " + asset.getOriginalFilename(),
                asset.getClient().getId(),
                asset.getClient().getName(),
                asset.getClient().getCompany(),
                project.getId(),
                project.getName(),
                project.getProjectKey(),
                assignment == null || assignment.getService() == null
                        ? null
                        : assignment.getService().getName(),
                false,
                asset.getUploadedAt()
        );
    }

    /**
     * Maps feedback action.
     *
     * @param project project
     * @return the admin action item response
     */
    private AdminActionItemResponse mapFeedbackAction(ClientProjectEntity project) {
        ClientServiceEntity assignment = project.getClientService();

        return new AdminActionItemResponse(
                project.getId(),
                "feedback_review",
                "Review client feedback",
                project.getClient().getId(),
                project.getClient().getName(),
                project.getClient().getCompany(),
                project.getId(),
                project.getName(),
                project.getProjectKey(),
                assignment == null || assignment.getService() == null
                        ? null
                        : assignment.getService().getName(),
                false,
                project.getRevisionPendingAt()
        );
    }

    /**
     * Checks whether this is technical fixture.
     *
     * @param client customer account in scope
     * @return true if the value is technical fixture
     */
    private boolean isTechnicalFixture(ClientEntity client) {
        String name = normalizeForFixtureCheck(client.getName());
        String company = normalizeForFixtureCheck(client.getCompany());
        String email = normalizeForFixtureCheck(client.getEmail());

        return name.startsWith("e2e ") ||
                name.equals("e2e") ||
                company.startsWith("e2e ") ||
                company.startsWith("altaira e2e") ||
                email.endsWith("@example.com") ||
                email.endsWith("@example.test") ||
                email.endsWith(".test");
    }

    /**
     * Normalizes for fixture check. Uses locale-independent case normalization.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String normalizeForFixtureCheck(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Finds project. Looks up the referenced record by identifier.
     *
     * @param projectId identifier of the client project
     * @return the client project record
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    private ClientProjectEntity findProject(UUID projectId) {
        return clientProjectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Client project", projectId));
    }

    /**
     * Finds asset. Looks up the referenced record by identifier.
     *
     * @param assetId identifier of the asset
     * @return the client project asset record
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    private ClientProjectAssetEntity findAsset(UUID assetId) {
        return clientProjectAssetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Client project asset", assetId));
    }

    /**
     * Checks the project’s customer before any client-scoped operation proceeds.
     *
     * @param project project
     * @param client customer account in scope
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (forbidden)
     */
    private void ensureProjectBelongsToClient(ClientProjectEntity project, ClientEntity client) {
        if (!project.getClient().getId().equals(client.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access to this client project");
        }
    }

    /**
     * Normalizes asset type.
     *
     * @param assetType asset type
     * @return the resulting text
     */
    private String normalizeAssetType(String assetType) {
        String normalized = trimToLimit(assetType, 80);

        if (normalized == null || normalized.isBlank()) {
            return "general";
        }

        return normalized.toLowerCase().replaceAll("[^a-z0-9_-]", "_");
    }

    /**
     * Maps asset.
     *
     * @param asset asset
     * @return the client project asset response
     */
    private ClientProjectAssetResponse mapAsset(ClientProjectAssetEntity asset) {
        return new ClientProjectAssetResponse(
                asset.getId(),
                asset.getProject().getId(),
                asset.getClient().getId(),
                asset.getAssetType(),
                asset.getNotes(),
                asset.getOriginalFilename(),
                asset.getContentType(),
                asset.getSizeBytes(),
                asset.getChecksumSha256(),
                externalUrl(asset),
                asset.getStatus(),
                asset.getAdminFeedback(),
                asset.getUploadedAt(),
                asset.getReviewedAt()
        );
    }

    /**
     * Maps config snapshot.
     *
     * @param snapshot snapshot
     * @return the client project config snapshot response
     */
    private ClientProjectConfigSnapshotResponse mapConfigSnapshot(ClientProjectConfigSnapshotEntity snapshot) {
        return new ClientProjectConfigSnapshotResponse(
                snapshot.getId(),
                snapshot.getProject().getId(),
                snapshot.getClient().getId(),
                snapshot.getServiceKey(),
                snapshot.getArtifactType(),
                snapshot.getLabel(),
                snapshot.getConfigJson(),
                snapshot.getCreatedByUsername(),
                snapshot.getCreatedAt()
        );
    }

    /**
     * Parses config snapshot JSON.
     *
     * @param configJson JSON representation of config
     * @return the value produced by this operation
     * @throws IllegalArgumentException when validation reports “Configuration snapshot must be a JSON object” or “Configuration snapshot must be valid JSON”
     */
    private com.fasterxml.jackson.databind.JsonNode parseConfigSnapshotJson(String configJson) {
        try {
            var jsonNode = objectMapper.readTree(configJson);

            if (jsonNode == null || !jsonNode.isObject()) {
                throw new IllegalArgumentException("Configuration snapshot must be a JSON object");
            }

            return jsonNode;
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Configuration snapshot must be valid JSON");
        }
    }

    /**
     * Normalizes external URL.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     * @throws IllegalArgumentException when validation reports “Project link must be a valid http or https URL”
     */
    private String normalizeExternalUrl(String value) {
        String safeUrl = trimToLimit(value, 500);

        try {
            URI uri = URI.create(safeUrl);
            String scheme = uri.getScheme();

            if (scheme == null || uri.getHost() == null ||
                    (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
                throw new IllegalArgumentException("Project link must be a valid http or https URL");
            }

            return uri.toString();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Project link must be a valid http or https URL");
        }
    }

    /**
     * Checks whether this is external link.
     *
     * @param asset asset
     * @return true if the value is external link
     */
    private boolean isExternalLink(ClientProjectAssetEntity asset) {
        return asset.getStorageKey() != null && asset.getStorageKey().startsWith(EXTERNAL_LINK_STORAGE_PREFIX);
    }

    /**
     * Reads the external link associated with the project asset.
     *
     * @param asset asset
     * @return the resulting text
     */
    private String externalUrl(ClientProjectAssetEntity asset) {
        if (!isExternalLink(asset)) {
            return null;
        }

        String value = asset.getStorageKey().substring(EXTERNAL_LINK_STORAGE_PREFIX.length());
        int separatorIndex = value.indexOf(':');
        return separatorIndex >= 0 ? value.substring(separatorIndex + 1) : value;
    }

    /**
     * Trims surrounding whitespace. Absent values return null.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    /**
     * Trims surrounding whitespace and caps the result at the supplied length. Absent values use an empty string.
     *
     * @param value stored or supplied representation
     * @param maxLength max length
     * @return the resulting text
     */
    private String trimToLimit(String value, int maxLength) {
        if (value == null) {
            return "";
        }

        String trimmed = value.trim();
        return trimmed.length() <= maxLength ? trimmed : trimmed.substring(0, maxLength);
    }

    /**
     * Groups the client project asset download data used by client portal service.
     *
     * @param asset asset
     * @param inputStream input stream
     */
    public record ClientProjectAssetDownload(ClientProjectAssetEntity asset, InputStream inputStream) {}

    /**
     * Groups the module definition data used by client portal service.
     *
     * @param moduleKey key identifying the module
     * @param title short heading shown to a reader
     * @param description human-readable description
     */
    private record ModuleDefinition(String moduleKey, String title, String description) {}
}
