package com.altaira.backend.service;

import com.altaira.backend.dto.clientcrm.ClientCrmWebhookTokenResponse;
import com.altaira.backend.dto.clientcrm.CreateClientCrmWebhookTokenRequest;
import com.altaira.backend.entity.ClientCrmWebhookTokenEntity;
import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.exception.ResourceNotFoundException;
import com.altaira.backend.repository.ClientCrmWebhookTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

/** Coordinates client CRM webhook token operations in the backend. */
@Service
@Transactional
public class ClientCrmWebhookTokenService {

    /** Token prefix used by client CRM webhook token service. */
    private static final String TOKEN_PREFIX = "alw_";
    /** Default webhook path used by client CRM webhook token service. */
    private static final String DEFAULT_WEBHOOK_PATH = "/api/v1/client-crm/webhooks/leads";

    /** Persistence access for token. */
    private final ClientCrmWebhookTokenRepository tokenRepository;
    /** Client management operations used by this component. */
    private final ClientManagementService clientManagementService;
    /** Secure random. */
    private final SecureRandom secureRandom = new SecureRandom();
    /** URL for public webhook. */
    private final String publicWebhookUrl;

    /**
     * Creates the client CRM webhook token service with its injected collaborators.
     *
     * @param tokenRepository persistence access for token
     * @param clientManagementService client management operations used by this component
     * @param publicWebhookUrl URL for public webhook
     */
    public ClientCrmWebhookTokenService(
            ClientCrmWebhookTokenRepository tokenRepository,
            ClientManagementService clientManagementService,
            @Value("${altaira.client-crm.webhook.public-url:}") String publicWebhookUrl
    ) {
        this.tokenRepository = tokenRepository;
        this.clientManagementService = clientManagementService;
        this.publicWebhookUrl = publicWebhookUrl;
    }

    /**
     * Lists tokens.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @return the client CRM webhook token response items
     */
    @Transactional(readOnly = true)
    public List<ClientCrmWebhookTokenResponse> listTokens(UUID clientId) {
        ClientEntity client = clientManagementService.findClientEntity(clientId);
        return tokenRepository.findAllByClientOrderByCreatedAtDesc(client)
                .stream()
                .map(token -> map(token, null))
                .toList();
    }

    /**
     * Creates token. Persists changes through {@code tokenRepository}.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param request input carrying create client CRM webhook token
     * @return the client CRM webhook token response
     */
    public ClientCrmWebhookTokenResponse createToken(UUID clientId, CreateClientCrmWebhookTokenRequest request) {
        ClientEntity client = clientManagementService.findClientEntity(clientId);
        String apiKey = generateUniqueApiKey();

        ClientCrmWebhookTokenEntity token = new ClientCrmWebhookTokenEntity();
        token.setClient(client);
        token.setLabel(normalizeLabel(request == null ? null : request.getLabel()));
        token.setTokenHash(hashToken(apiKey));
        token.setTokenPrefix(apiKey.substring(0, Math.min(apiKey.length(), 16)));
        token.setActive(true);

        return map(tokenRepository.save(token), apiKey);
    }

    /**
     * Revokes token. Persists changes through {@code tokenRepository}.
     *
     * @param clientId identifier of the client whose workspace is in scope
     * @param tokenId identifier of the token
     * @return the client CRM webhook token response
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the referenced record cannot be found
     */
    public ClientCrmWebhookTokenResponse revokeToken(UUID clientId, UUID tokenId) {
        ClientEntity client = clientManagementService.findClientEntity(clientId);
        ClientCrmWebhookTokenEntity token = tokenRepository.findByIdAndClient(tokenId, client)
                .orElseThrow(() -> new ResourceNotFoundException("Client CRM webhook token", tokenId));

        token.setActive(false);
        token.setRevokedAt(Instant.now());
        return map(tokenRepository.save(token), null);
    }

    /**
     * Requires active token. Persists changes through {@code tokenRepository}.
     *
     * @param apiKey key identifying the api
     * @return the client CRM webhook token record
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (unauthorized)
     */
    public ClientCrmWebhookTokenEntity requireActiveToken(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Client CRM webhook API key is required");
        }

        ClientCrmWebhookTokenEntity token = tokenRepository.findByTokenHashAndActiveTrue(hashToken(apiKey.trim()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid client CRM webhook API key"));

        token.setLastUsedAt(Instant.now());
        return tokenRepository.save(token);
    }

    /**
     * Generates unique API key.
     *
     * @return the resulting text
     * @throws IllegalStateException when validation reports “Could not generate a unique CRM webhook API key”
     */
    private String generateUniqueApiKey() {
        for (int attempt = 0; attempt < 10; attempt++) {
            byte[] randomBytes = new byte[32];
            secureRandom.nextBytes(randomBytes);
            String apiKey = TOKEN_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

            if (!tokenRepository.existsByTokenHash(hashToken(apiKey))) {
                return apiKey;
            }
        }

        throw new IllegalStateException("Could not generate a unique CRM webhook API key");
    }

    /**
     * Normalizes label.
     *
     * @param label display label
     * @return the resulting text
     */
    private String normalizeLabel(String label) {
        if (label == null || label.isBlank()) {
            return "Website form";
        }

        String trimmed = label.trim();
        return trimmed.length() > 120 ? trimmed.substring(0, 120) : trimmed;
    }

    /**
     * Computes a SHA-256 digest for stored token lookup.
     *
     * @param token credential supplied for this operation
     * @return the resulting text
     * @throws IllegalStateException when validation reports “Could not hash CRM webhook API key”
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Could not hash CRM webhook API key", ex);
        }
    }

    /**
     * Builds {@code ClientCrmWebhookTokenResponse} from {@code ClientCrmWebhookTokenEntity}, selecting the fields used by the caller.
     *
     * @param token credential supplied for this operation
     * @param apiKey key identifying the api
     * @return the client CRM webhook token response
     */
    private ClientCrmWebhookTokenResponse map(ClientCrmWebhookTokenEntity token, String apiKey) {
        return new ClientCrmWebhookTokenResponse(
                token.getId(),
                token.getClient().getId(),
                token.getLabel(),
                token.getTokenPrefix(),
                token.isActive(),
                apiKey,
                publicWebhookUrl == null || publicWebhookUrl.isBlank() ? DEFAULT_WEBHOOK_PATH : publicWebhookUrl,
                token.getLastUsedAt(),
                token.getRevokedAt(),
                token.getCreatedAt(),
                token.getUpdatedAt()
        );
    }
}
