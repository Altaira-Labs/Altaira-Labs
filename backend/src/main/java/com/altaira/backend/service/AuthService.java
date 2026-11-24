package com.altaira.backend.service;

import com.altaira.backend.dto.auth.AuthUserResponse;
import com.altaira.backend.dto.auth.LoginRequest;
import com.altaira.backend.dto.auth.LoginResponse;
import com.altaira.backend.entity.AppUserEntity;
import com.altaira.backend.entity.AppUserSessionEntity;
import com.altaira.backend.model.SecurityEventType;
import com.altaira.backend.model.UserRole;
import com.altaira.backend.repository.AppUserRepository;
import com.altaira.backend.repository.AppUserSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.function.Predicate;

/** Admin authentication and session creation. Admin credentials remain backend-only. */
@Service
@Transactional
public class AuthService {

    /** Persistence access for app user. */
    private final AppUserRepository appUserRepository;
    /** Persistence access for app user session. */
    private final AppUserSessionRepository appUserSessionRepository;
    /** Security event operations used by this component. */
    private final SecurityEventService securityEventService;
    /** Encoder used to compare or store credential hashes. */
    private final PasswordEncoder passwordEncoder;
    /** Session hours. */
    private final long sessionHours;
    /** Secure random. */
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Creates the auth service with its injected collaborators.
     *
     * @param appUserRepository persistence access for app user
     * @param appUserSessionRepository persistence access for app user session
     * @param securityEventService security event operations used by this component
     * @param passwordEncoder encoder used to compare or store credential hashes
     * @param sessionHours session hours
     */
    public AuthService(
            AppUserRepository appUserRepository,
            AppUserSessionRepository appUserSessionRepository,
            SecurityEventService securityEventService,
            PasswordEncoder passwordEncoder,
            @Value("${altaira.auth.session-hours}") long sessionHours
    ) {
        this.appUserRepository = appUserRepository;
        this.appUserSessionRepository = appUserSessionRepository;
        this.securityEventService = securityEventService;
        this.passwordEncoder = passwordEncoder;
        this.sessionHours = sessionHours;
    }

    /**
     * Authenticates the supplied credentials for the supported sign-in flow.
     *
     * @param request input carrying login
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @return the login response
     */
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        return loginForAudience(request, ipAddress, userAgent, role -> true, null);
    }

    /**
     * Restricts sign-in to the staff audience.
     *
     * @param request input carrying login
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @return the login response
     */
    public LoginResponse loginAdmin(LoginRequest request, String ipAddress, String userAgent) {
        return loginForAudience(request, ipAddress, userAgent, UserRole::isAdminRole, "Admin role required");
    }

    /**
     * Restricts sign-in to the client-workspace audience.
     *
     * @param request input carrying login
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @return the login response
     */
    public LoginResponse loginClient(LoginRequest request, String ipAddress, String userAgent) {
        return loginForAudience(request, ipAddress, userAgent, UserRole::isClientRole, "Client role required");
    }

    /**
     * Checks the active account and password first, then checks whether the account’s role is permitted in this login area. Failed attempts and successful sessions produce security events.
     *
     * @param request input carrying login
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @param allowedRole predicate deciding whether the account may sign in to the selected audience
     * @param roleError message returned when the account role is outside the selected audience
     * @return the login response
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (unauthorized, forbidden)
     */
    private LoginResponse loginForAudience(
            LoginRequest request,
            String ipAddress,
            String userAgent,
            Predicate<UserRole> allowedRole,
            String roleError
    ) {
        String username = normalizeUsername(request.getUsername());
        String password = request.getPassword() == null ? "" : request.getPassword();

        var user = appUserRepository.findByUsernameIgnoreCase(username);

        if (user.isEmpty() || !user.get().isActive() || !passwordEncoder.matches(password, user.get().getPasswordHash())) {
            securityEventService.record(
                    SecurityEventType.LOGIN_FAILED,
                    user.orElse(null),
                    username,
                    false,
                    ipAddress,
                    userAgent,
                    "Invalid username, inactive user, or invalid password"
            );
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        AppUserEntity entity = user.get();

        if (!allowedRole.test(UserRole.parse(entity.getRole()))) {
            securityEventService.record(
                    SecurityEventType.LOGIN_FAILED,
                    entity,
                    username,
                    false,
                    ipAddress,
                    userAgent,
                    "Role is not allowed for the requested login audience"
            );
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, roleError);
        }

        return createSessionForUser(entity, ipAddress, userAgent, "Session created");
    }

    /**
     * Revokes earlier sessions, stores a digest of the new token and returns the bearer token with its expiry to the caller.
     *
     * @param entity record carrying app user
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @param metadata additional context recorded with the event
     * @return the login response
     */
    public LoginResponse createSessionForUser(AppUserEntity entity, String ipAddress, String userAgent, String metadata) {
        return createSessionForUser(entity, ipAddress, userAgent, metadata, true);
    }

    /**
     * Revokes earlier sessions, stores a digest of the new token and returns the bearer token with its expiry to the caller.
     *
     * @param entity record carrying app user
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @param metadata additional context recorded with the event
     * @param linkSecurityEventToUser whether the security event should reference the account
     * @return the login response
     */
    public LoginResponse createSessionForUser(AppUserEntity entity, String ipAddress, String userAgent, String metadata, boolean linkSecurityEventToUser) {
        Instant now = Instant.now();
        entity.setLastLoginAt(now);
        revokeExistingSessions(entity, now);

        String sessionToken = createSessionToken();
        Instant expiresAt = now.plus(Math.max(sessionHours, 1), ChronoUnit.HOURS);

        AppUserSessionEntity session = new AppUserSessionEntity();
        session.setUser(entity);
        session.setSessionTokenHash(hashToken(sessionToken));
        session.setExpiresAt(expiresAt);
        appUserSessionRepository.save(session);

        securityEventService.record(
                SecurityEventType.LOGIN_SUCCESS,
                linkSecurityEventToUser ? entity : null,
                entity.getUsername(),
                true,
                ipAddress,
                userAgent,
                metadata == null || metadata.isBlank() ? "Session created" : metadata
        );

        return new LoginResponse(sessionToken, expiresAt, mapUser(entity));
    }

    /**
     * Looks up the active session and returns the account fields used by the signed-in interface.
     *
     * @param sessionToken credential used to identify the current session
     * @return current user
     */
    public AuthUserResponse getCurrentUser(String sessionToken) {
        AppUserSessionEntity session = findUsableSession(sessionToken);
        return mapUser(session.getUser());
    }

    /**
     * Returns the account behind a currently usable session. The caller can then check membership or roles.
     *
     * @param sessionToken credential used to identify the current session
     * @return current user entity
     */
    public AppUserEntity getCurrentUserEntity(String sessionToken) {
        return findUsableSession(sessionToken).getUser();
    }

    /**
     * Revokes the session identified by the supplied token.
     *
     * @param sessionToken credential used to identify the current session
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     */
    public void logout(String sessionToken, String ipAddress, String userAgent) {
        if (sessionToken == null || sessionToken.isBlank()) {
            return;
        }

        appUserSessionRepository.findBySessionTokenHash(hashToken(sessionToken))
                .ifPresent(session -> {
                    if (session.getRevokedAt() == null) {
                        session.setRevokedAt(Instant.now());
                        securityEventService.record(
                                SecurityEventType.LOGOUT,
                                session.getUser(),
                                session.getUser().getUsername(),
                                true,
                                ipAddress,
                                userAgent,
                                "Session revoked"
                        );
                    }
                });
    }

    /**
     * Resolves the token digest to a session and rejects expired or revoked sessions and inactive accounts.
     *
     * @param sessionToken credential used to identify the current session
     * @return the app user session record
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (unauthorized)
     */
    AppUserSessionEntity findUsableSession(String sessionToken) {
        if (sessionToken == null || sessionToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing admin session");
        }

        AppUserSessionEntity session = appUserSessionRepository.findBySessionTokenHash(hashToken(sessionToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid admin session"));

        if (!session.isUsable(Instant.now()) || !session.getUser().isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid admin session");
        }

        return session;
    }

    /**
     * Builds the account view with the identifier, username, role, activation state and last login.
     *
     * @param user user account in scope
     * @return the auth user response
     */
    public AuthUserResponse mapUser(AppUserEntity user) {
        return new AuthUserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.isActive(),
                user.getLastLoginAt()
        );
    }

    /**
     * Marks open sessions for the account as revoked when a new login supersedes them.
     *
     * @param user user account in scope
     * @param now now
     */
    private void revokeExistingSessions(AppUserEntity user, Instant now) {
        appUserSessionRepository.findAllByUserAndRevokedAtIsNull(user)
                .forEach(session -> {
                    if (session.getRevokedAt() == null && session.getExpiresAt().isAfter(now)) {
                        session.setRevokedAt(now);
                    }
                });
    }

    /**
     * Derives the digest used to look up a session without storing the bearer token itself.
     *
     * @param sessionToken credential used to identify the current session
     * @return the resulting text
     * @throws IllegalStateException when validation reports “SHA-256 is not available”
     */
    public String hashToken(String sessionToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sessionToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    /**
     * Creates session token.
     *
     * @return the resulting text
     */
    private String createSessionToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Normalizes username.
     *
     * @param username account name used to identify the user
     * @return the resulting text
     */
    private String normalizeUsername(String username) {
        if (username == null) {
            return "";
        }

        return username.trim().toLowerCase();
    }
}
