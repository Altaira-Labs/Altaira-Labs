package com.altaira.backend.security;

import com.altaira.backend.entity.AppUserEntity;
import com.altaira.backend.entity.ClientEntity;

/**
 * Carries the authenticated user, the selected client and their role in that workspace after membership checks.
 *
 * @param user user account in scope
 * @param client customer account in scope
 * @param accessRole membership role in this customer’s workspace
 */
public record ClientAccessContext(AppUserEntity user, ClientEntity client, String accessRole) {
}
