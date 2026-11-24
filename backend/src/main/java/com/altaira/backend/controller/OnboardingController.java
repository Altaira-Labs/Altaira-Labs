package com.altaira.backend.controller;

import com.altaira.backend.dto.onboarding.OnboardingFileResponse;
import com.altaira.backend.dto.onboarding.OnboardingDashboardResponse;
import com.altaira.backend.dto.onboarding.OnboardingTaskResponse;
import com.altaira.backend.dto.onboarding.ReviewOnboardingTaskRequest;
import com.altaira.backend.dto.onboarding.SubmitOnboardingTaskRequest;
import com.altaira.backend.dto.media.CompletePrivateUploadRequest;
import com.altaira.backend.dto.media.PreparePrivateUploadRequest;
import com.altaira.backend.dto.media.UploadUrlResponse;
import com.altaira.backend.entity.AppUserEntity;
import com.altaira.backend.security.AdminAccessService;
import com.altaira.backend.security.ClientAccessService;
import com.altaira.backend.service.AuthService;
import com.altaira.backend.service.OnboardingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/** HTTP endpoints for onboarding under {@code /api/v1/onboarding}. */
@RestController
@RequestMapping("/api/v1/onboarding")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class OnboardingController {

    /** Onboarding operations used by this component. */
    private final OnboardingService onboardingService;
    /** Client access operations used by this component. */
    private final ClientAccessService clientAccessService;
    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;
    /** Auth operations used by this component. */
    private final AuthService authService;

    /**
     * Creates the onboarding controller with its injected collaborators.
     *
     * @param onboardingService onboarding operations used by this component
     * @param clientAccessService client access operations used by this component
     * @param adminAccessService admin access operations used by this component
     * @param authService auth operations used by this component
     */
    public OnboardingController(
            OnboardingService onboardingService,
            ClientAccessService clientAccessService,
            AdminAccessService adminAccessService,
            AuthService authService
    ) {
        this.onboardingService = onboardingService;
        this.clientAccessService = clientAccessService;
        this.adminAccessService = adminAccessService;
        this.authService = authService;
    }

    /**
     * GET {@code /api/v1/onboarding/client/me}. Retrieves my onboarding. Membership is checked before the workspace operation.
     *
     * @param clientSessionToken session credential supplied for the client workspace
     * @return my onboarding
     */
    @GetMapping("/client/me")
    public OnboardingDashboardResponse getMyOnboarding(
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        return onboardingService.getClientDashboard(context);
    }

    /**
     * PATCH {@code /api/v1/onboarding/client/tasks/{taskId}/submit}. Submits client task. Membership is checked before the workspace operation.
     *
     * @param taskId identifier of the task being acted on
     * @param request input carrying submit onboarding task
     * @param clientSessionToken session credential supplied for the client workspace
     * @param httpRequest incoming HTTP request, including its headers
     * @return the onboarding task response
     */
    @PatchMapping("/client/tasks/{taskId}/submit")
    public OnboardingTaskResponse submitClientTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody SubmitOnboardingTaskRequest request,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken,
            HttpServletRequest httpRequest
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return onboardingService.submitClientTask(
                context,
                taskId,
                request,
                clientIp(httpRequest),
                userAgent(httpRequest)
        );
    }

    /**
     * POST {@code /api/v1/onboarding/client/tasks/{taskId}/files}. Submits client files. Membership is checked before the workspace operation.
     *
     * @param taskId identifier of the task being acted on
     * @param files files
     * @param notes free-text notes associated with the item
     * @param clientSessionToken session credential supplied for the client workspace
     * @param httpRequest incoming HTTP request, including its headers
     * @return the onboarding task response
     */
    @PostMapping(value = "/client/tasks/{taskId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public OnboardingTaskResponse submitClientFiles(
            @PathVariable UUID taskId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart(name = "notes", required = false) String notes,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken,
            HttpServletRequest httpRequest
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return onboardingService.submitClientFiles(
                context,
                taskId,
                files,
                notes,
                clientIp(httpRequest),
                userAgent(httpRequest)
        );
    }

    /**
     * POST {@code /api/v1/onboarding/client/tasks/{taskId}/upload-url}. Prepares client task upload. Membership is checked before the workspace operation.
     *
     * @param taskId identifier of the task being acted on
     * @param request input carrying prepare private upload
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the upload URL response
     */
    @PostMapping("/client/tasks/{taskId}/upload-url")
    public UploadUrlResponse prepareClientTaskUpload(
            @PathVariable UUID taskId,
            @Valid @RequestBody PreparePrivateUploadRequest request,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return onboardingService.prepareClientTaskUpload(context, taskId, request);
    }

    /**
     * POST {@code /api/v1/onboarding/client/tasks/{taskId}/files/complete}. Complete client task upload. Membership is checked before the workspace operation.
     *
     * @param taskId identifier of the task being acted on
     * @param request input carrying complete private upload
     * @param clientSessionToken session credential supplied for the client workspace
     * @param httpRequest incoming HTTP request, including its headers
     * @return the onboarding task response
     */
    @PostMapping("/client/tasks/{taskId}/files/complete")
    public OnboardingTaskResponse completeClientTaskUpload(
            @PathVariable UUID taskId,
            @Valid @RequestBody CompletePrivateUploadRequest request,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken,
            HttpServletRequest httpRequest
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return onboardingService.completeClientTaskUpload(
                context,
                taskId,
                request,
                clientIp(httpRequest),
                userAgent(httpRequest)
        );
    }

    /**
     * GET {@code /api/v1/onboarding/client/files/{fileId}/download}. Downloads client task file. Membership is checked before the workspace operation.
     *
     * @param fileId identifier of the file
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the HTTP response for this operation
     * @throws IOException if the underlying input or output cannot be completed
     */
    @GetMapping("/client/files/{fileId}/download")
    public ResponseEntity<InputStreamResource> downloadClientTaskFile(
            @PathVariable UUID fileId,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) throws IOException {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        var download = onboardingService.getFileDownload(context, fileId);
        var file = download.file();
        MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;

        if (file.getContentType() != null && !file.getContentType().isBlank()) {
            try {
                contentType = MediaType.parseMediaType(file.getContentType());
            } catch (IllegalArgumentException ignored) {
                contentType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(file.getSizeBytes())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(file.getOriginalFilename(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(new InputStreamResource(download.inputStream()));
    }

    /**
     * GET {@code /api/v1/onboarding/admin/clients/{clientId}}. Retrieves client onboarding as admin. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return client onboarding as admin
     */
    @GetMapping("/admin/clients/{clientId}")
    public OnboardingDashboardResponse getClientOnboardingAsAdmin(
            @PathVariable UUID clientId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return onboardingService.getAdminDashboard(clientId);
    }

    /**
     * POST {@code /api/v1/onboarding/admin/clients/{clientId}/generate}. Generates client onboarding as admin. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the onboarding dashboard response
     */
    @PostMapping("/admin/clients/{clientId}/generate")
    public OnboardingDashboardResponse generateClientOnboardingAsAdmin(
            @PathVariable UUID clientId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return onboardingService.regenerateAdminDashboard(clientId);
    }

    /**
     * PATCH {@code /api/v1/onboarding/admin/tasks/{taskId}/approve}. Approves task. Internal access is checked before delegation.
     *
     * @param taskId identifier of the task being acted on
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @param httpRequest incoming HTTP request, including its headers
     * @return the onboarding task response
     */
    @PatchMapping("/admin/tasks/{taskId}/approve")
    public OnboardingTaskResponse approveTask(
            @PathVariable UUID taskId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken,
            HttpServletRequest httpRequest
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return onboardingService.approveTask(taskId, optionalAdminUser(adminSessionToken), clientIp(httpRequest), userAgent(httpRequest));
    }

    /**
     * PATCH {@code /api/v1/onboarding/admin/tasks/{taskId}/reject}. Rejects task. Internal access is checked before delegation.
     *
     * @param taskId identifier of the task being acted on
     * @param request input carrying review onboarding task
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @param httpRequest incoming HTTP request, including its headers
     * @return the onboarding task response
     */
    @PatchMapping("/admin/tasks/{taskId}/reject")
    public OnboardingTaskResponse rejectTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody ReviewOnboardingTaskRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken,
            HttpServletRequest httpRequest
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return onboardingService.rejectTask(
                taskId,
                request.getFeedback(),
                optionalAdminUser(adminSessionToken),
                clientIp(httpRequest),
                userAgent(httpRequest)
        );
    }

    /**
     * GET {@code /api/v1/onboarding/admin/tasks/{taskId}/files}. Lists task files. Internal access is checked before delegation.
     *
     * @param taskId identifier of the task being acted on
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the onboarding file response items
     */
    @GetMapping("/admin/tasks/{taskId}/files")
    public List<OnboardingFileResponse> listTaskFiles(
            @PathVariable UUID taskId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return onboardingService.listTaskFilesAsAdmin(taskId);
    }

    /**
     * GET {@code /api/v1/onboarding/admin/files/{fileId}/download}. Downloads task file. Internal access is checked before delegation.
     *
     * @param fileId identifier of the file
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the HTTP response for this operation
     * @throws IOException if the underlying input or output cannot be completed
     */
    @GetMapping("/admin/files/{fileId}/download")
    public ResponseEntity<InputStreamResource> downloadTaskFile(
            @PathVariable UUID fileId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) throws IOException {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        var download = onboardingService.getFileDownloadAsAdmin(fileId);
        var file = download.file();
        MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;

        if (file.getContentType() != null && !file.getContentType().isBlank()) {
            try {
                contentType = MediaType.parseMediaType(file.getContentType());
            } catch (IllegalArgumentException ignored) {
                contentType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(file.getSizeBytes())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(file.getOriginalFilename(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(new InputStreamResource(download.inputStream()));
    }

    /**
     * Resolves the signed-in internal user when staff session information is supplied.
     *
     * @param adminSessionToken session credential for the internal user
     * @return the app user record
     */
    private AppUserEntity optionalAdminUser(String adminSessionToken) {
        try {
            return authService.getCurrentUserEntity(adminSessionToken);
        } catch (ResponseStatusException ex) {
            return null;
        }
    }

    /**
     * Obtains the network address associated with the request.
     *
     * @param request input carrying http servlet
     * @return the resulting text
     */
    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * Reads the User-Agent header for the operation’s audit context.
     *
     * @param request input carrying http servlet
     * @return the resulting text
     */
    private String userAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
