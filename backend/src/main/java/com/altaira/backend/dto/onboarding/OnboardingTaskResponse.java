package com.altaira.backend.dto.onboarding;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for onboarding task. */
public class OnboardingTaskResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the client whose workspace is in scope. */
    private UUID clientId;
    /** Identifier of the service assignment for this client. */
    private UUID clientServiceId;
    /** Stable key for the service. */
    private String serviceKey;
    /** Sector used to select the intake rules. */
    private String sectorType;
    /** Key identifying the task. */
    private String taskKey;
    /** Short heading shown to a reader. */
    private String title;
    /** Human-readable description. */
    private String description;
    /** Task type. */
    private String taskType;
    /** Current state in the workflow. */
    private String status;
    /** Whether the item is required. */
    private boolean required;
    /** Critical. */
    private boolean critical;
    /** Position used when displaying the items. */
    private int sortOrder;
    /** JSON representation of data. */
    private String dataJson;
    /** JSON representation of file metadata. */
    private String fileMetadataJson;
    /** Signature full name. */
    private String signatureFullName;
    /** Identifier of the signature document. */
    private String signatureDocumentId;
    /** Signature consent. */
    private boolean signatureConsent;
    /** Admin feedback. */
    private String adminFeedback;
    /** Time associated with submitted. */
    private Instant submittedAt;
    /** Time associated with approved. */
    private Instant approvedAt;
    /** Time associated with rejected. */
    private Instant rejectedAt;
    /** Time the work was completed. */
    private Instant completedAt;
    /** Time the record was created. */
    private Instant createdAt;
    /** Time of the most recent update. */
    private Instant updatedAt;

    /** Constructs {@code OnboardingTaskResponse} with its default state. */
    public OnboardingTaskResponse() {}

    /**
     * Constructs {@code OnboardingTaskResponse} using the supplied id, client id, client service id and remaining fields.
     *
     * @param id unique identifier of the record
     * @param clientId identifier of the client whose workspace is in scope
     * @param clientServiceId identifier of the service assignment for this client
     * @param serviceKey stable key for the service
     * @param sectorType sector used to select the intake rules
     * @param taskKey key identifying the task
     * @param title short heading shown to a reader
     * @param description human-readable description
     * @param taskType task type
     * @param status current state in the workflow
     * @param required whether the item is required
     * @param critical critical
     * @param sortOrder position used when displaying the items
     * @param dataJson JSON representation of data
     * @param fileMetadataJson JSON representation of file metadata
     * @param signatureFullName signature full name
     * @param signatureDocumentId identifier of the signature document
     * @param signatureConsent signature consent
     * @param adminFeedback admin feedback
     * @param submittedAt time associated with submitted
     * @param approvedAt time associated with approved
     * @param rejectedAt time associated with rejected
     * @param completedAt time the work was completed
     * @param createdAt time the record was created
     * @param updatedAt time of the most recent update
     */
    public OnboardingTaskResponse(
            UUID id,
            UUID clientId,
            UUID clientServiceId,
            String serviceKey,
            String sectorType,
            String taskKey,
            String title,
            String description,
            String taskType,
            String status,
            boolean required,
            boolean critical,
            int sortOrder,
            String dataJson,
            String fileMetadataJson,
            String signatureFullName,
            String signatureDocumentId,
            boolean signatureConsent,
            String adminFeedback,
            Instant submittedAt,
            Instant approvedAt,
            Instant rejectedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.clientId = clientId;
        this.clientServiceId = clientServiceId;
        this.serviceKey = serviceKey;
        this.sectorType = sectorType;
        this.taskKey = taskKey;
        this.title = title;
        this.description = description;
        this.taskType = taskType;
        this.status = status;
        this.required = required;
        this.critical = critical;
        this.sortOrder = sortOrder;
        this.dataJson = dataJson;
        this.fileMetadataJson = fileMetadataJson;
        this.signatureFullName = signatureFullName;
        this.signatureDocumentId = signatureDocumentId;
        this.signatureConsent = signatureConsent;
        this.adminFeedback = adminFeedback;
        this.submittedAt = submittedAt;
        this.approvedAt = approvedAt;
        this.rejectedAt = rejectedAt;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this onboarding task.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the client whose workspace is in scope} */
    public UUID getClientId() { return clientId; }
    /**
     * Sets the identifier of the client whose workspace is in scope on this onboarding task.
     *
     * @param clientId identifier of the client whose workspace is in scope
     */
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    /** {@return identifier of the service assignment for this client} */
    public UUID getClientServiceId() { return clientServiceId; }
    /**
     * Sets the identifier of the service assignment for this client on this onboarding task.
     *
     * @param clientServiceId identifier of the service assignment for this client
     */
    public void setClientServiceId(UUID clientServiceId) { this.clientServiceId = clientServiceId; }

    /** {@return stable key for the service} */
    public String getServiceKey() { return serviceKey; }
    /**
     * Sets the stable key for the service on this onboarding task.
     *
     * @param serviceKey stable key for the service
     */
    public void setServiceKey(String serviceKey) { this.serviceKey = serviceKey; }

    /** {@return sector used to select the intake rules} */
    public String getSectorType() { return sectorType; }
    /**
     * Sets the sector used to select the intake rules on this onboarding task.
     *
     * @param sectorType sector used to select the intake rules
     */
    public void setSectorType(String sectorType) { this.sectorType = sectorType; }

    /** {@return key identifying the task} */
    public String getTaskKey() { return taskKey; }
    /**
     * Sets the key identifying the task on this onboarding task.
     *
     * @param taskKey key identifying the task
     */
    public void setTaskKey(String taskKey) { this.taskKey = taskKey; }

    /** {@return short heading shown to a reader} */
    public String getTitle() { return title; }
    /**
     * Sets the short heading shown to a reader on this onboarding task.
     *
     * @param title short heading shown to a reader
     */
    public void setTitle(String title) { this.title = title; }

    /** {@return human-readable description} */
    public String getDescription() { return description; }
    /**
     * Sets the human-readable description on this onboarding task.
     *
     * @param description human-readable description
     */
    public void setDescription(String description) { this.description = description; }

    /** {@return task type} */
    public String getTaskType() { return taskType; }
    /**
     * Sets the task type on this onboarding task.
     *
     * @param taskType task type
     */
    public void setTaskType(String taskType) { this.taskType = taskType; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this onboarding task.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return whether the item is required} */
    public boolean isRequired() { return required; }
    /**
     * Sets the whether the item is required on this onboarding task.
     *
     * @param required whether the item is required
     */
    public void setRequired(boolean required) { this.required = required; }

    /** {@return critical} */
    public boolean isCritical() { return critical; }
    /**
     * Sets the critical on this onboarding task.
     *
     * @param critical critical
     */
    public void setCritical(boolean critical) { this.critical = critical; }

    /** {@return position used when displaying the items} */
    public int getSortOrder() { return sortOrder; }
    /**
     * Sets the position used when displaying the items on this onboarding task.
     *
     * @param sortOrder position used when displaying the items
     */
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    /** {@return JSON representation of data} */
    public String getDataJson() { return dataJson; }
    /**
     * Sets the JSON representation of data on this onboarding task.
     *
     * @param dataJson JSON representation of data
     */
    public void setDataJson(String dataJson) { this.dataJson = dataJson; }

    /** {@return JSON representation of file metadata} */
    public String getFileMetadataJson() { return fileMetadataJson; }
    /**
     * Sets the JSON representation of file metadata on this onboarding task.
     *
     * @param fileMetadataJson JSON representation of file metadata
     */
    public void setFileMetadataJson(String fileMetadataJson) { this.fileMetadataJson = fileMetadataJson; }

    /** {@return signature full name} */
    public String getSignatureFullName() { return signatureFullName; }
    /**
     * Sets the signature full name on this onboarding task.
     *
     * @param signatureFullName signature full name
     */
    public void setSignatureFullName(String signatureFullName) { this.signatureFullName = signatureFullName; }

    /** {@return identifier of the signature document} */
    public String getSignatureDocumentId() { return signatureDocumentId; }
    /**
     * Sets the identifier of the signature document on this onboarding task.
     *
     * @param signatureDocumentId identifier of the signature document
     */
    public void setSignatureDocumentId(String signatureDocumentId) { this.signatureDocumentId = signatureDocumentId; }

    /** {@return signature consent} */
    public boolean isSignatureConsent() { return signatureConsent; }
    /**
     * Sets the signature consent on this onboarding task.
     *
     * @param signatureConsent signature consent
     */
    public void setSignatureConsent(boolean signatureConsent) { this.signatureConsent = signatureConsent; }

    /** {@return admin feedback} */
    public String getAdminFeedback() { return adminFeedback; }
    /**
     * Sets the admin feedback on this onboarding task.
     *
     * @param adminFeedback admin feedback
     */
    public void setAdminFeedback(String adminFeedback) { this.adminFeedback = adminFeedback; }

    /** {@return time associated with submitted} */
    public Instant getSubmittedAt() { return submittedAt; }
    /**
     * Sets the time associated with submitted on this onboarding task.
     *
     * @param submittedAt time associated with submitted
     */
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }

    /** {@return time associated with approved} */
    public Instant getApprovedAt() { return approvedAt; }
    /**
     * Sets the time associated with approved on this onboarding task.
     *
     * @param approvedAt time associated with approved
     */
    public void setApprovedAt(Instant approvedAt) { this.approvedAt = approvedAt; }

    /** {@return time associated with rejected} */
    public Instant getRejectedAt() { return rejectedAt; }
    /**
     * Sets the time associated with rejected on this onboarding task.
     *
     * @param rejectedAt time associated with rejected
     */
    public void setRejectedAt(Instant rejectedAt) { this.rejectedAt = rejectedAt; }

    /** {@return time the work was completed} */
    public Instant getCompletedAt() { return completedAt; }
    /**
     * Sets the time the work was completed on this onboarding task.
     *
     * @param completedAt time the work was completed
     */
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this onboarding task.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
    /**
     * Sets the time of the most recent update on this onboarding task.
     *
     * @param updatedAt time of the most recent update
     */
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
