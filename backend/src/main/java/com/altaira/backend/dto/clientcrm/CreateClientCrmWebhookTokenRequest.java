package com.altaira.backend.dto.clientcrm;

import jakarta.validation.constraints.Size;

/** Input for create client CRM webhook token in the clientCRM API. */
public class CreateClientCrmWebhookTokenRequest {

    /** Display label. */
    @Size(max = 120, message = "Label must be at most 120 characters")
    private String label;

    /** {@return display label} */
    public String getLabel() { return label; }
    /**
     * Sets the display label on this create client CRM webhook token.
     *
     * @param label display label
     */
    public void setLabel(String label) { this.label = label; }
}
