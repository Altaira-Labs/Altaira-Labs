package com.altaira.backend.security;

import com.altaira.backend.service.AuthService;
import com.altaira.backend.entity.AppUserEntity;
import com.altaira.backend.model.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Resolves an internal session and checks the user role before administrative operations. */
@Service
public class AdminAccessService {

    /** Internal API token operations used by this component. */
    private final InternalApiTokenService internalApiTokenService;
    /** Auth operations used by this component. */
    private final AuthService authService;

    /**
     * Creates the admin access service with its injected collaborators.
     *
     * @param internalApiTokenService internal API token operations used by this component
     * @param authService auth operations used by this component
     */
    public AdminAccessService(InternalApiTokenService internalApiTokenService, AuthService authService) {
        this.internalApiTokenService = internalApiTokenService;
        this.authService = authService;
    }

    /**
     * Requires admin access.
     *
     * @param internalApiToken credential presented to the internal API boundary
     * @param adminSessionToken session credential for the internal user
     * @return the app user record
     * @throws org.springframework.web.server.ResponseStatusException if a request is rejected (forbidden, unauthorized)
     */
    public AppUserEntity requireAdminAccess(String internalApiToken, String adminSessionToken) {
        if (internalApiTokenService.isValidToken(internalApiToken)) {
            return null;
        }

        try {
            var user = authService.getCurrentUserEntity(adminSessionToken);
            UserRole role = UserRole.parse(user.getRole());
            if (!role.isAdminRole()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
            }
            return user;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized admin access");
        }
    }
}
