package com.altaira.backend.security;

import com.altaira.backend.entity.AppUserEntity;
import com.altaira.backend.exception.ResourceNotFoundException;
import com.altaira.backend.model.UserRole;
import com.altaira.backend.repository.ClientRepository;
import com.altaira.backend.repository.ClientUserAccessRepository;
import com.altaira.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Resolves the account behind a session and the membership connecting that account to a client.
 * <p>A person can sign in successfully and still lack access to a particular customer's data.
 * The single-argument lookup requires exactly one active membership. When more than one is
 * available, the caller must use the overload that supplies the client identifier.
 * <p>Changing workspace data requires a further check on both the account role and its
 * membership role. This is what gives a viewer read-only access.
 */
@Service
public class ClientAccessService {

    /** Auth operations used by this component. */
    private final AuthService authService;
    /** Persistence access for client user access. */
    private final ClientUserAccessRepository clientUserAccessRepository;
    /** Persistence access for client. */
    private final ClientRepository clientRepository;

    /**
     * Creates the client access service with its injected collaborators.
     *
     * @param authService auth operations used by this component
     * @param clientUserAccessRepository persistence access for client user access
     * @param clientRepository persistence access for client
     */
    public ClientAccessService(
            AuthService authService,
            ClientUserAccessRepository clientUserAccessRepository,
            ClientRepository clientRepository
    ) {
        this.authService = authService;
        this.clientUserAccessRepository = clientUserAccessRepository;
        this.clientRepository = clientRepository;
    }

    /**
     * Resolves the session to an active workspace membership. Rejects users without a client role or access to that workspace. An ambiguous session with several active memberships must select a workspace.
     *
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the client access context
     * @throws org.springframework.web.server.ResponseStatusException if the session lacks the required workspace access
     */
    public ClientAccessContext requireClientAccess(String clientSessionToken) {
        AppUserEntity user = authService.getCurrentUserEntity(clientSessionToken);
        UserRole role = UserRole.parse(user.getRole());

        if (!role.isClientRole()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Client role required");
        }

        var accessList = clientUserAccessRepository.findAllByUserAndActiveTrue(user);

        if (accessList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No client workspace access");
        }

        if (accessList.size() > 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Multiple active client workspaces require explicit workspace selection"
            );
        }

        var access = accessList.get(0);
        return new ClientAccessContext(user, access.getClient(), access.getRole());
    }

    /**
     * Resolves the session to an active workspace membership. Rejects users without a client role or access to that workspace.
     *
     * @param clientSessionToken session credential supplied for the client workspace
     * @param clientId identifier of the client whose workspace is in scope
     * @return the client access context
     * @throws org.springframework.web.server.ResponseStatusException if the session lacks the required workspace access
     * @throws com.altaira.backend.exception.ResourceNotFoundException if the requested client does not exist
     */
    public ClientAccessContext requireClientAccess(String clientSessionToken, UUID clientId) {
        AppUserEntity user = authService.getCurrentUserEntity(clientSessionToken);
        UserRole role = UserRole.parse(user.getRole());

        if (!role.isClientRole()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Client role required");
        }

        var client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));

        var access = clientUserAccessRepository.findByUserAndClientAndActiveTrue(user, client)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No access to this client workspace"));

        return new ClientAccessContext(user, client, access.getRole());
    }

    /**
     * Requires both a client-user account and a writable membership before the caller can change workspace data.
     *
     * @param context context carrying client access
     * @throws org.springframework.web.server.ResponseStatusException if the current user is not allowed to change this workspace
     */
    public void requireClientWriteAccess(ClientAccessContext context) {
        UserRole userRole = UserRole.parse(context.user().getRole());
        String accessRole = context.accessRole() == null ? "" : context.accessRole().trim().toLowerCase();

        if (userRole != UserRole.CLIENT_USER || !"client_user".equals(accessRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Client workspace is read-only for this account");
        }
    }
}
