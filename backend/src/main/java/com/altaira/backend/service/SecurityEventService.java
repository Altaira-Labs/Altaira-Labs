package com.altaira.backend.service;

import com.altaira.backend.entity.AppUserEntity;
import com.altaira.backend.entity.SecurityEventEntity;
import com.altaira.backend.model.SecurityEventType;
import com.altaira.backend.repository.SecurityEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Authentication and security audit events. Logs contain safe metadata, not credentials. */
@Service
public class SecurityEventService {

    /** Persistence access for security event. */
    private final SecurityEventRepository securityEventRepository;

    /**
     * Creates the security event service with its injected collaborators.
     *
     * @param securityEventRepository persistence access for security event
     */
    public SecurityEventService(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }

    /**
     * Writes the security event with the supplied audit context.
     *
     * @param eventType event type
     * @param user user account in scope
     * @param username account name used to identify the user
     * @param success success
     * @param ipAddress network address associated with the request
     * @param userAgent client software reported by the HTTP request
     * @param metadata additional context recorded with the event
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(
            SecurityEventType eventType,
            AppUserEntity user,
            String username,
            boolean success,
            String ipAddress,
            String userAgent,
            String metadata
    ) {
        SecurityEventEntity event = new SecurityEventEntity();
        event.setEventType(eventType.value());
        event.setUser(user);
        event.setUsername(normalizeOptional(username));
        event.setSuccess(success);
        event.setIpAddress(trimToLimit(ipAddress, 80));
        event.setUserAgent(trimToLimit(userAgent, 500));
        event.setMetadata(trimToLimit(metadata, 1000));
        securityEventRepository.save(event);
    }

    /**
     * Normalizes optional.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    /**
     * Trims surrounding whitespace and caps the result at the supplied length. Absent values return null.
     *
     * @param value stored or supplied representation
     * @param limit upper bound applied when reading or accepting data
     * @return the resulting text
     */
    private String trimToLimit(String value, int limit) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.length() <= limit ? trimmed : trimmed.substring(0, limit);
    }
}
