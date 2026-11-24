package com.altaira.backend.provisioning;

import com.altaira.backend.model.AutomationLevel;

import java.util.List;

/**
 * Groups the shared resource decision data used by provisioning.
 *
 * @param key key used to identify the item
 * @param displayName user-facing label
 * @param selectionState current selection state of the proposed tool
 * @param automationLevel degree of automation associated with the step
 * @param required whether the item is required
 * @param reason explanation of the decision
 * @param usedByTracks used by tracks
 */
public record SharedResourceDecision(
        String key,
        String displayName,
        String selectionState,
        AutomationLevel automationLevel,
        boolean required,
        String reason,
        List<ProvisioningTrack> usedByTracks
) {
    /**
     * Captures an immutable copy of the list inputs for this shared resource decision.
     *
     * @param key key used to identify the item
     * @param displayName user-facing label
     * @param selectionState current selection state of the proposed tool
     * @param automationLevel degree of automation associated with the step
     * @param required whether the item is required
     * @param reason explanation of the decision
     * @param usedByTracks used by tracks
     */
    public SharedResourceDecision {
        usedByTracks = List.copyOf(usedByTracks);
    }
}
