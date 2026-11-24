package com.altaira.backend.dto.media;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Input for prepare private upload in the media API. */
public class PreparePrivateUploadRequest {

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

    /** {@return filename} */
    public String getFilename() { return filename; }
    /**
     * Sets the filename on this prepare private upload.
     *
     * @param filename filename
     */
    public void setFilename(String filename) { this.filename = filename; }

    /** {@return media type declared for the file} */
    public String getContentType() { return contentType; }
    /**
     * Sets the media type declared for the file on this prepare private upload.
     *
     * @param contentType media type declared for the file
     */
    public void setContentType(String contentType) { this.contentType = contentType; }

    /** {@return content length in bytes} */
    public long getSizeBytes() { return sizeBytes; }
    /**
     * Sets the content length in bytes on this prepare private upload.
     *
     * @param sizeBytes content length in bytes
     */
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }

    /** {@return asset type} */
    public String getAssetType() { return assetType; }
    /**
     * Sets the asset type on this prepare private upload.
     *
     * @param assetType asset type
     */
    public void setAssetType(String assetType) { this.assetType = assetType; }
}
