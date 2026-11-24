package com.altaira.backend.entity;

import jakarta.persistence.*;

import java.util.UUID;

/** Maps provisioning external resource records to the {@code provisioning_external_resources} table. */
@Entity
@Table(
        name = "provisioning_external_resources",
        uniqueConstraints = @UniqueConstraint(
                name = "provisioning_external_resource_idempotency_unique",
                columnNames = {"idempotency_key"}
        )
)
public class ProvisioningExternalResourceEntity {

    /** Primary identifier for this provisioning external resource. */
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

    /** Identifier assigned by the external provider. Stored in {@code external_resource_id}. */
    @Column(name = "external_resource_id", length = 240)
    private String externalResourceId;

    /** Link to the resource in the external system. Stored in {@code external_url}. */
    @Column(name = "external_url", length = 1000)
    private String externalUrl;

    /** Current state in the workflow. */
    @Column(nullable = false, length = 30)
    private String status;

    /** Key used to recognize a repeated operation. Stored in {@code idempotency_key}. */
    @Column(name = "idempotency_key", nullable = false, length = 200)
    private String idempotencyKey;

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this provisioning external resource.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }
    /** {@return proposed or saved delivery plan} */
    public ProvisioningPlanEntity getPlan() { return plan; }
    /**
     * Sets the proposed or saved delivery plan on this provisioning external resource.
     *
     * @param plan proposed or saved delivery plan
     */
    public void setPlan(ProvisioningPlanEntity plan) { this.plan = plan; }
    /** {@return key identifying the integration provider} */
    public String getProviderKey() { return providerKey; }
    /**
     * Sets the key identifying the integration provider on this provisioning external resource.
     *
     * @param providerKey key identifying the integration provider
     */
    public void setProviderKey(String providerKey) { this.providerKey = providerKey; }
    /** {@return kind of external or internal resource} */
    public String getResourceType() { return resourceType; }
    /**
     * Sets the kind of external or internal resource on this provisioning external resource.
     *
     * @param resourceType kind of external or internal resource
     */
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    /** {@return identifier assigned by the external provider} */
    public String getExternalResourceId() { return externalResourceId; }
    /**
     * Sets the identifier assigned by the external provider on this provisioning external resource.
     *
     * @param externalResourceId identifier assigned by the external provider
     */
    public void setExternalResourceId(String externalResourceId) { this.externalResourceId = externalResourceId; }
    /** {@return link to the resource in the external system} */
    public String getExternalUrl() { return externalUrl; }
    /**
     * Sets the link to the resource in the external system on this provisioning external resource.
     *
     * @param externalUrl link to the resource in the external system
     */
    public void setExternalUrl(String externalUrl) { this.externalUrl = externalUrl; }
    /** {@return current state in the workflow} */
    public String getStatus() { return status; }
    /**
     * Sets the current state in the workflow on this provisioning external resource.
     *
     * @param status current state in the workflow
     */
    public void setStatus(String status) { this.status = status; }
    /** {@return key used to recognize a repeated operation} */
    public String getIdempotencyKey() { return idempotencyKey; }
    /**
     * Sets the key used to recognize a repeated operation on this provisioning external resource.
     *
     * @param idempotencyKey key used to recognize a repeated operation
     */
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
