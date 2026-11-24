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

/** Coordinates internal note operations in the backend. */
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
     * Adds lead note. Persists changes through {@code internalNoteRepository}.
     *
     * @param leadId identifier of the source sales enquiry
     * @param request input carrying create internal note
     * @return the internal note response
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
     * Retrieves lead notes. Looks up the referenced record by identifier.
     *
     * @param leadId identifier of the source sales enquiry
     * @return the internal note response items
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
     * Adds client note. Persists changes through {@code internalNoteRepository}.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param request input carrying create internal note
     * @return the internal note response
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
     * Retrieves client notes.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @return the internal note response items
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
