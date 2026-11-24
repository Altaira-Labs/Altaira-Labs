package com.altaira.backend.service;

import com.altaira.backend.dto.client.ClientResponse;
import com.altaira.backend.dto.onboarding.OnboardingDashboardResponse;
import com.altaira.backend.dto.onboarding.OnboardingFileResponse;
import com.altaira.backend.dto.onboarding.OnboardingTaskResponse;
import com.altaira.backend.dto.onboarding.SubmitOnboardingTaskRequest;
import com.altaira.backend.dto.media.CompletePrivateUploadRequest;
import com.altaira.backend.dto.media.PreparePrivateUploadRequest;
import com.altaira.backend.dto.media.UploadUrlResponse;
import com.altaira.backend.entity.*;
import com.altaira.backend.exception.ResourceNotFoundException;
import com.altaira.backend.model.OnboardingTaskStatus;
import com.altaira.backend.model.OnboardingTaskType;
import com.altaira.backend.model.SectorType;
import com.altaira.backend.repository.ClientServiceRepository;
import com.altaira.backend.repository.ClientWorkspaceRepository;
import com.altaira.backend.repository.OnboardingAuditLogRepository;
import com.altaira.backend.repository.OnboardingFileRepository;
import com.altaira.backend.repository.OnboardingTaskRepository;
import com.altaira.backend.security.ClientAccessContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Submit, approve or reject onboarding work. Critical gates derive from real task state. */
@Service
@Transactional
public class OnboardingService {

    /** Client management operations used by this component. */
    private final ClientManagementService clientManagementService;
    /** Persistence access for client service. */
    private final ClientServiceRepository clientServiceRepository;
    /** Persistence access for client workspace. */
    private final ClientWorkspaceRepository clientWorkspaceRepository;
    /** Persistence access for onboarding task. */
    private final OnboardingTaskRepository onboardingTaskRepository;
    /** Persistence access for onboarding file. */
    private final OnboardingFileRepository onboardingFileRepository;
    /** Persistence access for onboarding audit log. */
    private final OnboardingAuditLogRepository onboardingAuditLogRepository;
    /** Onboarding template operations used by this component. */
    private final OnboardingTemplateService onboardingTemplateService;
    /** Onboarding file storage operations used by this component. */
    private final OnboardingFileStorageService onboardingFileStorageService;
    /** Media upload URL operations used by this component. */
    private final MediaUploadUrlService mediaUploadUrlService;
    /** Signed contract PDF operations used by this component. */
    private final SignedContractPdfService signedContractPdfService;
    /** Onboarding notification operations used by this component. */
    private final OnboardingNotificationService onboardingNotificationService;
    /** Jackson mapper used to read or write JSON. */
    private final ObjectMapper objectMapper;

    /**
     * Creates the onboarding service with its injected collaborators.
     *
     * @param clientManagementService client management operations used by this component
     * @param clientServiceRepository persistence access for client service
     * @param clientWorkspaceRepository persistence access for client workspace
     * @param onboardingTaskRepository persistence access for onboarding task
     * @param onboardingFileRepository persistence access for onboarding file
     * @param onboardingAuditLogRepository persistence access for onboarding audit log
     * @param onboardingTemplateService onboarding template operations used by this component
     * @param onboardingFileStorageService onboarding file storage operations used by this component
     * @param mediaUploadUrlService media upload URL operations used by this component
     * @param signedContractPdfService signed contract PDF operations used by this component
     * @param onboardingNotificationService onboarding notification operations used by this component
     * @param objectMapper Jackson mapper used to read or write JSON
     */
    public OnboardingService(
            ClientManagementService clientManagementService,
            ClientServiceRepository clientServiceRepository,
            ClientWorkspaceRepository clientWorkspaceRepository,
            OnboardingTaskRepository onboardingTaskRepository,
            OnboardingFileRepository onboardingFileRepository,
            OnboardingAuditLogRepository onboardingAuditLogRepository,
            OnboardingTemplateService onboardingTemplateService,
            OnboardingFileStorageService onboardingFileStorageService,
            MediaUploadUrlService mediaUploadUrlService,
            SignedContractPdfService signedContractPdfService,
            OnboardingNotificationService onboardingNotificationService,
            ObjectMapper objectMapper
    ) {
        this.clientManagementService = clientManagementService;
        this.clientServiceRepository = clientServiceRepository;
        this.clientWorkspaceRepository = clientWorkspaceRepository;
        this.onboardingTaskRepository = onboardingTaskRepository;
        this.onboardingFileRepository = onboardingFileRepository;
        this.onboardingAuditLogRepository = onboardingAuditLogRepository;
        this.onboardingTemplateService = onboardingTemplateService;
        this.onboardingFileStorageService = onboardingFileStorageService;
        this.mediaUploadUrlService = mediaUploadUrlService;
        this.signedContractPdfService = signedContractPdfService;
        this.onboardingNotificationService = onboardingNotificationService;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves admin dashboard.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @return admin dashboard
     */
    public OnboardingDashboardResponse getAdminDashboard(UUID clientId) {
        ClientEntity client = clientManagementService.findClientEntity(clientId);
        ClientWorkspaceEntity workspace = ensureWorkspaceAndTasks(client);
        OnboardingDashboardResponse dashboard = mapDashboard(workspace);
        dashboard.setAccessRole("admin");
        dashboard.setCanEdit(true);
        return dashboard;
    }

    /**
     * Retrieves client dashboard.
     *
     * @param context context carrying client access
     * @return client dashboard
     */
    public OnboardingDashboardResponse getClientDashboard(ClientAccessContext context) {
        ClientWorkspaceEntity workspace = ensureWorkspaceAndTasks(context.client());
        OnboardingDashboardResponse dashboard = mapDashboard(workspace);
        dashboard.setAccessRole(context.accessRole());
        dashboard.setCanEdit(
                "client_user".equalsIgnoreCase(context.user().getRole()) &&
                        "client_user".equalsIgnoreCase(context.accessRole())
        );
        return dashboard;
    }

    /**
     * Refreshes the customer’s onboarding tasks and returns the administrative dashboard.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @return the onboarding dashboard response
     */
    public OnboardingDashboardResponse regenerateAdminDashboard(UUID clientId) {
        ClientEntity client = clientManagementService.findClientEntity(clientId);
        ClientWorkspaceEntity workspace = ensureWorkspaceAndTasks(client);
        OnboardingDashboardResponse dashboard = mapDashboard(workspace);
        dashboard.setAccessRole("admin");
        dashboard.setCanEdit(true);
        return dashboard;
    }

    /**
     * Submits client task. Persists changes through {@code onboardingTaskRepository}.
     *
     * @param context context carrying client access
     * @param taskId identifier of the task being acted on
     * @param request input carrying submit onboarding task
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @return the onboarding task response
     */
    public OnboardingTaskResponse submitClientTask(
            ClientAccessContext context,
            UUID taskId,
            SubmitOnboardingTaskRequest request,
            String ipAddress,
            String userAgent
    ) {
        OnboardingTaskEntity task = findTask(taskId);
        ensureTaskBelongsToClient(task, context.client());

        OnboardingTaskType taskType = OnboardingTaskType.parse(task.getTaskType());
        Instant now = Instant.now();

        if (taskType == OnboardingTaskType.SIGNATURE) {
            applySignatureSubmission(task, request, ipAddress, userAgent, now);
        } else if (taskType == OnboardingTaskType.FILE_UPLOAD) {
            applyFileSubmission(task, request);
        } else if (taskType == OnboardingTaskType.PREFERENCES_FORM) {
            applyPreferencesSubmission(task, request);
        }

        task.setStatus(OnboardingTaskStatus.SUBMITTED.value());
        task.setSubmittedAt(now);
        task.setRejectedAt(null);
        task.setAdminFeedback(null);
        task.setCompletedAt(null);

        OnboardingTaskEntity saved = onboardingTaskRepository.save(task);
        recordAudit(saved, context.user(), context.accessRole(), "client_task_submitted", ipAddress, userAgent, "{}");

        if (taskType == OnboardingTaskType.SIGNATURE) {
            byte[] signedPdf = signedContractPdfService.buildSignedContractEvidence(saved.getClient(), saved);
            onboardingNotificationService.sendContractSubmitted(saved.getClient(), saved, signedPdf);
        }

        refreshWorkspaceStatus(saved.getWorkspace());
        return mapTask(saved);
    }

    /**
     * Submits client files. Persists changes through {@code onboardingTaskRepository}.
     *
     * @param context context carrying client access
     * @param taskId identifier of the task being acted on
     * @param files files
     * @param notes free-text notes associated with the item
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @return the onboarding task response
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
     * @throws IllegalArgumentException when validation reports “At least one file is required”
     */
    public OnboardingTaskResponse submitClientFiles(
            ClientAccessContext context,
            UUID taskId,
            List<MultipartFile> files,
            String notes,
            String ipAddress,
            String userAgent
    ) {
        OnboardingTaskEntity task = findTask(taskId);
        ensureTaskBelongsToClient(task, context.client());

        if (OnboardingTaskType.parse(task.getTaskType()) != OnboardingTaskType.FILE_UPLOAD) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This onboarding task does not accept files");
        }

        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            throw new IllegalArgumentException("At least one file is required");
        }

        List<OnboardingFileResponse> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            var storedFile = onboardingFileStorageService.store(context.client().getId(), task.getId(), file);

            OnboardingFileEntity fileEntity = new OnboardingFileEntity();
            fileEntity.setTask(task);
            fileEntity.setWorkspace(task.getWorkspace());
            fileEntity.setClient(task.getClient());
            fileEntity.setOriginalFilename(storedFile.originalFilename());
            fileEntity.setStoredFilename(storedFile.storedFilename());
            fileEntity.setStorageKey(storedFile.storageKey());
            fileEntity.setContentType(storedFile.contentType());
            fileEntity.setSizeBytes(storedFile.sizeBytes());
            fileEntity.setChecksumSha256(storedFile.checksumSha256());

            uploadedFiles.add(mapFile(onboardingFileRepository.save(fileEntity)));
        }

        Instant now = Instant.now();
        List<OnboardingFileResponse> allTaskFiles = onboardingFileRepository.findAllByTaskOrderByCreatedAtAsc(task)
                .stream()
                .map(this::mapFile)
                .toList();

        String safeNotes = trimToLimit(notes, 2000);
        task.setFileMetadataJson(toJson(allTaskFiles));
        task.setDataJson(toJson(Map.of("notes", safeNotes == null ? "" : safeNotes)));
        task.setStatus(OnboardingTaskStatus.SUBMITTED.value());
        task.setSubmittedAt(now);
        task.setRejectedAt(null);
        task.setAdminFeedback(null);
        task.setCompletedAt(null);

        OnboardingTaskEntity saved = onboardingTaskRepository.save(task);
        recordAudit(
                saved,
                context.user(),
                context.accessRole(),
                "client_files_uploaded",
                ipAddress,
                userAgent,
                toJson(Map.of("uploadedFileCount", uploadedFiles.size()))
        );
        refreshWorkspaceStatus(saved.getWorkspace());
        return mapTask(saved);
    }

    /**
     * Prepares client task upload.
     *
     * @param context context carrying client access
     * @param taskId identifier of the task being acted on
     * @param request input carrying prepare private upload
     * @return the upload URL response
     */
    public UploadUrlResponse prepareClientTaskUpload(
            ClientAccessContext context,
            UUID taskId,
            PreparePrivateUploadRequest request
    ) {
        OnboardingTaskEntity task = findTask(taskId);
        ensureTaskBelongsToClient(task, context.client());
        ensureFileUploadTask(task);

        return mediaUploadUrlService.createContextUploadUrl(
                context.client().getId(),
                storageFolderForTask(task),
                task.getServiceKey(),
                "onboarding",
                task.getId(),
                request
        );
    }

    /**
     * Validates the uploaded file and completes its association with the onboarding task.
     *
     * @param context context carrying client access
     * @param taskId identifier of the task being acted on
     * @param request input carrying complete private upload
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @return the onboarding task response
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (conflict)
     */
    public OnboardingTaskResponse completeClientTaskUpload(
            ClientAccessContext context,
            UUID taskId,
            CompletePrivateUploadRequest request,
            String ipAddress,
            String userAgent
    ) {
        OnboardingTaskEntity task = findTask(taskId);
        ensureTaskBelongsToClient(task, context.client());
        ensureFileUploadTask(task);

        var verified = mediaUploadUrlService.verifyCompletedUpload(
                context.client().getId(),
                storageFolderForTask(task),
                task.getServiceKey(),
                "onboarding",
                task.getId(),
                request.getBucket(),
                request.getObjectKey(),
                request.getFilename(),
                request.getContentType(),
                request.getSizeBytes()
        );

        var existing = onboardingFileRepository.findByStorageKey(verified.storageKey());
        if (existing.isPresent()) {
            OnboardingFileEntity existingFile = existing.get();
            if (!existingFile.getClient().getId().equals(context.client().getId()) ||
                    !existingFile.getTask().getId().equals(task.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Uploaded object is already assigned to another context");
            }
            return mapTask(task);
        }

        OnboardingFileEntity fileEntity = new OnboardingFileEntity();
        fileEntity.setTask(task);
        fileEntity.setWorkspace(task.getWorkspace());
        fileEntity.setClient(task.getClient());
        fileEntity.setOriginalFilename(verified.originalFilename());
        fileEntity.setStoredFilename(verified.storedFilename());
        fileEntity.setStorageKey(verified.storageKey());
        fileEntity.setContentType(verified.contentType());
        fileEntity.setSizeBytes(verified.sizeBytes());
        fileEntity.setChecksumSha256(verified.checksumSha256());

        try {
            onboardingFileRepository.save(fileEntity);
            markFileTaskSubmitted(task, request.getNotes(), ipAddress, userAgent, context);
            return mapTask(task);
        } catch (RuntimeException ex) {
            mediaUploadUrlService.deleteStoredObjectQuietly(verified.storageKey());
            throw ex;
        }
    }

    /**
     * Approves task. Persists changes through {@code onboardingTaskRepository}.
     *
     * @param taskId identifier of the task being acted on
     * @param adminUser internal user performing the action
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @return the onboarding task response
     */
    public OnboardingTaskResponse approveTask(UUID taskId, AppUserEntity adminUser, String ipAddress, String userAgent) {
        OnboardingTaskEntity task = findTask(taskId);
        Instant now = Instant.now();

        task.setStatus(OnboardingTaskStatus.APPROVED.value());
        task.setApprovedAt(now);
        task.setRejectedAt(null);
        task.setCompletedAt(now);
        task.setAdminFeedback(null);

        if (OnboardingTaskType.SIGNATURE.value().equals(task.getTaskType())) {
            task.getWorkspace().setContractApprovedAt(now);
        }

        OnboardingTaskEntity saved = onboardingTaskRepository.save(task);
        recordAudit(saved, adminUser, adminUser == null ? "admin" : adminUser.getRole(), "admin_task_approved", ipAddress, userAgent, "{}");
        refreshWorkspaceStatus(saved.getWorkspace());
        return mapTask(saved);
    }

    /**
     * Lists task files as admin.
     *
     * @param taskId identifier of the task being acted on
     * @return the onboarding file response items
     */
    @Transactional(readOnly = true)
    public List<OnboardingFileResponse> listTaskFilesAsAdmin(UUID taskId) {
        OnboardingTaskEntity task = findTask(taskId);
        return onboardingFileRepository.findAllByTaskOrderByCreatedAtAsc(task)
                .stream()
                .map(this::mapFile)
                .toList();
    }

    /**
     * Retrieves file download as admin. Looks up the referenced record by identifier.
     *
     * @param fileId identifier of the file
     * @return file download as admin
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    @Transactional(readOnly = true)
    public OnboardingFileDownload getFileDownloadAsAdmin(UUID fileId) {
        OnboardingFileEntity file = onboardingFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Onboarding file", fileId));
        try {
            return new OnboardingFileDownload(file, onboardingFileStorageService.open(file.getStorageKey()));
        } catch (IOException ex) {
            throw new ResourceNotFoundException("Stored onboarding file", fileId);
        }
    }

    /**
     * Retrieves file download. Looks up the referenced record by identifier.
     *
     * @param context context carrying client access
     * @param fileId identifier of the file
     * @return file download
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (forbidden)
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    @Transactional(readOnly = true)
    public OnboardingFileDownload getFileDownload(ClientAccessContext context, UUID fileId) {
        OnboardingFileEntity file = onboardingFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Onboarding file", fileId));

        if (!file.getClient().getId().equals(context.client().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access to this onboarding file");
        }

        try {
            return new OnboardingFileDownload(file, onboardingFileStorageService.open(file.getStorageKey()));
        } catch (IOException ex) {
            throw new ResourceNotFoundException("Stored onboarding file", fileId);
        }
    }

    /**
     * Ensures file upload task.
     *
     * @param task task
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
     */
    private void ensureFileUploadTask(OnboardingTaskEntity task) {
        if (OnboardingTaskType.parse(task.getTaskType()) != OnboardingTaskType.FILE_UPLOAD) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This onboarding task does not accept files");
        }
    }

    /**
     * Selects the storage folder used by this onboarding task.
     *
     * @param task task
     * @return the resulting text
     */
    private String storageFolderForTask(OnboardingTaskEntity task) {
        String context = String.join(" ",
                task.getTaskKey() == null ? "" : task.getTaskKey(),
                task.getTitle() == null ? "" : task.getTitle(),
                task.getServiceKey() == null ? "" : task.getServiceKey()
        ).toLowerCase();

        if (context.matches(".*(contract|agreement|legal|privacy|nda|identity|migration).*")) {
            return "legal";
        }
        if (context.matches(".*(brand|logo|website|content|seo).*")) {
            return "branding";
        }
        return "multimedia";
    }

    /**
     * Marks file task submitted. Persists changes through {@code onboardingTaskRepository}.
     *
     * @param task task
     * @param notes free-text notes associated with the item
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @param context context carrying client access
     */
    private void markFileTaskSubmitted(
            OnboardingTaskEntity task,
            String notes,
            String ipAddress,
            String userAgent,
            ClientAccessContext context
    ) {
        List<OnboardingFileResponse> allTaskFiles = onboardingFileRepository.findAllByTaskOrderByCreatedAtAsc(task)
                .stream()
                .map(this::mapFile)
                .toList();
        Instant now = Instant.now();
        String safeNotes = trimToLimit(notes, 2000);

        task.setFileMetadataJson(toJson(allTaskFiles));
        task.setDataJson(toJson(Map.of("notes", safeNotes == null ? "" : safeNotes)));
        task.setStatus(OnboardingTaskStatus.SUBMITTED.value());
        task.setSubmittedAt(now);
        task.setRejectedAt(null);
        task.setAdminFeedback(null);
        task.setCompletedAt(null);

        OnboardingTaskEntity saved = onboardingTaskRepository.save(task);
        recordAudit(
                saved,
                context.user(),
                context.accessRole(),
                "client_s3_file_confirmed",
                ipAddress,
                userAgent,
                toJson(Map.of("fileCount", allTaskFiles.size()))
        );
        refreshWorkspaceStatus(saved.getWorkspace());
    }

    /**
     * Rejects task. Persists changes through {@code onboardingTaskRepository}.
     *
     * @param taskId identifier of the task being acted on
     * @param feedback feedback
     * @param adminUser internal user performing the action
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @return the onboarding task response
     */
    public OnboardingTaskResponse rejectTask(UUID taskId, String feedback, AppUserEntity adminUser, String ipAddress, String userAgent) {
        OnboardingTaskEntity task = findTask(taskId);
        Instant now = Instant.now();

        task.setStatus(OnboardingTaskStatus.REJECTED.value());
        task.setRejectedAt(now);
        task.setApprovedAt(null);
        task.setCompletedAt(null);
        task.setAdminFeedback(trimToLimit(feedback, 1000));

        OnboardingTaskEntity saved = onboardingTaskRepository.save(task);
        recordAudit(saved, adminUser, adminUser == null ? "admin" : adminUser.getRole(), "admin_task_rejected", ipAddress, userAgent, metadata("feedback", feedback));
        refreshWorkspaceStatus(saved.getWorkspace());
        return mapTask(saved);
    }

    /**
     * Ensures workspace and tasks.
     *
     * @param client customer account in scope
     * @return the client workspace record
     */
    ClientWorkspaceEntity ensureWorkspaceAndTasks(ClientEntity client) {
        ClientWorkspaceEntity workspace = clientWorkspaceRepository.findByClient(client)
                .orElseGet(() -> createWorkspace(client));

        SectorType sectorType = SectorType.parse(client.getSectorType());

        for (var template : onboardingTemplateService.generalTemplates(sectorType)) {
            ensureTask(workspace, client, null, template);
        }

        clientServiceRepository.findAllByClientOrderByCreatedAtDesc(client)
                .forEach(clientService -> onboardingTemplateService.serviceTemplates(clientService, sectorType)
                        .forEach(template -> ensureTask(workspace, client, clientService, template)));

        refreshWorkspaceStatus(workspace);
        return workspace;
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
        workspace.setStatus("onboarding");
        return clientWorkspaceRepository.save(workspace);
    }

    /**
     * Ensures task. Persists changes through {@code onboardingTaskRepository}.
     *
     * @param workspace client workspace in scope
     * @param client customer account in scope
     * @param clientService client operations used by this component
     * @param template template used to construct the item
     */
    private void ensureTask(
            ClientWorkspaceEntity workspace,
            ClientEntity client,
            ClientServiceEntity clientService,
            OnboardingTemplateService.TaskTemplate template
    ) {
        var existingTask = clientService == null
                ? onboardingTaskRepository.findByWorkspaceAndClientServiceIsNullAndTaskKey(workspace, template.taskKey())
                : onboardingTaskRepository.findByWorkspaceAndClientServiceAndTaskKey(workspace, clientService, template.taskKey());

        if (existingTask.isPresent()) {
            return;
        }

        OnboardingTaskEntity task = new OnboardingTaskEntity();
        task.setWorkspace(workspace);
        task.setClient(client);
        task.setClientService(clientService);
        task.setServiceKey(template.serviceKey());
        task.setSectorType(template.sectorType().value());
        task.setTaskKey(template.taskKey());
        task.setTitle(template.title());
        task.setDescription(template.description());
        task.setTaskType(template.taskType().value());
        task.setRequired(template.required());
        task.setCritical(template.critical());
        task.setSortOrder(template.sortOrder());
        task.setStatus(OnboardingTaskStatus.PENDING.value());
        onboardingTaskRepository.save(task);
    }

    /**
     * Applies signature submission.
     *
     * @param task task
     * @param request input carrying submit onboarding task
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @param now now
     * @throws IllegalArgumentException when validation reports “Signer full name is required” or “Document ID is required”
     */
    private void applySignatureSubmission(
            OnboardingTaskEntity task,
            SubmitOnboardingTaskRequest request,
            String ipAddress,
            String userAgent,
            Instant now
    ) {
        if (request.getSignatureFullName() == null || request.getSignatureFullName().isBlank()) {
            throw new IllegalArgumentException("Signer full name is required");
        }

        if (request.getSignatureDocumentId() == null || request.getSignatureDocumentId().isBlank()) {
            throw new IllegalArgumentException("Document ID is required");
        }

        if (!request.isSignatureConsent()) {
            throw new IllegalArgumentException("Explicit legal consent is required");
        }

        task.setSignatureFullName(request.getSignatureFullName().trim());
        task.setSignatureDocumentId(request.getSignatureDocumentId().trim());
        task.setSignatureConsent(true);
        task.setSignedIp(trimToLimit(ipAddress, 80));
        task.setSignedUserAgent(trimToLimit(userAgent, 500));
        task.setSignedAt(now);
        task.setDataJson(toJson(Map.of(
                "signatureFullName", task.getSignatureFullName(),
                "signatureDocumentId", task.getSignatureDocumentId(),
                "signatureConsent", true,
                "signedAt", now.toString()
        )));
        task.getWorkspace().setContractSubmittedAt(now);
    }

    /**
     * Applies file submission.
     *
     * @param task task
     * @param request input carrying submit onboarding task
     * @throws IllegalArgumentException when validation reports “At least one file reference is required”
     */
    private void applyFileSubmission(OnboardingTaskEntity task, SubmitOnboardingTaskRequest request) {
        if (request.getFiles() == null || request.getFiles().isEmpty()) {
            throw new IllegalArgumentException("At least one file reference is required");
        }

        task.setFileMetadataJson(toJson(request.getFiles()));
        task.setDataJson(toJson(request.getData() == null ? Map.of() : request.getData()));
    }

    /**
     * Applies preferences submission.
     *
     * @param task task
     * @param request input carrying submit onboarding task
     * @throws IllegalArgumentException when validation reports “Preference data is required”
     */
    private void applyPreferencesSubmission(OnboardingTaskEntity task, SubmitOnboardingTaskRequest request) {
        if (request.getData() == null || request.getData().isEmpty()) {
            throw new IllegalArgumentException("Preference data is required");
        }

        task.setDataJson(toJson(request.getData()));
    }

    /**
     * Re-evaluates the workspace onboarding status from the delivery tasks and their approval state.
     *
     * @param workspace client workspace in scope
     */
    private void refreshWorkspaceStatus(ClientWorkspaceEntity workspace) {
        List<OnboardingTaskEntity> tasks = onboardingTaskRepository.findAllByWorkspaceOrderBySortOrderAscCreatedAtAsc(workspace);

        List<OnboardingTaskEntity> criticalSignatureTasks = tasks.stream()
                .filter(this::isCriticalSignatureTask)
                .toList();
        boolean contractSubmitted = !criticalSignatureTasks.isEmpty() && criticalSignatureTasks.stream().allMatch(this::isSubmittedOrApproved);
        boolean contractApproved = !criticalSignatureTasks.isEmpty() && criticalSignatureTasks.stream().allMatch(this::isApproved);
        boolean requiredComplete = tasks.stream()
                .filter(OnboardingTaskEntity::isRequired)
                .allMatch(task -> OnboardingTaskStatus.APPROVED.value().equals(task.getStatus()));

        if (contractSubmitted && workspace.getContractSubmittedAt() == null) {
            workspace.setContractSubmittedAt(Instant.now());
        }

        if (contractApproved && workspace.getContractApprovedAt() == null) {
            workspace.setContractApprovedAt(Instant.now());
        }

        boolean wasCompleted = workspace.isOnboardingCompleted();
        workspace.setOnboardingCompleted(requiredComplete && !tasks.isEmpty());
        workspace.setStatus(workspace.isOnboardingCompleted() ? "active" : "onboarding");
        clientWorkspaceRepository.save(workspace);

        if (!wasCompleted && workspace.isOnboardingCompleted()) {
            onboardingNotificationService.sendOnboardingReadyForReview(workspace.getClient());
        }
    }

    /**
     * Finds task. Looks up the referenced record by identifier.
     *
     * @param taskId identifier of the task being acted on
     * @return the onboarding task record
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    private OnboardingTaskEntity findTask(UUID taskId) {
        return onboardingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Onboarding task", taskId));
    }

    /**
     * Checks that the onboarding task belongs to the selected customer before acting on it.
     *
     * @param task task
     * @param client customer account in scope
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (forbidden)
     */
    private void ensureTaskBelongsToClient(OnboardingTaskEntity task, ClientEntity client) {
        if (!task.getClient().getId().equals(client.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access to this onboarding task");
        }
    }

    /**
     * Records audit. Persists changes through {@code onboardingAuditLogRepository}.
     *
     * @param task task
     * @param actor actor
     * @param actorRole actor role
     * @param action action being recorded
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @param metadataJson JSON representation of metadata
     */
    private void recordAudit(
            OnboardingTaskEntity task,
            AppUserEntity actor,
            String actorRole,
            String action,
            String ipAddress,
            String userAgent,
            String metadataJson
    ) {
        OnboardingAuditLogEntity auditLog = new OnboardingAuditLogEntity();
        auditLog.setTask(task);
        auditLog.setWorkspace(task.getWorkspace());
        auditLog.setClient(task.getClient());
        auditLog.setActorUser(actor);
        auditLog.setActorRole(actorRole == null || actorRole.isBlank() ? "unknown" : actorRole);
        auditLog.setAction(action);
        auditLog.setIpAddress(trimToLimit(ipAddress, 80));
        auditLog.setUserAgent(trimToLimit(userAgent, 500));
        auditLog.setMetadataJson(metadataJson == null || metadataJson.isBlank() ? "{}" : metadataJson);
        onboardingAuditLogRepository.save(auditLog);
    }

    /**
     * Maps dashboard.
     *
     * @param workspace client workspace in scope
     * @return the onboarding dashboard response
     */
    private OnboardingDashboardResponse mapDashboard(ClientWorkspaceEntity workspace) {
        List<OnboardingTaskResponse> taskResponses = onboardingTaskRepository.findAllByWorkspaceOrderBySortOrderAscCreatedAtAsc(workspace)
                .stream()
                .map(this::mapTask)
                .toList();

        int totalRequired = (int) taskResponses.stream().filter(OnboardingTaskResponse::isRequired).count();
        int completedRequired = (int) taskResponses.stream()
                .filter(OnboardingTaskResponse::isRequired)
                .filter(task -> OnboardingTaskStatus.APPROVED.value().equals(task.getStatus()))
                .count();
        int submitted = (int) taskResponses.stream()
                .filter(task -> OnboardingTaskStatus.SUBMITTED.value().equals(task.getStatus()))
                .count();
        int rejected = (int) taskResponses.stream()
                .filter(task -> OnboardingTaskStatus.REJECTED.value().equals(task.getStatus()))
                .count();
        List<OnboardingTaskResponse> criticalSignatureTasks = taskResponses.stream()
                .filter(this::isCriticalSignatureTask)
                .toList();
        boolean contractSubmitted = !criticalSignatureTasks.isEmpty() && criticalSignatureTasks.stream().allMatch(this::isSubmittedOrApproved);
        boolean contractApproved = !criticalSignatureTasks.isEmpty() && criticalSignatureTasks.stream().allMatch(this::isApproved);

        return new OnboardingDashboardResponse(
                workspace.getId(),
                clientManagementService.map(workspace.getClient()),
                workspace.getName(),
                workspace.getStatus(),
                workspace.isOnboardingCompleted(),
                contractSubmitted,
                contractApproved,
                totalRequired,
                completedRequired,
                submitted,
                rejected,
                contractSubmitted ? workspace.getContractSubmittedAt() : null,
                contractApproved ? workspace.getContractApprovedAt() : null,
                taskResponses
        );
    }

    /**
     * Checks whether this is critical signature task.
     *
     * @param task task
     * @return true if the value is critical signature task
     */
    private boolean isCriticalSignatureTask(OnboardingTaskEntity task) {
        return task.isRequired() &&
                task.isCritical() &&
                OnboardingTaskType.SIGNATURE.value().equals(task.getTaskType());
    }

    /**
     * Checks whether this is critical signature task.
     *
     * @param task task
     * @return true if the value is critical signature task
     */
    private boolean isCriticalSignatureTask(OnboardingTaskResponse task) {
        return task.isRequired() &&
                task.isCritical() &&
                OnboardingTaskType.SIGNATURE.value().equals(task.getTaskType());
    }

    /**
     * Checks whether this is submitted or approved.
     *
     * @param task task
     * @return true if the value is submitted or approved
     */
    private boolean isSubmittedOrApproved(OnboardingTaskEntity task) {
        return OnboardingTaskStatus.SUBMITTED.value().equals(task.getStatus()) ||
                OnboardingTaskStatus.APPROVED.value().equals(task.getStatus());
    }

    /**
     * Checks whether this is submitted or approved.
     *
     * @param task task
     * @return true if the value is submitted or approved
     */
    private boolean isSubmittedOrApproved(OnboardingTaskResponse task) {
        return OnboardingTaskStatus.SUBMITTED.value().equals(task.getStatus()) ||
                OnboardingTaskStatus.APPROVED.value().equals(task.getStatus());
    }

    /**
     * Checks whether this is approved.
     *
     * @param task task
     * @return true if the value is approved
     */
    private boolean isApproved(OnboardingTaskEntity task) {
        return OnboardingTaskStatus.APPROVED.value().equals(task.getStatus());
    }

    /**
     * Checks whether this is approved.
     *
     * @param task task
     * @return true if the value is approved
     */
    private boolean isApproved(OnboardingTaskResponse task) {
        return OnboardingTaskStatus.APPROVED.value().equals(task.getStatus());
    }

    /**
     * Maps task.
     *
     * @param task task
     * @return the onboarding task response
     */
    private OnboardingTaskResponse mapTask(OnboardingTaskEntity task) {
        UUID clientServiceId = task.getClientService() == null ? null : task.getClientService().getId();

        return new OnboardingTaskResponse(
                task.getId(),
                task.getClient().getId(),
                clientServiceId,
                task.getServiceKey(),
                task.getSectorType(),
                task.getTaskKey(),
                task.getTitle(),
                task.getDescription(),
                task.getTaskType(),
                task.getStatus(),
                task.isRequired(),
                task.isCritical(),
                task.getSortOrder(),
                task.getDataJson(),
                task.getFileMetadataJson(),
                task.getSignatureFullName(),
                task.getSignatureDocumentId(),
                task.isSignatureConsent(),
                task.getAdminFeedback(),
                task.getSubmittedAt(),
                task.getApprovedAt(),
                task.getRejectedAt(),
                task.getCompletedAt(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    /**
     * Maps file.
     *
     * @param file file
     * @return the onboarding file response
     */
    private OnboardingFileResponse mapFile(OnboardingFileEntity file) {
        return new OnboardingFileResponse(
                file.getId(),
                file.getTask().getId(),
                file.getClient().getId(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSizeBytes(),
                file.getChecksumSha256(),
                file.getCreatedAt()
        );
    }

    /**
     * Groups the onboarding file download data used by onboarding service.
     *
     * @param file file
     * @param inputStream input stream
     */
    public record OnboardingFileDownload(OnboardingFileEntity file, InputStream inputStream) {}

    /**
     * Collects the audit details attached to this onboarding action.
     *
     * @param key key used to identify the item
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String metadata(String key, String value) {
        return toJson(Map.of(key, value == null ? "" : value));
    }

    /**
     * Converts the value to JSON.
     *
     * @param value stored or supplied representation
     * @return JSON
     * @throws IllegalArgumentException when validation reports “Could not serialize onboarding data”
     */
    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Could not serialize onboarding data", ex);
        }
    }

    /**
     * Trims surrounding whitespace and caps the result at the supplied length. Absent values return null.
     *
     * @param value stored or supplied representation
     * @param limit upper bound applied when reading or accepting data
     * @return the resulting text
     */
    private String trimToLimit(String value, int limit) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.length() <= limit ? trimmed : trimmed.substring(0, limit);
    }
}
