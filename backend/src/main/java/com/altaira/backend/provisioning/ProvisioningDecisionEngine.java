package com.altaira.backend.provisioning;

import com.altaira.backend.integration.provisioning.ProvisioningProviderRegistry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Turns discovery answers and selected services into an explainable delivery plan.
 * <p>For example, a request for a booking website may involve more than one track. The
 * engine collects each applicable policy's choices and combines the tools, manual work
 * and shared resources into a single decision. The result explains what the consultancy
 * expects to build. A separate execution workflow controls any provider action.
 */
@Component
public class ProvisioningDecisionEngine {

    /** Tools included in the delivery decision. */
    private final ProvisioningToolCatalog tools;
    /** Policies. */
    private final List<TrackDecisionPolicy> policies;

    /**
     * Constructs {@code ProvisioningDecisionEngine} using the supplied provider registry.
     *
     * @param providerRegistry provider registry
     */
    public ProvisioningDecisionEngine(ProvisioningProviderRegistry providerRegistry) {
        this.tools = new ProvisioningToolCatalog(providerRegistry);
        this.policies = List.of(
                new WebTrackDecisionPolicy(tools),
                new BookingTrackDecisionPolicy(tools),
                new CrmTrackDecisionPolicy(tools),
                new AutomationTrackDecisionPolicy(tools),
                new DashboardTrackDecisionPolicy(tools)
        );
    }

    /**
     * Evaluates the selected service tracks and combines their choices into one provisioning decision.
     *
     * @param requirements requirements
     * @param businessName name used by the business
     * @return the provisioning decision
     */
    public ProvisioningDecision decide(RequirementSet requirements, String businessName) {
        List<String> inferredServices = inferServices(requirements);
        return decide(requirements, "general", Map.of(), inferredServices, businessName);
    }

    /**
     * Evaluates the selected service tracks and combines their choices into one provisioning decision.
     *
     * @param requirements requirements
     * @param formKey key selecting the discovery questionnaire
     * @param responses answers supplied in the discovery questionnaire
     * @param recommendedServices catalogue services proposed after discovery
     * @param businessName name used by the business
     * @return the provisioning decision
     */
    public ProvisioningDecision decide(
            RequirementSet requirements,
            String formKey,
            Map<String, Object> responses,
            List<String> recommendedServices,
            String businessName
    ) {
        ProvisioningDecisionContext context = new ProvisioningDecisionContext(
                requirements,
                formKey,
                responses,
                recommendedServices,
                businessName
        );
        List<TrackDecision> tracks = policies.stream()
                .filter(policy -> policy.supports(context))
                .map(policy -> policy.decide(context))
                .toList();
        if (tracks.isEmpty()) {
            tracks = List.of(new WebTrackDecisionPolicy(tools).decide(context));
        }

        List<ProvisioningDecision.ManualStepDecision> planSteps = new ArrayList<>();
        if (context.isGeneral() && tracks.size() > 1) {
            planSteps.add(tools.manual(
                    "DISCOVERY_APPROVAL",
                    "Approve the composed solution",
                    "The admin must confirm scope before provisioning.",
                    true
            ));
        }
        return ProvisioningDecision.compose(tracks, planSteps);
    }

    /**
     * Infers service tracks from the supplied request information for the decision pass.
     *
     * @param requirements requirements
     * @return the string items
     */
    private List<String> inferServices(RequirementSet requirements) {
        List<String> services = new ArrayList<>();
        if (requirements.has(RequirementSet.FRONTEND)
                || requirements.has(RequirementSet.STATIC_SITE)
                || requirements.has(RequirementSet.CMS)) {
            services.add(ProvisioningTrack.WEB.serviceKey());
        }
        if (requirements.has(RequirementSet.BOOKING)) services.add(ProvisioningTrack.BOOKING.serviceKey());
        if (requirements.has(RequirementSet.CRM)) services.add(ProvisioningTrack.CRM.serviceKey());
        if (requirements.has(RequirementSet.AUTOMATION)) services.add(ProvisioningTrack.AUTOMATION.serviceKey());
        if (requirements.has(RequirementSet.DASHBOARD)) services.add(ProvisioningTrack.DASHBOARD.serviceKey());
        if (services.isEmpty()) services.add(ProvisioningTrack.WEB.serviceKey());
        return services;
    }
}
