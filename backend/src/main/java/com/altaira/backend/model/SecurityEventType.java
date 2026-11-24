package com.altaira.backend.model;

/** Allowed security event type values understood by the backend. */
public enum SecurityEventType {
    /** Represents login success with serialized value {@code login_success}. */
    LOGIN_SUCCESS("login_success"),
    /** Represents login failed with serialized value {@code login_failed}. */
    LOGIN_FAILED("login_failed"),
    /** Represents user created with serialized value {@code user_created}. */
    USER_CREATED("user_created"),
    /** Represents client invitation created with serialized value {@code client_invitation_created}. */
    CLIENT_INVITATION_CREATED("client_invitation_created"),
    /** Represents client invitation accepted with serialized value {@code client_invitation_accepted}. */
    CLIENT_INVITATION_ACCEPTED("client_invitation_accepted"),
    /** Represents password changed with serialized value {@code password_changed}. */
    PASSWORD_CHANGED("password_changed"),
    /** Represents user disabled with serialized value {@code user_disabled}. */
    USER_DISABLED("user_disabled"),
    /** Represents logout with serialized value {@code logout}. */
    LOGOUT("logout");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code SecurityEventType} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    SecurityEventType(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this security event type.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }
}
