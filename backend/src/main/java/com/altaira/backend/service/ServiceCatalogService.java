package com.altaira.backend.service;

import com.altaira.backend.dto.service.CreateServiceRequest;
import com.altaira.backend.dto.service.ServiceResponse;
import com.altaira.backend.dto.service.UpdateServiceRequest;
import com.altaira.backend.entity.ServiceEntity;
import com.altaira.backend.exception.ResourceNotFoundException;
import com.altaira.backend.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Maintains the catalogue of consultancy offerings and resolves launch modules to active catalogue
 * services. The catalogue is shared across customers; a customer-specific assignment is stored
 * separately.
 */
@Service
@Transactional
public class ServiceCatalogService {

    /** Service name by module key used by service catalog service. */
    private static final Map<String, String> SERVICE_NAME_BY_MODULE_KEY = Map.of(
            "web_seo", "Website Development",
            "booking", "Booking Systems",
            "crm", "CRM / Business Systems",
            "automation", "Automation Workflows",
            "dashboard", "Internal Dashboards"
    );

    /** Persistence access for service. */
    private final ServiceRepository serviceRepository;

    /**
     * Creates the service catalog service with its injected collaborators.
     *
     * @param serviceRepository persistence access for service
     */
    public ServiceCatalogService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /**
     * Adds a service to the catalogue. A missing active flag makes the new entry active.
     *
     * @param request name, category, description and optional active flag
     * @return the saved catalogue entry with its identifier and timestamps
     */
    public ServiceResponse createService(CreateServiceRequest request) {
        ServiceEntity entity = new ServiceEntity();
        entity.setName(trimRequired(request.getName()));
        entity.setCategory(trimOptional(request.getCategory()));
        entity.setDescription(trimOptional(request.getDescription()));
        entity.setActive(request.getActive() == null || request.getActive());

        return map(serviceRepository.save(entity));
    }

    /**
     * Lists all catalogue entries in name order.
     *
     * @return service descriptions in ascending name order
     */
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::map)
                .toList();
    }

    /**
     * Loads one entry from the service catalogue.
     *
     * @param id identifier of the catalogue entry
     * @return the service name, category, description, active flag and timestamps
     */
    public ServiceResponse getServiceById(UUID id) {
        return map(findServiceEntity(id));
    }

    /**
     * Replaces the editable catalogue details and changes the active flag only when supplied.
     *
     * @param id unique identifier of the record
     * @param request replacement catalogue details and optional active flag
     * @return the saved version of the service
     */
    public ServiceResponse updateService(UUID id, UpdateServiceRequest request) {
        ServiceEntity entity = findServiceEntity(id);
        entity.setName(trimRequired(request.getName()));
        entity.setCategory(trimOptional(request.getCategory()));
        entity.setDescription(trimOptional(request.getDescription()));

        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }

        return map(serviceRepository.save(entity));
    }

    /**
     * Locates the persisted catalogue entry before assigning or editing a service.
     *
     * @param id identifier of the catalogue entry
     * @return the stored service with the requested identifier
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    ServiceEntity findServiceEntity(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
    }

    /**
     * Resolves a launch module to the active catalogue entry configured for it.
     *
     * @param moduleKey key identifying the module
     * @return the active service required to launch that module
     * @throws IllegalArgumentException when validation reports “Unsupported launch service key”
     */
    public ServiceEntity findServiceForModuleKey(String moduleKey) {
        String serviceName = SERVICE_NAME_BY_MODULE_KEY.get(moduleKey);
        if (serviceName == null) {
            throw new IllegalArgumentException("Unsupported launch service key");
        }

        ServiceEntity service = serviceRepository.findByNameIgnoreCase(serviceName)
                .orElseThrow(() -> new IllegalStateException(
                        "Required service catalogue entry is missing: " + serviceName
                ));

        if (!service.isActive()) {
            throw new IllegalStateException("Required service catalogue entry is inactive: " + serviceName);
        }
        return service;
    }

    /**
     * Builds {@code ServiceResponse} from {@code ServiceEntity}, selecting the fields used by the caller.
     *
     * @param entity record carrying service
     * @return the service response
     */
    ServiceResponse map(ServiceEntity entity) {
        return new ServiceResponse(
                entity.getId(),
                entity.getName(),
                entity.getCategory(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
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
}
