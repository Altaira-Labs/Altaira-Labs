package com.altaira.backend.service;

import com.altaira.backend.dto.auth.GoogleClientLoginRequest;
import com.altaira.backend.dto.auth.LoginResponse;
import com.altaira.backend.entity.AppUserEntity;
import com.altaira.backend.model.SecurityEventType;
import com.altaira.backend.model.UserRole;
import com.altaira.backend.repository.AppUserRepository;
import com.altaira.backend.repository.ClientUserAccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;

/** Invitation-aware Google sign-in. Google identity does not bypass workspace entitlement. */
@Service
@Transactional
public class ClientGoogleAuthService {

    /** Persistence access for app user. */
    private final AppUserRepository appUserRepository;
    /** Persistence access for client user access. */
    private final ClientUserAccessRepository clientUserAccessRepository;
    /** Auth operations used by this component. */
    private final AuthService authService;
    /** Security event operations used by this component. */
    private final SecurityEventService securityEventService;
    /** Jackson mapper used to read or write JSON. */
    private final ObjectMapper objectMapper;
    /** Identifier of the Google client. */
    private final String googleClientId;
    /** URL for token info. */
    private final URI tokenInfoUrl;
    /** Client used to send the outbound HTTP request. */
    private final HttpClient httpClient;

    /**
     * Creates the client Google auth service with its injected collaborators.
     *
     * @param appUserRepository persistence access for app user
     * @param clientUserAccessRepository persistence access for client user access
     * @param authService auth operations used by this component
     * @param securityEventService security event operations used by this component
     * @param objectMapper Jackson mapper used to read or write JSON
     * @param googleClientId identifier of the Google client
     * @param tokenInfoUrl URL for token info
     */
    @Autowired
    public ClientGoogleAuthService(
            AppUserRepository appUserRepository,
            ClientUserAccessRepository clientUserAccessRepository,
            AuthService authService,
            SecurityEventService securityEventService,
            ObjectMapper objectMapper,
            @Value("${altaira.client.google.client-id}") String googleClientId,
            @Value("${altaira.client.google.token-info-url}") String tokenInfoUrl
    ) {
        this(
                appUserRepository,
                clientUserAccessRepository,
                authService,
                securityEventService,
                objectMapper,
                googleClientId,
                tokenInfoUrl,
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build()
        );
    }

    /**
     * Creates the client Google auth service with its injected collaborators.
     *
     * @param appUserRepository persistence access for app user
     * @param clientUserAccessRepository persistence access for client user access
     * @param authService auth operations used by this component
     * @param securityEventService security event operations used by this component
     * @param objectMapper Jackson mapper used to read or write JSON
     * @param googleClientId identifier of the Google client
     * @param tokenInfoUrl URL for token info
     * @param httpClient client used to send the outbound HTTP request
     */
    ClientGoogleAuthService(
            AppUserRepository appUserRepository,
            ClientUserAccessRepository clientUserAccessRepository,
            AuthService authService,
            SecurityEventService securityEventService,
            ObjectMapper objectMapper,
            String googleClientId,
            String tokenInfoUrl,
            HttpClient httpClient
    ) {
        this.appUserRepository = appUserRepository;
        this.clientUserAccessRepository = clientUserAccessRepository;
        this.authService = authService;
        this.securityEventService = securityEventService;
        this.objectMapper = objectMapper;
        this.googleClientId = googleClientId == null ? "" : googleClientId.trim();
        this.tokenInfoUrl = URI.create((tokenInfoUrl == null || tokenInfoUrl.isBlank()) ? "https://oauth2.googleapis.com/tokeninfo" : tokenInfoUrl.trim());
        this.httpClient = httpClient;
    }

    /**
     * Authenticates the supplied credentials for the supported sign-in flow.
     *
     * @param request input carrying Google client login
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @return the login response
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (service unavailable, conflict)
     */
    public LoginResponse login(GoogleClientLoginRequest request, String ipAddress, String userAgent) {
        if (googleClientId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Google client login is not configured");
        }

        GoogleIdentity identity = verifyGoogleCredential(request.getCredential());
        String email = identity.email();

        AppUserEntity user = appUserRepository.findByUsernameIgnoreCase(email)
                .orElseThrow(() -> unauthorizedGoogleLogin(email, ipAddress, userAgent, "Google email is not invited"));

        UserRole role = UserRole.parse(user.getRole());
        if (!user.isActive() || !role.isClientRole()) {
            throw unauthorizedGoogleLogin(email, ipAddress, userAgent, "Google email is not an active client user");
        }

        var activeAccesses = clientUserAccessRepository.findAllByUserAndActiveTrue(user);
        if (activeAccesses.isEmpty()) {
            throw unauthorizedGoogleLogin(email, ipAddress, userAgent, "Google email has no active client workspace access");
        }

        if (activeAccesses.size() > 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Multiple active client workspaces require explicit workspace selection"
            );
        }

        return authService.createSessionForUser(user, ipAddress, userAgent, "Client Google login");
    }

    /**
     * Verifies Google credential. Uses locale-independent case normalization.
     *
     * @param credential credential
     * @return the Google identity
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (bad request, unauthorized)
     */
    private GoogleIdentity verifyGoogleCredential(String credential) {
        if (credential == null || credential.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Google credential is required");
        }

        URI requestUri = URI.create(tokenInfoUrl + "?id_token=" + URLEncoder.encode(credential.trim(), StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder(requestUri)
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        JsonNode json;
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google credential");
            }
            json = objectMapper.readTree(response.body());
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Could not verify Google credential");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google credential verification interrupted");
        }

        String audience = text(json, "aud");
        if (!googleClientId.equals(audience)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google credential audience");
        }

        if (!"true".equalsIgnoreCase(text(json, "email_verified"))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google email is not verified");
        }

        String email = text(json, "email").trim().toLowerCase(Locale.ROOT);
        if (email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google credential does not include an email");
        }

        return new GoogleIdentity(email);
    }

    /**
     * Builds the response for a rejected Google sign-in attempt.
     *
     * @param email contact email address
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @param metadata additional context recorded with the event
     * @return the response status exception
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (unauthorized)
     */
    private ResponseStatusException unauthorizedGoogleLogin(String email, String ipAddress, String userAgent, String metadata) {
        securityEventService.record(
                SecurityEventType.LOGIN_FAILED,
                null,
                email,
                false,
                ipAddress,
                userAgent,
                metadata
        );
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google account is not invited to a client workspace");
    }

    /**
     * Reads a JSON field as text, using an empty string for a missing or null value.
     *
     * @param json JSON
     * @param fieldName field name
     * @return the resulting text
     */
    private String text(JsonNode json, String fieldName) {
        JsonNode value = json == null ? null : json.get(fieldName);
        return value == null || value.isNull() ? "" : value.asText("");
    }

    /**
     * Groups the Google identity data used by client Google auth service.
     *
     * @param email contact email address
     */
    private record GoogleIdentity(String email) {}
}
