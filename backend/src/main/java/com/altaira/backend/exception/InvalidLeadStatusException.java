package com.altaira.backend.exception;

/** Signals that the supplied lead status cannot be used. */
public class InvalidLeadStatusException extends IllegalArgumentException {

    /**
     * Constructs {@code InvalidLeadStatusException} using the supplied message.
     *
     * @param message text associated with the operation
     */
    public InvalidLeadStatusException(String message) {
        super(message);
    }
}
