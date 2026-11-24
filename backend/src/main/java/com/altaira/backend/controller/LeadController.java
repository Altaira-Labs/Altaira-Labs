package com.altaira.backend.controller;

import com.altaira.backend.dto.lead.CreateLeadRequest;
import com.altaira.backend.dto.lead.CreateAdminLeadIntakeRequest;
import com.altaira.backend.dto.lead.LeadAssessmentResponse;
import com.altaira.backend.dto.lead.LeadConversionRequest;
import com.altaira.backend.dto.lead.LeadConversionResponse;
import com.altaira.backend.dto.lead.LeadIntakeResponse;
import com.altaira.backend.dto.lead.LeadResponse;
import com.altaira.backend.dto.lead.SaveLeadAssessmentRequest;
import com.altaira.backend.dto.lead.UpdateLeadStatusRequest;
import com.altaira.backend.security.AdminAccessService;
import com.altaira.backend.security.RateLimiterService;
import com.altaira.backend.service.LeadService;
import com.altaira.backend.service.LeadAssessmentService;
import com.altaira.backend.service.LeadConversionService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** HTTP endpoints for lead under {@code /api/v1/leads}. */
@RestController
@RequestMapping("/api/v1/leads")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class LeadController {

    /** Lead operations used by this component. */
    private final LeadService leadService;
    /** Lead assessment operations used by this component. */
    private final LeadAssessmentService leadAssessmentService;
    /** Lead conversion operations used by this component. */
    private final LeadConversionService leadConversionService;
    /** Rate limiter operations used by this component. */
    private final RateLimiterService rateLimiterService;
    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;

    /**
     * Creates the lead controller with its injected collaborators.
     *
     * @param leadService lead operations used by this component
     * @param leadAssessmentService lead assessment operations used by this component
     * @param leadConversionService lead conversion operations used by this component
     * @param rateLimiterService rate limiter operations used by this component
     * @param adminAccessService admin access operations used by this component
     */
    public LeadController(
            LeadService leadService,
            LeadAssessmentService leadAssessmentService,
            LeadConversionService leadConversionService,
            RateLimiterService rateLimiterService,
            AdminAccessService adminAccessService
    ) {
        this.leadService = leadService;
        this.leadAssessmentService = leadAssessmentService;
        this.leadConversionService = leadConversionService;
        this.rateLimiterService = rateLimiterService;
        this.adminAccessService = adminAccessService;
    }

    /**
     * POST {@code /api/v1/leads/admin-intake}. Creates admin intake. Internal access is checked before delegation.
     *
     * @param request input carrying create admin lead intake
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the HTTP response for this operation
     */
    @PostMapping("/admin-intake")
    public ResponseEntity<LeadIntakeResponse> createAdminIntake(
            @Valid @RequestBody CreateAdminLeadIntakeRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(leadAssessmentService.createAdminIntake(request));
    }

    /**
     * GET {@code /api/v1/leads}. Returns all leads. Internal access is checked before delegation.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the lead response items
     */
    @GetMapping
    public List<LeadResponse> getAllLeads(
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return leadService.getAllLeads();
    }

    /**
     * GET {@code /api/v1/leads/{id}}. Retrieves lead by id. Internal access is checked before delegation.
     *
     * @param id unique identifier of the record
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return identifier of the lead by
     */
    @GetMapping("/{id}")
    public LeadResponse getLeadById(
            @PathVariable UUID id,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return leadService.getLeadById(id);
    }

    /**
     * GET {@code /api/v1/leads/{id}/assessments}. Retrieves assessments. Internal access is checked before delegation.
     *
     * @param id unique identifier of the record
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the lead assessment response items
     */
    @GetMapping("/{id}/assessments")
    public List<LeadAssessmentResponse> getAssessments(
            @PathVariable UUID id,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return leadAssessmentService.getAssessments(id);
    }

    /**
     * PUT {@code /api/v1/leads/{id}/assessments/{formKey}}. Saves assessment. Internal access is checked before delegation.
     *
     * @param id unique identifier of the record
     * @param formKey key selecting the discovery questionnaire
     * @param request input carrying save lead assessment
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the lead assessment response
     */
    @PutMapping("/{id}/assessments/{formKey}")
    public LeadAssessmentResponse saveAssessment(
            @PathVariable UUID id,
            @PathVariable String formKey,
            @Valid @RequestBody SaveLeadAssessmentRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return leadAssessmentService.saveAssessment(id, formKey, request);
    }

    /**
     * POST {@code /api/v1/leads/{id}/convert}. Converts lead. Internal access is checked before delegation.
     *
     * @param id unique identifier of the record
     * @param request input carrying lead conversion
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the lead conversion response
     */
    @PostMapping("/{id}/convert")
    public LeadConversionResponse convertLead(
            @PathVariable UUID id,
            @Valid @RequestBody LeadConversionRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return leadConversionService.convert(id, request);
    }

    /**
     * PATCH {@code /api/v1/leads/{id}/status}. Updates status. Internal access is checked before delegation.
     *
     * @param id unique identifier of the record
     * @param request input carrying update lead status
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the lead response
     */
    @PatchMapping("/{id}/status")
    public LeadResponse updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLeadStatusRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return leadService.updateStatus(id, request.getStatus());
    }

    /**
     * POST {@code /api/v1/leads}. Creates lead.
     *
     * @param request input carrying create lead
     * @param httpRequest incoming HTTP request, including its headers
     * @return the HTTP response for this operation
     */
    @PostMapping
    public ResponseEntity<?> createLead(@Valid @RequestBody CreateLeadRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        Bucket bucket = rateLimiterService.resolveBucket(ip);

        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(429).body(Map.of(
                    "error", "Too many requests",
                    "message", "Rate limit exceeded. Try again later."
            ));
        }

        LeadResponse createdLead = leadService.createLead(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLead);
    }
}
