package com.altaira.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Stores onboarding files locally for development and transparently reads S3-backed
 * objects after a direct upload has been verified by {@link MediaUploadUrlService}.
 */
@Service
public class OnboardingFileStorageService {

    /** Storage root. */
    private final Path storageRoot;
    /** Max file size bytes. */
    private final long maxFileSizeBytes;
    /** Media upload URL operations used by this component. */
    private final MediaUploadUrlService mediaUploadUrlService;

    /**
     * Creates the onboarding file storage service with its injected collaborators.
     *
     * @param storageDir storage dir
     * @param maxFileSizeBytes max file size bytes
     * @param mediaUploadUrlService media upload URL operations used by this component
     */
    public OnboardingFileStorageService(
            @Value("${altaira.onboarding.storage-dir}") String storageDir,
            @Value("${altaira.onboarding.max-file-size-bytes}") long maxFileSizeBytes,
            MediaUploadUrlService mediaUploadUrlService
    ) {
        this.storageRoot = Path.of(storageDir).toAbsolutePath().normalize();
        this.maxFileSizeBytes = maxFileSizeBytes;
        this.mediaUploadUrlService = mediaUploadUrlService;
    }

    /**
     * Stores the file under the customer and task identifiers so it can be retrieved from that onboarding task.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param taskId identifier of the task being acted on
     * @param file file
     * @return the stored onboarding file
     */
    public StoredOnboardingFile store(UUID clientId, UUID taskId, MultipartFile file) {
        return storeFile(clientId + "/" + taskId, file);
    }

    /**
     * Stores project asset.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param projectId identifier of the client project
     * @param file file
     * @return the stored onboarding file
     */
    public StoredOnboardingFile storeProjectAsset(UUID clientId, UUID projectId, MultipartFile file) {
        return storeFile(clientId + "/projects/" + projectId, file);
    }

    /**
     * Stores file.
     *
     * @param storagePrefix storage prefix
     * @param file file
     * @return the stored onboarding file
     * @throws IllegalArgumentException when validation reports “Uploaded file cannot be empty” or “Uploaded file is larger than the configured limit”
     * @throws IllegalStateException when validation reports “Could not store onboarding file”
     */
    private StoredOnboardingFile storeFile(String storagePrefix, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file cannot be empty");
        }

        try {
            byte[] bytes = file.getBytes();

            if (bytes.length > maxFileSizeBytes) {
                throw new IllegalArgumentException("Uploaded file is larger than the configured limit");
            }

            String originalFilename = safeFilename(file.getOriginalFilename());
            String storedFilename = UUID.randomUUID() + "-" + originalFilename;
            String storageKey = storagePrefix + "/" + storedFilename;
            Path target = resolveStorageKey(storageKey);

            Files.createDirectories(target.getParent());
            Files.write(target, bytes, StandardOpenOption.CREATE_NEW);

            return new StoredOnboardingFile(
                    originalFilename,
                    storedFilename,
                    storageKey,
                    file.getContentType() == null || file.getContentType().isBlank() ? "application/octet-stream" : file.getContentType(),
                    bytes.length,
                    sha256(bytes)
            );
        } catch (IOException ex) {
            throw new IllegalStateException("Could not store onboarding file", ex);
        }
    }

    /**
     * Resolves a storage key to its local path using the storage-root check.
     *
     * @param storageKey key identifying the storage
     * @return the resolved file location
     */
    public Path resolve(String storageKey) {
        return resolveStorageKey(storageKey);
    }

    /**
     * Opens a stream from S3 for an S3 key and from the validated storage path for a local key.
     *
     * @param storageKey key identifying the storage
     * @return the input stream
     * @throws IOException if the underlying input or output cannot be completed
     */
    public InputStream open(String storageKey) throws IOException {
        if (mediaUploadUrlService.isS3StorageKey(storageKey)) {
            return mediaUploadUrlService.openStoredObject(storageKey);
        }

        return Files.newInputStream(resolveStorageKey(storageKey));
    }

    /**
     * Resolves storage key.
     *
     * @param storageKey key identifying the storage
     * @return the resolved file location
     * @throws IllegalArgumentException when validation reports “Invalid onboarding storage key”
     */
    private Path resolveStorageKey(String storageKey) {
        Path target = storageRoot.resolve(storageKey).normalize();

        if (!target.startsWith(storageRoot)) {
            throw new IllegalArgumentException("Invalid onboarding storage key");
        }

        return target;
    }

    /**
     * Converts the supplied filename into a name suitable for the local storage path.
     *
     * @param filename filename
     * @return the resulting text
     */
    private String safeFilename(String filename) {
        String candidate = filename == null || filename.isBlank() ? "upload.bin" : Path.of(filename).getFileName().toString();
        String sanitized = candidate.replaceAll("[^A-Za-z0-9._-]", "_");
        return sanitized.isBlank() ? "upload.bin" : sanitized;
    }

    /**
     * Computes the SHA-256 digest of the supplied content.
     *
     * @param bytes binary content associated with the item
     * @return the resulting text
     * @throws IllegalStateException when validation reports “SHA-256 digest is not available”
     */
    private String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(bytes));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 digest is not available", ex);
        }
    }

    /**
     * Groups the stored onboarding file data used by onboarding file storage service.
     *
     * @param originalFilename filename supplied by the uploader
     * @param storedFilename stored filename
     * @param storageKey key identifying the storage
     * @param contentType media type declared for the file
     * @param sizeBytes content length in bytes
     * @param checksumSha256 SHA-256 digest used to identify file contents
     */
    public record StoredOnboardingFile(
            String originalFilename,
            String storedFilename,
            String storageKey,
            String contentType,
            long sizeBytes,
            String checksumSha256
    ) {}
}
