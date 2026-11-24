package com.altaira.backend.controller;

import com.altaira.backend.dto.auth.LoginResponse;
import com.altaira.backend.dto.clientinvitation.AcceptClientInvitationRequest;
import com.altaira.backend.service.ClientInvitationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** HTTP endpoints for client invitation under {@code /api/v1/client-invitations}. */
@RestController
@RequestMapping("/api/v1/client-invitations")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class ClientInvitationController {

    /** Client invitation operations used by this component. */
    private final ClientInvitationService clientInvitationService;

    /**
     * Creates the client invitation controller with its injected collaborators.
     *
     * @param clientInvitationService client invitation operations used by this component
     */
    public ClientInvitationController(ClientInvitationService clientInvitationService) {
        this.clientInvitationService = clientInvitationService;
    }

    /**
     * POST {@code /api/v1/client-invitations/accept}. Accepts invitation.
     *
     * @param request input carrying accept client invitation
     * @param httpRequest incoming HTTP request, including its headers
     * @return the HTTP response for this operation
     */
    @PostMapping("/accept")
    public ResponseEntity<LoginResponse> acceptInvitation(
            @Valid @RequestBody AcceptClientInvitationRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(clientInvitationService.acceptInvitation(
                request,
                clientIp(httpRequest),
                userAgent(httpRequest)
        ));
    }

    /**
     * Obtains the network address associated with the request.
     *
     * @param request input carrying http servlet
     * @return the resulting text
     */
    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * Reads the User-Agent header for the operation’s audit context.
     *
     * @param request input carrying http servlet
     * @return the resulting text
     */
    private String userAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
