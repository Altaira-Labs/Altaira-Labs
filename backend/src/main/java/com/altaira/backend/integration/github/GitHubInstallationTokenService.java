package com.altaira.backend.integration.github;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

/** Obtains installation access tokens for the configured GitHub App. */
@Service
public class GitHubInstallationTokenService implements GitHubInstallationTokenProvider {

    /** Properties. */
    private final GitHubAppProperties properties;
    /** JWT operations used by this component. */
    private final GitHubAppJwtService jwtService;
    /** Api client. */
    private final GitHubApiClient apiClient;

    /**
     * Creates the GitHub installation token service with its injected collaborators.
     *
     * @param properties properties
     * @param jwtService JWT operations used by this component
     * @param apiClient api client
     */
    public GitHubInstallationTokenService(
            GitHubAppProperties properties,
            GitHubAppJwtService jwtService,
            GitHubApiClient apiClient
    ) {
        this.properties = properties;
        this.jwtService = jwtService;
        this.apiClient = apiClient;
    }

    /**
     * Creates installation token.
     *
     * @return the resulting text
     */
    @Override
    public String createInstallationToken() {
        if (!properties.canAttemptConnection()) {
            throw GitHubIntegrationException.missingConfiguration(
                    "GitHub App credentials and installation ID are required."
            );
        }

        String jwt = jwtService.createJwt(properties);
        JsonNode response = apiClient.post(
                properties.apiUri("/app/installations/" + properties.installationId() + "/access_tokens"),
                jwt,
                null
        );
        String token = response.path("token").asText("");
        if (token.isBlank()) {
            throw GitHubIntegrationException.safe("GitHub installation token response did not include a token.");
        }
        return token;
    }
}
