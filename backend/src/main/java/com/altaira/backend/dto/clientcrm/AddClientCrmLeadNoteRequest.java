package com.altaira.backend.dto.clientcrm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for add client CRM lead note in the clientCRM API. */
public class AddClientCrmLeadNoteRequest {

    /** Text or file content carried by this item. The request must supply nonblank text. */
    @NotBlank(message = "Note content is required")
    @Size(max = 1200, message = "Note content must be at most 1200 characters")
    private String content;

    /** Whether the client can see the item. */
    private Boolean visibleToClient;

    /** {@return text or file content carried by this item} */
    public String getContent() { return content; }
    /**
     * Sets the text or file content carried by this item on this add client CRM lead note.
     *
     * @param content text or file content carried by this item
     */
    public void setContent(String content) { this.content = content; }

    /** {@return whether the client can see the item} */
    public Boolean getVisibleToClient() { return visibleToClient; }
    /**
     * Sets the whether the client can see the item on this add client CRM lead note.
     *
     * @param visibleToClient whether the client can see the item
     */
    public void setVisibleToClient(Boolean visibleToClient) { this.visibleToClient = visibleToClient; }
}
