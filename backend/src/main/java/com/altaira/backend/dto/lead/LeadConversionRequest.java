package com.altaira.backend.dto.lead;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Input for lead conversion in the lead API. */
public class LeadConversionRequest {

    /** Catalogue keys identifying the requested deliveries. */
    @NotEmpty(message = "At least one service is required")
    @Size(max = 5, message = "At most five services can be assigned")
    private List<String> serviceKeys;

    /** Confirmed. */
    @AssertTrue(message = "Lead conversion must be explicitly confirmed")
    private boolean confirmed;

    /** Free-text notes associated with the item. */
    @Size(max = 1000, message = "Conversion notes must be at most 1000 characters")
    private String notes;

    /** {@return catalogue keys identifying the requested deliveries} */
    public List<String> getServiceKeys() { return serviceKeys; }
    /**
     * Sets the catalogue keys identifying the requested deliveries on this lead conversion.
     *
     * @param serviceKeys catalogue keys identifying the requested deliveries
     */
    public void setServiceKeys(List<String> serviceKeys) { this.serviceKeys = serviceKeys; }
    /** {@return confirmed} */
    public boolean isConfirmed() { return confirmed; }
    /**
     * Sets the confirmed on this lead conversion.
     *
     * @param confirmed confirmed
     */
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
    /** {@return free-text notes associated with the item} */
    public String getNotes() { return notes; }
    /**
     * Sets the free-text notes associated with the item on this lead conversion.
     *
     * @param notes free-text notes associated with the item
     */
    public void setNotes(String notes) { this.notes = notes; }
}
