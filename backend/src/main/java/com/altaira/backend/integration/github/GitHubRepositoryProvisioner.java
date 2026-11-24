package com.altaira.backend.integration.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Minimal GitHub App adapter for ensuring one private organization repository.
 * Validation and an idempotent lookup prevent public or duplicate repositories.
 */
@Service
public class GitHubRepositoryProvisioner {

    /** Safe repository name used by GitHub repository provisioner. */
    private static final Pattern SAFE_REPOSITORY_NAME = Pattern.compile("[a-z0-9](?:[a-z0-9._-]{0,98}[a-z0-9])?");
    /** Safe organization name used by GitHub repository provisioner. */
    private static final Pattern SAFE_ORGANIZATION_NAME = Pattern.compile("[A-Za-z0-9](?:[A-Za-z0-9-]{0,37}[A-Za-z0-9])?");

    /** Properties. */
    private final GitHubAppProperties properties;
    /** Api client. */
    private final GitHubApiClient apiClient;
    /** Token provider. */
    private final GitHubInstallationTokenProvider tokenProvider;
    /** Jackson mapper used to read or write JSON. */
    private final ObjectMapper objectMapper;

    /**
     * Creates the GitHub repository provisioner with its injected collaborators.
     *
     * @param properties properties
     * @param apiClient api client
     * @param tokenProvider token provider
     * @param objectMapper Jackson mapper used to read or write JSON
     */
    public GitHubRepositoryProvisioner(
            GitHubAppProperties properties,
            GitHubApiClient apiClient,
            GitHubInstallationTokenProvider tokenProvider,
            ObjectMapper objectMapper
    ) {
        this.properties = properties;
        this.apiClient = apiClient;
        this.tokenProvider = tokenProvider;
        this.objectMapper = objectMapper;
    }

    /**
     * Ensures private repository.
     *
     * @param repositoryName repository name
     * @param description human-readable description
     * @return the GitHub repository provisioning result
     */
    public GitHubRepositoryProvisioningResult ensurePrivateRepository(
            String repositoryName,
            String description
    ) {
        requireExecutionGate();
        String safeName = validateRepositoryName(repositoryName);
        String organization = validateOrganization(properties.org());
        String installationToken = tokenProvider.createInstallationToken();

        JsonNode existing = findExistingRepository(organization, safeName, installationToken);
        if (existing != null) {
            return validatedResult(existing, false, true, organization, safeName);
        }

        ObjectNode request = objectMapper.createObjectNode();
        request.put("name", safeName);
        request.put("description", description == null ? "" : description.trim());
        request.put("private", true);
        request.put("auto_init", false);
        request.put("has_issues", true);

        try {
            JsonNode created = apiClient.post(
                    properties.apiUri("/orgs/" + organization + "/repos"),
                    installationToken,
                    request
            );
            return validatedResult(created, true, false, organization, safeName);
        } catch (GitHubIntegrationException exception) {
            if (!exception.hasStatusCode(422)) {
                throw exception;
            }

            JsonNode concurrentlyCreated = findExistingRepository(
                    organization,
                    safeName,
                    installationToken
            );
            if (concurrentlyCreated == null) {
                throw exception;
            }
            return validatedResult(concurrentlyCreated, false, true, organization, safeName);
        }
    }

    /**
     * Finds existing repository.
     *
     * @param organization organization
     * @param repositoryName repository name
     * @param installationToken credential for installation
     * @return the JSON node
     */
    private JsonNode findExistingRepository(String organization, String repositoryName, String installationToken) {
        try {
            return apiClient.get(
                    properties.apiUri("/repos/" + organization + "/" + repositoryName),
                    installationToken
            );
        } catch (GitHubIntegrationException exception) {
            if (exception.hasStatusCode(404)) {
                return null;
            }
            throw exception;
        }
    }

    /**
     * Checks that the returned repository belongs to the expected organization and is private before describing the outcome.
     *
     * @param repository repository
     * @param created whether a resource was created
     * @param reused whether an existing resource was used
     * @param expectedOrganization expected organization
     * @param expectedName expected name
     * @return the GitHub repository provisioning result
     */
    private GitHubRepositoryProvisioningResult validatedResult(
            JsonNode repository,
            boolean created,
            boolean reused,
            String expectedOrganization,
            String expectedName
    ) {
        String owner = repository.path("owner").path("login").asText("");
        String name = repository.path("name").asText("");
        boolean privateRepository = repository.path("private").asBoolean(false);

        if (!expectedOrganization.equalsIgnoreCase(owner)
                || !expectedName.equalsIgnoreCase(name)
                || !privateRepository) {
            throw GitHubIntegrationException.safe(
                    "GitHub repository response did not match the approved private repository target."
            );
        }

        return new GitHubRepositoryProvisioningResult(
                created,
                reused,
                repository.path("id").asText(""),
                repository.path("full_name").asText(expectedOrganization + "/" + expectedName),
                repository.path("html_url").asText(""),
                "private"
        );
    }

    /** Requires execution gate. */
    private void requireExecutionGate() {
        if (!properties.canMutateResources()) {
            throw GitHubIntegrationException.safe(
                    "GitHub repository creation is blocked unless the app and provisioning flags are enabled and dry-run is disabled."
            );
        }
    }

    /**
     * Validates repository name.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String validateRepositoryName(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (!SAFE_REPOSITORY_NAME.matcher(trimmed).matches()) {
            throw GitHubIntegrationException.safe("GitHub repository name is not an approved normalized name.");
        }
        return trimmed;
    }

    /**
     * Validates organization.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String validateOrganization(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (!SAFE_ORGANIZATION_NAME.matcher(trimmed).matches()) {
            throw GitHubIntegrationException.safe("GitHub organization name is invalid.");
        }
        return trimmed;
    }
}
