package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientCrmLeadEntity;
import com.altaira.backend.entity.ClientCrmLeadNoteEntity;
import com.altaira.backend.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data queries for client CRM lead note records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ClientCrmLeadNoteRepository extends JpaRepository<ClientCrmLeadNoteEntity, UUID> {
    /**
     * Lists matching records by lead; orders the result by created at ascending.
     *
     * @param lead sales enquiry in scope
     * @return the client CRM lead note items in query order
     */
    List<ClientCrmLeadNoteEntity> findAllByLeadOrderByCreatedAtAsc(ClientCrmLeadEntity lead);

    /**
     * Deletes all by client.
     *
     * @param client customer account in scope
     */
    void deleteAllByClient(ClientEntity client);
}
