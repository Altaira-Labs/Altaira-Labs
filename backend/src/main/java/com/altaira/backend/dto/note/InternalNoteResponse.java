package com.altaira.backend.dto.note;

import java.time.Instant;
import java.util.UUID;

/** Fields the API returns for internal note. */
public class InternalNoteResponse {

    /** Unique identifier of the record. */
    private UUID id;
    /** Identifier of the source sales enquiry. */
    private UUID leadId;
    /** Identifier of the client whose workspace is in scope. */
    private UUID clientId;
    /** Text or file content carried by this item. */
    private String content;
    /** Author. */
    private String author;
    /** Time the record was created. */
    private Instant createdAt;

    /** Constructs {@code InternalNoteResponse} with its default state. */
    public InternalNoteResponse() {}

    /**
     * Constructs {@code InternalNoteResponse} using the supplied id, lead id, client id and remaining fields.
     *
     * @param id unique identifier of the record
     * @param leadId identifier of the source sales enquiry
     * @param clientId identifier of the client whose workspace is in scope
     * @param content text or file content carried by this item
     * @param author author
     * @param createdAt time the record was created
     */
    public InternalNoteResponse(UUID id, UUID leadId, UUID clientId, String content, String author, Instant createdAt) {
        this.id = id;
        this.leadId = leadId;
        this.clientId = clientId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this internal note.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return identifier of the source sales enquiry} */
    public UUID getLeadId() { return leadId; }
    /**
     * Sets the identifier of the source sales enquiry on this internal note.
     *
     * @param leadId identifier of the source sales enquiry
     */
    public void setLeadId(UUID leadId) { this.leadId = leadId; }

    /** {@return identifier of the client whose workspace is in scope} */
    public UUID getClientId() { return clientId; }
    /**
     * Sets the identifier of the client whose workspace is in scope on this internal note.
     *
     * @param clientId identifier of the client whose workspace is in scope
     */
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    /** {@return text or file content carried by this item} */
    public String getContent() { return content; }
    /**
     * Sets the text or file content carried by this item on this internal note.
     *
     * @param content text or file content carried by this item
     */
    public void setContent(String content) { this.content = content; }

    /** {@return author} */
    public String getAuthor() { return author; }
    /**
     * Sets the author on this internal note.
     *
     * @param author author
     */
    public void setAuthor(String author) { this.author = author; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this internal note.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
