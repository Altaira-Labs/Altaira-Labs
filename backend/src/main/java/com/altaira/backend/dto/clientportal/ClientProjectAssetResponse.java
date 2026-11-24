package com.altaira.backend.dto.clientportal;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for client project asset. */
public class ClientProjectAssetResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the client project. */
    private UUID projectId;
    /** Identifier of the client whose workspace is in scope. */
    private UUID clientId;
    /** Asset type. */
    private String assetType;
    /** Free-text notes associated with the item. */
    private String notes;
    /** Filename supplied by the uploader. */
    private String originalFilename;
    /** Media type declared for the file. */
    private String contentType;
    /** Content length in bytes. */
    private long sizeBytes;
    /** SHA-256 digest used to identify file contents. */
    private String checksumSha256;
    /** Link to the resource in the external system. */
    private String externalUrl;
    /** Current state in the workflow. */
    private String status;
    /** Admin feedback. */
    private String adminFeedback;
    /** Time associated with uploaded. */
    private Instant uploadedAt;
    /** Time associated with reviewed. */
    private Instant reviewedAt;

    /** Constructs {@code ClientProjectAssetResponse} with its default state. */
    public ClientProjectAssetResponse() {}

    /**
     * Constructs {@code ClientProjectAssetResponse} using the supplied id, project id, client id and remaining fields.
     *
     * @param id unique identifier of the record
     * @param projectId identifier of the client project
     * @param clientId identifier of the client whose workspace is in scope
     * @param assetType asset type
     * @param notes free-text notes associated with the item
     * @param originalFilename filename supplied by the uploader
     * @param contentType media type declared for the file
     * @param sizeBytes content length in bytes
     * @param checksumSha256 SHA-256 digest used to identify file contents
     * @param externalUrl link to the resource in the external system
     * @param status current state in the workflow
     * @param adminFeedback admin feedback
     * @param uploadedAt time associated with uploaded
     * @param reviewedAt time associated with reviewed
     */
    public ClientProjectAssetResponse(
            UUID id,
            UUID projectId,
            UUID clientId,
            String assetType,
            String notes,
            String originalFilename,
            String contentType,
            long sizeBytes,
            String checksumSha256,
            String externalUrl,
            String status,
            String adminFeedback,
            Instant uploadedAt,
            Instant reviewedAt
    ) {
        this.id = id;
        this.projectId = projectId;
        this.clientId = clientId;
        this.assetType = assetType;
        this.notes = notes;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.checksumSha256 = checksumSha256;
        this.externalUrl = externalUrl;
        this.status = status;
        this.adminFeedback = adminFeedback;
        this.uploadedAt = uploadedAt;
        this.reviewedAt = reviewedAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client project asset.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the client project} */
    public UUID getProjectId() { return projectId; }
    /**
     * Sets the identifier of the client project on this client project asset.
     *
     * @param projectId identifier of the client project
     */
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    /** {@return identifier of the client whose workspace is in scope} */
    public UUID getClientId() { return clientId; }
    /**
     * Sets the identifier of the client whose workspace is in scope on this client project asset.
     *
     * @param clientId identifier of the client whose workspace is in scope
     */
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    /** {@return asset type} */
    public String getAssetType() { return assetType; }
    /**
     * Sets the asset type on this client project asset.
     *
     * @param assetType asset type
     */
    public void setAssetType(String assetType) { this.assetType = assetType; }

    /** {@return free-text notes associated with the item} */
    public String getNotes() { return notes; }
    /**
     * Sets the free-text notes associated with the item on this client project asset.
     *
     * @param notes free-text notes associated with the item
     */
    public void setNotes(String notes) { this.notes = notes; }

    /** {@return filename supplied by the uploader} */
    public String getOriginalFilename() { return originalFilename; }
    /**
     * Sets the filename supplied by the uploader on this client project asset.
     *
     * @param originalFilename filename supplied by the uploader
     */
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    /** {@return media type declared for the file} */
    public String getContentType() { return contentType; }
    /**
     * Sets the media type declared for the file on this client project asset.
     *
     * @param contentType media type declared for the file
     */
    public void setContentType(String contentType) { this.contentType = contentType; }

    /** {@return content length in bytes} */
    public long getSizeBytes() { return sizeBytes; }
    /**
     * Sets the content length in bytes on this client project asset.
     *
     * @param sizeBytes content length in bytes
     */
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }

    /** {@return SHA-256 digest used to identify file contents} */
    public String getChecksumSha256() { return checksumSha256; }
    /**
     * Sets the SHA-256 digest used to identify file contents on this client project asset.
     *
     * @param checksumSha256 SHA-256 digest used to identify file contents
     */
    public void setChecksumSha256(String checksumSha256) { this.checksumSha256 = checksumSha256; }

    /** {@return link to the resource in the external system} */
    public String getExternalUrl() { return externalUrl; }
    /**
     * Sets the link to the resource in the external system on this client project asset.
     *
     * @param externalUrl link to the resource in the external system
     */
    public void setExternalUrl(String externalUrl) { this.externalUrl = externalUrl; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this client project asset.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return admin feedback} */
    public String getAdminFeedback() { return adminFeedback; }
    /**
     * Sets the admin feedback on this client project asset.
     *
     * @param adminFeedback admin feedback
     */
    public void setAdminFeedback(String adminFeedback) { this.adminFeedback = adminFeedback; }

    /** {@return time associated with uploaded} */
    public Instant getUploadedAt() { return uploadedAt; }
    /**
     * Sets the time associated with uploaded on this client project asset.
     *
     * @param uploadedAt time associated with uploaded
     */
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }

    /** {@return time associated with reviewed} */
    public Instant getReviewedAt() { return reviewedAt; }
    /**
     * Sets the time associated with reviewed on this client project asset.
     *
     * @param reviewedAt time associated with reviewed
     */
    public void setReviewedAt(Instant reviewedAt) { this.reviewedAt = reviewedAt; }
}
