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

/** HTTP endpoints for client portal under {@code /api/v1/client-portal}. */
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
     * GET {@code /api/v1/client-portal/me}. Retrieves my portal. Membership is checked before the workspace operation.
     *
     * @param clientSessionToken session credential supplied for the client workspace
     * @return my portal
     */
    @GetMapping("/me")
    public ClientPortalResponse getMyPortal(
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        return clientPortalService.getPortal(context);
    }

    /**
     * PATCH {@code /api/v1/client-portal/client/projects/{projectId}/feedback}. Submits project feedback. Membership is checked before the workspace operation.
     *
     * @param projectId identifier of the client project
     * @param request input carrying submit project feedback
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the client project response
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
     * GET {@code /api/v1/client-portal/client/projects/{projectId}/assets}. Lists project assets. Membership is checked before the workspace operation.
     *
     * @param projectId identifier of the client project
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the client project asset response items
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
     * POST {@code /api/v1/client-portal/client/projects/{projectId}/assets}. Uploads project assets. Membership is checked before the workspace operation.
     *
     * @param projectId identifier of the client project
     * @param files files
     * @param assetType asset type
     * @param notes free-text notes associated with the item
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the client project asset response items
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
     * POST {@code /api/v1/client-portal/client/projects/{projectId}/assets/upload-url}. Prepares project asset upload. Membership is checked before the workspace operation.
     *
     * @param projectId identifier of the client project
     * @param request input carrying prepare private upload
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the upload URL response
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
     * POST {@code /api/v1/client-portal/client/projects/{projectId}/assets/complete}. Complete project asset upload. Membership is checked before the workspace operation.
     *
     * @param projectId identifier of the client project
     * @param request input carrying complete private upload
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the client project asset response
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
     * POST {@code /api/v1/client-portal/client/projects/{projectId}/links}. Creates project link. Membership is checked before the workspace operation.
     *
     * @param projectId identifier of the client project
     * @param request input carrying create project link
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the client project asset response
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
     * GET {@code /api/v1/client-portal/client/project-assets/{assetId}/download}. Downloads project asset. Membership is checked before the workspace operation.
     *
     * @param assetId identifier of the asset
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the HTTP response for this operation
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
     * GET {@code /api/v1/client-portal/admin/clients/{clientId}}. Retrieves client portal as admin. Internal access is checked before delegation.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return client portal as admin
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
     * GET {@code /api/v1/client-portal/admin/projects}. Lists projects as admin. Internal access is checked before delegation.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the admin client project summary response items
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
     * GET {@code /api/v1/client-portal/admin/actions}. Lists action items as admin. Internal access is checked before delegation.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the admin action item response items
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
     * PATCH {@code /api/v1/client-portal/admin/projects/{projectId}}. Updates project as admin. Internal access is checked before delegation.
     *
     * @param projectId identifier of the client project
     * @param request input carrying update client project
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client project response
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
     * GET {@code /api/v1/client-portal/admin/projects/{projectId}/assets}. Lists project assets as admin. Internal access is checked before delegation.
     *
     * @param projectId identifier of the client project
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client project asset response items
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
     * GET {@code /api/v1/client-portal/admin/projects/{projectId}/config-snapshots}. Lists project config snapshots as admin. Internal access is checked before delegation.
     *
     * @param projectId identifier of the client project
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client project config snapshot response items
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
     * POST {@code /api/v1/client-portal/admin/projects/{projectId}/config-snapshots}. Creates project config snapshot as admin. Internal access is checked before delegation.
     *
     * @param projectId identifier of the client project
     * @param request input carrying create client project config snapshot
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client project config snapshot response
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
     * PATCH {@code /api/v1/client-portal/admin/project-assets/{assetId}/approve}. Approves project asset as admin. Internal access is checked before delegation.
     *
     * @param assetId identifier of the asset
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client project asset response
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
     * PATCH {@code /api/v1/client-portal/admin/project-assets/{assetId}/reject}. Rejects project asset as admin. Internal access is checked before delegation.
     *
     * @param assetId identifier of the asset
     * @param request input carrying review project asset
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the client project asset response
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
     * GET {@code /api/v1/client-portal/admin/project-assets/{assetId}/download}. Downloads project asset as admin. Internal access is checked before delegation.
     *
     * @param assetId identifier of the asset
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the HTTP response for this operation
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
