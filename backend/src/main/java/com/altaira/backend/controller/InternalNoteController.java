package com.altaira.backend.controller;

import com.altaira.backend.dto.note.CreateInternalNoteRequest;
import com.altaira.backend.dto.note.InternalNoteResponse;
import com.altaira.backend.security.AdminAccessService;
import com.altaira.backend.service.InternalNoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** HTTP endpoints for internal note. */
@RestController
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class InternalNoteController {

    /** Internal note operations used by this component. */
    private final InternalNoteService internalNoteService;
    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;

    /**
     * Creates the internal note controller with its injected collaborators.
     *
     * @param internalNoteService internal note operations used by this component
     * @param adminAccessService admin access operations used by this component
     */
    public InternalNoteController(InternalNoteService internalNoteService, AdminAccessService adminAccessService) {
        this.internalNoteService = internalNoteService;
        this.adminAccessService = adminAccessService;
    }

    /**
     * GET {@code /api/v1/leads/{leadId}/notes}. Retrieves lead notes. Internal access is checked before delegation.
     *
     * @param leadId identifier of the source sales enquiry
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the internal note response items
     */
    @GetMapping("/api/v1/leads/{leadId}/notes")
    public List<InternalNoteResponse> getLeadNotes(
            @PathVariable UUID leadId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return internalNoteService.getLeadNotes(leadId);
    }

    /**
     * POST {@code /api/v1/leads/{leadId}/notes}. Adds lead note. Internal access is checked before delegation.
     *
     * @param leadId identifier of the source sales enquiry
     * @param request input carrying create internal note
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the HTTP response for this operation
     */
    @PostMapping("/api/v1/leads/{leadId}/notes")
    public ResponseEntity<InternalNoteResponse> addLeadNote(
            @PathVariable UUID leadId,
            @Valid @RequestBody CreateInternalNoteRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(internalNoteService.addLeadNote(leadId, request));
    }
}
