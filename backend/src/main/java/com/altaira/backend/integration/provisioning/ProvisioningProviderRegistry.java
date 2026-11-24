package com.altaira.backend.integration.provisioning;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Resolves a provider by key and exposes its capabilities to the planning engine. */
@Component
public class ProvisioningProviderRegistry {

    /** Providers. */
    private final Map<String, ProvisioningProvider> providers;

    /**
     * Constructs {@code ProvisioningProviderRegistry} using the supplied providers.
     *
     * @param providers providers
     */
    public ProvisioningProviderRegistry(List<ProvisioningProvider> providers) {
        Map<String, ProvisioningProvider> indexed = new LinkedHashMap<>();
        providers.forEach(provider -> indexed.put(provider.key(), provider));
        this.providers = Map.copyOf(indexed);
    }

    /**
     * Looks up the provisioning adapter registered for the supplied provider key.
     *
     * @param key key used to identify the item
     * @return the matching value, or an empty optional when it is absent
     */
    public Optional<ProvisioningProvider> find(String key) {
        return Optional.ofNullable(providers.get(key));
    }

    /**
     * Builds the provider result for a requested resource.
     *
     * @param key key used to identify the item
     * @param request input carrying provisioning
     * @return the provisioning result
     */
    public ProvisioningResult prepare(String key, ProvisioningRequest request) {
        return find(key)
                .map(provider -> provider.prepare(request))
                .orElseGet(() -> ProvisioningResult.unregistered(request));
    }

    /**
     * Lists the registered adapters so their supported capabilities can be inspected.
     *
     * @return the provisioning provider items
     */
    public List<ProvisioningProvider> all() {
        return List.copyOf(providers.values());
    }
}
