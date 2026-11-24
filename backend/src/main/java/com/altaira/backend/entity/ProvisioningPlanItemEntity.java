package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.util.UUID;

/** Maps provisioning plan item records to the {@code provisioning_plan_items} table. */
@Entity
@Table(name = "provisioning_plan_items")
public class ProvisioningPlanItemEntity {

    /** Primary identifier for this provisioning plan item. */
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

    /** Kind of external or internal resource. Stored in {@code resource_type}. */
    @Column(name = "resource_type", nullable = false, length = 60)
    private String resourceType;

    /** Name assigned to the resource. Stored in {@code resource_name}. */
    @Column(name = "resource_name", nullable = false, length = 240)
    private String resourceName;

    /** Action being recorded. */
    @Column(nullable = false, length = 40)
    private String action;

    /** Current state in the workflow. */
    @Column(nullable = false, length = 30)
    private String status;

    /** Whether the item is required. */
    @Column(nullable = false)
    private boolean required;

    /** Explanation of the decision. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    /** Position used when displaying the items. Stored in {@code sort_order}. */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this provisioning plan item.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }
    /** {@return proposed or saved delivery plan} */
    public ProvisioningPlanEntity getPlan() { return plan; }
    /**
     * Sets the proposed or saved delivery plan on this provisioning plan item.
     *
     * @param plan proposed or saved delivery plan
     */
    public void setPlan(ProvisioningPlanEntity plan) { this.plan = plan; }
    /** {@return key identifying the integration provider} */
    public String getProviderKey() { return providerKey; }
    /**
     * Sets the key identifying the integration provider on this provisioning plan item.
     *
     * @param providerKey key identifying the integration provider
     */
    public void setProviderKey(String providerKey) { this.providerKey = providerKey; }
    /** {@return kind of external or internal resource} */
    public String getResourceType() { return resourceType; }
    /**
     * Sets the kind of external or internal resource on this provisioning plan item.
     *
     * @param resourceType kind of external or internal resource
     */
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    /** {@return name assigned to the resource} */
    public String getResourceName() { return resourceName; }
    /**
     * Sets the name assigned to the resource on this provisioning plan item.
     *
     * @param resourceName name assigned to the resource
     */
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    /** {@return action being recorded} */
    public String getAction() { return action; }
    /**
     * Sets the action being recorded on this provisioning plan item.
     *
     * @param action action being recorded
     */
    public void setAction(String action) { this.action = action; }
    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this provisioning plan item.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
    /** {@return whether the item is required} */
    public boolean isRequired() { return required; }
    /**
     * Sets the whether the item is required on this provisioning plan item.
     *
     * @param required whether the item is required
     */
    public void setRequired(boolean required) { this.required = required; }
    /** {@return explanation of the decision} */
    public String getReason() { return reason; }
    /**
     * Sets the explanation of the decision on this provisioning plan item.
     *
     * @param reason explanation of the decision
     */
    public void setReason(String reason) { this.reason = reason; }
    /** {@return position used when displaying the items} */
    public int getSortOrder() { return sortOrder; }
    /**
     * Sets the position used when displaying the items on this provisioning plan item.
     *
     * @param sortOrder position used when displaying the items
     */
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
