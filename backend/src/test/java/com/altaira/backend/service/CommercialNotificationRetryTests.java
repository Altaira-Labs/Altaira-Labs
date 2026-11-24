package com.altaira.backend.service;

import com.altaira.backend.dto.commercial.CommercialFlowResponse;
import com.altaira.backend.entity.CommercialFlowEntity;
import com.altaira.backend.entity.CommercialPaymentSessionEntity;
import com.altaira.backend.entity.LeadAssessmentEntity;
import com.altaira.backend.entity.LeadEntity;
import com.altaira.backend.entity.ProvisioningPlanEntity;
import com.altaira.backend.model.CommercialPaymentStatus;
import com.altaira.backend.model.ProvisioningPlanStatus;
import com.altaira.backend.repository.CommercialEmailLogRepository;
import com.altaira.backend.repository.CommercialFlowRepository;
import com.altaira.backend.repository.CommercialPaymentSessionRepository;
import com.altaira.backend.repository.LeadAssessmentRepository;
import com.altaira.backend.repository.LeadRepository;
import com.altaira.backend.repository.ProvisioningPlanRepository;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/** Exercises commercial notification retry behavior with JUnit. */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:commercial_notification_retry;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "altaira.commercial.email.enabled=true",
        "altaira.commercial.email.resend.api-key=re_test_local_only",
        "altaira.commercial.email.from=onboarding@resend.dev",
        "altaira.commercial.stripe.enabled=false",
        "altaira.commercial.stripe.mode=mock",
        "altaira.onboarding.email.enabled=false",
        "contact.email.enabled=false"
})
@ActiveProfiles("test")
@Transactional
class CommercialNotificationRetryTests {
    /** Request bodies used by commercial notification retry tests. */
    private static final List<String> REQUEST_BODIES = new CopyOnWriteArrayList<>();
    /** Resend stub. */
    private static HttpServer resendStub;

    /** Commercial flow operations used by this component. */
    @Autowired private CommercialFlowService commercialFlowService;
    /** Persistence access for lead. */
    @Autowired private LeadRepository leadRepository;
    /** Persistence access for assessment. */
    @Autowired private LeadAssessmentRepository assessmentRepository;
    /** Persistence access for plan. */
    @Autowired private ProvisioningPlanRepository planRepository;
    /** Persistence access for flow. */
    @Autowired private CommercialFlowRepository flowRepository;
    /** Persistence access for session. */
    @Autowired private CommercialPaymentSessionRepository sessionRepository;
    /** Persistence access for email log. */
    @Autowired private CommercialEmailLogRepository emailLogRepository;

    /**
     * Starts resend stub.
     *
     * @throws Exception if the operation cannot complete
     */
    @BeforeAll
    static void startResendStub() throws Exception {
        resendStub = HttpServer.create(new InetSocketAddress(0), 0);
        resendStub.createContext("/emails", exchange -> {
            REQUEST_BODIES.add(new String(exchange.getRequestBody().readAllBytes()));
            byte[] response = ("{\"id\":\"email_retry_" + REQUEST_BODIES.size() + "\"}").getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        resendStub.start();
    }

    /** Performs the stop resend stub operation. */
    @AfterAll
    static void stopResendStub() {
        resendStub.stop(0);
    }

    /**
     * Configures the test notification provider for the local retry scenario.
     *
     * @param registry registry
     */
    @DynamicPropertySource
    static void resendProperties(DynamicPropertyRegistry registry) {
        registry.add("altaira.commercial.email.resend.api-url",
                () -> "http://localhost:" + resendStub.getAddress().getPort() + "/emails");
    }

    /** Regression case: retries eligible emails once and exposes delivery audit. */
    @Test
    void retriesEligibleEmailsOnceAndExposesDeliveryAudit() {
        REQUEST_BODIES.clear();
        ProvisioningPlanEntity plan = confirmedFlowFixture();

        CommercialFlowResponse firstRetry = commercialFlowService.retryNotifications(plan.getId());

        assertThat(REQUEST_BODIES).hasSize(3);
        assertThat(firstRetry.emailDeliveries())
                .extracting(CommercialFlowResponse.EmailDelivery::emailType)
                .containsExactlyInAnyOrder("PAYMENT_REQUEST", "PAYMENT_CONFIRMED", "PLAN_SUMMARY");
        assertThat(firstRetry.emailDeliveries()).allMatch(delivery -> "SENT".equals(delivery.status()));
        assertThat(firstRetry.safeMessage()).contains("without duplicating");

        CommercialFlowResponse secondRetry = commercialFlowService.retryNotifications(plan.getId());

        assertThat(REQUEST_BODIES).hasSize(3);
        assertThat(emailLogRepository.count()).isEqualTo(3);
        assertThat(secondRetry.emailDeliveries()).hasSize(3);
        assertThat(secondRetry.safeMessage()).contains("No pending commercial email");
    }

    /**
     * Constructs a commercial flow whose payment has already been confirmed. Persists changes through {@code sessionRepository}.
     *
     * @return the provisioning plan record
     */
    private ProvisioningPlanEntity confirmedFlowFixture() {
        String suffix = UUID.randomUUID().toString();
        LeadEntity lead = new LeadEntity();
        lead.setFullName("Retry Test Owner");
        lead.setBusinessName("Retry Test Company");
        lead.setEmail("retry-" + suffix + "@example.com");
        lead.setIndustry("Clinics");
        lead.setServiceInterest("Web");
        lead.setGoals("Verify idempotent notification retry.");
        lead.setStatus("qualified");
        lead.setCreatedAt(Instant.now());
        lead = leadRepository.saveAndFlush(lead);

        LeadAssessmentEntity assessment = new LeadAssessmentEntity();
        assessment.setLead(lead);
        assessment.setFormKey("general");
        assessment.setSchemaVersion(1);
        assessment.setStatus("reviewed");
        assessment.setResponsesJson("{}");
        assessment.setRecommendedServicesJson("[\"web_seo\"]");
        assessment.setQualificationSummary("Retry test fixture.");
        assessment = assessmentRepository.saveAndFlush(assessment);

        ProvisioningPlanEntity plan = new ProvisioningPlanEntity();
        plan.setLead(lead);
        plan.setAssessment(assessment);
        plan.setRouteKey("WEB_STATIC");
        plan.setAutomationLevel("A2");
        plan.setAutomationScope("Web delivery");
        plan.setStatus(ProvisioningPlanStatus.APPROVED.value());
        plan.setDryRun(true);
        plan.setNormalizedRequirementsJson("{}");
        plan.setDecisionReason("Retry test fixture.");
        plan.setRisksJson("[]");
        plan.setCostEstimate("Test only");
        plan.setTracksJson("[]");
        plan.setSharedResourcesJson("[]");
        plan = planRepository.saveAndFlush(plan);

        CommercialFlowEntity flow = new CommercialFlowEntity();
        flow.setPlan(plan);
        flow.setLead(lead);
        flow.setPaymentStatus(CommercialPaymentStatus.PAYMENT_CONFIRMED.name());
        flow.setPaymentConfirmedAt(Instant.now());
        flow = flowRepository.saveAndFlush(flow);

        CommercialPaymentSessionEntity session = new CommercialPaymentSessionEntity();
        session.setFlow(flow);
        session.setProviderMode("test");
        session.setStatus(CommercialPaymentStatus.PAYMENT_CONFIRMED.name());
        session.setAmountMinor(150000);
        session.setCurrency("EUR");
        session.setCustomerEmail(lead.getEmail());
        session.setDescription("Retry test payment");
        session.setProviderSessionId("cs_test_" + suffix);
        session.setCheckoutUrl("https://checkout.stripe.test/retry-" + suffix);
        session.setIdempotencyKey("retry-test:" + suffix);
        session.setConfirmedAt(Instant.now());
        sessionRepository.saveAndFlush(session);
        return plan;
    }
}
