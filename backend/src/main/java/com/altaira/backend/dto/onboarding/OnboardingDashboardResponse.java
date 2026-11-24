package com.altaira.backend.dto.onboarding;

import com.altaira.backend.dto.client.ClientResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Fields the API returns for onboarding dashboard. */
public class OnboardingDashboardResponse {
    /** Identifier of the workspace. */
    private UUID workspaceId;
    /** Customer account in scope. */
    private ClientResponse client;
    /** Workspace name. */
    private String workspaceName;
    /** Current state in the workflow. */
    private String status;
    /** Onboarding completed. */
    private boolean onboardingCompleted;
    /** Contract submitted. */
    private boolean contractSubmitted;
    /** Contract approved. */
    private boolean contractApproved;
    /** Total required tasks. */
    private int totalRequiredTasks;
    /** Completed required tasks. */
    private int completedRequiredTasks;
    /** Submitted tasks. */
    private int submittedTasks;
    /** Rejected tasks. */
    private int rejectedTasks;
    /** Time associated with contract submitted. */
    private Instant contractSubmittedAt;
    /** Time associated with contract approved. */
    private Instant contractApprovedAt;
    /** Tasks. */
    private List<OnboardingTaskResponse> tasks;
    /** Membership role in this customer’s workspace. */
    private String accessRole;
    /** Can edit. */
    private boolean canEdit;

    /** Constructs {@code OnboardingDashboardResponse} with its default state. */
    public OnboardingDashboardResponse() {}

    /**
     * Constructs {@code OnboardingDashboardResponse} using the supplied workspace id, client, workspace name and remaining fields.
     *
     * @param workspaceId identifier of the workspace
     * @param client customer account in scope
     * @param workspaceName workspace name
     * @param status current state in the workflow
     * @param onboardingCompleted onboarding completed
     * @param contractSubmitted contract submitted
     * @param contractApproved contract approved
     * @param totalRequiredTasks total required tasks
     * @param completedRequiredTasks completed required tasks
     * @param submittedTasks submitted tasks
     * @param rejectedTasks rejected tasks
     * @param contractSubmittedAt time associated with contract submitted
     * @param contractApprovedAt time associated with contract approved
     * @param tasks tasks
     */
    public OnboardingDashboardResponse(
            UUID workspaceId,
            ClientResponse client,
            String workspaceName,
            String status,
            boolean onboardingCompleted,
            boolean contractSubmitted,
            boolean contractApproved,
            int totalRequiredTasks,
            int completedRequiredTasks,
            int submittedTasks,
            int rejectedTasks,
            Instant contractSubmittedAt,
            Instant contractApprovedAt,
            List<OnboardingTaskResponse> tasks
    ) {
        this.workspaceId = workspaceId;
        this.client = client;
        this.workspaceName = workspaceName;
        this.status = status;
        this.onboardingCompleted = onboardingCompleted;
        this.contractSubmitted = contractSubmitted;
        this.contractApproved = contractApproved;
        this.totalRequiredTasks = totalRequiredTasks;
        this.completedRequiredTasks = completedRequiredTasks;
        this.submittedTasks = submittedTasks;
        this.rejectedTasks = rejectedTasks;
        this.contractSubmittedAt = contractSubmittedAt;
        this.contractApprovedAt = contractApprovedAt;
        this.tasks = tasks;
    }

    /** {@return identifier of the workspace} */
    public UUID getWorkspaceId() { return workspaceId; }
    /**
     * Sets the identifier of the workspace on this onboarding dashboard.
     *
     * @param workspaceId identifier of the workspace
     */
    public void setWorkspaceId(UUID workspaceId) { this.workspaceId = workspaceId; }

    /** {@return customer account in scope} */
    public ClientResponse getClient() { return client; }
    /**
     * Sets the customer account in scope on this onboarding dashboard.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientResponse client) { this.client = client; }

    /** {@return workspace name} */
    public String getWorkspaceName() { return workspaceName; }
    /**
     * Sets the workspace name on this onboarding dashboard.
     *
     * @param workspaceName workspace name
     */
    public void setWorkspaceName(String workspaceName) { this.workspaceName = workspaceName; }

    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this onboarding dashboard.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }

    /** {@return onboarding completed} */
    public boolean isOnboardingCompleted() { return onboardingCompleted; }
    /**
     * Sets the onboarding completed on this onboarding dashboard.
     *
     * @param onboardingCompleted onboarding completed
     */
    public void setOnboardingCompleted(boolean onboardingCompleted) { this.onboardingCompleted = onboardingCompleted; }

    /** {@return contract submitted} */
    public boolean isContractSubmitted() { return contractSubmitted; }
    /**
     * Sets the contract submitted on this onboarding dashboard.
     *
     * @param contractSubmitted contract submitted
     */
    public void setContractSubmitted(boolean contractSubmitted) { this.contractSubmitted = contractSubmitted; }

    /** {@return contract approved} */
    public boolean isContractApproved() { return contractApproved; }
    /**
     * Sets the contract approved on this onboarding dashboard.
     *
     * @param contractApproved contract approved
     */
    public void setContractApproved(boolean contractApproved) { this.contractApproved = contractApproved; }

    /** {@return total required tasks} */
    public int getTotalRequiredTasks() { return totalRequiredTasks; }
    /**
     * Sets the total required tasks on this onboarding dashboard.
     *
     * @param totalRequiredTasks total required tasks
     */
    public void setTotalRequiredTasks(int totalRequiredTasks) { this.totalRequiredTasks = totalRequiredTasks; }

    /** {@return completed required tasks} */
    public int getCompletedRequiredTasks() { return completedRequiredTasks; }
    /**
     * Sets the completed required tasks on this onboarding dashboard.
     *
     * @param completedRequiredTasks completed required tasks
     */
    public void setCompletedRequiredTasks(int completedRequiredTasks) { this.completedRequiredTasks = completedRequiredTasks; }

    /** {@return submitted tasks} */
    public int getSubmittedTasks() { return submittedTasks; }
    /**
     * Sets the submitted tasks on this onboarding dashboard.
     *
     * @param submittedTasks submitted tasks
     */
    public void setSubmittedTasks(int submittedTasks) { this.submittedTasks = submittedTasks; }

    /** {@return rejected tasks} */
    public int getRejectedTasks() { return rejectedTasks; }
    /**
     * Sets the rejected tasks on this onboarding dashboard.
     *
     * @param rejectedTasks rejected tasks
     */
    public void setRejectedTasks(int rejectedTasks) { this.rejectedTasks = rejectedTasks; }

    /** {@return time associated with contract submitted} */
    public Instant getContractSubmittedAt() { return contractSubmittedAt; }
    /**
     * Sets the time associated with contract submitted on this onboarding dashboard.
     *
     * @param contractSubmittedAt time associated with contract submitted
     */
    public void setContractSubmittedAt(Instant contractSubmittedAt) { this.contractSubmittedAt = contractSubmittedAt; }

    /** {@return time associated with contract approved} */
    public Instant getContractApprovedAt() { return contractApprovedAt; }
    /**
     * Sets the time associated with contract approved on this onboarding dashboard.
     *
     * @param contractApprovedAt time associated with contract approved
     */
    public void setContractApprovedAt(Instant contractApprovedAt) { this.contractApprovedAt = contractApprovedAt; }

    /** {@return tasks} */
    public List<OnboardingTaskResponse> getTasks() { return tasks; }
    /**
     * Sets the tasks on this onboarding dashboard.
     *
     * @param tasks tasks
     */
    public void setTasks(List<OnboardingTaskResponse> tasks) { this.tasks = tasks; }

    /** {@return membership role in this customer’s workspace} */
    public String getAccessRole() { return accessRole; }
    /**
     * Sets the membership role in this customer’s workspace on this onboarding dashboard.
     *
     * @param accessRole membership role in this customer’s workspace
     */
    public void setAccessRole(String accessRole) { this.accessRole = accessRole; }

    /** {@return can edit} */
    public boolean isCanEdit() { return canEdit; }
    /**
     * Sets the can edit on this onboarding dashboard.
     *
     * @param canEdit can edit
     */
    public void setCanEdit(boolean canEdit) { this.canEdit = canEdit; }
}
