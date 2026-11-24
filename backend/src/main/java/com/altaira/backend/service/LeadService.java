package com.altaira.backend.service;

import com.altaira.backend.dto.lead.CreateLeadRequest;
import com.altaira.backend.dto.lead.CreateAdminLeadIntakeRequest;
import com.altaira.backend.dto.lead.LeadResponse;
import com.altaira.backend.entity.LeadEntity;
import com.altaira.backend.exception.LeadNotFoundException;
import com.altaira.backend.model.LeadStatus;
import com.altaira.backend.repository.LeadRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Creates the consultancy's enquiries and lets staff review or update them.
 * <p>The public website writes into this sales inbox. Enquiries managed by a customer
 * inside their own CRM follow a separate path through {@link ClientCrmLeadService}.
 * Keeping the two uses of “lead” distinct is important when choosing the relevant repository.
 */
@Service
public class LeadService {

    /** Persistence access for lead. */
    private final LeadRepository leadRepository;
    /** Lead notification operations used by this component. */
    private final LeadNotificationService leadNotificationService;

    /**
     * Creates the lead service with its injected collaborators.
     *
     * @param leadRepository persistence access for lead
     * @param leadNotificationService lead notification operations used by this component
     */
    public LeadService(LeadRepository leadRepository, LeadNotificationService leadNotificationService) {
        this.leadRepository = leadRepository;
        this.leadNotificationService = leadNotificationService;
    }

    /**
     * Creates lead.
     *
     * @param request input carrying create lead
     * @return the lead response
     */
    public LeadResponse createLead(CreateLeadRequest request) {
        LeadEntity saved = saveLead(
                request.getFullName(),
                request.getBusinessName(),
                request.getEmail(),
                request.getPhone(),
                request.getIndustry(),
                request.getServiceInterest(),
                request.getGoals()
        );
        EmailNotificationResult notificationResult = leadNotificationService.sendLeadCreatedNotification(saved);

        LeadResponse response = map(saved);
        response.setEmailNotificationSent(notificationResult.sent());
        response.setEmailNotificationMessage(notificationResult.message());
        return response;
    }

    /**
     * Creates admin lead.
     *
     * @param request input carrying create admin lead intake
     * @param formKey key selecting the discovery questionnaire
     * @return the lead response
     */
    public LeadResponse createAdminLead(CreateAdminLeadIntakeRequest request, String formKey) {
        return map(saveLead(
                request.getFullName(),
                request.getBusinessName(),
                request.getEmail(),
                request.getPhone(),
                request.getIndustry(),
                formKey,
                request.getGoals()
        ));
    }

    /**
     * Returns all leads.
     *
     * @return the lead response items
     */
    public List<LeadResponse> getAllLeads() {
        return leadRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::map)
                .toList();
    }

    /**
     * Retrieves lead by id. Looks up the referenced record by identifier.
     *
     * @param id unique identifier of the record
     * @return identifier of the lead by
     * @throws com.altaira.backend.exception.LeadNotFoundException if the sales enquiry does not exist
     */
    public LeadResponse getLeadById(UUID id) {
        LeadEntity entity = leadRepository.findById(id)
                .orElseThrow(() -> new LeadNotFoundException(id));

        return map(entity);
    }

    /**
     * Updates status. Persists changes through {@code leadRepository}.
     *
     * @param id unique identifier of the record
     * @param status current state in the workflow
     * @return the lead response
     * @throws com.altaira.backend.exception.LeadNotFoundException if the sales enquiry does not exist
     */
    public LeadResponse updateStatus(UUID id, String status) {
        String normalizedStatus = LeadStatus.parse(status).value();

        LeadEntity entity = leadRepository.findById(id)
                .orElseThrow(() -> new LeadNotFoundException(id));

        entity.setStatus(normalizedStatus);

        LeadEntity updated = leadRepository.save(entity);

        return map(updated);
    }

    /**
     * Saves lead. Persists changes through {@code leadRepository}. Uses locale-independent case normalization.
     *
     * @param fullName name as displayed to a person
     * @param businessName name used by the business
     * @param email contact email address
     * @param phone contact telephone number
     * @param industry industry
     * @param serviceInterest service interest
     * @param goals goals
     * @return the lead record
     */
    private LeadEntity saveLead(
            String fullName,
            String businessName,
            String email,
            String phone,
            String industry,
            String serviceInterest,
            String goals
    ) {
        LeadEntity entity = new LeadEntity();
        entity.setFullName(trimRequired(fullName));
        entity.setBusinessName(trimRequired(businessName));
        entity.setEmail(trimRequired(email).toLowerCase(Locale.ROOT));
        entity.setPhone(trimOptional(phone));
        entity.setIndustry(trimOptional(industry));
        entity.setServiceInterest(trimOptional(serviceInterest));
        entity.setGoals(trimOptional(goals));
        entity.setStatus(LeadStatus.NEW.value());
        entity.setCreatedAt(Instant.now());
        return leadRepository.save(entity);
    }

    /**
     * Trims surrounding whitespace. Absent values use an empty string.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String trimRequired(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Trims surrounding whitespace. Absent values use an empty string.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String trimOptional(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.trim();
    }

    /**
     * Builds {@code LeadResponse} from {@code LeadEntity}, selecting the fields used by the caller.
     *
     * @param entity record carrying lead
     * @return the lead response
     */
    private LeadResponse map(LeadEntity entity) {
        return new LeadResponse(
                entity.getId(),
                entity.getFullName(),
                entity.getBusinessName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getIndustry(),
                entity.getServiceInterest(),
                entity.getGoals(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
