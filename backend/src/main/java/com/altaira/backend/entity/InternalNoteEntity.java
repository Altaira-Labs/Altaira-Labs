package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Maps internal note records to the {@code internal_notes} table. */
@Entity
@Table(name = "internal_notes")
public class InternalNoteEntity {

    /** Primary identifier for this internal note. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Sales enquiry in scope. Joined through {@code lead_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private LeadEntity lead;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    /** Text or file content carried by this item. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** Author. */
    @Column(nullable = false)
    private String author;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Constructs {@code InternalNoteEntity} with its default state. */
    public InternalNoteEntity() {}

    /** Initializes created at before the record is inserted. */
    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this internal note.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return sales enquiry in scope} */
    public LeadEntity getLead() { return lead; }
    /**
     * Sets the sales enquiry in scope on this internal note.
     *
     * @param lead sales enquiry in scope
     */
    public void setLead(LeadEntity lead) { this.lead = lead; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this internal note.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

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
