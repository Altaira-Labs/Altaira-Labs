package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps client project asset records to the {@code client_project_assets} table. */
@Entity
@Table(name = "client_project_assets")
public class ClientProjectAssetEntity {

    /** Primary identifier for this client project asset. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Project. Joined through {@code project_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private ClientProjectEntity project;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    /** Uploaded by user. Joined through {@code uploaded_by_user_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id")
    private AppUserEntity uploadedByUser;

    /** Asset type. Stored in {@code asset_type}. */
    @Column(name = "asset_type", nullable = false, length = 80)
    private String assetType = "general";

    /** Free-text notes associated with the item. */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /** Filename supplied by the uploader. Stored in {@code original_filename}. */
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    /** Stored filename. Stored in {@code stored_filename}. */
    @Column(name = "stored_filename", nullable = false)
    private String storedFilename;

    /** Key identifying the storage. Stored in {@code storage_key}. */
    @Column(name = "storage_key", nullable = false, unique = true, length = 700)
    private String storageKey;

    /** Media type declared for the file. Stored in {@code content_type}. */
    @Column(name = "content_type", length = 120)
    private String contentType;

    /** Content length in bytes. Stored in {@code size_bytes}. */
    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    /** SHA-256 digest used to identify file contents. Stored in {@code checksum_sha256}. */
    @Column(name = "checksum_sha256", length = 64)
    private String checksumSha256;

    /** Current state in the workflow. */
    @Column(nullable = false, length = 40)
    private String status = "uploaded";

    /** Admin feedback. Stored in {@code admin_feedback}. */
    @Column(name = "admin_feedback", columnDefinition = "TEXT")
    private String adminFeedback;

    /** Time associated with uploaded. Stored in {@code uploaded_at}. */
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Instant uploadedAt;

    /** Time associated with reviewed. Stored in {@code reviewed_at}. */
    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    /** Initializes uploaded at before the record is inserted. */
    @PrePersist
    void onCreate() {
        uploadedAt = Instant.now();
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client project asset.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return project} */
    public ClientProjectEntity getProject() { return project; }
    /**
     * Sets the project on this client project asset.
     *
     * @param project project
     */
    public void setProject(ClientProjectEntity project) { this.project = project; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this client project asset.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

    /** {@return uploaded by user} */
    public AppUserEntity getUploadedByUser() { return uploadedByUser; }
    /**
     * Sets the uploaded by user on this client project asset.
     *
     * @param uploadedByUser uploaded by user
     */
    public void setUploadedByUser(AppUserEntity uploadedByUser) { this.uploadedByUser = uploadedByUser; }

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

    /** {@return stored filename} */
    public String getStoredFilename() { return storedFilename; }
    /**
     * Sets the stored filename on this client project asset.
     *
     * @param storedFilename stored filename
     */
    public void setStoredFilename(String storedFilename) { this.storedFilename = storedFilename; }

    /** {@return key identifying the storage} */
    public String getStorageKey() { return storageKey; }
    /**
     * Sets the key identifying the storage on this client project asset.
     *
     * @param storageKey key identifying the storage
     */
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }

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
