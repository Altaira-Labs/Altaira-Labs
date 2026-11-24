package com.altaira.backend.dto.onboarding;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for onboarding file. */
public class OnboardingFileResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the task being acted on. */
    private UUID taskId;
    /** Identifier of the client whose workspace is in scope. */
    private UUID clientId;
    /** Filename supplied by the uploader. */
    private String originalFilename;
    /** Media type declared for the file. */
    private String contentType;
    /** Content length in bytes. */
    private long sizeBytes;
    /** SHA-256 digest used to identify file contents. */
    private String checksumSha256;
    /** Time the record was created. */
    private Instant createdAt;

    /** Constructs {@code OnboardingFileResponse} with its default state. */
    public OnboardingFileResponse() {}

    /**
     * Constructs {@code OnboardingFileResponse} using the supplied id, task id, client id and remaining fields.
     *
     * @param id unique identifier of the record
     * @param taskId identifier of the task being acted on
     * @param clientId identifier of the client whose workspace is in scope
     * @param originalFilename filename supplied by the uploader
     * @param contentType media type declared for the file
     * @param sizeBytes content length in bytes
     * @param checksumSha256 SHA-256 digest used to identify file contents
     * @param createdAt time the record was created
     */
    public OnboardingFileResponse(
            UUID id,
            UUID taskId,
            UUID clientId,
            String originalFilename,
            String contentType,
            long sizeBytes,
            String checksumSha256,
            Instant createdAt
    ) {
        this.id = id;
        this.taskId = taskId;
        this.clientId = clientId;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.checksumSha256 = checksumSha256;
        this.createdAt = createdAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this onboarding file.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the task being acted on} */
    public UUID getTaskId() { return taskId; }
    /**
     * Sets the identifier of the task being acted on on this onboarding file.
     *
     * @param taskId identifier of the task being acted on
     */
    public void setTaskId(UUID taskId) { this.taskId = taskId; }

    /** {@return identifier of the client whose workspace is in scope} */
    public UUID getClientId() { return clientId; }
    /**
     * Sets the identifier of the client whose workspace is in scope on this onboarding file.
     *
     * @param clientId identifier of the client whose workspace is in scope
     */
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    /** {@return filename supplied by the uploader} */
    public String getOriginalFilename() { return originalFilename; }
    /**
     * Sets the filename supplied by the uploader on this onboarding file.
     *
     * @param originalFilename filename supplied by the uploader
     */
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

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
