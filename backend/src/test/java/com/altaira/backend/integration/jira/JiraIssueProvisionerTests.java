package com.altaira.backend.integration.jira;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises Jira issue provisioner behavior with JUnit. Individual cases include {@code dryRunGateBlocksBeforeAnyNetworkCall} and {@code refusesAmbiguousIdempotencyMatch}.
 */
class JiraIssueProvisionerTests {

    /** Jackson mapper used to read or write JSON. */
    private final ObjectMapper objectMapper = new ObjectMapper();
    /** Server. */
    private HttpServer server;

    /** Performs the stop server operation. */
    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    /** Regression case: dry run gate blocks before any network call. */
    @Test
    void dryRunGateBlocksBeforeAnyNetworkCall() {
        JiraIssueProvisioner provisioner = provisioner(properties("http://127.0.0.1:1", true));

        JiraIntegrationException exception = assertThrows(
                JiraIntegrationException.class,
                () -> provisioner.ensureTask("Discovery", "Approved task", List.of("web"), "altaira-demo")
        );

        assertTrue(exception.safeMessage().contains("blocked"));
    }

    /**
     * Regression case: creates missing task with adf and idempotency label.
     *
     * @throws Exception if the operation cannot complete
     */
    @Test
    void createsMissingTaskWithAdfAndIdempotencyLabel() throws Exception {
        AtomicInteger createRequests = new AtomicInteger();
        startServer(exchange -> {
            assertTrue(exchange.getRequestHeaders().getFirst("Authorization").startsWith("Basic "));
            if (exchange.getRequestURI().getPath().endsWith("/search/jql")) {
                respond(exchange, 200, "{\"issues\":[]}");
                return;
            }

            createRequests.incrementAndGet();
            var request = objectMapper.readTree(exchange.getRequestBody());
            assertEquals("AL", request.path("fields").path("project").path("key").asText());
            assertEquals("Discovery", request.path("fields").path("summary").asText());
            assertEquals("Task", request.path("fields").path("issuetype").path("name").asText());
            assertEquals("doc", request.path("fields").path("description").path("type").asText());
            assertTrue(request.path("fields").path("labels").toString().contains("altaira-demo"));
            respond(exchange, 201, "{\"id\":\"10001\",\"key\":\"AL-12\"}");
        });

        JiraIssueProvisioningResult result = liveProvisioner().ensureTask(
                "Discovery",
                "Approved task",
                List.of("web"),
                "altaira-demo"
        );

        assertTrue(result.created());
        assertFalse(result.reused());
        assertEquals("AL-12", result.issueKey());
        assertTrue(result.issueUrl().endsWith("/browse/AL-12"));
        assertEquals(1, createRequests.get());
    }

    /**
     * Regression case: reuses single issue and does not create duplicate.
     *
     * @throws Exception if the operation cannot complete
     */
    @Test
    void reusesSingleIssueAndDoesNotCreateDuplicate() throws Exception {
        AtomicInteger createRequests = new AtomicInteger();
        startServer(exchange -> {
            if (exchange.getRequestURI().getPath().endsWith("/issue")) {
                createRequests.incrementAndGet();
            }
            respond(exchange, 200, existingIssues(1));
        });

        JiraIssueProvisioningResult result = liveProvisioner().ensureTask(
                "Discovery",
                "Approved task",
                List.of("web"),
                "altaira-demo"
        );

        assertFalse(result.created());
        assertTrue(result.reused());
        assertEquals("AL-12", result.issueKey());
        assertEquals(0, createRequests.get());
    }

    /**
     * Regression case: refuses ambiguous idempotency match.
     *
     * @throws Exception if the operation cannot complete
     */
    @Test
    void refusesAmbiguousIdempotencyMatch() throws Exception {
        startServer(exchange -> respond(exchange, 200, existingIssues(2)));

        JiraIntegrationException exception = assertThrows(
                JiraIntegrationException.class,
                () -> liveProvisioner().ensureTask(
                        "Discovery",
                        "Approved task",
                        List.of("web"),
                        "altaira-demo"
                )
        );

        assertTrue(exception.safeMessage().contains("Multiple Jira issues"));
    }

    /**
     * Constructs the execution-capable adapter for a test using the local provider stub.
     *
     * @return the Jira issue provisioner
     */
    private JiraIssueProvisioner liveProvisioner() {
        return provisioner(properties("http://127.0.0.1:" + server.getAddress().getPort(), false));
    }

    /**
     * Builds the Jira adapter used in this test scenario.
     *
     * @param properties properties
     * @return the Jira issue provisioner
     */
    private JiraIssueProvisioner provisioner(JiraProperties properties) {
        return new JiraIssueProvisioner(
                properties,
                new JiraApiClient(properties, objectMapper, HttpClient.newHttpClient()),
                objectMapper
        );
    }

    /**
     * Builds the Jira properties fixture used by the Jira issue provisioner tests.
     *
     * @param baseUrl URL for base
     * @param dryRun whether the operation is being simulated
     * @return the Jira properties
     */
    private JiraProperties properties(String baseUrl, boolean dryRun) {
        return new JiraProperties(
                true,
                true,
                dryRun,
                baseUrl,
                "automation@example.com",
                "test-api-token",
                "AL",
                "Task"
        );
    }

    /**
     * Starts server.
     *
     * @param handler handler
     * @throws IOException if the underlying input or output cannot be completed
     */
    private void startServer(ExchangeHandler handler) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            try {
                handler.handle(exchange);
            } catch (Throwable throwable) {
                respond(exchange, 500, "{\"message\":\"test handler failed\"}");
            }
        });
        server.start();
    }

    /**
     * Writes the HTTP status and response body returned by the local test server.
     *
     * @param exchange exchange
     * @param status current state in the workflow
     * @param body request or response content
     * @throws IOException if the underlying input or output cannot be completed
     */
    private void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    /**
     * Builds the stubbed response representing issues that already exist.
     *
     * @param count number of matching items
     * @return the resulting text
     */
    private String existingIssues(int count) {
        String issue = """
                {"id":"10001","key":"AL-12","fields":{"summary":"Discovery","project":{"key":"AL"}}}
                """;
        return count == 1
                ? "{\"issues\":[" + issue + "]}"
                : "{\"issues\":[" + issue + "," + issue.replace("10001", "10002").replace("AL-12", "AL-13") + "]}";
    }

    /** Contract implemented by exchange handler components. */
    @FunctionalInterface
    private interface ExchangeHandler {
        /**
         * Handles an HTTP exchange captured by the test server.
         *
         * @param exchange exchange
         * @throws Exception if the operation cannot complete
         */
        void handle(HttpExchange exchange) throws Exception;
    }
}
