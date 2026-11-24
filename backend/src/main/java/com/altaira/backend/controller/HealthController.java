package com.altaira.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/** HTTP endpoints for health under {@code /api/v1}. */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    /**
     * GET {@code /api/v1/health}. Health.
     *
     * @return the resulting key–value pairs
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "ok",
                "service", "altaira-os-backend",
                "timestamp", Instant.now().toString()
        );
    }
}
