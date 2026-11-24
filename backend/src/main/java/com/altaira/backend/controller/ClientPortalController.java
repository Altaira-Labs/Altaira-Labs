package com.altaira.backend.controller;

import com.altaira.backend.dto.clientportal.AdminActionItemResponse;
import com.altaira.backend.dto.clientportal.AdminClientProjectSummaryResponse;
import com.altaira.backend.dto.clientportal.ClientPortalResponse;
import com.altaira.backend.dto.clientportal.ClientProjectAssetResponse;
import com.altaira.backend.dto.clientportal.ClientProjectConfigSnapshotResponse;
import com.altaira.backend.dto.clientportal.ClientProjectResponse;
import com.altaira.backend.dto.clientportal.CreateClientProjectConfigSnapshotRequest;
import com.altaira.backend.dto.clientportal.CreateProjectLinkRequest;
import com.altaira.backend.dto.clientportal.ReviewProjectAssetRequest;
import com.altaira.backend.dto.clientportal.SubmitProjectFeedbackRequest;
import com.altaira.backend.dto.clientportal.UpdateClientProjectRequest;
import com.altaira.backend.dto.media.CompletePrivateUploadRequest;
import com.altaira.backend.dto.media.PreparePrivateUploadRequest;
import com.altaira.backend.dto.media.UploadUrlResponse;
import com.altaira.backend.security.AdminAccessService;
import com.altaira.backend.security.ClientAccessService;
import com.altaira.backend.service.ClientPortalService;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * HTTP interface to customer projects under {@code /api/v1/client-portal}. Client sessions can view
 * their project resources and submit feedback or uploads when permitted. Staff routes expose the
 * review queue, project updates and customer previews.
 */
@RestController
@RequestMapping("/api/v1/client-portal")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class ClientPortalController {

    /** Client access operations used by this component. */
    private final ClientAccessService clientAccessService;
    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;
    /** Client portal operations used by this component. */
    private final ClientPortalService clientPortalService;

    /**
     * Creates the client portal controller with its injected collaborators.
     *
     * @param clientAccessService client access operations used by this component
     * @param adminAccessService admin access operations used by this component
     * @param clientPortalService client portal operations used by this component
     */
    public ClientPortalController(
            ClientAccessService clientAccessService,
            AdminAccessService adminAccessService,
            ClientPortalService clientPortalService
    ) {
        this.clientAccessService = clientAccessService;
        this.adminAccessService = adminAccessService;
        this.clientPortalService = clientPortalService;
    }

    /**
     * GET {@code /api/v1/client-portal/me}.
     * Opens the authenticated customer's portal using its onboarding dashboard and current service
     * assignments.
     *
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the workspace, onboarding and contract state, module access and projects
     */
    @GetMapping("/me")
    public ClientPortalResponse getMyPortal(
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        return clientPortalService.getPortal(context);
    }

    /**
     * PATCH {@code /api/v1/client-portal/client/projects/{projectId}/feedback}.
     * Records the customer's latest feedback and moves the project into review with a pending revision
     * timestamp. Requires a client session with write access.
     *
     * @param projectId identifier of the client project
     * @param request the client's comments on the project
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the updated project in the review phase
     */
    @PatchMapping("/client/projects/{projectId}/feedback")
    public ClientProjectResponse submitProjectFeedback(
            @PathVariable UUID projectId,
            @Valid @RequestBody SubmitProjectFeedbackRequest request,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return clientPortalService.submitProjectFeedback(context, projectId, request);
    }

    /**
     * GET {@code /api/v1/client-portal/client/projects/{projectId}/assets}.
     * Lists assets after confirming contract approval and the project's ownership.
     *
     * @param projectId identifier of the client project
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the project's assets in upload order, oldest first
     */
    @GetMapping("/client/projects/{projectId}/assets")
    public List<ClientProjectAssetResponse> listProjectAssets(
            @PathVariable UUID projectId,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        return clientPortalService.listProjectAssets(context, projectId);
    }

    /**
     * POST {@code /api/v1/client-portal/client/projects/{projectId}/assets}.
     * Stores the supplied files under the customer's project once its contract is approved. Requires a
     * client session with write access.
     *
     * @param projectId identifier of the client project
     * @param files files
     * @param assetType asset type
     * @param notes free-text notes associated with the item
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the asset details created for the accepted uploads
     */
    @PostMapping(value = "/client/projects/{projectId}/assets", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<ClientProjectAssetResponse> uploadProjectAssets(
            @PathVariable UUID projectId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart(name = "assetType", required = false) String assetType,
            @RequestPart(name = "notes", required = false) String notes,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return clientPortalService.uploadProjectAssets(context, projectId, files, assetType, notes);
    }

    /**
     * POST {@code /api/v1/client-portal/client/projects/{projectId}/assets/upload-url}.
     * Issues upload instructions scoped to a project of the authenticated customer after contract
     * approval. Requires a client session with write access.
     *
     * @param projectId identifier of the client project
     * @param request file metadata used to prepare the upload
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the upload URL, expected HTTP method, object location and expiry
     */
    @PostMapping("/client/projects/{projectId}/assets/upload-url")
    public UploadUrlResponse prepareProjectAssetUpload(
            @PathVariable UUID projectId,
            @Valid @RequestBody PreparePrivateUploadRequest request,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return clientPortalService.prepareProjectAssetUpload(context, projectId, request);
    }

    /**
     * POST {@code /api/v1/client-portal/client/projects/{projectId}/assets/complete}.
     * Verifies the stored object against the project's upload request before recording it. A previously
     * registered object in the same project reuses its asset record. Requires a client session with write
     * access.
     *
     * @param projectId identifier of the client project
     * @param request storage location and file metadata to verify
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the verified project asset, newly recorded or reused
     */
    @PostMapping("/client/projects/{projectId}/assets/complete")
    public ClientProjectAssetResponse completeProjectAssetUpload(
            @PathVariable UUID projectId,
            @Valid @RequestBody CompletePrivateUploadRequest request,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return clientPortalService.completeProjectAssetUpload(context, projectId, request);
    }

    /**
     * POST {@code /api/v1/client-portal/client/projects/{projectId}/links}.
     * Registers an external URL as an asset on the customer's project. If no label is given, the URL's
     * host supplies it. Requires a client session with write access.
     *
     * @param projectId identifier of the client project
     * @param request external URL with an optional label and supporting notes
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the saved link asset and its project metadata
     */
    @PostMapping("/client/projects/{projectId}/links")
    public ClientProjectAssetResponse createProjectLink(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateProjectLinkRequest request,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return clientPortalService.createProjectLink(context, projectId, request);
    }

    /**
     * GET {@code /api/v1/client-portal/client/project-assets/{assetId}/download}.
     * Checks that the file belongs to the signed-in customer before opening it. External link assets
     * cannot be streamed.
     *
     * @param assetId identifier of the asset
     * @param clientSessionToken session credential supplied for the client workspace
     * @return HTTP 200 with the file stream, attachment filename, content length and content type
     * @throws IOException if the underlying input or output cannot be completed
     */
    @GetMapping("/client/project-assets/{assetId}/download")
    public ResponseEntity<InputStreamResource> downloadProjectAsset(
            @PathVariable UUID assetId,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) throws IOException {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        var download = clientPortalService.getProjectAssetDownload(context, assetId);
        var asset = download.asset();
        MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;

        if (asset.getContentType() != null && !asset.getContentType().isBlank()) {
            try {
                contentType = MediaType.parseMediaType(asset.getContentType());
            } catch (IllegalArgumentException ignored) {
                contentType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(asset.getSizeBytes())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(asset.getOriginalFilename(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(new InputStreamResource(download.inputStream()));
    }

    /**
     * GET {@code /api/v1/client-portal/admin/clients/{clientId}}.
     * Builds the portal for the customer selected by staff using that customer's onboarding dashboard.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the selected customer's portal and project overview
     */
    @GetMapping("/admin/clients/{clientId}")
    public ClientPortalResponse getClientPortalAsAdmin(
            @PathVariable UUID clientId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientPortalService.getPortalForAdmin(clientId);
    }

    /**
     * GET {@code /api/v1/client-portal/admin/projects}.
     * Lists customer projects for the staff directory, excluding recognised test accounts.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return project summaries ordered by most recent update
     */
    @GetMapping("/admin/projects")
    public List<AdminClientProjectSummaryResponse> listProjectsAsAdmin(
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientPortalService.listProjectsAsAdmin();
    }

    /**
     * GET {@code /api/v1/client-portal/admin/actions}.
     * Combines submitted onboarding tasks, uploaded assets and pending project feedback in one staff
     * review queue.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return actions with critical items first, then earlier action times
     */
    @GetMapping("/admin/actions")
    public List<AdminActionItemResponse> listActionItemsAsAdmin(
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientPortalService.listActionItemsAsAdmin();
    }

    /**
     * PATCH {@code /api/v1/client-portal/admin/projects/{projectId}}.
     * Applies supplied changes to the project name, phase and staging URL. Moving out of review clears the
     * pending revision timestamp. Requires internal access.
     *
     * @param projectId identifier of the client project
     * @param request the project name, phase or staging URL changes to apply
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the saved project overview
     */
    @PatchMapping("/admin/projects/{projectId}")
    public ClientProjectResponse updateProjectAsAdmin(
            @PathVariable UUID projectId,
            @Valid @RequestBody UpdateClientProjectRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientPortalService.updateProjectAsAdmin(projectId, request);
    }

    /**
     * GET {@code /api/v1/client-portal/admin/projects/{projectId}/assets}.
     * Lists the assets submitted to a project for staff review.
     *
     * @param projectId identifier of the client project
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the project's assets in upload order, oldest first
     */
    @GetMapping("/admin/projects/{projectId}/assets")
    public List<ClientProjectAssetResponse> listProjectAssetsAsAdmin(
            @PathVariable UUID projectId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientPortalService.listProjectAssetsAsAdmin(projectId);
    }

    /**
     * GET {@code /api/v1/client-portal/admin/projects/{projectId}/config-snapshots}.
     * Shows the project's saved configuration versions to staff.
     *
     * @param projectId identifier of the client project
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return configuration snapshots in reverse creation order
     */
    @GetMapping("/admin/projects/{projectId}/config-snapshots")
    public List<ClientProjectConfigSnapshotResponse> listProjectConfigSnapshotsAsAdmin(
            @PathVariable UUID projectId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientPortalService.listProjectConfigSnapshotsAsAdmin(projectId);
    }

    /**
     * POST {@code /api/v1/client-portal/admin/projects/{projectId}/config-snapshots}.
     * Parses the supplied JSON and stores a configuration snapshot with its project and author. Requires
     * internal access.
     *
     * @param projectId identifier of the client project
     * @param request JSON configuration, label and artifact type to save
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the saved snapshot and its metadata
     */
    @PostMapping("/admin/projects/{projectId}/config-snapshots")
    public ClientProjectConfigSnapshotResponse createProjectConfigSnapshotAsAdmin(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateClientProjectConfigSnapshotRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        var adminUser = adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        String createdByUsername = adminUser == null ? "internal_api" : adminUser.getUsername();
        return clientPortalService.createProjectConfigSnapshotAsAdmin(projectId, request, createdByUsername);
    }

    /**
     * PATCH {@code /api/v1/client-portal/admin/project-assets/{assetId}/approve}.
     * Marks the asset approved, clears prior review feedback and records the review time. Requires
     * internal access.
     *
     * @param assetId identifier of the asset
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the saved asset with its new review state
     */
    @PatchMapping("/admin/project-assets/{assetId}/approve")
    public ClientProjectAssetResponse approveProjectAssetAsAdmin(
            @PathVariable UUID assetId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientPortalService.approveProjectAssetAsAdmin(assetId);
    }

    /**
     * PATCH {@code /api/v1/client-portal/admin/project-assets/{assetId}/reject}.
     * Marks the asset rejected and records the review time and any staff feedback.
     *
     * @param assetId identifier of the asset
     * @param request optional feedback explaining the rejection
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the saved asset with its feedback and review state
     */
    @PatchMapping("/admin/project-assets/{assetId}/reject")
    public ClientProjectAssetResponse rejectProjectAssetAsAdmin(
            @PathVariable UUID assetId,
            @Valid @RequestBody ReviewProjectAssetRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return clientPortalService.rejectProjectAssetAsAdmin(assetId, request);
    }

    /**
     * GET {@code /api/v1/client-portal/admin/project-assets/{assetId}/download}.
     * Opens a stored project file for a staff download. External links have no file stream.
     *
     * @param assetId identifier of the asset
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return HTTP 200 with the file stream, attachment filename, content length and content type
     * @throws IOException if the underlying input or output cannot be completed
     */
    @GetMapping("/admin/project-assets/{assetId}/download")
    public ResponseEntity<InputStreamResource> downloadProjectAssetAsAdmin(
            @PathVariable UUID assetId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) throws IOException {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        var download = clientPortalService.getProjectAssetDownloadAsAdmin(assetId);
        var asset = download.asset();
        MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;

        if (asset.getContentType() != null && !asset.getContentType().isBlank()) {
            try {
                contentType = MediaType.parseMediaType(asset.getContentType());
            } catch (IllegalArgumentException ignored) {
                contentType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(asset.getSizeBytes())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(asset.getOriginalFilename(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(new InputStreamResource(download.inputStream()));
    }
}
