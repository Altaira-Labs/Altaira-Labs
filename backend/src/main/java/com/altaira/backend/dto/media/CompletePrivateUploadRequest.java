package com.altaira.backend.dto.media;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Input for complete private upload in the media API. */
public class CompletePrivateUploadRequest {

    /** Bucket. The request must supply nonblank text. */
    @NotBlank(message = "bucket is required")
    @Size(max = 255, message = "bucket must be 255 characters or fewer")
    private String bucket;

    /** Storage key identifying the private file. The request must supply nonblank text. */
    @NotBlank(message = "objectKey is required")
    @Size(max = 700, message = "objectKey must be 700 characters or fewer")
    private String objectKey;

    /** Filename. The request must supply nonblank text. */
    @NotBlank(message = "filename is required")
    @Size(max = 180, message = "filename must be 180 characters or fewer")
    private String filename;

    /** Media type declared for the file. */
    @Size(max = 120, message = "contentType must be 120 characters or fewer")
    private String contentType;

    /** Content length in bytes. */
    @Positive(message = "sizeBytes must be greater than zero")
    @Max(value = 15728640, message = "file must be 15 MB or smaller")
    private long sizeBytes;

    /** Asset type. */
    @Size(max = 80, message = "assetType must be 80 characters or fewer")
    private String assetType;

    /** Free-text notes associated with the item. */
    @Size(max = 2000, message = "notes must be 2000 characters or fewer")
    private String notes;

    /** {@return bucket} */
    public String getBucket() { return bucket; }
    /**
     * Sets the bucket on this complete private upload.
     *
     * @param bucket bucket
     */
    public void setBucket(String bucket) { this.bucket = bucket; }

    /** {@return storage key identifying the private file} */
    public String getObjectKey() { return objectKey; }
    /**
     * Sets the storage key identifying the private file on this complete private upload.
     *
     * @param objectKey storage key identifying the private file
     */
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }

    /** {@return filename} */
    public String getFilename() { return filename; }
    /**
     * Sets the filename on this complete private upload.
     *
     * @param filename filename
     */
    public void setFilename(String filename) { this.filename = filename; }

    /** {@return media type declared for the file} */
    public String getContentType() { return contentType; }
    /**
     * Sets the media type declared for the file on this complete private upload.
     *
     * @param contentType media type declared for the file
     */
    public void setContentType(String contentType) { this.contentType = contentType; }

    /** {@return content length in bytes} */
    public long getSizeBytes() { return sizeBytes; }
    /**
     * Sets the content length in bytes on this complete private upload.
     *
     * @param sizeBytes content length in bytes
     */
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }

    /** {@return asset type} */
    public String getAssetType() { return assetType; }
    /**
     * Sets the asset type on this complete private upload.
     *
     * @param assetType asset type
     */
    public void setAssetType(String assetType) { this.assetType = assetType; }

    /** {@return free-text notes associated with the item} */
    public String getNotes() { return notes; }
    /**
     * Sets the free-text notes associated with the item on this complete private upload.
     *
     * @param notes free-text notes associated with the item
     */
    public void setNotes(String notes) { this.notes = notes; }
}
