package com.altaira.backend.controller;

import com.altaira.backend.dto.auth.AuthUserResponse;
import com.altaira.backend.dto.auth.GoogleClientLoginRequest;
import com.altaira.backend.dto.auth.LoginRequest;
import com.altaira.backend.dto.auth.LoginResponse;
import com.altaira.backend.security.RateLimiterService;
import com.altaira.backend.service.AuthService;
import com.altaira.backend.service.ClientGoogleAuthService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** HTTP endpoints for auth under {@code /api/v1/auth}. */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class AuthController {

    /** Auth operations used by this component. */
    private final AuthService authService;
    /** Client Google auth operations used by this component. */
    private final ClientGoogleAuthService clientGoogleAuthService;
    /** Rate limiter operations used by this component. */
    private final RateLimiterService rateLimiterService;

    /**
     * Creates the auth controller with its injected collaborators.
     *
     * @param authService auth operations used by this component
     * @param clientGoogleAuthService client Google auth operations used by this component
     * @param rateLimiterService rate limiter operations used by this component
     */
    public AuthController(
            AuthService authService,
            ClientGoogleAuthService clientGoogleAuthService,
            RateLimiterService rateLimiterService
    ) {
        this.authService = authService;
        this.clientGoogleAuthService = clientGoogleAuthService;
        this.rateLimiterService = rateLimiterService;
    }

    /**
     * POST {@code /api/v1/auth/login}. Login.
     *
     * @param request input carrying login
     * @param httpRequest incoming HTTP request, including its headers
     * @return the HTTP response for this operation
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = resolveIpAddress(httpRequest);
        String username = request.getUsername() == null ? "" : request.getUsername().trim().toLowerCase();
        Bucket bucket = rateLimiterService.resolveLoginBucket(ipAddress + ":" + username);

        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", "Too many login attempts",
                    "message", "Please wait a few minutes before trying again."
            ));
        }

        LoginResponse response = authService.login(request, ipAddress, resolveUserAgent(httpRequest));
        return ResponseEntity.ok(response);
    }

    /**
     * POST {@code /api/v1/auth/admin/login}. Admin login.
     *
     * @param request input carrying login
     * @param httpRequest incoming HTTP request, including its headers
     * @return the HTTP response for this operation
     */
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = resolveIpAddress(httpRequest);
        String username = request.getUsername() == null ? "" : request.getUsername().trim().toLowerCase();
        Bucket bucket = rateLimiterService.resolveLoginBucket(ipAddress + ":admin:" + username);

        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", "Too many login attempts",
                    "message", "Please wait a few minutes before trying again."
            ));
        }

        LoginResponse response = authService.loginAdmin(request, ipAddress, resolveUserAgent(httpRequest));
        return ResponseEntity.ok(response);
    }

    /**
     * POST {@code /api/v1/auth/client/login}. Client login.
     *
     * @param request input carrying login
     * @param httpRequest incoming HTTP request, including its headers
     * @return the HTTP response for this operation
     */
    @PostMapping("/client/login")
    public ResponseEntity<?> clientLogin(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = resolveIpAddress(httpRequest);
        String username = request.getUsername() == null ? "" : request.getUsername().trim().toLowerCase();
        Bucket bucket = rateLimiterService.resolveLoginBucket(ipAddress + ":client:" + username);

        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", "Too many login attempts",
                    "message", "Please wait a few minutes before trying again."
            ));
        }

        LoginResponse response = authService.loginClient(request, ipAddress, resolveUserAgent(httpRequest));
        return ResponseEntity.ok(response);
    }

    /**
     * POST {@code /api/v1/auth/client/google}. Client Google login.
     *
     * @param request input carrying Google client login
     * @param httpRequest incoming HTTP request, including its headers
     * @return the HTTP response for this operation
     */
    @PostMapping("/client/google")
    public ResponseEntity<?> clientGoogleLogin(@Valid @RequestBody GoogleClientLoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = resolveIpAddress(httpRequest);
        Bucket bucket = rateLimiterService.resolveLoginBucket(ipAddress + ":client-google");

        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", "Too many login attempts",
                    "message", "Please wait a few minutes before trying again."
            ));
        }

        LoginResponse response = clientGoogleAuthService.login(request, ipAddress, resolveUserAgent(httpRequest));
        return ResponseEntity.ok(response);
    }

    /**
     * GET {@code /api/v1/auth/me}. Me.
     *
     * @param sessionToken credential used to identify the current session
     * @return the auth user response
     */
    @GetMapping("/me")
    public AuthUserResponse me(
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String sessionToken
    ) {
        return authService.getCurrentUser(sessionToken);
    }

    /**
     * POST {@code /api/v1/auth/logout}. Logout.
     *
     * @param sessionToken credential used to identify the current session
     * @param httpRequest incoming HTTP request, including its headers
     * @return the resulting key–value pairs
     */
    @PostMapping("/logout")
    public Map<String, Boolean> logout(
            @RequestHeader(name = "X-Admin-Session-Token", required = false) String sessionToken,
            HttpServletRequest httpRequest
    ) {
        authService.logout(sessionToken, resolveIpAddress(httpRequest), resolveUserAgent(httpRequest));
        return Map.of("success", true);
    }

    /**
     * Resolves ip address.
     *
     * @param request input carrying http servlet
     * @return the resulting text
     */
    private String resolveIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * Resolves user agent.
     *
     * @param request input carrying http servlet
     * @return the resulting text
     */
    private String resolveUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
