package com.altaira.backend.provisioning;

import com.altaira.backend.model.AutomationLevel;
import com.altaira.backend.model.ProvisioningRoute;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Collects decisions across service tracks and the shared resources needed to implement them. */
public final class ProvisioningDecision {

    /** Shared keys used by provisioning decision. */
    private static final Set<String> SHARED_KEYS = Set.of(
            "DRIVE", "JIRA", "GITHUB", "AUTH", "NEON", "RESEND", "CALENDAR", "STRIPE"
    );

    /** Delivery route selected by the planning policy. */
    private final ProvisioningRoute route;
    /** Key identifying the route. */
    private final String routeKey;
    /** Degree of automation associated with the step. */
    private final AutomationLevel automationLevel;
    /** Automation scope. */
    private final String automationScope;
    /** Explanation of the decision. */
    private final String reason;
    /** Cost estimate. */
    private final String costEstimate;
    /** Concerns to consider before carrying out the plan. */
    private final List<String> risks;
    /** Tools included in the delivery decision. */
    private final List<ToolDecision> tools;
    /** Items. */
    private final List<PlanItemDecision> items;
    /** Work that still requires a person. */
    private final List<ManualStepDecision> manualSteps;
    /** Tracks. */
    private final List<TrackDecision> tracks;
    /** Shared resources. */
    private final List<SharedResourceDecision> sharedResources;

    /**
     * Captures an immutable copy of the list inputs for this provisioning decision.
     *
     * @param route delivery route selected by the planning policy
     * @param routeKey key identifying the route
     * @param automationLevel degree of automation associated with the step
     * @param automationScope automation scope
     * @param reason explanation of the decision
     * @param costEstimate cost estimate
     * @param risks concerns to consider before carrying out the plan
     * @param tools tools included in the delivery decision
     * @param items items
     * @param manualSteps work that still requires a person
     * @param tracks tracks
     * @param sharedResources shared resources
     */
    private ProvisioningDecision(
            ProvisioningRoute route,
            String routeKey,
            AutomationLevel automationLevel,
            String automationScope,
            String reason,
            String costEstimate,
            List<String> risks,
            List<ToolDecision> tools,
            List<PlanItemDecision> items,
            List<ManualStepDecision> manualSteps,
            List<TrackDecision> tracks,
            List<SharedResourceDecision> sharedResources
    ) {
        this.route = route;
        this.routeKey = routeKey;
        this.automationLevel = automationLevel;
        this.automationScope = automationScope;
        this.reason = reason;
        this.costEstimate = costEstimate;
        this.risks = List.copyOf(risks);
        this.tools = List.copyOf(tools);
        this.items = List.copyOf(items);
        this.manualSteps = List.copyOf(manualSteps);
        this.tracks = List.copyOf(tracks);
        this.sharedResources = List.copyOf(sharedResources);
    }

    /**
     * Combines per-track decisions and shared resources into the final delivery plan.
     *
     * @param tracks tracks
     * @param planManualSteps plan manual steps
     * @return the provisioning decision
     * @throws IllegalArgumentException when validation reports “At least one provisioning track is required”
     */
    public static ProvisioningDecision compose(
            List<TrackDecision> tracks,
            List<ManualStepDecision> planManualSteps
    ) {
        if (tracks == null || tracks.isEmpty()) {
            throw new IllegalArgumentException("At least one provisioning track is required");
        }
        List<TrackDecision> orderedTracks = tracks.stream()
                .sorted(java.util.Comparator.comparingInt(track -> track.track().ordinal()))
                .toList();
        ProvisioningRoute route = orderedTracks.size() == 1
                ? orderedTracks.getFirst().route()
                : ProvisioningRoute.COMPOSITE;
        String routeKey = orderedTracks.size() == 1
                ? route.name()
                : "COMPOSITE[" + orderedTracks.stream()
                .map(track -> track.route().name())
                .collect(java.util.stream.Collectors.joining(",")) + "]";

        List<ToolDecision> flattenedTools = flattenTools(orderedTracks);
        List<SharedResourceDecision> shared = buildSharedResources(orderedTracks, flattenedTools);
        List<PlanItemDecision> items = orderedTracks.stream().flatMap(track -> track.items().stream()).toList();
        List<ManualStepDecision> manualSteps = deduplicateManualSteps(orderedTracks, planManualSteps);
        List<String> risks = orderedTracks.stream()
                .flatMap(track -> track.risks().stream())
                .distinct()
                .toList();

        return new ProvisioningDecision(
                route,
                routeKey,
                weakestLevel(orderedTracks),
                "partial",
                orderedTracks.stream()
                        .map(track -> track.track().name() + ": " + track.reason())
                        .collect(java.util.stream.Collectors.joining(" ")),
                "Dry-run only. Provider plans, subscriptions and usage costs require admin approval.",
                risks,
                flattenedTools,
                items,
                manualSteps,
                orderedTracks,
                shared
        );
    }

    /**
     * Collects the selected tools across all service tracks.
     *
     * @param tracks tracks
     * @return the tool decision items
     */
    private static List<ToolDecision> flattenTools(List<TrackDecision> tracks) {
        Map<String, ToolDecision> decisions = new LinkedHashMap<>();
        tracks.stream().flatMap(track -> track.tools().stream()).forEach(candidate -> {
            ToolDecision current = decisions.get(candidate.key());
            if (current == null || "selected".equals(candidate.selectionState())) {
                decisions.put(candidate.key(), candidate);
            }
        });
        return List.copyOf(decisions.values());
    }

    /**
     * Builds shared resources. Copies the list before returning it.
     *
     * @param tracks tracks
     * @param flattenedTools flattened tools
     * @return the shared resource decision items
     */
    private static List<SharedResourceDecision> buildSharedResources(
            List<TrackDecision> tracks,
            List<ToolDecision> flattenedTools
    ) {
        List<SharedResourceDecision> result = new ArrayList<>();
        for (ToolDecision tool : flattenedTools) {
            if (!SHARED_KEYS.contains(tool.key())) {
                continue;
            }
            List<ProvisioningTrack> users = tracks.stream()
                    .filter(track -> track.tools().stream().anyMatch(candidate ->
                            candidate.key().equals(tool.key()) && "selected".equals(candidate.selectionState())))
                    .map(TrackDecision::track)
                    .toList();
            result.add(new SharedResourceDecision(
                    tool.key(),
                    tool.displayName(),
                    tool.selectionState(),
                    tool.automationLevel(),
                    tool.required(),
                    users.isEmpty()
                            ? tool.reason()
                            : "Shared once across tracks: " + users,
                    users
            ));
        }
        return List.copyOf(result);
    }

    /**
     * Removes duplicate manual steps. Copies the list before returning it.
     *
     * @param tracks tracks
     * @param planManualSteps plan manual steps
     * @return the manual step decision items
     */
    private static List<ManualStepDecision> deduplicateManualSteps(
            List<TrackDecision> tracks,
            List<ManualStepDecision> planManualSteps
    ) {
        Map<String, ManualStepDecision> steps = new LinkedHashMap<>();
        if (planManualSteps != null) {
            planManualSteps.forEach(step -> steps.putIfAbsent(step.providerKey(), step));
        }
        tracks.stream().flatMap(track -> track.manualSteps().stream())
                .forEach(step -> steps.putIfAbsent(step.providerKey(), step));
        return List.copyOf(steps.values());
    }

    /**
     * Selects the least automated level across the chosen tracks so the combined plan reflects work still needed from a person.
     *
     * @param tracks tracks
     * @return the automation level
     */
    private static AutomationLevel weakestLevel(List<TrackDecision> tracks) {
        Set<AutomationLevel> levels = new LinkedHashSet<>();
        tracks.forEach(track -> levels.add(track.automationLevel()));
        if (levels.contains(AutomationLevel.M)) return AutomationLevel.M;
        if (levels.contains(AutomationLevel.A1)) return AutomationLevel.A1;
        if (levels.contains(AutomationLevel.A2)) return AutomationLevel.A2;
        return AutomationLevel.A3;
    }

    /**
     * Returns the delivery route selected by the planning policy for this provisioning decision.
     *
     * @return delivery route selected by the planning policy
     */
    public ProvisioningRoute route() { return route; }
    /**
     * Returns the key identifying the route for this provisioning decision.
     *
     * @return key identifying the route
     */
    public String routeKey() { return routeKey; }
    /**
     * Returns the degree of automation associated with the step for this provisioning decision.
     *
     * @return degree of automation associated with the step
     */
    public AutomationLevel automationLevel() { return automationLevel; }
    /**
     * Returns the automation scope for this provisioning decision.
     *
     * @return automation scope
     */
    public String automationScope() { return automationScope; }
    /**
     * Returns the explanation of the decision for this provisioning decision.
     *
     * @return explanation of the decision
     */
    public String reason() { return reason; }
    /**
     * Returns the cost estimate for this provisioning decision.
     *
     * @return cost estimate
     */
    public String costEstimate() { return costEstimate; }
    /**
     * Returns the concerns to consider before carrying out the plan for this provisioning decision.
     *
     * @return concerns to consider before carrying out the plan
     */
    public List<String> risks() { return risks; }
    /**
     * Returns the tools included in the delivery decision for this provisioning decision.
     *
     * @return tools included in the delivery decision
     */
    public List<ToolDecision> tools() { return tools; }
    /**
     * Returns the items for this provisioning decision.
     *
     * @return items
     */
    public List<PlanItemDecision> items() { return items; }
    /**
     * Returns the work that still requires a person for this provisioning decision.
     *
     * @return work that still requires a person
     */
    public List<ManualStepDecision> manualSteps() { return manualSteps; }
    /**
     * Returns the tracks for this provisioning decision.
     *
     * @return tracks
     */
    public List<TrackDecision> tracks() { return tracks; }
    /**
     * Returns the shared resources for this provisioning decision.
     *
     * @return shared resources
     */
    public List<SharedResourceDecision> sharedResources() { return sharedResources; }

    /**
     * Groups the tool decision data used by provisioning decision.
     *
     * @param key key used to identify the item
     * @param displayName user-facing label
     * @param selectionState current selection state of the proposed tool
     * @param automationLevel degree of automation associated with the step
     * @param required whether the item is required
     * @param reason explanation of the decision
     */
    public record ToolDecision(
            String key,
            String displayName,
            String selectionState,
            AutomationLevel automationLevel,
            boolean required,
            String reason
    ) {}

    /**
     * Groups the plan item decision data used by provisioning decision.
     *
     * @param providerKey key identifying the integration provider
     * @param resourceType kind of external or internal resource
     * @param resourceName name assigned to the resource
     * @param action action being recorded
     * @param required whether the item is required
     * @param reason explanation of the decision
     */
    public record PlanItemDecision(
            String providerKey,
            String resourceType,
            String resourceName,
            String action,
            boolean required,
            String reason
    ) {}

    /**
     * Groups the manual step decision data used by provisioning decision.
     *
     * @param providerKey key identifying the integration provider
     * @param title short heading shown to a reader
     * @param reason explanation of the decision
     * @param required whether the item is required
     */
    public record ManualStepDecision(
            String providerKey,
            String title,
            String reason,
            boolean required
    ) {}
}
