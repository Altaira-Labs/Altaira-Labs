package com.altaira.backend.provisioning;

/** Defines the decision a track-specific planner must produce from the normalized client requirements. */
public interface TrackDecisionPolicy {
    /**
     * Identifies the single service track whose delivery rules this policy owns.
     *
     * @return the provisioning track
     */
    ProvisioningTrack track();
    /**
     * Determines whether this policy should participate for the supplied planning context.
     *
     * @param context context carrying provisioning decision
     * @return true when the check succeeds
     */
    boolean supports(ProvisioningDecisionContext context);
    /**
     * Produces the rule choice, reasons and proposed work for this service track.
     *
     * @param context context carrying provisioning decision
     * @return the track decision
     */
    TrackDecision decide(ProvisioningDecisionContext context);
}
