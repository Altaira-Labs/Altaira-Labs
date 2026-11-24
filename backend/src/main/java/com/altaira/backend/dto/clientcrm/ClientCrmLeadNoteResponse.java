package com.altaira.backend.dto.clientcrm;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for client CRM lead note. */
public class ClientCrmLeadNoteResponse {
    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the source sales enquiry. */
    private UUID leadId;
    /** Text or file content carried by this item. */
    private String content;
    /** Author username. */
    private String authorUsername;
    /** Author role. */
    private String authorRole;
    /** Whether the client can see the item. */
    private boolean visibleToClient;
    /** Time the record was created. */
    private Instant createdAt;

    /** Constructs {@code ClientCrmLeadNoteResponse} with its default state. */
    public ClientCrmLeadNoteResponse() {}

    /**
     * Constructs {@code ClientCrmLeadNoteResponse} using the supplied id, lead id, content and remaining fields.
     *
     * @param id unique identifier of the record
     * @param leadId identifier of the source sales enquiry
     * @param content text or file content carried by this item
     * @param authorUsername author username
     * @param authorRole author role
     * @param visibleToClient whether the client can see the item
     * @param createdAt time the record was created
     */
    public ClientCrmLeadNoteResponse(
            UUID id,
            UUID leadId,
            String content,
            String authorUsername,
            String authorRole,
            boolean visibleToClient,
            Instant createdAt
    ) {
        this.id = id;
        this.leadId = leadId;
        this.content = content;
        this.authorUsername = authorUsername;
        this.authorRole = authorRole;
        this.visibleToClient = visibleToClient;
        this.createdAt = createdAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client CRM lead note.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the source sales enquiry} */
    public UUID getLeadId() { return leadId; }
    /**
     * Sets the identifier of the source sales enquiry on this client CRM lead note.
     *
     * @param leadId identifier of the source sales enquiry
     */
    public void setLeadId(UUID leadId) { this.leadId = leadId; }

    /** {@return text or file content carried by this item} */
    public String getContent() { return content; }
    /**
     * Sets the text or file content carried by this item on this client CRM lead note.
     *
     * @param content text or file content carried by this item
     */
    public void setContent(String content) { this.content = content; }

    /** {@return author username} */
    public String getAuthorUsername() { return authorUsername; }
    /**
     * Sets the author username on this client CRM lead note.
     *
     * @param authorUsername author username
     */
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    /** {@return author role} */
    public String getAuthorRole() { return authorRole; }
    /**
     * Sets the author role on this client CRM lead note.
     *
     * @param authorRole author role
     */
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }

    /** {@return whether the client can see the item} */
    public boolean isVisibleToClient() { return visibleToClient; }
    /**
     * Sets the whether the client can see the item on this client CRM lead note.
     *
     * @param visibleToClient whether the client can see the item
     */
    public void setVisibleToClient(boolean visibleToClient) { this.visibleToClient = visibleToClient; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this client CRM lead note.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
