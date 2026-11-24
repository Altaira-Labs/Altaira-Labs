package com.altaira.backend.service;

import com.altaira.backend.entity.AppUserEntity;
import com.altaira.backend.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Exercises demo admin seeder behavior with JUnit. Individual cases include {@code disabledSeederDoesNotReadOrModifyUsers} and {@code enabledSeederRejectsKnownWeakPassword}.
 */
@ExtendWith(MockitoExtension.class)
class DemoAdminSeederTests {

    /** Persistence access for app user. */
    @Mock
    private AppUserRepository appUserRepository;

    /** Encoder used to compare or store credential hashes. */
    @Mock
    private PasswordEncoder passwordEncoder;

    /** Security event operations used by this component. */
    @Mock
    private SecurityEventService securityEventService;

    /** Regression case: disabled seeder does not read or modify users. */
    @Test
    void disabledSeederDoesNotReadOrModifyUsers() {
        DemoAdminSeeder seeder = new DemoAdminSeeder(
                appUserRepository,
                passwordEncoder,
                securityEventService,
                false,
                "admin",
                "private-password",
                "admin"
        );

        seeder.run(null);

        verify(appUserRepository, never()).findByUsernameIgnoreCase(any());
        verify(appUserRepository, never()).save(any());
    }

    /** Regression case: enabled seeder reconciles existing configured user. */
    @Test
    void enabledSeederReconcilesExistingConfiguredUser() {
        AppUserEntity existingUser = new AppUserEntity();
        existingUser.setUsername("private-admin");
        existingUser.setPasswordHash("$2a$10$old");
        existingUser.setRole("auditor");
        existingUser.setActive(false);

        when(appUserRepository.findByUsernameIgnoreCase("private-admin")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("new-private-password", "$2a$10$old")).thenReturn(false);
        when(passwordEncoder.encode("new-private-password")).thenReturn("$2a$10$new");
        when(appUserRepository.save(existingUser)).thenReturn(existingUser);

        DemoAdminSeeder seeder = new DemoAdminSeeder(
                appUserRepository,
                passwordEncoder,
                securityEventService,
                true,
                "Private-Admin",
                "new-private-password",
                "admin"
        );

        seeder.run(null);

        assertThat(existingUser.getPasswordHash()).isEqualTo("$2a$10$new");
        assertThat(existingUser.getRole()).isEqualTo("admin");
        assertThat(existingUser.isActive()).isTrue();
        verify(appUserRepository).save(existingUser);
    }

    /** Regression case: enabled seeder rejects known weak password. */
    @Test
    void enabledSeederRejectsKnownWeakPassword() {
        DemoAdminSeeder seeder = new DemoAdminSeeder(
                appUserRepository,
                passwordEncoder,
                securityEventService,
                true,
                "admin123",
                "admin123",
                "admin"
        );

        assertThatThrownBy(() -> seeder.run(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 16 characters");

        verify(appUserRepository, never()).findByUsernameIgnoreCase(any());
        verify(appUserRepository, never()).save(any());
    }
}
