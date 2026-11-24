package com.altaira.backend.controller;

import com.altaira.backend.dto.service.CreateServiceRequest;
import com.altaira.backend.dto.service.ServiceResponse;
import com.altaira.backend.dto.service.UpdateServiceRequest;
import com.altaira.backend.security.AdminAccessService;
import com.altaira.backend.service.ServiceCatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Staff endpoints under {@code /api/v1/services} for managing the catalogue of services that can be
 * assigned to customer accounts.
 */
@RestController
@RequestMapping("/api/v1/services")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class ServiceCatalogController {

    /** Service catalog operations used by this component. */
    private final ServiceCatalogService serviceCatalogService;
    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;

    /**
     * Creates the service catalog controller with its injected collaborators.
     *
     * @param serviceCatalogService service catalog operations used by this component
     * @param adminAccessService admin access operations used by this component
     */
    public ServiceCatalogController(ServiceCatalogService serviceCatalogService, AdminAccessService adminAccessService) {
        this.serviceCatalogService = serviceCatalogService;
        this.adminAccessService = adminAccessService;
    }

    /**
     * GET {@code /api/v1/services}. Returns all services. Internal access is checked before delegation.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the service response items
     */
    @GetMapping
    public List<ServiceResponse> getAllServices(
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return serviceCatalogService.getAllServices();
    }

    /**
     * GET {@code /api/v1/services/{id}}.
     * Loads one catalogue entry for an authorized internal caller.
     *
     * @param id identifier of the catalogue entry
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the service description, active flag and catalogue metadata
     */
    @GetMapping("/{id}")
    public ServiceResponse getServiceById(
            @PathVariable UUID id,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return serviceCatalogService.getServiceById(id);
    }

    /**
     * POST {@code /api/v1/services}. Creates service. Internal access is checked before delegation.
     *
     * @param request input carrying create service
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the HTTP response for this operation
     */
    @PostMapping
    public ResponseEntity<ServiceResponse> createService(
            @Valid @RequestBody CreateServiceRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceCatalogService.createService(request));
    }

    /**
     * PATCH {@code /api/v1/services/{id}}. Updates service. Internal access is checked before delegation.
     *
     * @param id unique identifier of the record
     * @param request input carrying update service
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the service response
     */
    @PatchMapping("/{id}")
    public ServiceResponse updateService(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateServiceRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalApiToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminSessionToken
    ) {
        adminAccessService.requireAdminAccess(internalApiToken, adminSessionToken);
        return serviceCatalogService.updateService(id, request);
    }
}
