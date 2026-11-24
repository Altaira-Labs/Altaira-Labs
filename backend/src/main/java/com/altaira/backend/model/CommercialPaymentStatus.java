package com.altaira.backend.model;

/** Allowed commercial payment status values understood by the backend. */
public enum CommercialPaymentStatus {
    /** Represents the payment not started option for commercial payment status. */
    PAYMENT_NOT_STARTED,
    /** Represents the payment link created option for commercial payment status. */
    PAYMENT_LINK_CREATED,
    /** Represents the payment pending option for commercial payment status. */
    PAYMENT_PENDING,
    /** Represents the payment confirmed option for commercial payment status. */
    PAYMENT_CONFIRMED,
    /** Represents the payment failed option for commercial payment status. */
    PAYMENT_FAILED,
    /** Represents the payment cancelled option for commercial payment status. */
    PAYMENT_CANCELLED
}
