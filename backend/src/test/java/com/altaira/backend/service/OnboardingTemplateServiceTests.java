package com.altaira.backend.service;

import com.altaira.backend.entity.ClientServiceEntity;
import com.altaira.backend.entity.ServiceEntity;
import com.altaira.backend.model.OnboardingTaskType;
import com.altaira.backend.model.SectorType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises onboarding template service behavior with JUnit. Individual cases include {@code createsWebSeoOnboardingTasksWithLegalAndBrandInputs} and {@code createsDashboardOnboardingTasksForNdaRolesProtocolsAndMigration}.
 */
class OnboardingTemplateServiceTests {

    /** Onboarding template operations used by this component. */
    private final OnboardingTemplateService onboardingTemplateService = new OnboardingTemplateService();

    /** Regression case: creates web seo onboarding tasks with legal and brand inputs. */
    @Test
    void createsWebSeoOnboardingTasksWithLegalAndBrandInputs() {
        Map<String, OnboardingTemplateService.TaskTemplate> tasks = templatesFor("Professional Websites and SEO");

        assertSignatureTask(tasks.get("web-seo-contract-signature"), "web_seo");
        assertEquals(OnboardingTaskType.FILE_UPLOAD, tasks.get("web-brand-assets").taskType());
        assertEquals(OnboardingTaskType.PREFERENCES_FORM, tasks.get("web-pages-content").taskType());
        assertEquals(OnboardingTaskType.PREFERENCES_FORM, tasks.get("web-domain-access-context").taskType());
        assertTrue(tasks.get("web-pages-content").description().contains("competitor websites"));
        assertTrue(tasks.get("web-domain-access-context").description().contains("Do not submit passwords"));
    }

    /** Regression case: creates booking onboarding tasks for rules policies and integrations. */
    @Test
    void createsBookingOnboardingTasksForRulesPoliciesAndIntegrations() {
        Map<String, OnboardingTemplateService.TaskTemplate> tasks = templatesFor("Booking System");

        assertSignatureTask(tasks.get("booking-contract-signature"), "booking");
        assertCriticalForm(tasks.get("booking-business-rules"), "booking");
        assertEquals(OnboardingTaskType.PREFERENCES_FORM, tasks.get("booking-resources-spaces").taskType());
        assertEquals(OnboardingTaskType.PREFERENCES_FORM, tasks.get("booking-policies-cancellations").taskType());
        assertFalse(tasks.get("booking-integrations").required());
    }

    /** Regression case: creates CRM onboarding tasks for data processing catalogue and migration. */
    @Test
    void createsCrmOnboardingTasksForDataProcessingCatalogueAndMigration() {
        Map<String, OnboardingTemplateService.TaskTemplate> tasks = templatesFor("CRM Lead Management Systems");

        assertSignatureTask(tasks.get("crm-data-processing-signature"), "crm");
        assertEquals(OnboardingTaskType.PREFERENCES_FORM, tasks.get("crm-service-catalogue").taskType());
        assertCriticalForm(tasks.get("crm-pipeline-fields"), "crm");
        assertEquals(OnboardingTaskType.PREFERENCES_FORM, tasks.get("crm-web-form-integrations").taskType());
        assertTrue(tasks.get("crm-web-form-integrations").description().contains("CRM intake endpoint"));
        assertEquals(OnboardingTaskType.FILE_UPLOAD, tasks.get("crm-contact-import").taskType());
    }

    /** Regression case: creates automation onboarding tasks for channels tone and recipes. */
    @Test
    void createsAutomationOnboardingTasksForChannelsToneAndRecipes() {
        Map<String, OnboardingTemplateService.TaskTemplate> tasks = templatesFor("Workflow Automation");

        assertSignatureTask(tasks.get("automation-gdpr-signature"), "automation");
        assertCriticalForm(tasks.get("automation-channel-connections"), "automation");
        assertEquals(OnboardingTaskType.PREFERENCES_FORM, tasks.get("automation-brand-tone").taskType());
        assertEquals(OnboardingTaskType.PREFERENCES_FORM, tasks.get("automation-goals-recipes").taskType());
    }

    /** Regression case: creates dashboard onboarding tasks for nda roles protocols and migration. */
    @Test
    void createsDashboardOnboardingTasksForNdaRolesProtocolsAndMigration() {
        Map<String, OnboardingTemplateService.TaskTemplate> tasks = templatesFor("Management Dashboard");

        assertSignatureTask(tasks.get("dashboard-nda-signature"), "dashboard");
        assertCriticalForm(tasks.get("dashboard-roles-staff"), "dashboard");
        assertEquals(OnboardingTaskType.PREFERENCES_FORM, tasks.get("dashboard-operational-protocols").taskType());
        assertEquals(OnboardingTaskType.FILE_UPLOAD, tasks.get("dashboard-data-migration").taskType());
        assertEquals(OnboardingTaskType.PREFERENCES_FORM, tasks.get("dashboard-kpis").taskType());
    }

    /**
     * Returns the task templates produced for the supplied test services.
     *
     * @param serviceName service name
     * @return the resulting key–value pairs
     */
    private Map<String, OnboardingTemplateService.TaskTemplate> templatesFor(String serviceName) {
        ClientServiceEntity clientService = new ClientServiceEntity();
        ServiceEntity service = new ServiceEntity();
        service.setName(serviceName);
        clientService.setService(service);

        List<OnboardingTemplateService.TaskTemplate> templates = onboardingTemplateService.serviceTemplates(
                clientService,
                SectorType.CLINICS
        );

        return templates.stream().collect(Collectors.toMap(
                OnboardingTemplateService.TaskTemplate::taskKey,
                Function.identity()
        ));
    }

    /**
     * Checks the contract-signature task defined by the selected template.
     *
     * @param task task
     * @param expectedServiceKey key identifying the expected service
     */
    private void assertSignatureTask(OnboardingTemplateService.TaskTemplate task, String expectedServiceKey) {
        assertEquals(OnboardingTaskType.SIGNATURE, task.taskType());
        assertEquals(expectedServiceKey, task.serviceKey());
        assertTrue(task.required());
        assertTrue(task.critical());
    }

    /**
     * Checks the required form in the selected onboarding template.
     *
     * @param task task
     * @param expectedServiceKey key identifying the expected service
     */
    private void assertCriticalForm(OnboardingTemplateService.TaskTemplate task, String expectedServiceKey) {
        assertEquals(OnboardingTaskType.PREFERENCES_FORM, task.taskType());
        assertEquals(expectedServiceKey, task.serviceKey());
        assertTrue(task.required());
        assertTrue(task.critical());
    }
}
