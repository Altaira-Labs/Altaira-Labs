package com.altaira.backend.controller;

import com.altaira.backend.dto.commercial.CommercialFlowResponse;
import com.altaira.backend.dto.commercial.CreateCheckoutRequest;
import com.altaira.backend.security.AdminAccessService;
import com.altaira.backend.service.CommercialFlowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/** HTTP endpoints for commercial flow under {@code /api/v1}. */
@RestController
@RequestMapping("/api/v1")
public class CommercialFlowController {
    /** Commercial flow operations used by this component. */
    private final CommercialFlowService commercialFlowService;
    /** Admin access operations used by this component. */
    private final AdminAccessService adminAccessService;

    /**
     * Creates the commercial flow controller with its injected collaborators.
     *
     * @param commercialFlowService commercial flow operations used by this component
     * @param adminAccessService admin access operations used by this component
     */
    public CommercialFlowController(CommercialFlowService commercialFlowService, AdminAccessService adminAccessService) {
        this.commercialFlowService = commercialFlowService;
        this.adminAccessService = adminAccessService;
    }

    /**
     * GET {@code /api/v1/provisioning-plans/{planId}/commercial-flow}. Retrieves flow. Internal access is checked before delegation.
     *
     * @param planId identifier of the saved provisioning plan
     * @param internalToken credential for internal
     * @param adminToken credential for admin
     * @return flow
     */
    @GetMapping("/provisioning-plans/{planId}/commercial-flow")
    public CommercialFlowResponse getFlow(
            @PathVariable UUID planId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminToken
    ) {
        adminAccessService.requireAdminAccess(internalToken, adminToken);
        return commercialFlowService.getFlow(planId);
    }

    /**
     * POST {@code /api/v1/provisioning-plans/{planId}/payments/checkout}. Creates checkout. Internal access is checked before delegation.
     *
     * @param planId identifier of the saved provisioning plan
     * @param request input carrying create checkout
     * @param internalToken credential for internal
     * @param adminToken credential for admin
     * @return the commercial flow response
     */
    @PostMapping("/provisioning-plans/{planId}/payments/checkout")
    public CommercialFlowResponse createCheckout(
            @PathVariable UUID planId,
            @Valid @RequestBody CreateCheckoutRequest request,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminToken
    ) {
        adminAccessService.requireAdminAccess(internalToken, adminToken);
        return commercialFlowService.createCheckout(planId, request);
    }

    /**
     * POST {@code /api/v1/provisioning-plans/{planId}/payments/mock-confirm}. Confirms mock. Internal access is checked before delegation.
     *
     * @param planId identifier of the saved provisioning plan
     * @param internalToken credential for internal
     * @param adminToken credential for admin
     * @return the commercial flow response
     */
    @PostMapping("/provisioning-plans/{planId}/payments/mock-confirm")
    public CommercialFlowResponse confirmMock(
            @PathVariable UUID planId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminToken
    ) {
        adminAccessService.requireAdminAccess(internalToken, adminToken);
        return commercialFlowService.confirmMock(planId);
    }

    /**
     * POST {@code /api/v1/provisioning-plans/{planId}/commercial-flow/retry}. Retry activation. Internal access is checked before delegation.
     *
     * @param planId identifier of the saved provisioning plan
     * @param internalToken credential for internal
     * @param adminToken credential for admin
     * @return the commercial flow response
     */
    @PostMapping("/provisioning-plans/{planId}/commercial-flow/retry")
    public CommercialFlowResponse retryActivation(
            @PathVariable UUID planId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminToken
    ) {
        adminAccessService.requireAdminAccess(internalToken, adminToken);
        return commercialFlowService.retryActivation(planId);
    }

    /**
     * POST {@code /api/v1/provisioning-plans/{planId}/commercial-flow/notifications/retry}. Retry notifications. Internal access is checked before delegation.
     *
     * @param planId identifier of the saved provisioning plan
     * @param internalToken credential for internal
     * @param adminToken credential for admin
     * @return the commercial flow response
     */
    @PostMapping("/provisioning-plans/{planId}/commercial-flow/notifications/retry")
    public CommercialFlowResponse retryNotifications(
            @PathVariable UUID planId,
            @RequestHeader(name = "X-Internal-API-Token", required = false) String internalToken,
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String adminToken
    ) {
        adminAccessService.requireAdminAccess(internalToken, adminToken);
        return commercialFlowService.retryNotifications(planId);
    }

    /**
     * POST {@code /api/v1/payments/stripe/webhook}. Stripe webhook.
     *
     * @param payload data passed across this boundary
     * @param signature signature accompanying the event
     * @return the HTTP response for this operation
     */
    @PostMapping("/payments/stripe/webhook")
    public ResponseEntity<Map<String, Boolean>> stripeWebhook(
            @RequestBody String payload,
            @RequestHeader(name = "Stripe-Signature") String signature
    ) {
        commercialFlowService.processStripeWebhook(payload, signature);
        return ResponseEntity.ok(Map.of("received", true));
    }
}
