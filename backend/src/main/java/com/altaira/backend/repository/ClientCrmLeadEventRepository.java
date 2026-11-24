package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientCrmLeadEntity;
import com.altaira.backend.entity.ClientCrmLeadEventEntity;
import com.altaira.backend.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data queries for client CRM lead event records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ClientCrmLeadEventRepository extends JpaRepository<ClientCrmLeadEventEntity, UUID> {
    /**
     * Lists matching records by lead; orders the result by created at ascending id ascending.
     *
     * @param lead sales enquiry in scope
     * @return the client CRM lead event items in query order
     */
    List<ClientCrmLeadEventEntity> findAllByLeadOrderByCreatedAtAscIdAsc(ClientCrmLeadEntity lead);

    /**
     * Deletes all by client.
     *
     * @param client customer account in scope
     */
    void deleteAllByClient(ClientEntity client);
}
