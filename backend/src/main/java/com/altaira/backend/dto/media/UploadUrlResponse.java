package com.altaira.backend.dto.media;

import java.time.Instant;
import java.util.Map;

/** Fields the API returns for upload URL. */
public class UploadUrlResponse {
    /** Method. */
    private String method;
    /** URL for upload. */
    private String uploadUrl;
    /** Bucket. */
    private String bucket;
    /** Storage key identifying the private file. */
    private String objectKey;
    /** URL for public. */
    private String publicUrl;
    /** Time after which this item can no longer be used. */
    private Instant expiresAt;
    /** HTTP headers carried with the request or response. */
    private Map<String, String> headers;

    /** Constructs {@code UploadUrlResponse} with its default state. */
    public UploadUrlResponse() {}

    /**
     * Constructs {@code UploadUrlResponse} using the supplied method, upload URL, bucket and remaining fields.
     *
     * @param method method
     * @param uploadUrl URL for upload
     * @param bucket bucket
     * @param objectKey storage key identifying the private file
     * @param publicUrl URL for public
     * @param expiresAt time after which this item can no longer be used
     * @param headers HTTP headers carried with the request or response
     */
    public UploadUrlResponse(
            String method,
            String uploadUrl,
            String bucket,
            String objectKey,
            String publicUrl,
            Instant expiresAt,
            Map<String, String> headers
    ) {
        this.method = method;
        this.uploadUrl = uploadUrl;
        this.bucket = bucket;
        this.objectKey = objectKey;
        this.publicUrl = publicUrl;
        this.expiresAt = expiresAt;
        this.headers = headers;
    }

    /** {@return method} */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the method on this upload URL.
     *
     * @param method method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /** {@return URL for upload} */
    public String getUploadUrl() {
        return uploadUrl;
    }

    /**
     * Sets the URL for upload on this upload URL.
     *
     * @param uploadUrl URL for upload
     */
    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    /** {@return bucket} */
    public String getBucket() {
        return bucket;
    }

    /**
     * Sets the bucket on this upload URL.
     *
     * @param bucket bucket
     */
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    /** {@return storage key identifying the private file} */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * Sets the storage key identifying the private file on this upload URL.
     *
     * @param objectKey storage key identifying the private file
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    /** {@return URL for public} */
    public String getPublicUrl() {
        return publicUrl;
    }

    /**
     * Sets the URL for public on this upload URL.
     *
     * @param publicUrl URL for public
     */
    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    /** {@return time after which this item can no longer be used} */
    public Instant getExpiresAt() {
        return expiresAt;
    }

    /**
     * Sets the time after which this item can no longer be used on this upload URL.
     *
     * @param expiresAt time after which this item can no longer be used
     */
    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    /** {@return HTTP headers carried with the request or response} */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Sets the HTTP headers carried with the request or response on this upload URL.
     *
     * @param headers HTTP headers carried with the request or response
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
