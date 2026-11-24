package com.altaira.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises GitHub integration endpoint behavior with JUnit. Individual cases include {@code githubStatusIsAdminProtectedAndDoesNotExposeSecretsWhenDisabled} and {@code liveJiraExecutionRequiresAdminAndRemainsBlockedByDefault}.
 */
@SpringBootTest(properties = {
        "altaira.contact.email.enabled=false"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GitHubIntegrationEndpointTests {

    /** Internal token used by GitHub integration endpoint tests. */
    private static final String INTERNAL_TOKEN = "test-internal-token";

    /** Mock mvc. */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Regression case: GitHub status is admin protected and does not expose secrets when disabled.
     *
     * @throws Exception if the operation cannot complete
     */
    @Test
    void githubStatusIsAdminProtectedAndDoesNotExposeSecretsWhenDisabled() throws Exception {
        mockMvc.perform(get("/api/v1/integrations/github/status"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/integrations/github/status")
                        .header("X-Internal-API-Token", INTERNAL_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.provisioningEnabled").value(false))
                .andExpect(jsonPath("$.dryRun").value(true))
                .andExpect(jsonPath("$.org").value("Altaira-Labs"))
                .andExpect(jsonPath("$.appConfigured").value(false))
                .andExpect(jsonPath("$.installationIdConfigured").value(false))
                .andExpect(jsonPath("$.installationReachable").value(false))
                .andExpect(jsonPath("$.repositoriesVisible.count").value(0))
                .andExpect(content().string(not(containsString("privateKey"))))
                .andExpect(content().string(not(containsString("private-key"))))
                .andExpect(content().string(not(containsString("jwt"))))
                .andExpect(content().string(not(containsString("token"))));
    }

    /**
     * Regression case: GitHub dry run returns provisioning plan without creating resources.
     *
     * @throws Exception if the operation cannot complete
     */
    @Test
    void githubDryRunReturnsProvisioningPlanWithoutCreatingResources() throws Exception {
        mockMvc.perform(post("/api/v1/integrations/github/provisioning/dry-run")
                        .header("X-Internal-API-Token", INTERNAL_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectName": "Booking MVP",
                                  "clientName": "Cafe Central",
                                  "description": "Safe dry-run plan only",
                                  "privateRepository": true,
                                  "serviceKeys": ["web_seo", "booking"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dryRun").value(true))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.provisioningEnabled").value(false))
                .andExpect(jsonPath("$.executionAllowed").value(false))
                .andExpect(jsonPath("$.org").value("Altaira-Labs"))
                .andExpect(jsonPath("$.repoToCreate").value("Altaira-Labs/cafe-central-booking-mvp"))
                .andExpect(jsonPath("$.normalizedName").value("cafe-central-booking-mvp"))
                .andExpect(jsonPath("$.visibility").value("private"))
                .andExpect(jsonPath("$.preparedSteps.createRepository.executed").value(false))
                .andExpect(jsonPath("$.preparedSteps.createInitialFiles.executed").value(false))
                .andExpect(jsonPath("$.preparedSteps.createLabels.executed").value(false))
                .andExpect(jsonPath("$.preparedSteps.createIssues.executed").value(false));
    }

    /**
     * Regression case: live repository execution requires admin and remains blocked by default.
     *
     * @throws Exception if the operation cannot complete
     */
    @Test
    void liveRepositoryExecutionRequiresAdminAndRemainsBlockedByDefault() throws Exception {
        String request = """
                {"confirmation":"CONFIRM_PRIVATE_REPOSITORY"}
                """;

        mockMvc.perform(post("/api/v1/provisioning-plans/00000000-0000-0000-0000-000000000001/providers/github/repository")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/provisioning-plans/00000000-0000-0000-0000-000000000001/providers/github/repository")
                        .header("X-Internal-API-Token", INTERNAL_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(containsString("writes are disabled")))
                .andExpect(content().string(not(containsString("privateKey"))))
                .andExpect(content().string(not(containsString("token"))));
    }

    /**
     * Regression case: live Jira execution requires admin and remains blocked by default.
     *
     * @throws Exception if the operation cannot complete
     */
    @Test
    void liveJiraExecutionRequiresAdminAndRemainsBlockedByDefault() throws Exception {
        String request = """
                {"confirmation":"CONFIRM_JIRA_ISSUES"}
                """;

        mockMvc.perform(post("/api/v1/provisioning-plans/00000000-0000-0000-0000-000000000001/providers/jira/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/provisioning-plans/00000000-0000-0000-0000-000000000001/providers/jira/issues")
                        .header("X-Internal-API-Token", INTERNAL_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(containsString("writes are disabled")))
                .andExpect(content().string(not(containsString("test-token"))))
                .andExpect(content().string(not(containsString("apiToken"))));
    }
}
