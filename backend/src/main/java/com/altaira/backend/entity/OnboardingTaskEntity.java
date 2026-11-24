package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps onboarding task records to the {@code onboarding_tasks} table. */
@Entity
@Table(name = "onboarding_tasks")
public class OnboardingTaskEntity {

    /** Primary identifier for this onboarding task. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Client workspace in scope. Joined through {@code workspace_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private ClientWorkspaceEntity workspace;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    /** Client operations used by this component. Joined through {@code client_service_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_service_id")
    private ClientServiceEntity clientService;

    /** Stable key for the service. Stored in {@code service_key}. */
    @Column(name = "service_key", nullable = false, length = 80)
    private String serviceKey = "general";

    /** Sector used to select the intake rules. Stored in {@code sector_type}. */
    @Column(name = "sector_type", nullable = false, length = 80)
    private String sectorType = "custom";

    /** Key identifying the task. Stored in {@code task_key}. */
    @Column(name = "task_key", nullable = false, length = 120)
    private String taskKey;

    /** Short heading shown to a reader. */
    @Column(nullable = false)
    private String title;

    /** Human-readable description. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Task type. Stored in {@code task_type}. */
    @Column(name = "task_type", nullable = false, length = 40)
    private String taskType;

    /** Current state in the workflow. */
    @Column(nullable = false, length = 40)
    private String status = "pending";

    /** Whether the item is required. */
    @Column(nullable = false)
    private boolean required = true;

    /** Critical. */
    @Column(nullable = false)
    private boolean critical = false;

    /** Position used when displaying the items. Stored in {@code sort_order}. */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 100;

    /** JSON representation of data. Stored in {@code data_json}. */
    @Column(name = "data_json", nullable = false, columnDefinition = "TEXT")
    private String dataJson = "{}";

    /** JSON representation of file metadata. Stored in {@code file_metadata_json}. */
    @Column(name = "file_metadata_json", nullable = false, columnDefinition = "TEXT")
    private String fileMetadataJson = "[]";

    /** Signature full name. Stored in {@code signature_full_name}. */
    @Column(name = "signature_full_name")
    private String signatureFullName;

    /** Identifier of the signature document. Stored in {@code signature_document_id}. */
    @Column(name = "signature_document_id")
    private String signatureDocumentId;

    /** Signature consent. Stored in {@code signature_consent}. */
    @Column(name = "signature_consent", nullable = false)
    private boolean signatureConsent = false;

    /** Signed ip. Stored in {@code signed_ip}. */
    @Column(name = "signed_ip", length = 80)
    private String signedIp;

    /** Signed user agent. Stored in {@code signed_user_agent}. */
    @Column(name = "signed_user_agent", length = 500)
    private String signedUserAgent;

    /** Time associated with signed. Stored in {@code signed_at}. */
    @Column(name = "signed_at")
    private Instant signedAt;

    /** Key identifying the signed PDF storage. Stored in {@code signed_pdf_storage_key}. */
    @Column(name = "signed_pdf_storage_key")
    private String signedPdfStorageKey;

    /** Admin feedback. Stored in {@code admin_feedback}. */
    @Column(name = "admin_feedback", columnDefinition = "TEXT")
    private String adminFeedback;

    /** Time associated with submitted. Stored in {@code submitted_at}. */
    @Column(name = "submitted_at")
    private Instant submittedAt;

    /** Time associated with approved. Stored in {@code approved_at}. */
    @Column(name = "approved_at")
    private Instant approvedAt;

    /** Time associated with rejected. Stored in {@code rejected_at}. */
    @Column(name = "rejected_at")
    private Instant rejectedAt;

    /** Time the work was completed. Stored in {@code completed_at}. */
    @Column(name = "completed_at")
    private Instant completedAt;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Time of the most recent update. Stored in {@code updated_at}. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Initializes now, created at, updated at before the record is inserted. */
    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    /** Refreshes the update timestamp before the persisted record changes. */
    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this onboarding task.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return client workspace in scope} */
    public ClientWorkspaceEntity getWorkspace() { return workspace; }
    /**
     * Sets the client workspace in scope on this onboarding task.
     *
     * @param workspace client workspace in scope
     */
    public void setWorkspace(ClientWorkspaceEntity workspace) { this.workspace = workspace; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this onboarding task.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

    /** {@return client operations used by this component} */
    public ClientServiceEntity getClientService() { return clientService; }
    /**
     * Sets the client operations used by this component on this onboarding task.
     *
     * @param clientService client operations used by this component
     */
    public void setClientService(ClientServiceEntity clientService) { this.clientService = clientService; }

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

    /** {@return signed ip} */
    public String getSignedIp() { return signedIp; }
    /**
     * Sets the signed ip on this onboarding task.
     *
     * @param signedIp signed ip
     */
    public void setSignedIp(String signedIp) { this.signedIp = signedIp; }

    /** {@return signed user agent} */
    public String getSignedUserAgent() { return signedUserAgent; }
    /**
     * Sets the signed user agent on this onboarding task.
     *
     * @param signedUserAgent signed user agent
     */
    public void setSignedUserAgent(String signedUserAgent) { this.signedUserAgent = signedUserAgent; }

    /** {@return time associated with signed} */
    public Instant getSignedAt() { return signedAt; }
    /**
     * Sets the time associated with signed on this onboarding task.
     *
     * @param signedAt time associated with signed
     */
    public void setSignedAt(Instant signedAt) { this.signedAt = signedAt; }

    /** {@return key identifying the signed PDF storage} */
    public String getSignedPdfStorageKey() { return signedPdfStorageKey; }
    /**
     * Sets the key identifying the signed PDF storage on this onboarding task.
     *
     * @param signedPdfStorageKey key identifying the signed PDF storage
     */
    public void setSignedPdfStorageKey(String signedPdfStorageKey) { this.signedPdfStorageKey = signedPdfStorageKey; }

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
