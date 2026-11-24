package com.altaira.backend.entity;

import com.altaira.backend.model.*;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Maps commercial flow records to the {@code commercial_flows} table. */
@Entity
@Table(name = "commercial_flows")
public class CommercialFlowEntity {

    /** Primary identifier for this commercial flow. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Proposed or saved delivery plan. Joined through {@code plan_id}. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false, unique = true)
    private ProvisioningPlanEntity plan;

    /** Sales enquiry in scope. Joined through {@code lead_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id", nullable = false)
    private LeadEntity lead;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    /** Client workspace in scope. Joined through {@code workspace_id}. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private ClientWorkspaceEntity workspace;

    /** Current payment state. Stored in {@code payment_status}. */
    @Column(name = "payment_status", nullable = false, length = 40)
    private String paymentStatus = CommercialPaymentStatus.PAYMENT_NOT_STARTED.name();

    /** Client status. Stored in {@code client_status}. */
    @Column(name = "client_status", nullable = false, length = 40)
    private String clientStatus = CommercialClientStatus.CLIENT_DRAFT.name();

    /** Current workspace activation state. Stored in {@code workspace_status}. */
    @Column(name = "workspace_status", nullable = false, length = 40)
    private String workspaceStatus = CommercialWorkspaceStatus.WORKSPACE_PENDING.name();

    /** Current invitation state. Stored in {@code invitation_status}. */
    @Column(name = "invitation_status", nullable = false, length = 40)
    private String invitationStatus = CommercialInvitationStatus.INVITATION_PENDING.name();

    /** Current delivery preparation state. Stored in {@code provisioning_status}. */
    @Column(name = "provisioning_status", nullable = false, length = 40)
    private String provisioningStatus = CommercialProvisioningStatus.PROVISIONING_PENDING.name();

    /** Activation error. Stored in {@code activation_error}. */
    @Column(name = "activation_error", columnDefinition = "TEXT")
    private String activationError;

    /** Time associated with payment confirmed. Stored in {@code payment_confirmed_at}. */
    @Column(name = "payment_confirmed_at")
    private Instant paymentConfirmedAt;

    /** Time the workspace became active. Stored in {@code activated_at}. */
    @Column(name = "activated_at")
    private Instant activatedAt;

    /** Time associated with invitation sent. Stored in {@code invitation_sent_at}. */
    @Column(name = "invitation_sent_at")
    private Instant invitationSentAt;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Time of the most recent update. Stored in {@code updated_at}. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Initializes now, created at, updated at before the record is inserted. */
    @PrePersist
    void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }

    /** Refreshes the update timestamp before the persisted record changes. */
    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /** {@return proposed or saved delivery plan} */
    public ProvisioningPlanEntity getPlan() { return plan; }
    /**
     * Sets the proposed or saved delivery plan on this commercial flow.
     *
     * @param plan proposed or saved delivery plan
     */
    public void setPlan(ProvisioningPlanEntity plan) { this.plan = plan; }
    /** {@return sales enquiry in scope} */
    public LeadEntity getLead() { return lead; }
    /**
     * Sets the sales enquiry in scope on this commercial flow.
     *
     * @param lead sales enquiry in scope
     */
    public void setLead(LeadEntity lead) { this.lead = lead; }
    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this commercial flow.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }
    /** {@return client workspace in scope} */
    public ClientWorkspaceEntity getWorkspace() { return workspace; }
    /**
     * Sets the client workspace in scope on this commercial flow.
     *
     * @param workspace client workspace in scope
     */
    public void setWorkspace(ClientWorkspaceEntity workspace) { this.workspace = workspace; }
    /** {@return current payment state} */
    public String getPaymentStatus() { return paymentStatus; }
    /**
     * Sets the current payment state on this commercial flow.
     *
     * @param paymentStatus current payment state
     */
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    /** {@return client status} */
    public String getClientStatus() { return clientStatus; }
    /**
     * Sets the client status on this commercial flow.
     *
     * @param clientStatus client status
     */
    public void setClientStatus(String clientStatus) { this.clientStatus = clientStatus; }
    /** {@return current workspace activation state} */
    public String getWorkspaceStatus() { return workspaceStatus; }
    /**
     * Sets the current workspace activation state on this commercial flow.
     *
     * @param workspaceStatus current workspace activation state
     */
    public void setWorkspaceStatus(String workspaceStatus) { this.workspaceStatus = workspaceStatus; }
    /** {@return current invitation state} */
    public String getInvitationStatus() { return invitationStatus; }
    /**
     * Sets the current invitation state on this commercial flow.
     *
     * @param invitationStatus current invitation state
     */
    public void setInvitationStatus(String invitationStatus) { this.invitationStatus = invitationStatus; }
    /** {@return current delivery preparation state} */
    public String getProvisioningStatus() { return provisioningStatus; }
    /**
     * Sets the current delivery preparation state on this commercial flow.
     *
     * @param provisioningStatus current delivery preparation state
     */
    public void setProvisioningStatus(String provisioningStatus) { this.provisioningStatus = provisioningStatus; }
    /** {@return activation error} */
    public String getActivationError() { return activationError; }
    /**
     * Sets the activation error on this commercial flow.
     *
     * @param activationError activation error
     */
    public void setActivationError(String activationError) { this.activationError = activationError; }
    /** {@return time associated with payment confirmed} */
    public Instant getPaymentConfirmedAt() { return paymentConfirmedAt; }
    /**
     * Sets the time associated with payment confirmed on this commercial flow.
     *
     * @param paymentConfirmedAt time associated with payment confirmed
     */
    public void setPaymentConfirmedAt(Instant paymentConfirmedAt) { this.paymentConfirmedAt = paymentConfirmedAt; }
    /** {@return time the workspace became active} */
    public Instant getActivatedAt() { return activatedAt; }
    /**
     * Sets the time the workspace became active on this commercial flow.
     *
     * @param activatedAt time the workspace became active
     */
    public void setActivatedAt(Instant activatedAt) { this.activatedAt = activatedAt; }
    /** {@return time associated with invitation sent} */
    public Instant getInvitationSentAt() { return invitationSentAt; }
    /**
     * Sets the time associated with invitation sent on this commercial flow.
     *
     * @param invitationSentAt time associated with invitation sent
     */
    public void setInvitationSentAt(Instant invitationSentAt) { this.invitationSentAt = invitationSentAt; }
    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /** {@return time of the most recent update} */
    public Instant getUpdatedAt() { return updatedAt; }
}
