package com.altaira.backend.dto.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for create upload URL in the media API. */
public class CreateUploadUrlRequest {

    /** Folder. The request must supply nonblank text. */
    @NotBlank(message = "folder is required")
    private String folder;

    /** Stable key for the service. */
    @Size(max = 80, message = "serviceKey must be 80 characters or fewer")
    private String serviceKey;

    /** Filename. The request must supply nonblank text. */
    @NotBlank(message = "filename is required")
    @Size(max = 180, message = "filename must be 180 characters or fewer")
    private String filename;

    /** Media type declared for the file. */
    @Size(max = 120, message = "contentType must be 120 characters or fewer")
    private String contentType;

    /** {@return folder} */
    public String getFolder() {
        return folder;
    }

    /**
     * Sets the folder on this create upload URL.
     *
     * @param folder folder
     */
    public void setFolder(String folder) {
        this.folder = folder;
    }

    /** {@return stable key for the service} */
    public String getServiceKey() {
        return serviceKey;
    }

    /**
     * Sets the stable key for the service on this create upload URL.
     *
     * @param serviceKey stable key for the service
     */
    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    /** {@return filename} */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename on this create upload URL.
     *
     * @param filename filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /** {@return media type declared for the file} */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the media type declared for the file on this create upload URL.
     *
     * @param contentType media type declared for the file
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
