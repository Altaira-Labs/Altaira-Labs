package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.InternalNoteEntity;
import com.altaira.backend.entity.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data queries for internal note records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface InternalNoteRepository extends JpaRepository<InternalNoteEntity, UUID> {
    /**
     * Lists matching records by lead; orders the result by created at descending.
     *
     * @param lead sales enquiry in scope
     * @return the internal note items in query order
     */
    List<InternalNoteEntity> findAllByLeadOrderByCreatedAtDesc(LeadEntity lead);
    /**
     * Lists matching records by client; orders the result by created at descending.
     *
     * @param client customer account in scope
     * @return the internal note items in query order
     */
    List<InternalNoteEntity> findAllByClientOrderByCreatedAtDesc(ClientEntity client);
}
