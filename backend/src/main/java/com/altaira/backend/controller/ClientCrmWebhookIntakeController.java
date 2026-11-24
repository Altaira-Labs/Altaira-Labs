package com.altaira.backend.controller;

import com.altaira.backend.dto.clientcrm.ClientCrmLeadResponse;
import com.altaira.backend.dto.clientcrm.CreateClientCrmLeadRequest;
import com.altaira.backend.service.ClientCrmLeadService;
import com.altaira.backend.service.ClientCrmWebhookTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** HTTP endpoints for client CRM webhook intake under {@code /api/v1/client-crm/webhooks}. */
@RestController
@RequestMapping("/api/v1/client-crm/webhooks")
@CrossOrigin(origins = "*", allowedHeaders = {
        "Content-Type",
        "X-Altaira-Webhook-Key",
        "Authorization"
})
public class ClientCrmWebhookIntakeController {

    /** Token operations used by this component. */
    private final ClientCrmWebhookTokenService tokenService;
    /** Lead operations used by this component. */
    private final ClientCrmLeadService leadService;

    /**
     * Creates the client CRM webhook intake controller with its injected collaborators.
     *
     * @param tokenService token operations used by this component
     * @param leadService lead operations used by this component
     */
    public ClientCrmWebhookIntakeController(
            ClientCrmWebhookTokenService tokenService,
            ClientCrmLeadService leadService
    ) {
        this.tokenService = tokenService;
        this.leadService = leadService;
    }

    /**
     * POST {@code /api/v1/client-crm/webhooks/leads}. Creates lead from webhook.
     *
     * @param request input carrying create client CRM lead
     * @param webhookKey key identifying the webhook
     * @param authorization authorization
     * @return the client CRM lead response
     */
    @PostMapping("/leads")
    @ResponseStatus(HttpStatus.CREATED)
    public ClientCrmLeadResponse createLeadFromWebhook(
            @Valid @RequestBody CreateClientCrmLeadRequest request,
            @RequestHeader(name = "X-Altaira-Webhook-Key", required = false) String webhookKey,
            @RequestHeader(name = "Authorization", required = false) String authorization
    ) {
        var token = tokenService.requireActiveToken(extractApiKey(webhookKey, authorization));
        return leadService.createClientLeadFromWebhook(token, request);
    }

    /**
     * Extracts api key.
     *
     * @param webhookKey key identifying the webhook
     * @param authorization authorization
     * @return the resulting text
     */
    private String extractApiKey(String webhookKey, String authorization) {
        if (webhookKey != null && !webhookKey.isBlank()) {
            return webhookKey.trim();
        }

        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length()).trim();
        }

        return "";
    }
}
