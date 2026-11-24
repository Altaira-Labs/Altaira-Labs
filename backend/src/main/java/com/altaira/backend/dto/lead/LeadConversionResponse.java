package com.altaira.backend.dto.lead;

import com.altaira.backend.dto.client.ClientResponse;
import com.altaira.backend.dto.clientportal.ClientProjectResponse;
import com.altaira.backend.dto.clientservice.ClientServiceResponse;

import java.util.List;

/** Fields the API returns for lead conversion. */
public class LeadConversionResponse {
    /** Sales enquiry in scope. */
    private LeadResponse lead;
    /** Customer account in scope. */
    private ClientResponse client;
    /** Whether conversion created a customer account. */
    private boolean clientCreated;
    /** Service assignments. */
    private List<ClientServiceResponse> serviceAssignments;
    /** Workspace ready. */
    private boolean workspaceReady;
    /** Projects. */
    private List<ClientProjectResponse> projects;

    /** Constructs {@code LeadConversionResponse} with its default state. */
    public LeadConversionResponse() {}

    /**
     * Constructs {@code LeadConversionResponse} using the supplied lead, client, client created and remaining fields.
     *
     * @param lead sales enquiry in scope
     * @param client customer account in scope
     * @param clientCreated whether conversion created a customer account
     * @param serviceAssignments service assignments
     * @param workspaceReady workspace ready
     * @param projects projects
     */
    public LeadConversionResponse(
            LeadResponse lead,
            ClientResponse client,
            boolean clientCreated,
            List<ClientServiceResponse> serviceAssignments,
            boolean workspaceReady,
            List<ClientProjectResponse> projects
    ) {
        this.lead = lead;
        this.client = client;
        this.clientCreated = clientCreated;
        this.serviceAssignments = serviceAssignments;
        this.workspaceReady = workspaceReady;
        this.projects = projects;
    }

    /** {@return sales enquiry in scope} */
    public LeadResponse getLead() { return lead; }
    /**
     * Sets the sales enquiry in scope on this lead conversion.
     *
     * @param lead sales enquiry in scope
     */
    public void setLead(LeadResponse lead) { this.lead = lead; }
    /** {@return customer account in scope} */
    public ClientResponse getClient() { return client; }
    /**
     * Sets the customer account in scope on this lead conversion.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientResponse client) { this.client = client; }
    /** {@return whether conversion created a customer account} */
    public boolean isClientCreated() { return clientCreated; }
    /**
     * Sets the whether conversion created a customer account on this lead conversion.
     *
     * @param clientCreated whether conversion created a customer account
     */
    public void setClientCreated(boolean clientCreated) { this.clientCreated = clientCreated; }
    /** {@return service assignments} */
    public List<ClientServiceResponse> getServiceAssignments() { return serviceAssignments; }
    /**
     * Sets the service assignments on this lead conversion.
     *
     * @param serviceAssignments service assignments
     */
    public void setServiceAssignments(List<ClientServiceResponse> serviceAssignments) { this.serviceAssignments = serviceAssignments; }
    /** {@return workspace ready} */
    public boolean isWorkspaceReady() { return workspaceReady; }
    /**
     * Sets the workspace ready on this lead conversion.
     *
     * @param workspaceReady workspace ready
     */
    public void setWorkspaceReady(boolean workspaceReady) { this.workspaceReady = workspaceReady; }
    /** {@return projects} */
    public List<ClientProjectResponse> getProjects() { return projects; }
    /**
     * Sets the projects on this lead conversion.
     *
     * @param projects projects
     */
    public void setProjects(List<ClientProjectResponse> projects) { this.projects = projects; }
}
