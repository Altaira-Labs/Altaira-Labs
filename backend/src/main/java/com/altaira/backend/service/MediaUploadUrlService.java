package com.altaira.backend.service;

import com.altaira.backend.dto.media.CreateUploadUrlRequest;
import com.altaira.backend.dto.media.PreparePrivateUploadRequest;
import com.altaira.backend.dto.media.UploadUrlResponse;
import com.altaira.backend.security.ClientAccessContext;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Owns the private S3 object boundary used by onboarding and project assets.
 * It validates tenant-scoped keys and file metadata before issuing short-lived
 * presigned uploads or opening a stored object; it never exposes AWS credentials.
 */
@Service
public class MediaUploadUrlService {

    /** Default content type used by media upload URL service. */
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    /** S3 storage prefix used by media upload URL service. */
    private static final String S3_STORAGE_PREFIX = "s3://";
    /** Allowed folders used by media upload URL service. */
    private static final Set<String> ALLOWED_FOLDERS = Set.of("branding", "legal", "multimedia");
    /** Blocked extensions used by media upload URL service. */
    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
            "app", "bat", "cmd", "com", "dll", "dmg", "exe", "html", "htm", "jar",
            "js", "mjs", "msi", "php", "ps1", "scr", "sh", "vbs"
    );

    /** Whether the capability is enabled. */
    private final boolean enabled;
    /** Region. */
    private final String region;
    /** Private bucket. */
    private final String privateBucket;
    /** Public bucket. */
    private final String publicBucket;
    /** URL for public base. */
    private final String publicBaseUrl;
    /** Presign ttl in seconds. */
    private final long presignTtlSeconds;
    /** Max file size bytes. */
    private final long maxFileSizeBytes;
    /** S3 client. */
    private final S3Client s3Client;
    /** Presigner. */
    private final S3Presigner presigner;

    /**
     * Constructs {@code MediaUploadUrlService} using the supplied enabled, region, private bucket and remaining fields.
     *
     * @param enabled whether the capability is enabled
     * @param region region
     * @param privateBucket private bucket
     * @param publicBucket public bucket
     * @param publicBaseUrl URL for public base
     * @param presignTtlSeconds presign ttl in seconds
     * @param maxFileSizeBytes max file size bytes
     * @throws IllegalStateException when validation reports “S3 uploads are enabled but region or private bucket is missing”
     */
    @Autowired
    public MediaUploadUrlService(
            @Value("${altaira.media.s3.enabled:false}") boolean enabled,
            @Value("${altaira.media.s3.region:}") String region,
            @Value("${altaira.media.s3.private-bucket:}") String privateBucket,
            @Value("${altaira.media.s3.public-bucket:}") String publicBucket,
            @Value("${altaira.media.s3.public-base-url:}") String publicBaseUrl,
            @Value("${altaira.media.s3.presign-ttl-seconds:900}") long presignTtlSeconds,
            @Value("${altaira.onboarding.max-file-size-bytes:15728640}") long maxFileSizeBytes
    ) {
        this.enabled = enabled;
        this.region = trimToNull(region);
        this.privateBucket = trimToNull(privateBucket);
        this.publicBucket = trimToNull(publicBucket);
        this.publicBaseUrl = trimToNull(publicBaseUrl);
        this.presignTtlSeconds = Math.max(60, Math.min(presignTtlSeconds, 3600));
        this.maxFileSizeBytes = Math.max(1, maxFileSizeBytes);

        if (enabled && (this.region == null || this.privateBucket == null)) {
            throw new IllegalStateException("S3 uploads are enabled but region or private bucket is missing");
        }

        if (enabled) {
            Region awsRegion = Region.of(this.region);
            this.s3Client = S3Client.builder().region(awsRegion).build();
            this.presigner = S3Presigner.builder().region(awsRegion).build();
        } else {
            this.s3Client = null;
            this.presigner = null;
        }
    }

    /**
     * Constructs {@code MediaUploadUrlService} using the supplied enabled, region, private bucket and remaining fields.
     *
     * @param enabled whether the capability is enabled
     * @param region region
     * @param privateBucket private bucket
     * @param publicBucket public bucket
     * @param publicBaseUrl URL for public base
     * @param presignTtlSeconds presign ttl in seconds
     */
    MediaUploadUrlService(
            boolean enabled,
            String region,
            String privateBucket,
            String publicBucket,
            String publicBaseUrl,
            long presignTtlSeconds
    ) {
        this(enabled, region, privateBucket, publicBucket, publicBaseUrl, presignTtlSeconds, 15_728_640L);
    }

    /**
     * Constructs {@code MediaUploadUrlService} using the supplied enabled, region, private bucket and remaining fields.
     *
     * @param enabled whether the capability is enabled
     * @param region region
     * @param privateBucket private bucket
     * @param publicBucket public bucket
     * @param publicBaseUrl URL for public base
     * @param presignTtlSeconds presign ttl in seconds
     * @param maxFileSizeBytes max file size bytes
     * @param s3Client S3 client
     * @param presigner presigner
     * @throws IllegalStateException when validation reports “S3 uploads are enabled but configuration or clients are missing”
     */
    MediaUploadUrlService(
            boolean enabled,
            String region,
            String privateBucket,
            String publicBucket,
            String publicBaseUrl,
            long presignTtlSeconds,
            long maxFileSizeBytes,
            S3Client s3Client,
            S3Presigner presigner
    ) {
        this.enabled = enabled;
        this.region = trimToNull(region);
        this.privateBucket = trimToNull(privateBucket);
        this.publicBucket = trimToNull(publicBucket);
        this.publicBaseUrl = trimToNull(publicBaseUrl);
        this.presignTtlSeconds = Math.max(60, Math.min(presignTtlSeconds, 3600));
        this.maxFileSizeBytes = Math.max(1, maxFileSizeBytes);

        if (enabled && (this.region == null || this.privateBucket == null || s3Client == null || presigner == null)) {
            throw new IllegalStateException("S3 uploads are enabled but configuration or clients are missing");
        }

        this.s3Client = enabled ? s3Client : null;
        this.presigner = enabled ? presigner : null;
    }

    /** {@return whether the capability is enabled} */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Creates client upload URL.
     *
     * @param context context carrying client access
     * @param request input carrying create upload URL
     * @return the upload URL response
     */
    public UploadUrlResponse createClientUploadUrl(ClientAccessContext context, CreateUploadUrlRequest request) {
        String folder = normalizeFolder(request.getFolder());
        return createUploadUrl(
                context.client().getId(),
                folder,
                request.getServiceKey(),
                "general",
                UUID.randomUUID(),
                request.getFilename(),
                request.getContentType(),
                null
        );
    }

    /**
     * Creates context upload URL.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param folder folder
     * @param serviceKey stable key for the service
     * @param contextType context type
     * @param contextId identifier of the context
     * @param request input carrying prepare private upload
     * @return the upload URL response
     */
    public UploadUrlResponse createContextUploadUrl(
            UUID clientId,
            String folder,
            String serviceKey,
            String contextType,
            UUID contextId,
            PreparePrivateUploadRequest request
    ) {
        validateFileSize(request.getSizeBytes());
        return createUploadUrl(
                clientId,
                normalizeFolder(folder),
                serviceKey,
                contextType,
                contextId,
                request.getFilename(),
                request.getContentType(),
                request.getSizeBytes()
        );
    }

    /**
     * Verifies completed upload.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param folder folder
     * @param serviceKey stable key for the service
     * @param contextType context type
     * @param contextId identifier of the context
     * @param bucket bucket
     * @param objectKey storage key identifying the private file
     * @param filename filename
     * @param expectedContentType expected content type
     * @param expectedSizeBytes expected size bytes
     * @return the verified S3 object
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (forbidden, bad request, service unavailable)
     */
    public VerifiedS3Object verifyCompletedUpload(
            UUID clientId,
            String folder,
            String serviceKey,
            String contextType,
            UUID contextId,
            String bucket,
            String objectKey,
            String filename,
            String expectedContentType,
            long expectedSizeBytes
    ) {
        requireEnabled();
        validateFileSize(expectedSizeBytes);

        String normalizedBucket = trimToNull(bucket);
        String normalizedKey = trimToNull(objectKey);
        String normalizedFilename = sanitizeFilename(filename);
        String normalizedContentType = normalizeContentType(expectedContentType);
        String normalizedFolder = normalizeFolder(folder);

        ensureAllowedBucket(normalizedBucket);
        if (!normalizedBucket.equals(bucketForFolder(normalizedFolder))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "S3 bucket does not match the upload category");
        }
        ensureExpectedObjectKey(
                normalizedKey,
                normalizedFolder,
                clientId,
                serviceKey,
                contextType,
                contextId,
                normalizedFilename
        );

        try {
            HeadObjectResponse head = s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(normalizedBucket)
                    .key(normalizedKey)
                    .build());

            verifyMetadata(head.metadata(), clientId, serviceKey, contextType, contextId, expectedSizeBytes);

            long actualSize = head.contentLength();
            if (actualSize != expectedSizeBytes || actualSize > maxFileSizeBytes) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file size does not match the prepared upload");
            }

            String actualContentType = normalizeContentType(head.contentType());
            if (!actualContentType.equalsIgnoreCase(normalizedContentType)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file content type does not match the prepared upload");
            }

            return new VerifiedS3Object(
                    S3_STORAGE_PREFIX + normalizedBucket + "/" + normalizedKey,
                    normalizedBucket,
                    normalizedKey,
                    normalizedFilename,
                    storedFilename(normalizedKey),
                    actualContentType,
                    actualSize,
                    null
            );
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (S3Exception ex) {
            if (ex.statusCode() == 404) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded object could not be found in S3");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Uploaded object could not be verified in S3");
        } catch (SdkClientException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "S3 could not be reached to verify the upload");
        }
    }

    /**
     * Checks whether this is S3 storage key.
     *
     * @param storageKey key identifying the storage
     * @return true if the value is S3 storage key
     */
    public boolean isS3StorageKey(String storageKey) {
        return storageKey != null && storageKey.startsWith(S3_STORAGE_PREFIX);
    }

    /**
     * Opens stored object.
     *
     * @param storageKey key identifying the storage
     * @return the value produced by this operation
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (not found, service unavailable)
     */
    public ResponseInputStream<GetObjectResponse> openStoredObject(String storageKey) {
        requireEnabled();
        StoredObjectLocation location = parseStorageKey(storageKey);
        ensureAllowedBucket(location.bucket());

        try {
            return s3Client.getObject(GetObjectRequest.builder()
                    .bucket(location.bucket())
                    .key(location.objectKey())
                    .build());
        } catch (S3Exception ex) {
            if (ex.statusCode() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stored S3 object was not found");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Stored S3 object could not be downloaded");
        } catch (SdkClientException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "S3 could not be reached to download the object");
        }
    }

    /**
     * Deletes stored object quietly.
     *
     * @param storageKey key identifying the storage
     */
    public void deleteStoredObjectQuietly(String storageKey) {
        if (!enabled || !isS3StorageKey(storageKey)) {
            return;
        }

        try {
            StoredObjectLocation location = parseStorageKey(storageKey);
            ensureAllowedBucket(location.bucket());
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(location.bucket())
                    .key(location.objectKey())
                    .build());
        } catch (RuntimeException ignored) {
            // Best-effort cleanup only. The original operation reports its own failure.
        }
    }

    /** Closes clients. */
    @PreDestroy
    void closeClients() {
        if (presigner != null) {
            presigner.close();
        }
        if (s3Client != null) {
            s3Client.close();
        }
    }

    /**
     * Creates upload URL.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param folder folder
     * @param serviceKey stable key for the service
     * @param contextType context type
     * @param contextId identifier of the context
     * @param filename filename
     * @param contentType media type declared for the file
     * @param expectedSizeBytes expected size bytes
     * @return the upload URL response
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (service unavailable)
     */
    private UploadUrlResponse createUploadUrl(
            UUID clientId,
            String folder,
            String serviceKey,
            String contextType,
            UUID contextId,
            String filename,
            String contentType,
            Long expectedSizeBytes
    ) {
        String normalizedContentType = normalizeContentType(contentType);
        String safeFilename = sanitizeFilename(filename);
        ensureSafeFile(safeFilename, normalizedContentType);
        requireEnabled();

        String normalizedServiceKey = sanitizePathSegment(serviceKey == null || serviceKey.isBlank() ? "general" : serviceKey);
        String normalizedContextType = sanitizePathSegment(contextType);
        String objectKey = buildObjectKey(
                folder,
                clientId,
                normalizedServiceKey,
                normalizedContextType,
                contextId,
                safeFilename
        );
        String bucket = bucketForFolder(folder);
        Instant expiresAt = Instant.now().plusSeconds(presignTtlSeconds);
        Map<String, String> metadata = uploadMetadata(
                clientId,
                normalizedServiceKey,
                normalizedContextType,
                contextId,
                expectedSizeBytes
        );

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .contentType(normalizedContentType)
                    .metadata(metadata)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(presignTtlSeconds))
                    .putObjectRequest(putObjectRequest)
                    .build();

            var presignedRequest = presigner.presignPutObject(presignRequest);
            Map<String, String> headers = new LinkedHashMap<>();
            headers.put("Content-Type", normalizedContentType);
            metadata.forEach((key, value) -> headers.put("x-amz-meta-" + key, value));

            return new UploadUrlResponse(
                    "PUT",
                    presignedRequest.url().toString(),
                    bucket,
                    objectKey,
                    publicUrlFor(folder, objectKey),
                    expiresAt,
                    headers
            );
        } catch (SdkClientException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "S3 presigned upload could not be created");
        }
    }

    /**
     * Uploads metadata.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param serviceKey stable key for the service
     * @param contextType context type
     * @param contextId identifier of the context
     * @param expectedSizeBytes expected size bytes
     * @return the resulting key–value pairs
     */
    private Map<String, String> uploadMetadata(
            UUID clientId,
            String serviceKey,
            String contextType,
            UUID contextId,
            Long expectedSizeBytes
    ) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("altaira-client-id", clientId.toString());
        metadata.put("altaira-service-key", serviceKey);
        metadata.put("altaira-context-type", contextType);
        metadata.put("altaira-context-id", contextId.toString());
        if (expectedSizeBytes != null) {
            metadata.put("altaira-expected-size", expectedSizeBytes.toString());
        }
        return metadata;
    }

    /**
     * Verifies metadata.
     *
     * @param metadata additional context recorded with the event
     * @param clientId identifier of the client whose workspace is in scope
     * @param serviceKey stable key for the service
     * @param contextType context type
     * @param contextId identifier of the context
     * @param expectedSizeBytes expected size bytes
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request)
     */
    private void verifyMetadata(
            Map<String, String> metadata,
            UUID clientId,
            String serviceKey,
            String contextType,
            UUID contextId,
            long expectedSizeBytes
    ) {
        String normalizedServiceKey = sanitizePathSegment(serviceKey == null || serviceKey.isBlank() ? "general" : serviceKey);
        String normalizedContextType = sanitizePathSegment(contextType);

        if (!clientId.toString().equals(metadata.get("altaira-client-id")) ||
                !normalizedServiceKey.equals(metadata.get("altaira-service-key")) ||
                !normalizedContextType.equals(metadata.get("altaira-context-type")) ||
                !contextId.toString().equals(metadata.get("altaira-context-id")) ||
                !Long.toString(expectedSizeBytes).equals(metadata.get("altaira-expected-size"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded object metadata does not match the prepared upload");
        }
    }

    /**
     * Ensures expected object key.
     *
     * @param objectKey storage key identifying the private file
     * @param folder folder
     * @param clientId identifier of the client whose workspace is in scope
     * @param serviceKey stable key for the service
     * @param contextType context type
     * @param contextId identifier of the context
     * @param filename filename
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (forbidden)
     * @throws IllegalArgumentException when validation reports “objectKey is required”
     */
    private void ensureExpectedObjectKey(
            String objectKey,
            String folder,
            UUID clientId,
            String serviceKey,
            String contextType,
            UUID contextId,
            String filename
    ) {
        if (objectKey == null) {
            throw new IllegalArgumentException("objectKey is required");
        }

        String normalizedServiceKey = sanitizePathSegment(serviceKey == null || serviceKey.isBlank() ? "general" : serviceKey);
        String normalizedContextType = sanitizePathSegment(contextType);
        String expectedPrefix = folder + "/" + clientId + "/" + normalizedServiceKey + "/" +
                normalizedContextType + "/" + contextId + "/";

        if (!objectKey.startsWith(expectedPrefix) || !objectKey.endsWith("-" + filename)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "S3 object does not belong to this client context");
        }
    }

    /**
     * Ensures allowed bucket.
     *
     * @param bucket bucket
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (forbidden)
     */
    private void ensureAllowedBucket(String bucket) {
        if (bucket == null || (!bucket.equals(privateBucket) && !bucket.equals(publicBucket))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "S3 bucket is not allowed for this application");
        }
    }

    /**
     * Ensures safe file. Uses locale-independent case normalization.
     *
     * @param filename filename
     * @param contentType media type declared for the file
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (unsupported media type)
     */
    private void ensureSafeFile(String filename, String contentType) {
        int separator = filename.lastIndexOf('.');
        String extension = separator < 0 ? "" : filename.substring(separator + 1).toLowerCase(Locale.ROOT);
        String normalizedType = contentType.toLowerCase(Locale.ROOT);

        if (BLOCKED_EXTENSIONS.contains(extension) ||
                normalizedType.contains("javascript") ||
                normalizedType.equals("text/html") ||
                normalizedType.equals("application/x-msdownload")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "This file type is not allowed");
        }
    }

    /**
     * Validates file size.
     *
     * @param sizeBytes content length in bytes
     * @throws IllegalArgumentException when validation reports “Uploaded file cannot be empty” or “Uploaded file is larger than the configured limit”
     */
    private void validateFileSize(long sizeBytes) {
        if (sizeBytes <= 0) {
            throw new IllegalArgumentException("Uploaded file cannot be empty");
        }
        if (sizeBytes > maxFileSizeBytes) {
            throw new IllegalArgumentException("Uploaded file is larger than the configured limit");
        }
    }

    /**
     * Requires enabled.
     *
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (not implemented)
     */
    private void requireEnabled() {
        if (!enabled) {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "S3 presigned uploads are not configured");
        }
    }

    /**
     * Normalizes folder. Uses locale-independent case normalization.
     *
     * @param rawFolder raw folder
     * @return the resulting text
     * @throws IllegalArgumentException when validation reports “folder is required”
     */
    private String normalizeFolder(String rawFolder) {
        String normalized = trimToNull(rawFolder);
        if (normalized == null) {
            throw new IllegalArgumentException("folder is required");
        }

        normalized = normalized.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]+", "-");
        if (!ALLOWED_FOLDERS.contains(normalized)) {
            throw new IllegalArgumentException("folder must be one of: branding, legal, multimedia");
        }
        return normalized;
    }

    /**
     * Selects the configured object-storage bucket for the requested folder.
     *
     * @param folder folder
     * @return the resulting text
     */
    private String bucketForFolder(String folder) {
        if (("branding".equals(folder) || "multimedia".equals(folder)) && publicBucket != null) {
            return publicBucket;
        }
        return privateBucket;
    }

    /**
     * Constructs a public asset link only for branding or multimedia files and only when a public base URL is configured.
     *
     * @param folder folder
     * @param objectKey storage key identifying the private file
     * @return the resulting text
     */
    private String publicUrlFor(String folder, String objectKey) {
        if ((!"branding".equals(folder) && !"multimedia".equals(folder)) || publicBaseUrl == null) {
            return null;
        }
        return publicBaseUrl.replaceAll("/+$", "") + "/" + objectKey;
    }

    /**
     * Builds object key.
     *
     * @param folder folder
     * @param clientId identifier of the client whose workspace is in scope
     * @param serviceKey stable key for the service
     * @param contextType context type
     * @param contextId identifier of the context
     * @param filename filename
     * @return the resulting text
     */
    private String buildObjectKey(
            String folder,
            UUID clientId,
            String serviceKey,
            String contextType,
            UUID contextId,
            String filename
    ) {
        return folder + "/" + clientId + "/" + serviceKey + "/" + contextType + "/" + contextId + "/" +
                UUID.randomUUID() + "-" + filename;
    }

    /**
     * Parses storage key.
     *
     * @param storageKey key identifying the storage
     * @return the stored object location
     * @throws IllegalArgumentException when validation reports “Invalid S3 storage key”
     */
    private StoredObjectLocation parseStorageKey(String storageKey) {
        if (!isS3StorageKey(storageKey)) {
            throw new IllegalArgumentException("Invalid S3 storage key");
        }

        String location = storageKey.substring(S3_STORAGE_PREFIX.length());
        int separator = location.indexOf('/');
        if (separator <= 0 || separator == location.length() - 1) {
            throw new IllegalArgumentException("Invalid S3 storage key");
        }
        return new StoredObjectLocation(location.substring(0, separator), location.substring(separator + 1));
    }

    /**
     * Builds the filename used to store the uploaded content.
     *
     * @param objectKey storage key identifying the private file
     * @return the resulting text
     */
    private String storedFilename(String objectKey) {
        int separator = objectKey.lastIndexOf('/');
        return separator < 0 ? objectKey : objectKey.substring(separator + 1);
    }

    /**
     * Sanitizes path segment. Uses locale-independent case normalization.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String sanitizePathSegment(String value) {
        String source = trimToNull(value);
        if (source == null) {
            return "general";
        }
        String sanitized = source.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]+", "-");
        sanitized = sanitized.replaceAll("^-+|-+$", "");
        return sanitized.isBlank() ? "general" : sanitized;
    }

    /**
     * Sanitizes filename.
     *
     * @param filename filename
     * @return the resulting text
     * @throws IllegalArgumentException when validation reports “filename is required”
     */
    private String sanitizeFilename(String filename) {
        String sanitized = trimToNull(filename);
        if (sanitized == null) {
            throw new IllegalArgumentException("filename is required");
        }

        sanitized = sanitized.replace("\\", "/");
        int lastSlash = sanitized.lastIndexOf('/');
        if (lastSlash >= 0) {
            sanitized = sanitized.substring(lastSlash + 1);
        }

        sanitized = sanitized.replaceAll("[^A-Za-z0-9._-]+", "-").replaceAll("^-+|-+$", "");
        return sanitized.isBlank() ? "upload.bin" : sanitized;
    }

    /**
     * Normalizes content type.
     *
     * @param rawContentType raw content type
     * @return the resulting text
     */
    private String normalizeContentType(String rawContentType) {
        String normalized = trimToNull(rawContentType);
        return normalized == null ? DEFAULT_CONTENT_TYPE : normalized;
    }

    /**
     * Trims surrounding whitespace. Absent values return null.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    /**
     * Groups the verified S3 object data used by media upload URL service.
     *
     * @param storageKey key identifying the storage
     * @param bucket bucket
     * @param objectKey storage key identifying the private file
     * @param originalFilename filename supplied by the uploader
     * @param storedFilename stored filename
     * @param contentType media type declared for the file
     * @param sizeBytes content length in bytes
     * @param checksumSha256 SHA-256 digest used to identify file contents
     */
    public record VerifiedS3Object(
            String storageKey,
            String bucket,
            String objectKey,
            String originalFilename,
            String storedFilename,
            String contentType,
            long sizeBytes,
            String checksumSha256
    ) {}

    /**
     * Groups the stored object location data used by media upload URL service.
     *
     * @param bucket bucket
     * @param objectKey storage key identifying the private file
     */
    private record StoredObjectLocation(String bucket, String objectKey) {}
}
