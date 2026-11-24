package com.altaira.backend.service;

import com.altaira.backend.dto.note.CreateInternalNoteRequest;
import com.altaira.backend.dto.note.InternalNoteResponse;
import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.InternalNoteEntity;
import com.altaira.backend.entity.LeadEntity;
import com.altaira.backend.exception.LeadNotFoundException;
import com.altaira.backend.repository.InternalNoteRepository;
import com.altaira.backend.repository.LeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Stores staff notes against either a sales enquiry or a customer account. Each list is scoped to its
 * parent and ordered from newest note to oldest.
 */
@Service
@Transactional
public class InternalNoteService {

    /** Persistence access for internal note. */
    private final InternalNoteRepository internalNoteRepository;
    /** Persistence access for lead. */
    private final LeadRepository leadRepository;
    /** Client management operations used by this component. */
    private final ClientManagementService clientManagementService;

    /**
     * Creates the internal note service with its injected collaborators.
     *
     * @param internalNoteRepository persistence access for internal note
     * @param leadRepository persistence access for lead
     * @param clientManagementService client management operations used by this component
     */
    public InternalNoteService(
            InternalNoteRepository internalNoteRepository,
            LeadRepository leadRepository,
            ClientManagementService clientManagementService
    ) {
        this.internalNoteRepository = internalNoteRepository;
        this.leadRepository = leadRepository;
        this.clientManagementService = clientManagementService;
    }

    /**
     * Saves the note against the selected enquiry with its resolved author.
     *
     * @param leadId identifier of the source sales enquiry
     * @param request note content and optional author
     * @return the stored note on that enquiry
     * @throws com.altaira.backend.exception.LeadNotFoundException if the sales enquiry does not exist
     */
    public InternalNoteResponse addLeadNote(UUID leadId, CreateInternalNoteRequest request) {
        LeadEntity lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException(leadId));

        InternalNoteEntity entity = new InternalNoteEntity();
        entity.setLead(lead);
        entity.setContent(trimRequired(request.getContent()));
        entity.setAuthor(resolveAuthor(request.getAuthor()));

        return map(internalNoteRepository.save(entity));
    }

    /**
     * Lists internal staff notes associated with one sales enquiry.
     *
     * @param leadId identifier of the source sales enquiry
     * @return the enquiry's notes, newest first
     * @throws com.altaira.backend.exception.LeadNotFoundException if the sales enquiry does not exist
     */
    public List<InternalNoteResponse> getLeadNotes(UUID leadId) {
        LeadEntity lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException(leadId));

        return internalNoteRepository.findAllByLeadOrderByCreatedAtDesc(lead)
                .stream()
                .map(this::map)
                .toList();
    }

    /**
     * Saves the note against the selected customer account with its resolved author.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param request note content and optional author
     * @return the stored note on that customer
     */
    public InternalNoteResponse addClientNote(UUID clientId, CreateInternalNoteRequest request) {
        ClientEntity client = clientManagementService.findClientEntity(clientId);

        InternalNoteEntity entity = new InternalNoteEntity();
        entity.setClient(client);
        entity.setContent(trimRequired(request.getContent()));
        entity.setAuthor(resolveAuthor(request.getAuthor()));

        return map(internalNoteRepository.save(entity));
    }

    /**
     * Lists internal staff notes associated with one customer.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @return the customer's notes, newest first
     */
    public List<InternalNoteResponse> getClientNotes(UUID clientId) {
        ClientEntity client = clientManagementService.findClientEntity(clientId);

        return internalNoteRepository.findAllByClientOrderByCreatedAtDesc(client)
                .stream()
                .map(this::map)
                .toList();
    }

    /**
     * Builds {@code InternalNoteResponse} from {@code InternalNoteEntity}, selecting the fields used by the caller.
     *
     * @param entity record carrying internal note
     * @return the internal note response
     */
    private InternalNoteResponse map(InternalNoteEntity entity) {
        UUID leadId = entity.getLead() == null ? null : entity.getLead().getId();
        UUID clientId = entity.getClient() == null ? null : entity.getClient().getId();

        return new InternalNoteResponse(
                entity.getId(),
                leadId,
                clientId,
                entity.getContent(),
                entity.getAuthor(),
                entity.getCreatedAt()
        );
    }

    /**
     * Resolves author.
     *
     * @param author author
     * @return the resulting text
     */
    private String resolveAuthor(String author) {
        if (author == null || author.isBlank()) {
            return "Admin";
        }

        return author.trim();
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
}
