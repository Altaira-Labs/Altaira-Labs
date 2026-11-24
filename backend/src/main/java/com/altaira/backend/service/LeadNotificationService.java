package com.altaira.backend.service;

import com.altaira.backend.entity.LeadEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Resend/SMTP contact notification. Secrets are never logged; provider failure is classified safely. */
@Service
public class LeadNotificationService {

    /** Logger for this component. */
    private static final Logger logger = LoggerFactory.getLogger(LeadNotificationService.class);
    /** Min timeout ms ({@code 250}). */
    private static final long MIN_TIMEOUT_MS = 250;
    /** Email provider smtp used by lead notification service. */
    private static final String EMAIL_PROVIDER_SMTP = "smtp";
    /** Email provider resend used by lead notification service. */
    private static final String EMAIL_PROVIDER_RESEND = "resend";

    /** Mail sender provider. */
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    /** Jackson mapper used to read or write JSON. */
    private final ObjectMapper objectMapper;
    /** Email executor. */
    private final ExecutorService emailExecutor;
    /** Client used to send the outbound HTTP request. */
    private final HttpClient httpClient;
    /** Whether the capability is enabled. */
    private final boolean enabled;
    /** Email provider. */
    private final String emailProvider;
    /** Notification to. */
    private final String notificationTo;
    /** Notification from. */
    private final String notificationFrom;
    /** Notification timeout ms. */
    private final long notificationTimeoutMs;
    /** Key identifying the resend API. */
    private final String resendApiKey;
    /** URL for resend API. */
    private final URI resendApiUrl;
    /** URL for admin dashboard. */
    private final String adminDashboardUrl;
    /** URL for brand logo. */
    private final String brandLogoUrl;
    /** URL for brand header image. */
    private final String brandHeaderImageUrl;
    /** Smtp host. */
    private final String smtpHost;
    /** Smtp port. */
    private final int smtpPort;
    /** Smtp username. */
    private final String smtpUsername;
    /** Whether smtp auth is enabled. */
    private final boolean smtpAuthEnabled;
    /** Whether smtp start tls is enabled. */
    private final boolean smtpStartTlsEnabled;
    /** Smtp start tls required. */
    private final boolean smtpStartTlsRequired;

    /**
     * Creates the lead notification service with its injected collaborators.
     *
     * @param mailSenderProvider mail sender provider
     * @param objectMapper Jackson mapper used to read or write JSON
     * @param enabled whether the capability is enabled
     * @param emailProvider email provider
     * @param notificationTo notification to
     * @param notificationFrom notification from
     * @param notificationTimeoutMs notification timeout ms
     * @param resendApiKey key identifying the resend API
     * @param resendApiUrl URL for resend API
     * @param adminDashboardUrl URL for admin dashboard
     * @param brandLogoUrl URL for brand logo
     * @param brandHeaderImageUrl URL for brand header image
     * @param smtpHost smtp host
     * @param smtpPort smtp port
     * @param smtpUsername smtp username
     * @param smtpAuthEnabled whether smtp auth is enabled
     * @param smtpStartTlsEnabled whether smtp start tls is enabled
     * @param smtpStartTlsRequired smtp start tls required
     */
    public LeadNotificationService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            ObjectMapper objectMapper,
            @Value("${altaira.contact.email.enabled:true}") boolean enabled,
            @Value("${altaira.contact.email.provider:smtp}") String emailProvider,
            @Value("${altaira.contact.email.to:altairalabs@gmail.com}") String notificationTo,
            @Value("${altaira.contact.email.from:}") String notificationFrom,
            @Value("${altaira.contact.email.timeout-ms:6000}") long notificationTimeoutMs,
            @Value("${altaira.contact.email.resend.api-key:}") String resendApiKey,
            @Value("${altaira.contact.email.resend.api-url:https://api.resend.com/emails}") String resendApiUrl,
            @Value("${altaira.contact.admin-dashboard-url:}") String adminDashboardUrl,
            @Value("${altaira.contact.brand-logo-url:}") String brandLogoUrl,
            @Value("${altaira.contact.brand-header-image-url:}") String brandHeaderImageUrl,
            @Value("${spring.mail.host:}") String smtpHost,
            @Value("${spring.mail.port:587}") int smtpPort,
            @Value("${spring.mail.username:}") String smtpUsername,
            @Value("${spring.mail.properties.mail.smtp.auth:true}") boolean smtpAuthEnabled,
            @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}") boolean smtpStartTlsEnabled,
            @Value("${spring.mail.properties.mail.smtp.starttls.required:true}") boolean smtpStartTlsRequired
    ) {
        this.mailSenderProvider = mailSenderProvider;
        this.objectMapper = objectMapper;
        this.emailExecutor = Executors.newVirtualThreadPerTaskExecutor();
        this.enabled = enabled;
        this.emailProvider = normalizeProvider(emailProvider);
        this.notificationTo = notificationTo;
        this.notificationFrom = notificationFrom;
        this.notificationTimeoutMs = Math.max(MIN_TIMEOUT_MS, notificationTimeoutMs);
        this.resendApiKey = resendApiKey == null ? "" : resendApiKey.trim();
        this.resendApiUrl = parseResendApiUrl(resendApiUrl);
        this.adminDashboardUrl = adminDashboardUrl == null ? "" : adminDashboardUrl.trim();
        this.brandLogoUrl = cleanImageUrl(brandLogoUrl);
        this.brandHeaderImageUrl = cleanImageUrl(brandHeaderImageUrl);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(this.notificationTimeoutMs))
                .build();
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpUsername = smtpUsername;
        this.smtpAuthEnabled = smtpAuthEnabled;
        this.smtpStartTlsEnabled = smtpStartTlsEnabled;
        this.smtpStartTlsRequired = smtpStartTlsRequired;

        logger.info(
                "Lead email notification config: enabled={}, provider={}, resendApiUrl={}, resendApiKeyConfigured={}, adminDashboardUrlConfigured={}, brandLogoUrlConfigured={}, brandHeaderImageUrlConfigured={}, smtpHost={}, smtpPort={}, smtpUsername={}, smtpAuthEnabled={}, smtpStartTlsEnabled={}, smtpStartTlsRequired={}, notificationFrom={}, notificationTo={}, timeoutMs={}",
                enabled,
                this.emailProvider,
                safeLogValue(this.resendApiUrl.toString()),
                !this.resendApiKey.isBlank(),
                !this.adminDashboardUrl.isBlank(),
                !this.brandLogoUrl.isBlank(),
                !this.brandHeaderImageUrl.isBlank(),
                safeLogValue(smtpHost),
                smtpPort,
                safeLogValue(smtpUsername),
                smtpAuthEnabled,
                smtpStartTlsEnabled,
                smtpStartTlsRequired,
                safeLogValue(notificationFrom),
                safeLogValue(notificationTo),
                this.notificationTimeoutMs
        );
    }

    /**
     * Sends lead created notification.
     *
     * @param lead sales enquiry in scope
     * @return the email notification result
     */
    public EmailNotificationResult sendLeadCreatedNotification(LeadEntity lead) {
        if (!enabled) {
            logger.info("Lead email notification skipped for lead {} because email notifications are disabled.", lead.getId());
            return EmailNotificationResult.notSent("Email notification is disabled.");
        }

        if (notificationTo == null || notificationTo.isBlank()) {
            logger.warn("Lead email notification recipient is blank for lead {}.", lead.getId());
            return EmailNotificationResult.notSent("Email notification recipient is not configured.");
        }

        Future<EmailNotificationResult> emailTask;
        if (EMAIL_PROVIDER_RESEND.equals(emailProvider)) {
            EmailNotificationResult configurationResult = validateResendConfiguration(lead);
            if (configurationResult != null) {
                return configurationResult;
            }
            emailTask = emailExecutor.submit(() -> sendResendEmail(lead));
        } else if (EMAIL_PROVIDER_SMTP.equals(emailProvider)) {
            JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
            if (mailSender == null) {
                logger.warn(
                        "Lead email notification is not configured for lead {}. provider={}, smtpHost={}, smtpPort={}, smtpUsername={}, notificationFrom={}, notificationTo={}",
                        lead.getId(),
                        emailProvider,
                        safeLogValue(smtpHost),
                        smtpPort,
                        safeLogValue(smtpUsername),
                        safeLogValue(notificationFrom),
                        safeLogValue(notificationTo)
                );
                return EmailNotificationResult.notSent("Email notification is not configured.");
            }
            emailTask = emailExecutor.submit(() -> sendSmtpEmail(mailSender, lead));
        } else {
            logger.warn("Lead email notification provider is unsupported for lead {}. provider={}", lead.getId(), safeLogValue(emailProvider));
            return EmailNotificationResult.notSent("Email provider is not supported. Check CONTACT_EMAIL_PROVIDER.");
        }

        try {
            return emailTask.get(notificationTimeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            emailTask.cancel(true);
            logger.warn(
                    "Lead email notification timed out after {} ms for lead {}. provider={}, resendApiUrl={}, smtpHost={}, smtpPort={}, smtpAuthEnabled={}, smtpStartTlsEnabled={}, smtpStartTlsRequired={}, smtpUsername={}, notificationFrom={}, notificationTo={}",
                    notificationTimeoutMs,
                    lead.getId(),
                    emailProvider,
                    safeLogValue(resendApiUrl.toString()),
                    safeLogValue(smtpHost),
                    smtpPort,
                    smtpAuthEnabled,
                    smtpStartTlsEnabled,
                    smtpStartTlsRequired,
                    safeLogValue(smtpUsername),
                    safeLogValue(notificationFrom),
                    safeLogValue(notificationTo)
            );
            return EmailNotificationResult.notSent("Email notification timed out; lead was saved.");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            logger.warn("Lead email notification was interrupted for lead {}", lead.getId(), ex);
            return EmailNotificationResult.notSent("Email notification was interrupted; lead was saved.");
        } catch (ExecutionException ex) {
            logger.warn("Lead email notification failed for lead {}", lead.getId(), ex.getCause());
            return EmailNotificationResult.notSent("Email notification could not be sent.");
        }
    }

    /**
     * Retrieves diagnostics.
     *
     * @return the resulting key–value pairs
     */
    public Map<String, Object> getDiagnostics() {
        Map<String, Object> diagnostics = new LinkedHashMap<>();
        diagnostics.put("enabled", enabled);
        diagnostics.put("provider", emailProvider);
        diagnostics.put("notificationTo", blankToNull(notificationTo));
        diagnostics.put("notificationFrom", blankToNull(notificationFrom));
        diagnostics.put("timeoutMs", notificationTimeoutMs);
        diagnostics.put("resendApiUrl", resendApiUrl.toString());
        diagnostics.put("resendApiKeyConfigured", !resendApiKey.isBlank());
        diagnostics.put("adminDashboardUrlConfigured", !adminDashboardUrl.isBlank());
        diagnostics.put("brandLogoUrlConfigured", !brandLogoUrl.isBlank());
        diagnostics.put("brandHeaderImageUrlConfigured", !brandHeaderImageUrl.isBlank());
        diagnostics.put("smtpHost", blankToNull(smtpHost));
        diagnostics.put("smtpPort", smtpPort);
        diagnostics.put("smtpUsername", blankToNull(smtpUsername));
        diagnostics.put("smtpAuthEnabled", smtpAuthEnabled);
        diagnostics.put("smtpStartTlsEnabled", smtpStartTlsEnabled);
        diagnostics.put("smtpStartTlsRequired", smtpStartTlsRequired);
        return diagnostics;
    }

    /** Performs the shutdown executor operation. */
    @PreDestroy
    void shutdownExecutor() {
        emailExecutor.shutdownNow();
    }

    /**
     * Sends smtp email.
     *
     * @param mailSender mail sender
     * @param lead sales enquiry in scope
     * @return the email notification result
     */
    private EmailNotificationResult sendSmtpEmail(JavaMailSender mailSender, LeadEntity lead) {
        try {
            logger.info(
                    "Sending lead email notification for lead {}. provider={}, smtpHost={}, smtpPort={}, smtpAuthEnabled={}, smtpStartTlsEnabled={}, smtpStartTlsRequired={}, smtpUsername={}, notificationFrom={}, notificationTo={}",
                    lead.getId(),
                    emailProvider,
                    safeLogValue(smtpHost),
                    smtpPort,
                    smtpAuthEnabled,
                    smtpStartTlsEnabled,
                    smtpStartTlsRequired,
                    safeLogValue(smtpUsername),
                    safeLogValue(notificationFrom),
                    safeLogValue(notificationTo)
            );

            SimpleMailMessage message = new SimpleMailMessage();
            if (notificationFrom != null && !notificationFrom.isBlank()) {
                message.setFrom(notificationFrom.trim());
            }
            message.setTo(notificationTo.trim());
            message.setReplyTo(cleanHeader(lead.getEmail()));
            message.setSubject("New Altaira Labs contact lead: " + cleanSubject(lead.getBusinessName()));
            message.setText(buildBody(lead));

            mailSender.send(message);
            logger.info("Lead email notification sent for lead {}", lead.getId());
            return EmailNotificationResult.success();
        } catch (MailException | IllegalArgumentException ex) {
            logger.warn(
                    "Lead email notification could not be sent for lead {}. provider={}, failureType={}, rootException={}, rootMessage={}, smtpHost={}, smtpPort={}, smtpAuthEnabled={}, smtpStartTlsEnabled={}, smtpStartTlsRequired={}, smtpUsername={}, notificationFrom={}, notificationTo={}",
                    lead.getId(),
                    emailProvider,
                    classifyFailure(ex),
                    rootCause(ex).getClass().getName(),
                    safeLogValue(rootMessage(ex)),
                    safeLogValue(smtpHost),
                    smtpPort,
                    smtpAuthEnabled,
                    smtpStartTlsEnabled,
                    smtpStartTlsRequired,
                    safeLogValue(smtpUsername),
                    safeLogValue(notificationFrom),
                    safeLogValue(notificationTo)
            );
            logger.debug("Lead email notification exception stack for lead {}", lead.getId(), ex);
            return EmailNotificationResult.notSent(describeFailure(ex));
        }
    }

    /**
     * Sends resend email.
     *
     * @param lead sales enquiry in scope
     * @return the email notification result
     */
    private EmailNotificationResult sendResendEmail(LeadEntity lead) {
        try {
            logger.info(
                    "Sending lead email notification for lead {}. provider={}, resendApiUrl={}, notificationFrom={}, notificationTo={}",
                    lead.getId(),
                    emailProvider,
                    safeLogValue(resendApiUrl.toString()),
                    safeLogValue(notificationFrom),
                    safeLogValue(notificationTo)
            );

            HttpRequest request = HttpRequest.newBuilder(resendApiUrl)
                    .timeout(Duration.ofMillis(notificationTimeoutMs))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(buildResendPayload(lead)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                logger.info("Lead email notification sent for lead {} through Resend. status={}", lead.getId(), response.statusCode());
                return EmailNotificationResult.success();
            }

            logger.warn(
                    "Lead email notification API request failed for lead {}. provider={}, status={}, resendApiUrl={}, notificationFrom={}, notificationTo={}, responseBody={}",
                    lead.getId(),
                    emailProvider,
                    response.statusCode(),
                    safeLogValue(resendApiUrl.toString()),
                    safeLogValue(notificationFrom),
                    safeLogValue(notificationTo),
                    safeLogValue(response.body())
            );
            return EmailNotificationResult.notSent(describeResendStatus(response.statusCode()));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            logger.warn("Lead email notification API request was interrupted for lead {}. provider={}", lead.getId(), emailProvider);
            return EmailNotificationResult.notSent("Email API request was interrupted; lead was saved.");
        } catch (IOException ex) {
            logger.warn(
                    "Lead email notification API connection failed for lead {}. provider={}, rootException={}, rootMessage={}, resendApiUrl={}, notificationFrom={}, notificationTo={}",
                    lead.getId(),
                    emailProvider,
                    rootCause(ex).getClass().getName(),
                    safeLogValue(rootMessage(ex)),
                    safeLogValue(resendApiUrl.toString()),
                    safeLogValue(notificationFrom),
                    safeLogValue(notificationTo)
            );
            logger.debug("Lead email notification API exception stack for lead {}", lead.getId(), ex);
            return EmailNotificationResult.notSent("Email API connection failed. Check RESEND_API_KEY, Resend sender verification and Render outbound HTTPS access.");
        } catch (IllegalArgumentException ex) {
            logger.warn(
                    "Lead email notification API request is invalid for lead {}. provider={}, rootMessage={}, resendApiUrl={}, notificationFrom={}, notificationTo={}",
                    lead.getId(),
                    emailProvider,
                    safeLogValue(rootMessage(ex)),
                    safeLogValue(resendApiUrl.toString()),
                    safeLogValue(notificationFrom),
                    safeLogValue(notificationTo)
            );
            return EmailNotificationResult.notSent("Email API request is invalid. Check CONTACT_NOTIFICATION_FROM, CONTACT_NOTIFICATION_TO and Resend configuration.");
        }
    }

    /**
     * Validates resend configuration.
     *
     * @param lead sales enquiry in scope
     * @return the email notification result
     */
    private EmailNotificationResult validateResendConfiguration(LeadEntity lead) {
        if (resendApiKey.isBlank()) {
            logger.warn("Lead email notification API key is not configured for lead {}. provider={}", lead.getId(), emailProvider);
            return EmailNotificationResult.notSent("Email API key is not configured. Check RESEND_API_KEY.");
        }

        if (notificationFrom == null || notificationFrom.isBlank()) {
            logger.warn("Lead email notification sender is blank for lead {}. provider={}", lead.getId(), emailProvider);
            return EmailNotificationResult.notSent("Email sender is not configured. Check CONTACT_NOTIFICATION_FROM.");
        }

        return null;
    }

    /**
     * Builds resend payload.
     *
     * @param lead sales enquiry in scope
     * @return the resulting text
     * @throws IllegalArgumentException when validation reports “Could not build Resend email payload”
     */
    private String buildResendPayload(LeadEntity lead) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("from", notificationFrom.trim());
        payload.put("to", List.of(notificationTo.trim()));

        String replyTo = cleanHeader(lead.getEmail());
        if (!replyTo.isBlank()) {
            payload.put("reply_to", replyTo);
        }

        payload.put("subject", "New Altaira Labs contact lead: " + cleanSubject(lead.getBusinessName()));
        payload.put("text", buildBody(lead));
        payload.put("html", buildHtmlBody(lead));

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Could not build Resend email payload", ex);
        }
    }

    /**
     * Interprets the mail provider’s HTTP response for delivery diagnostics.
     *
     * @param statusCode HTTP status returned by the request
     * @return the resulting text
     */
    private String describeResendStatus(int statusCode) {
        if (statusCode == 401 || statusCode == 403) {
            return "Email API authentication failed. Check RESEND_API_KEY.";
        }

        if (statusCode == 400 || statusCode == 422) {
            return "Email API rejected the request. Check CONTACT_NOTIFICATION_FROM, CONTACT_NOTIFICATION_TO and Resend domain verification.";
        }

        if (statusCode == 429) {
            return "Email API rate limit reached. Check Resend usage limits.";
        }

        if (statusCode >= 500) {
            return "Email API provider is temporarily unavailable. Check Resend status/logs.";
        }

        return "Email API request failed. Check Resend configuration and provider logs.";
    }

    /**
     * Builds the explanation for a failed delivery attempt.
     *
     * @param ex ex
     * @return the resulting text
     */
    private String describeFailure(Exception ex) {
        String failureType = classifyFailure(ex);

        if ("authentication_failed".equals(failureType)) {
            return "Email authentication failed. Check Render SMTP username/password or Google App Password.";
        }

        if ("connection_failed".equals(failureType)) {
            return "Email SMTP connection failed. Check Render SMTP host, port, STARTTLS and outbound access.";
        }

        if ("invalid_address".equals(failureType)) {
            return "Email address configuration is invalid. Check CONTACT_NOTIFICATION_TO and CONTACT_NOTIFICATION_FROM.";
        }

        return "Email notification could not be sent. Check Render SMTP environment variables and backend logs.";
    }

    /**
     * Groups the failed delivery into the category used by its diagnostics.
     *
     * @param ex ex
     * @return the resulting text
     */
    private String classifyFailure(Exception ex) {
        String details = failureDetails(ex);

        if (ex instanceof MailAuthenticationException
                || details.contains("authentication")
                || details.contains("authenticate")
                || details.contains("username")
                || details.contains("password")
                || details.contains("credentials")) {
            return "authentication_failed";
        }

        if (details.contains("connection")
                || details.contains("connect")
                || details.contains("timed out")
                || details.contains("timeout")
                || details.contains("refused")
                || details.contains("unknown host")) {
            return "connection_failed";
        }

        if (details.contains("invalid address") || details.contains("address failed")) {
            return "invalid_address";
        }

        return "unknown";
    }

    /**
     * Normalizes provider. Uses locale-independent case normalization.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String normalizeProvider(String value) {
        if (value == null || value.isBlank()) {
            return EMAIL_PROVIDER_SMTP;
        }

        return value.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Parses resend API URL.
     *
     * @param value stored or supplied representation
     * @return the uri
     */
    private URI parseResendApiUrl(String value) {
        String apiUrl = value == null || value.isBlank()
                ? "https://api.resend.com/emails"
                : value.trim();
        return URI.create(apiUrl);
    }

    /**
     * Collects diagnostic information about an unsuccessful delivery.
     *
     * @param ex ex
     * @return the resulting text
     */
    private String failureDetails(Exception ex) {
        return rootMessage(ex).toLowerCase(Locale.ROOT);
    }

    /**
     * Follows nested failures to their underlying cause.
     *
     * @param ex ex
     * @return the throwable
     */
    private Throwable rootCause(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }

        return root;
    }

    /**
     * Extracts the underlying failure message for diagnostics.
     *
     * @param ex ex
     * @return the resulting text
     */
    private String rootMessage(Throwable ex) {
        Throwable root = rootCause(ex);
        String message = root.getMessage();
        if (message == null || message.isBlank()) {
            message = ex.getMessage();
        }

        return message == null ? "" : message;
    }

    /**
     * Builds body.
     *
     * @param lead sales enquiry in scope
     * @return the resulting text
     */
    private String buildBody(LeadEntity lead) {
        String dashboardUrl = dashboardUrl(lead);
        String dashboardLine = dashboardUrl.isBlank()
                ? ""
                : "\nAdmin dashboard: " + dashboardUrl + "\n";

        return """
                New Altaira Labs contact request

                Lead ID: %s
                Name: %s
                Business: %s
                Email: %s
                Phone: %s
                Sector / context: %s
                Service interest: %s
                Created at: %s
                Status: %s

                Message / goals:
                %s

                Recommended next step:
                Review this request in the admin dashboard and schedule a discovery call.%s
                """.formatted(
                safe(lead.getId()),
                safe(lead.getFullName()),
                safe(lead.getBusinessName()),
                safe(lead.getEmail()),
                safe(lead.getPhone()),
                safe(lead.getIndustry()),
                safe(lead.getServiceInterest()),
                safe(lead.getCreatedAt()),
                safe(lead.getStatus()),
                safe(lead.getGoals()),
                dashboardLine
        );
    }

    /**
     * Builds html body.
     *
     * @param lead sales enquiry in scope
     * @return the resulting text
     */
    private String buildHtmlBody(LeadEntity lead) {
        String dashboardUrl = dashboardUrl(lead);
        String dashboardAction = dashboardUrl.isBlank()
                ? """
                  <p style="margin: 8px 0 0; color: #cbd5e1;">Review this request in the admin dashboard and schedule a discovery call.</p>
                  """
                : """
                  <p style="margin: 8px 0 18px; color: #cbd5e1;">Review this request in the admin dashboard and schedule a discovery call.</p>
                  <a href="%s" style="display: inline-block; padding: 13px 18px; background: #2563eb; color: #ffffff; text-decoration: none; font-weight: 700; border-radius: 0;">Open lead in dashboard</a>
                  """.formatted(escapeHtml(dashboardUrl));
        String logoMarkup = brandLogoUrl.isBlank()
                ? """
                  <div style="font-size: 18px; letter-spacing: 0.18em; font-weight: 700; color: #ffffff;">ALTAIRA LABS</div>
                  """
                : """
                  <img src="%s" width="180" alt="Altaira Labs" style="display: block; width: 180px; max-width: 180px; height: auto; border: 0;">
                  """.formatted(escapeHtml(brandLogoUrl));
        String headerImageMarkup = brandHeaderImageUrl.isBlank()
                ? """
                  <div style="height: 150px; background: #020617; border-top: 1px solid #1d4ed8; border-bottom: 1px solid #312e81;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="height: 150px;">
                      <tr>
                        <td align="center" style="color: #64748b; font-size: 12px; letter-spacing: 0.35em;">CONTACT INBOX</td>
                      </tr>
                    </table>
                  </div>
                  """
                : """
                  <img src="%s" width="720" alt="Altaira Labs workspace" style="display: block; width: 100%%; max-width: 720px; height: 180px; object-fit: cover; border: 0;">
                  """.formatted(escapeHtml(brandHeaderImageUrl));
        String leadRows = String.join("",
                htmlRow("Lead ID", safe(lead.getId())),
                htmlRow("Name", safe(lead.getFullName())),
                htmlRow("Business", safe(lead.getBusinessName())),
                htmlRow("Email", safe(lead.getEmail())),
                htmlRow("Phone", safe(lead.getPhone())),
                htmlRow("Sector / context", safe(lead.getIndustry())),
                htmlRow("Service interest", safe(lead.getServiceInterest())),
                htmlRow("Created at", safe(lead.getCreatedAt())),
                htmlRow("Status", safe(lead.getStatus()))
        );

        return """
                <!doctype html>
                <html lang="en">
                  <body style="margin: 0; padding: 0; background: #e5e7eb; color: #0f172a; font-family: Arial, Helvetica, sans-serif;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background: #e5e7eb; padding: 28px 0;">
                      <tr>
                        <td align="center">
                          <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width: 720px; background: #ffffff; border: 1px solid #cbd5e1;">
                            <tr>
                              <td style="background: #020617; color: #ffffff; padding: 22px 28px;">
                                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                  <tr>
                                    <td align="left" style="vertical-align: middle;">%s</td>
                                    <td align="right" style="vertical-align: middle;">
                                      <span style="display: inline-block; border: 1px solid #60a5fa; padding: 7px 10px; color: #bfdbfe; font-size: 11px; font-weight: 700; letter-spacing: 0.18em; text-transform: uppercase;">New lead</span>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                            <tr>
                              <td style="background: #020617;">%s</td>
                            </tr>
                            <tr>
                              <td style="background: #020617; color: #ffffff; padding: 28px 28px 30px;">
                                <p style="margin: 0 0 10px; color: #93c5fd; font-size: 12px; font-weight: 700; letter-spacing: 0.2em; text-transform: uppercase;">Contact inbox</p>
                                <h1 style="margin: 0; font-size: 30px; line-height: 1.18; letter-spacing: 0;">New contact lead received</h1>
                                <p style="margin: 14px 0 0; color: #cbd5e1; font-size: 15px; line-height: 1.6;">A visitor submitted a public contact request. The lead has been stored and is ready for review.</p>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding: 24px 28px;">
                                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="border-collapse: collapse; border: 1px solid #dbe3ef; margin-bottom: 22px;">
                                  <tr>
                                    <td style="padding: 16px 18px; background: #f8fafc; border-bottom: 1px solid #dbe3ef;">
                                      <p style="margin: 0; color: #475569; font-size: 12px; font-weight: 700; letter-spacing: 0.16em; text-transform: uppercase;">Lead summary</p>
                                      <h2 style="margin: 8px 0 0; color: #0f172a; font-size: 22px; line-height: 1.25;">%s</h2>
                                      <p style="margin: 8px 0 0; color: #475569; font-size: 14px;">%s</p>
                                    </td>
                                  </tr>
                                </table>

                                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="border-collapse: collapse; border: 1px solid #dbe3ef;">
                                  %s
                                </table>

                                <div style="margin-top: 24px; padding: 18px; border-left: 4px solid #2563eb; background: #f8fafc;">
                                  <p style="margin: 0 0 8px; color: #475569; font-size: 12px; font-weight: 700; letter-spacing: 0.14em; text-transform: uppercase;">Message / goals</p>
                                  <p style="margin: 0; color: #0f172a; font-size: 15px; line-height: 1.7;">%s</p>
                                </div>

                                <div style="margin-top: 24px; padding: 20px; background: #020617; border: 1px solid #1d4ed8;">
                                  <p style="margin: 0; color: #93c5fd; font-size: 12px; font-weight: 700; letter-spacing: 0.14em; text-transform: uppercase;">Recommended next step</p>
                                  %s
                                </div>

                                <p style="margin: 18px 0 0; color: #64748b; font-size: 12px; line-height: 1.6;">This is an internal Altaira Labs notification generated from the public contact tray.</p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """.formatted(
                logoMarkup,
                headerImageMarkup,
                escapeHtml(safe(lead.getBusinessName()).isBlank() ? "Website contact request" : safe(lead.getBusinessName())),
                escapeHtml(safe(lead.getFullName()).isBlank() ? safe(lead.getEmail()) : safe(lead.getFullName())),
                leadRows,
                escapeHtml(safe(lead.getGoals())).replace("\n", "<br>"),
                dashboardAction
        );
    }

    /**
     * Formats one label and value as a row for the HTML email.
     *
     * @param label display label
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String htmlRow(String label, String value) {
        return """
                <tr>
                  <td style="width: 180px; padding: 12px 14px; border-bottom: 1px solid #dbe3ef; background: #f8fafc; color: #475569; font-size: 13px; font-weight: 700;">%s</td>
                  <td style="padding: 12px 14px; border-bottom: 1px solid #dbe3ef; color: #0f172a; font-size: 14px;">%s</td>
                </tr>
                """.formatted(escapeHtml(label), escapeHtml(value));
    }

    /**
     * Builds the dashboard link included in the notification.
     *
     * @param lead sales enquiry in scope
     * @return the resulting text
     */
    private String dashboardUrl(LeadEntity lead) {
        if (adminDashboardUrl.isBlank()) {
            return "";
        }

        String leadId = safe(lead.getId());
        return adminDashboardUrl
                .replace("{leadId}", leadId)
                .replace("{id}", leadId);
    }

    /**
     * Removes line breaks and trims the text before it is used in a mail header.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String cleanSubject(String value) {
        String cleaned = safe(value).replaceAll("[\\r\\n]+", " ").trim();
        if (cleaned.isBlank()) {
            return "Website contact";
        }
        return cleaned.length() > 80 ? cleaned.substring(0, 80) : cleaned;
    }

    /**
     * Removes line breaks and trims the text before it is used in a mail header.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String cleanHeader(String value) {
        return safe(value).replaceAll("[\\r\\n]+", "").trim();
    }

    /**
     * Normalizes the image link before it is included in the outgoing message.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String cleanImageUrl(String value) {
        String cleaned = safe(value).replaceAll("[\\r\\n\\t]+", "").trim();
        if (cleaned.startsWith("https://")) {
            return cleaned;
        }

        if (!cleaned.isBlank()) {
            logger.warn("Ignoring contact email image URL because it is not a public HTTPS URL.");
        }

        return "";
    }

    /**
     * Escapes special characters before embedding the value in generated markup.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String escapeHtml(String value) {
        return safe(value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Returns the textual representation of the value, using an empty string for null.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }

    /**
     * Trims surrounding whitespace. Absent values return null.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    /**
     * Reduces the supplied value to a form suitable for the notification log.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String safeLogValue(String value) {
        if (value == null || value.isBlank()) {
            return "(blank)";
        }

        String cleaned = value.replaceAll("[\\r\\n\\t]+", " ").trim();
        return cleaned.length() > 240 ? cleaned.substring(0, 240) + "..." : cleaned;
    }
}
