package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.util.UUID;

/** Maps provisioning manual step records to the {@code provisioning_manual_steps} table. */
@Entity
@Table(name = "provisioning_manual_steps")
public class ProvisioningManualStepEntity {

    /** Primary identifier for this provisioning manual step. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Proposed or saved delivery plan. Joined through {@code plan_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private ProvisioningPlanEntity plan;

    /** Key identifying the integration provider. Stored in {@code provider_key}. */
    @Column(name = "provider_key", nullable = false, length = 40)
    private String providerKey;

    /** Short heading shown to a reader. */
    @Column(nullable = false, length = 220)
    private String title;

    /** Explanation of the decision. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    /** Whether the item is required. */
    @Column(nullable = false)
    private boolean required;

    /** Current state in the workflow. */
    @Column(nullable = false, length = 30)
    private String status;

    /** Position used when displaying the items. Stored in {@code sort_order}. */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this provisioning manual step.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }
    /** {@return proposed or saved delivery plan} */
    public ProvisioningPlanEntity getPlan() { return plan; }
    /**
     * Sets the proposed or saved delivery plan on this provisioning manual step.
     *
     * @param plan proposed or saved delivery plan
     */
    public void setPlan(ProvisioningPlanEntity plan) { this.plan = plan; }
    /** {@return key identifying the integration provider} */
    public String getProviderKey() { return providerKey; }
    /**
     * Sets the key identifying the integration provider on this provisioning manual step.
     *
     * @param providerKey key identifying the integration provider
     */
    public void setProviderKey(String providerKey) { this.providerKey = providerKey; }
    /** {@return short heading shown to a reader} */
    public String getTitle() { return title; }
    /**
     * Sets the short heading shown to a reader on this provisioning manual step.
     *
     * @param title short heading shown to a reader
     */
    public void setTitle(String title) { this.title = title; }
    /** {@return explanation of the decision} */
    public String getReason() { return reason; }
    /**
     * Sets the explanation of the decision on this provisioning manual step.
     *
     * @param reason explanation of the decision
     */
    public void setReason(String reason) { this.reason = reason; }
    /** {@return whether the item is required} */
    public boolean isRequired() { return required; }
    /**
     * Sets the whether the item is required on this provisioning manual step.
     *
     * @param required whether the item is required
     */
    public void setRequired(boolean required) { this.required = required; }
    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this provisioning manual step.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
    /** {@return position used when displaying the items} */
    public int getSortOrder() { return sortOrder; }
    /**
     * Sets the position used when displaying the items on this provisioning manual step.
     *
     * @param sortOrder position used when displaying the items
     */
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
