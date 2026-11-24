package com.altaira.backend.dto.clientportal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for create project link in the clientportal API. */
public class CreateProjectLinkRequest {

    /** Url. The request must supply nonblank text. */
    @NotBlank
    @Size(max = 500)
    private String url;

    /** Display label. */
    @Size(max = 160)
    private String label;

    /** Asset type. */
    @Size(max = 80)
    private String assetType;

    /** Free-text notes associated with the item. */
    @Size(max = 2000)
    private String notes;

    /** {@return url} */
    public String getUrl() { return url; }
    /**
     * Sets the url on this create project link.
     *
     * @param url url
     */
    public void setUrl(String url) { this.url = url; }

    /** {@return display label} */
    public String getLabel() { return label; }
    /**
     * Sets the display label on this create project link.
     *
     * @param label display label
     */
    public void setLabel(String label) { this.label = label; }

    /** {@return asset type} */
    public String getAssetType() { return assetType; }
    /**
     * Sets the asset type on this create project link.
     *
     * @param assetType asset type
     */
    public void setAssetType(String assetType) { this.assetType = assetType; }

    /** {@return free-text notes associated with the item} */
    public String getNotes() { return notes; }
    /**
     * Sets the free-text notes associated with the item on this create project link.
     *
     * @param notes free-text notes associated with the item
     */
    public void setNotes(String notes) { this.notes = notes; }
}
