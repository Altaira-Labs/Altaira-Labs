package com.altaira.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** Coordinates internal API token operations in the backend. */
@Service
public class InternalApiTokenService {

    /** Credential presented to the internal API boundary. */
    private final String internalApiToken;

    /**
     * Constructs {@code InternalApiTokenService} using the supplied internal API token.
     *
     * @param internalApiToken credential presented to the internal API boundary
     */
    public InternalApiTokenService(@Value("${altaira.internal-api-token}") String internalApiToken) {
        this.internalApiToken = internalApiToken;
    }

    /**
     * Requires valid token.
     *
     * @param providedToken credential for provided
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (unauthorized)
     */
    public void requireValidToken(String providedToken) {
        if (!isValidToken(providedToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid internal API token");
        }
    }

    /**
     * Checks whether this is valid token.
     *
     * @param providedToken credential for provided
     * @return true if the value is valid token
     */
    public boolean isValidToken(String providedToken) {
        if (providedToken == null || providedToken.isBlank() || internalApiToken == null || internalApiToken.isBlank()) {
            return false;
        }

        return MessageDigest.isEqual(
                providedToken.getBytes(StandardCharsets.UTF_8),
                internalApiToken.getBytes(StandardCharsets.UTF_8)
        );
    }
}
