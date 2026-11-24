package com.altaira.backend.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Exercises user role behavior with JUnit. */
class UserRoleTests {

    /** Regression case: missing role never defaults to admin. */
    @Test
    void missingRoleNeverDefaultsToAdmin() {
        assertThatThrownBy(() -> UserRole.parse(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("required");

        assertThatThrownBy(() -> UserRole.parse("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("required");
    }
}
