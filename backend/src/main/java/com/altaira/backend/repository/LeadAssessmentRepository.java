package com.altaira.backend.repository;

import com.altaira.backend.entity.LeadAssessmentEntity;
import com.altaira.backend.entity.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for lead assessment records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface LeadAssessmentRepository extends JpaRepository<LeadAssessmentEntity, UUID> {
    /**
     * Lists matching records by lead; orders the result by updated at descending.
     *
     * @param lead sales enquiry in scope
     * @return the lead assessment items in query order
     */
    List<LeadAssessmentEntity> findAllByLeadOrderByUpdatedAtDesc(LeadEntity lead);
    /**
     * Finds matching records by lead and form key.
     *
     * @param lead sales enquiry in scope
     * @param formKey key selecting the discovery questionnaire
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<LeadAssessmentEntity> findByLeadAndFormKey(LeadEntity lead, String formKey);
}
