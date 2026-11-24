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

    /** Stores enquiries received by the consultancy and reads them in creation order. */
    private final LeadRepository leadRepository;
    /** Sends the follow-up notification after a public enquiry has been saved. */
    private final LeadNotificationService leadNotificationService;

    /**
     * Connects the sales inbox to its database repository and its notification service.
     *
     * @param leadRepository repository for the consultancy’s sales inbox
     * @param leadNotificationService service that reports the outcome of the notification attempt
     */
    public LeadService(LeadRepository leadRepository, LeadNotificationService leadNotificationService) {
        this.leadRepository = leadRepository;
        this.leadNotificationService = leadNotificationService;
    }

    /**
     * Records a website enquiry, then attempts to notify the consultancy.
     * The returned notification fields describe that second step independently of the saved enquiry.
     *
     * @param request contact details, business and goals supplied through the public form
     * @return the saved enquiry and the notification outcome
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
     * Adds an enquiry entered by staff to the same sales inbox. The selected discovery form
     * becomes its service interest. This intake path does not send a notification.
     *
     * @param request contact and business details entered by staff
     * @param formKey key selecting the discovery questionnaire
     * @return the enquiry created from the staff intake
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
     * Lists the consultancy’s enquiries, newest first. The result is empty for a new inbox.
     *
     * @return a snapshot of each enquiry in creation order, newest first
     */
    public List<LeadResponse> getAllLeads() {
        return leadRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::map)
                .toList();
    }

    /**
     * Loads a single sales enquiry so staff can open its detail view.
     *
     * @param id unique identifier of the record
     * @return the enquiry’s contact details, business context and current status
     * @throws com.altaira.backend.exception.LeadNotFoundException if the sales enquiry does not exist
     */
    public LeadResponse getLeadById(UUID id) {
        LeadEntity entity = leadRepository.findById(id)
                .orElseThrow(() -> new LeadNotFoundException(id));

        return map(entity);
    }

    /**
     * Parses a status label and saves it on an existing sales enquiry.
     *
     * @param id unique identifier of the record
     * @param status requested status, matched after trimming whitespace and ignoring case
     * @throws com.altaira.backend.exception.InvalidLeadStatusException if the label is blank or unrecognized
     * @return the enquiry with its updated status
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
     * Creates the database row shared by the public and staff intake paths. Each enquiry starts
     * in {@link LeadStatus#NEW} with a creation time and a lowercase contact email.
     *
     * @param fullName name as displayed to a person
     * @param businessName name used by the business
     * @param email contact email address
     * @param phone contact telephone number
     * @param industry business sector, if supplied
     * @param serviceInterest requested service or questionnaire key selected by staff
     * @param goals work the prospective customer wants help with
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
     * Normalizes a required text field after validation at the API boundary.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String trimRequired(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Stores an omitted or blank optional field as an empty string.
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
     * Copies a database row into the response returned by the API. The caller can attach
     * a notification outcome when a public form submission triggered an email.
     *
     * @param entity saved sales enquiry
     * @return the contact, business and workflow details of the enquiry
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
