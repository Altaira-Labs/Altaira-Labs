package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.util.UUID;

/** Maps provisioning selected tool records to the {@code provisioning_selected_tools} table. */
@Entity
@Table(
        name = "provisioning_selected_tools",
        uniqueConstraints = @UniqueConstraint(
                name = "provisioning_tools_plan_key_unique",
                columnNames = {"plan_id", "tool_key"}
        )
)
public class ProvisioningSelectedToolEntity {

    /** Primary identifier for this provisioning selected tool. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Proposed or saved delivery plan. Joined through {@code plan_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private ProvisioningPlanEntity plan;

    /** Key identifying the recommended tool. Stored in {@code tool_key}. */
    @Column(name = "tool_key", nullable = false, length = 40)
    private String toolKey;

    /** User-facing label. Stored in {@code display_name}. */
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    /** Current selection state of the proposed tool. Stored in {@code selection_state}. */
    @Column(name = "selection_state", nullable = false, length = 20)
    private String selectionState;

    /** Degree of automation associated with the step. Stored in {@code automation_level}. */
    @Column(name = "automation_level", nullable = false, length = 4)
    private String automationLevel;

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
     * Sets the unique identifier of the record on this provisioning selected tool.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }
    /** {@return proposed or saved delivery plan} */
    public ProvisioningPlanEntity getPlan() { return plan; }
    /**
     * Sets the proposed or saved delivery plan on this provisioning selected tool.
     *
     * @param plan proposed or saved delivery plan
     */
    public void setPlan(ProvisioningPlanEntity plan) { this.plan = plan; }
    /** {@return key identifying the recommended tool} */
    public String getToolKey() { return toolKey; }
    /**
     * Sets the key identifying the recommended tool on this provisioning selected tool.
     *
     * @param toolKey key identifying the recommended tool
     */
    public void setToolKey(String toolKey) { this.toolKey = toolKey; }
    /** {@return user-facing label} */
    public String getDisplayName() { return displayName; }
    /**
     * Sets the user-facing label on this provisioning selected tool.
     *
     * @param displayName user-facing label
     */
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    /** {@return current selection state of the proposed tool} */
    public String getSelectionState() { return selectionState; }
    /**
     * Sets the current selection state of the proposed tool on this provisioning selected tool.
     *
     * @param selectionState current selection state of the proposed tool
     */
    public void setSelectionState(String selectionState) { this.selectionState = selectionState; }
    /** {@return degree of automation associated with the step} */
    public String getAutomationLevel() { return automationLevel; }
    /**
     * Sets the degree of automation associated with the step on this provisioning selected tool.
     *
     * @param automationLevel degree of automation associated with the step
     */
    public void setAutomationLevel(String automationLevel) { this.automationLevel = automationLevel; }
    /** {@return whether the item is required} */
    public boolean isRequired() { return required; }
    /**
     * Sets the whether the item is required on this provisioning selected tool.
     *
     * @param required whether the item is required
     */
    public void setRequired(boolean required) { this.required = required; }
    /** {@return explanation of the decision} */
    public String getReason() { return reason; }
    /**
     * Sets the explanation of the decision on this provisioning selected tool.
     *
     * @param reason explanation of the decision
     */
    public void setReason(String reason) { this.reason = reason; }
    /** {@return position used when displaying the items} */
    public int getSortOrder() { return sortOrder; }
    /**
     * Sets the position used when displaying the items on this provisioning selected tool.
     *
     * @param sortOrder position used when displaying the items
     */
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
