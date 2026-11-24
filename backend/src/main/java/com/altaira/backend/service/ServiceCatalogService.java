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

/** Coordinates service catalog operations in the backend. */
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
     * Creates service. Persists changes through {@code serviceRepository}.
     *
     * @param request input carrying create service
     * @return the service response
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
     * Returns all services.
     *
     * @return the service response items
     */
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::map)
                .toList();
    }

    /**
     * Retrieves service by id.
     *
     * @param id unique identifier of the record
     * @return identifier of the service by
     */
    public ServiceResponse getServiceById(UUID id) {
        return map(findServiceEntity(id));
    }

    /**
     * Updates service. Persists changes through {@code serviceRepository}.
     *
     * @param id unique identifier of the record
     * @param request input carrying update service
     * @return the service response
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
     * Finds service entity. Looks up the referenced record by identifier.
     *
     * @param id unique identifier of the record
     * @return the service record
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    ServiceEntity findServiceEntity(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
    }

    /**
     * Finds service for module key.
     *
     * @param moduleKey key identifying the module
     * @return the service record
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
