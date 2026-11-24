package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientCrmFollowUpActionEntity;
import com.altaira.backend.entity.ClientCrmLeadEntity;
import com.altaira.backend.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for client CRM follow up action records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ClientCrmFollowUpActionRepository extends JpaRepository<ClientCrmFollowUpActionEntity, UUID> {
    /**
     * Lists matching records by lead; orders the result by created at ascending id ascending.
     *
     * @param lead sales enquiry in scope
     * @return the client CRM follow up action items in query order
     */
    List<ClientCrmFollowUpActionEntity> findAllByLeadOrderByCreatedAtAscIdAsc(ClientCrmLeadEntity lead);

    /**
     * Finds matching records by id and lead.
     *
     * @param id unique identifier of the record
     * @param lead sales enquiry in scope
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<ClientCrmFollowUpActionEntity> findByIdAndLead(UUID id, ClientCrmLeadEntity lead);

    /**
     * Deletes all by client.
     *
     * @param client customer account in scope
     */
    void deleteAllByClient(ClientEntity client);
}
