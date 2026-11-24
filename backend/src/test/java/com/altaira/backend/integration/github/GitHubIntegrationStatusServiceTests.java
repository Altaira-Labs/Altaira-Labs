package com.altaira.backend.integration.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises GitHub integration status service behavior with JUnit. Individual cases include {@code disabledStatusDoesNotCallGitHub} and {@code enabledStatusUsesGitHubAppFlowAgainstMockApi}.
 */
class GitHubIntegrationStatusServiceTests {

    /** Regression case: disabled status does not call GitHub. */
    @Test
    void disabledStatusDoesNotCallGitHub() {
        GitHubAppProperties properties = new GitHubAppProperties(
                false,
                false,
                true,
                "Altaira-Labs",
                "http://127.0.0.1:9",
                "",
                "",
                "",
                "",
                ""
        );
        GitHubIntegrationStatusService service = new GitHubIntegrationStatusService(
                properties,
                new GitHubAppJwtService(new ObjectMapper()),
                new ThrowingGitHubApiClient()
        );

        var status = service.getStatus();

        assertFalse(status.enabled());
        assertFalse(status.installationReachable());
        assertEquals(0, status.repositoriesVisible().count());
        assertTrue(status.errors().isEmpty());
    }

    /** Regression case: invalid private key base64 returns safe status error without calling GitHub. */
    @Test
    void invalidPrivateKeyBase64ReturnsSafeStatusErrorWithoutCallingGitHub() {
        GitHubAppProperties properties = new GitHubAppProperties(
                true,
                false,
                true,
                "Altaira-Labs",
                "http://127.0.0.1:9",
                "4339634",
                "Iv23liG1o7dXGqZDEdd8",
                "147629095",
                "not-base64-secret",
                ""
        );
        GitHubIntegrationStatusService service = new GitHubIntegrationStatusService(
                properties,
                new GitHubAppJwtService(new ObjectMapper()),
                new ThrowingGitHubApiClient()
        );

        var status = service.getStatus();

        assertTrue(status.enabled());
        assertTrue(status.appConfigured());
        assertTrue(status.installationIdConfigured());
        assertFalse(status.installationReachable());
        assertEquals(List.of("GitHub App private key could not be decoded. Check GITHUB_APP_PRIVATE_KEY_BASE64."), status.errors());
    }

    /**
     * Regression case: enabled status uses GitHub app flow against mock API.
     *
     * @throws Exception if the operation cannot complete
     */
    @Test
    void enabledStatusUsesGitHubAppFlowAgainstMockApi() throws Exception {
        AtomicInteger requestCount = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/", exchange -> {
            requestCount.incrementAndGet();
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            if ("GET".equals(method) && "/app/installations/147629095".equals(path)) {
                sendJson(exchange, 200, """
                        {
                          "id": 147629095,
                          "account": {"login": "Altaira-Labs"},
                          "permissions": {"contents": "write", "metadata": "read"}
                        }
                        """);
                return;
            }
            if ("POST".equals(method) && "/app/installations/147629095/access_tokens".equals(path)) {
                sendJson(exchange, 201, """
                        {
                          "token": "test-installation-token",
                          "permissions": {"contents": "write", "issues": "read", "metadata": "read"}
                        }
                        """);
                return;
            }
            if ("GET".equals(method) && "/orgs/Altaira-Labs".equals(path)) {
                sendJson(exchange, 200, """
                        {"login": "Altaira-Labs"}
                        """);
                return;
            }
            if ("GET".equals(method) && "/installation/repositories".equals(path)) {
                sendJson(exchange, 200, """
                        {
                          "repositories": [
                            {
                              "name": "Altaira-Labs",
                              "full_name": "Altaira-Labs/Altaira-Labs",
                              "private": false,
                              "default_branch": "altaira-labs-branding",
                              "owner": {"login": "Altaira-Labs"}
                            },
                            {
                              "name": "Other",
                              "full_name": "Other/Other",
                              "private": true,
                              "default_branch": "main",
                              "owner": {"login": "Other"}
                            }
                          ]
                        }
                        """);
                return;
            }
            sendJson(exchange, 404, "{\"message\":\"not found\"}");
        });
        server.start();

        try {
            GitHubAppProperties properties = new GitHubAppProperties(
                    true,
                    false,
                    true,
                    "Altaira-Labs",
                    "http://127.0.0.1:" + server.getAddress().getPort(),
                    "4339634",
                    "Iv23liG1o7dXGqZDEdd8",
                    "147629095",
                    testPrivateKeyBase64(),
                    ""
            );
            GitHubIntegrationStatusService service = new GitHubIntegrationStatusService(
                    properties,
                    new GitHubAppJwtService(new ObjectMapper()),
                    new GitHubApiClient(new ObjectMapper())
            );

            var status = service.getStatus();

            assertTrue(status.enabled());
            assertTrue(status.appConfigured());
            assertTrue(status.installationIdConfigured());
            assertTrue(status.installationReachable());
            assertEquals(1, status.repositoriesVisible().count());
            assertEquals("Altaira-Labs/Altaira-Labs", status.repositoriesVisible().repositories().get(0).fullName());
            assertEquals("write", status.permissions().get("contents"));
            assertEquals("read", status.permissions().get("issues"));
            assertTrue(status.errors().isEmpty());
            assertEquals(4, requestCount.get());
        } finally {
            server.stop(0);
        }
    }

    /** Implements throwing GitHub API client behavior for the GitHub package. */
    private static class ThrowingGitHubApiClient extends GitHubApiClient {
        /** Constructs {@code ThrowingGitHubApiClient} with its default state. */
        ThrowingGitHubApiClient() {
            super(new ObjectMapper());
        }

        /**
         * Fails the test immediately if the adapter attempts an outbound GET call.
         *
         * @param uri uri
         * @param bearerToken credential for bearer
         * @return the JSON node
         */
        @Override
        public JsonNode get(URI uri, String bearerToken) {
            throw new AssertionError("GitHub API must not be called in this test");
        }

        /**
         * Fails the test immediately if the adapter attempts an outbound POST call.
         *
         * @param uri uri
         * @param bearerToken credential for bearer
         * @param body request or response content
         * @return the JSON node
         */
        @Override
        public JsonNode post(URI uri, String bearerToken, JsonNode body) {
            throw new AssertionError("GitHub API must not be called in this test");
        }
    }

    /**
     * Provides an encoded fixture key for the local test integration.
     *
     * @return the resulting text
     * @throws Exception if the operation cannot complete
     */
    private static String testPrivateKeyBase64() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var keyPair = generator.generateKeyPair();
        String pem = "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.UTF_8)).encodeToString(keyPair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----\n";
        return Base64.getEncoder().encodeToString(pem.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Sends JSON.
     *
     * @param exchange exchange
     * @param status current state in the workflow
     * @param json JSON
     * @throws IOException if the underlying input or output cannot be completed
     */
    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(body);
        }
    }
}
