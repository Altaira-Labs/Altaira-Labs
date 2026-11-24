package com.altaira.backend.service;

import com.altaira.backend.entity.ServiceEntity;
import com.altaira.backend.repository.ServiceRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/** Registers the initial set of services that consultants can assign to clients. */
@Component
@ConditionalOnProperty(
        name = "altaira.service-catalog.seed.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class ServiceCatalogSeeder {

    /** Persistence access for service. */
    private final ServiceRepository serviceRepository;

    /**
     * Creates the service catalog seeder with its injected collaborators.
     *
     * @param serviceRepository persistence access for service
     */
    public ServiceCatalogSeeder(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /** Performs the seed services operation. Persists changes through {@code serviceRepository}. */
    @EventListener(ApplicationReadyEvent.class)
    public void seedServices() {
        List<SeedService> services = List.of(
                new SeedService("Website Development", "Web", "Professional websites and landing pages that capture leads."),
                new SeedService("Booking Systems", "Operations", "Online booking flows, reminders, and scheduling support."),
                new SeedService("Automation Workflows", "Automation", "Automations for repetitive follow-up and internal tasks."),
                new SeedService("Internal Dashboards", "Operations", "Dashboards for leads, clients, services, and business visibility."),
                new SeedService("CRM / Business Systems", "Business Systems", "Lightweight CRM-style tools and internal management systems."),
                new SeedService("API Integration", "Integration", "Connections between business tools and external APIs."),
                new SeedService("Technical Consulting", "Consulting", "Technical planning and implementation support for small businesses.")
        );

        for (SeedService seed : services) {
            serviceRepository.findByNameIgnoreCase(seed.name()).orElseGet(() -> {
                ServiceEntity entity = new ServiceEntity();
                entity.setName(seed.name());
                entity.setCategory(seed.category());
                entity.setDescription(seed.description());
                entity.setActive(true);
                return serviceRepository.save(entity);
            });
        }
    }

    /**
     * Coordinates seed operations in the backend.
     *
     * @param name name used to identify the item
     * @param category category
     * @param description human-readable description
     */
    private record SeedService(String name, String category, String description) {}
}
