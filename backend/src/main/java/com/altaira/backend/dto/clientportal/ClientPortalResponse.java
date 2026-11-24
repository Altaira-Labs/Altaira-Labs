package com.altaira.backend.dto.clientportal;

import com.altaira.backend.dto.client.ClientResponse;

import java.util.List;
import java.util.UUID;

/** Fields the API returns for client portal. */
public class ClientPortalResponse {
    /** Identifier of the workspace. */
    private UUID workspaceId;
    /** Workspace name. */
    private String workspaceName;
    /** Current workspace activation state. */
    private String workspaceStatus;
    /** Customer account in scope. */
    private ClientResponse client;
    /** Onboarding completed. */
    private boolean onboardingCompleted;
    /** Contract approved. */
    private boolean contractApproved;
    /** Modules. */
    private List<ClientPortalModuleResponse> modules;
    /** Projects. */
    private List<ClientProjectResponse> projects;
    /** Membership role in this customer’s workspace. */
    private String accessRole;
    /** Can edit. */
    private boolean canEdit;

    /** Constructs {@code ClientPortalResponse} with its default state. */
    public ClientPortalResponse() {}

    /**
     * Constructs {@code ClientPortalResponse} using the supplied workspace id, workspace name, workspace status and remaining fields.
     *
     * @param workspaceId identifier of the workspace
     * @param workspaceName workspace name
     * @param workspaceStatus current workspace activation state
     * @param client customer account in scope
     * @param onboardingCompleted onboarding completed
     * @param contractApproved contract approved
     * @param modules modules
     * @param projects projects
     * @param accessRole membership role in this customer’s workspace
     * @param canEdit can edit
     */
    public ClientPortalResponse(
            UUID workspaceId,
            String workspaceName,
            String workspaceStatus,
            ClientResponse client,
            boolean onboardingCompleted,
            boolean contractApproved,
            List<ClientPortalModuleResponse> modules,
            List<ClientProjectResponse> projects,
            String accessRole,
            boolean canEdit
    ) {
        this.workspaceId = workspaceId;
        this.workspaceName = workspaceName;
        this.workspaceStatus = workspaceStatus;
        this.client = client;
        this.onboardingCompleted = onboardingCompleted;
        this.contractApproved = contractApproved;
        this.modules = modules;
        this.projects = projects;
        this.accessRole = accessRole;
        this.canEdit = canEdit;
    }

    /** {@return identifier of the workspace} */
    public UUID getWorkspaceId() { return workspaceId; }
    /**
     * Sets the identifier of the workspace on this client portal.
     *
     * @param workspaceId identifier of the workspace
     */
    public void setWorkspaceId(UUID workspaceId) { this.workspaceId = workspaceId; }

    /** {@return workspace name} */
    public String getWorkspaceName() { return workspaceName; }
    /**
     * Sets the workspace name on this client portal.
     *
     * @param workspaceName workspace name
     */
    public void setWorkspaceName(String workspaceName) { this.workspaceName = workspaceName; }

    /** {@return current workspace activation state} */
    public String getWorkspaceStatus() { return workspaceStatus; }
    /**
     * Sets the current workspace activation state on this client portal.
     *
     * @param workspaceStatus current workspace activation state
     */
    public void setWorkspaceStatus(String workspaceStatus) { this.workspaceStatus = workspaceStatus; }

    /** {@return customer account in scope} */
    public ClientResponse getClient() { return client; }
    /**
     * Sets the customer account in scope on this client portal.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientResponse client) { this.client = client; }

    /** {@return onboarding completed} */
    public boolean isOnboardingCompleted() { return onboardingCompleted; }
    /**
     * Sets the onboarding completed on this client portal.
     *
     * @param onboardingCompleted onboarding completed
     */
    public void setOnboardingCompleted(boolean onboardingCompleted) { this.onboardingCompleted = onboardingCompleted; }

    /** {@return contract approved} */
    public boolean isContractApproved() { return contractApproved; }
    /**
     * Sets the contract approved on this client portal.
     *
     * @param contractApproved contract approved
     */
    public void setContractApproved(boolean contractApproved) { this.contractApproved = contractApproved; }

    /** {@return modules} */
    public List<ClientPortalModuleResponse> getModules() { return modules; }
    /**
     * Sets the modules on this client portal.
     *
     * @param modules modules
     */
    public void setModules(List<ClientPortalModuleResponse> modules) { this.modules = modules; }

    /** {@return projects} */
    public List<ClientProjectResponse> getProjects() { return projects; }
    /**
     * Sets the projects on this client portal.
     *
     * @param projects projects
     */
    public void setProjects(List<ClientProjectResponse> projects) { this.projects = projects; }

    /** {@return membership role in this customer’s workspace} */
    public String getAccessRole() { return accessRole; }
    /**
     * Sets the membership role in this customer’s workspace on this client portal.
     *
     * @param accessRole membership role in this customer’s workspace
     */
    public void setAccessRole(String accessRole) { this.accessRole = accessRole; }

    /** {@return can edit} */
    public boolean isCanEdit() { return canEdit; }
    /**
     * Sets the can edit on this client portal.
     *
     * @param canEdit can edit
     */
    public void setCanEdit(boolean canEdit) { this.canEdit = canEdit; }
}
