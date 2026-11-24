package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps onboarding file records to the {@code onboarding_files} table. */
@Entity
@Table(name = "onboarding_files")
public class OnboardingFileEntity {

    /** Primary identifier for this onboarding file. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Task. Joined through {@code task_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private OnboardingTaskEntity task;

    /** Client workspace in scope. Joined through {@code workspace_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private ClientWorkspaceEntity workspace;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

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

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Initializes created at before the record is inserted. */
    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this onboarding file.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return task} */
    public OnboardingTaskEntity getTask() { return task; }
    /**
     * Sets the task on this onboarding file.
     *
     * @param task task
     */
    public void setTask(OnboardingTaskEntity task) { this.task = task; }

    /** {@return client workspace in scope} */
    public ClientWorkspaceEntity getWorkspace() { return workspace; }
    /**
     * Sets the client workspace in scope on this onboarding file.
     *
     * @param workspace client workspace in scope
     */
    public void setWorkspace(ClientWorkspaceEntity workspace) { this.workspace = workspace; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this onboarding file.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

    /** {@return filename supplied by the uploader} */
    public String getOriginalFilename() { return originalFilename; }
    /**
     * Sets the filename supplied by the uploader on this onboarding file.
     *
     * @param originalFilename filename supplied by the uploader
     */
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    /** {@return stored filename} */
    public String getStoredFilename() { return storedFilename; }
    /**
     * Sets the stored filename on this onboarding file.
     *
     * @param storedFilename stored filename
     */
    public void setStoredFilename(String storedFilename) { this.storedFilename = storedFilename; }

    /** {@return key identifying the storage} */
    public String getStorageKey() { return storageKey; }
    /**
     * Sets the key identifying the storage on this onboarding file.
     *
     * @param storageKey key identifying the storage
     */
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }

    /** {@return media type declared for the file} */
    public String getContentType() { return contentType; }
    /**
     * Sets the media type declared for the file on this onboarding file.
     *
     * @param contentType media type declared for the file
     */
    public void setContentType(String contentType) { this.contentType = contentType; }

    /** {@return content length in bytes} */
    public long getSizeBytes() { return sizeBytes; }
    /**
     * Sets the content length in bytes on this onboarding file.
     *
     * @param sizeBytes content length in bytes
     */
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }

    /** {@return SHA-256 digest used to identify file contents} */
    public String getChecksumSha256() { return checksumSha256; }
    /**
     * Sets the SHA-256 digest used to identify file contents on this onboarding file.
     *
     * @param checksumSha256 SHA-256 digest used to identify file contents
     */
    public void setChecksumSha256(String checksumSha256) { this.checksumSha256 = checksumSha256; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this onboarding file.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
