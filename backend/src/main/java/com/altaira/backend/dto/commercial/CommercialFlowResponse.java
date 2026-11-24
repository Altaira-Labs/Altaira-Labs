package com.altaira.backend.dto.commercial;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Fields the API returns for commercial flow.
 *
 * @param flowId identifier of the flow
 * @param planId identifier of the saved provisioning plan
 * @param leadId identifier of the source sales enquiry
 * @param clientId identifier of the client whose workspace is in scope
 * @param workspaceId identifier of the workspace
 * @param paymentStatus current payment state
 * @param clientStatus client status
 * @param workspaceStatus current workspace activation state
 * @param invitationStatus current invitation state
 * @param provisioningStatus current delivery preparation state
 * @param planApproved whether the commercial plan is approved
 * @param testMode whether the payment integration is in test mode
 * @param mockMode whether checkout is simulated locally
 * @param mockConfirmationAllowed whether a simulated payment can be confirmed manually
 * @param executionAllowed whether the plan allows an execution step
 * @param paymentSession checkout-session information for the plan
 * @param provisioningRun record of the delivery planning or execution attempt
 * @param emailDeliveries outcomes of commercial email delivery attempts
 * @param safeMessage message suitable for display in the interface
 * @param paymentConfirmedAt time associated with payment confirmed
 * @param activatedAt time the workspace became active
 */
public record CommercialFlowResponse(
        UUID flowId,
        UUID planId,
        UUID leadId,
        UUID clientId,
        UUID workspaceId,
        String paymentStatus,
        String clientStatus,
        String workspaceStatus,
        String invitationStatus,
        String provisioningStatus,
        boolean planApproved,
        boolean testMode,
        boolean mockMode,
        boolean mockConfirmationAllowed,
        boolean executionAllowed,
        PaymentSession paymentSession,
        ProvisioningRun provisioningRun,
        List<EmailDelivery> emailDeliveries,
        String safeMessage,
        Instant paymentConfirmedAt,
        Instant activatedAt
) {
    /**
     * Represents payment session in the commercial flow response payload.
     *
     * @param id unique identifier of the record
     * @param providerMode provider mode
     * @param status current state in the workflow
     * @param amountMinor checkout amount in the currency’s smallest unit
     * @param currency currency code for the payment
     * @param checkoutUrl URL for checkout
     * @param confirmedAt time confirmation was recorded
     */
    public record PaymentSession(
            UUID id,
            String providerMode,
            String status,
            long amountMinor,
            String currency,
            String checkoutUrl,
            Instant confirmedAt
    ) {}

    /**
     * Represents provisioning run in the commercial flow response payload.
     *
     * @param id unique identifier of the record
     * @param status current state in the workflow
     * @param dryRun whether the operation is being simulated
     * @param steps ordered steps of the operation
     */
    public record ProvisioningRun(
            UUID id,
            String status,
            boolean dryRun,
            List<ProvisioningStep> steps
    ) {}

    /**
     * Represents provisioning step in the commercial flow response payload.
     *
     * @param id unique identifier of the record
     * @param trackKey key identifying the track
     * @param provider integration adapter in use
     * @param action action being recorded
     * @param status current state in the workflow
     * @param manualActionRequired manual action required
     * @param safeError error detail suitable for showing to the caller
     * @param inputSummary input summary
     */
    public record ProvisioningStep(
            UUID id,
            String trackKey,
            String provider,
            String action,
            String status,
            boolean manualActionRequired,
            String safeError,
            Map<String, Object> inputSummary
    ) {}

    /**
     * Represents email delivery in the commercial flow response payload.
     *
     * @param id unique identifier of the record
     * @param emailType email type
     * @param recipient recipient
     * @param status current state in the workflow
     * @param safeError error detail suitable for showing to the caller
     * @param createdAt time the record was created
     * @param sentAt time associated with sent
     */
    public record EmailDelivery(
            UUID id,
            String emailType,
            String recipient,
            String status,
            String safeError,
            Instant createdAt,
            Instant sentAt
    ) {}
}
