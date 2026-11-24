package com.altaira.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Maps client CRM lead note records to the {@code client_crm_lead_notes} table. */
@Entity
@Table(name = "client_crm_lead_notes")
public class ClientCrmLeadNoteEntity {

    /** Primary identifier for this client CRM lead note. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Sales enquiry in scope. Joined through {@code lead_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id", nullable = false)
    private ClientCrmLeadEntity lead;

    /** Customer account in scope. Joined through {@code client_id}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    /** Author user. Joined through {@code author_user_id}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id")
    private AppUserEntity authorUser;

    /** Author role. Stored in {@code author_role}. */
    @Column(name = "author_role", nullable = false)
    private String authorRole;

    /** Whether the client can see the item. Stored in {@code visible_to_client}. */
    @Column(name = "visible_to_client", nullable = false)
    private boolean visibleToClient;

    /** Text or file content carried by this item. */
    @Column(nullable = false, columnDefinition = "text")
    private String content;

    /** Time the record was created. Stored in {@code created_at}. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Constructs {@code ClientCrmLeadNoteEntity} with its default state. */
    public ClientCrmLeadNoteEntity() {}

    /** Initializes created at before the record is inserted. */
    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    /** {@return unique identifier of the record} */
    public UUID getId() { return id; }
    /**
     * Sets the unique identifier of the record on this client CRM lead note.
     *
     * @param id unique identifier of the record
     */
    public void setId(UUID id) { this.id = id; }

    /** {@return sales enquiry in scope} */
    public ClientCrmLeadEntity getLead() { return lead; }
    /**
     * Sets the sales enquiry in scope on this client CRM lead note.
     *
     * @param lead sales enquiry in scope
     */
    public void setLead(ClientCrmLeadEntity lead) { this.lead = lead; }

    /** {@return customer account in scope} */
    public ClientEntity getClient() { return client; }
    /**
     * Sets the customer account in scope on this client CRM lead note.
     *
     * @param client customer account in scope
     */
    public void setClient(ClientEntity client) { this.client = client; }

    /** {@return author user} */
    public AppUserEntity getAuthorUser() { return authorUser; }
    /**
     * Sets the author user on this client CRM lead note.
     *
     * @param authorUser author user
     */
    public void setAuthorUser(AppUserEntity authorUser) { this.authorUser = authorUser; }

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

    /** {@return text or file content carried by this item} */
    public String getContent() { return content; }
    /**
     * Sets the text or file content carried by this item on this client CRM lead note.
     *
     * @param content text or file content carried by this item
     */
    public void setContent(String content) { this.content = content; }

    /** {@return time the record was created} */
    public Instant getCreatedAt() { return createdAt; }
    /**
     * Sets the time the record was created on this client CRM lead note.
     *
     * @param createdAt time the record was created
     */
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
