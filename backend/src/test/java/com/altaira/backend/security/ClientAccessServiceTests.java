package com.altaira.backend.security;

import com.altaira.backend.entity.AppUserEntity;
import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.ClientUserAccessEntity;
import com.altaira.backend.repository.ClientRepository;
import com.altaira.backend.repository.ClientUserAccessRepository;
import com.altaira.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Exercises client access service behavior with JUnit. Individual cases include {@code clientUserAccessCanWrite} and {@code ambiguousMultipleWorkspaceSessionIsRejected}.
 */
@ExtendWith(MockitoExtension.class)
class ClientAccessServiceTests {

    /** Auth operations used by this component. */
    @Mock
    private AuthService authService;

    /** Persistence access for client user access. */
    @Mock
    private ClientUserAccessRepository clientUserAccessRepository;

    /** Persistence access for client. */
    @Mock
    private ClientRepository clientRepository;

    /** Allows changes when both the account and its membership grant client-user access. */
    @Test
    void clientUserAccessCanWrite() {
        ClientAccessService service = service();
        ClientAccessContext context = context("client_user", "client_user");

        assertThatCode(() -> service.requireClientWriteAccess(context)).doesNotThrowAnyException();
    }

    /** Verifies that a workspace viewer receives a forbidden response on an attempted write. */
    @Test
    void viewerAccessIsReadOnly() {
        ClientAccessService service = service();
        ClientAccessContext context = context("viewer", "viewer");

        assertThatThrownBy(() -> service.requireClientWriteAccess(context))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN");
    }

    /** Verifies that an account with two active workspaces must select one before continuing. */
    @Test
    void ambiguousMultipleWorkspaceSessionIsRejected() {
        AppUserEntity user = new AppUserEntity();
        user.setRole("client_user");

        when(authService.getCurrentUserEntity("session-token")).thenReturn(user);
        when(clientUserAccessRepository.findAllByUserAndActiveTrue(user))
                .thenReturn(List.of(new ClientUserAccessEntity(), new ClientUserAccessEntity()));

        assertThatThrownBy(() -> service().requireClientAccess("session-token"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT")
                .hasMessageContaining("explicit workspace selection");
    }

    /**
     * Builds the client access service fixture used by the client access service tests.
     *
     * @return the client access service
     */
    private ClientAccessService service() {
        return new ClientAccessService(authService, clientUserAccessRepository, clientRepository);
    }

    /**
     * Builds a membership context using the account and workspace roles under test.
     *
     * @param userRole account role considered for authorization
     * @param accessRole membership role in this customer’s workspace
     * @return the client access context
     */
    private ClientAccessContext context(String userRole, String accessRole) {
        AppUserEntity user = new AppUserEntity();
        user.setRole(userRole);
        ClientEntity client = new ClientEntity();
        return new ClientAccessContext(user, client, accessRole);
    }
}
