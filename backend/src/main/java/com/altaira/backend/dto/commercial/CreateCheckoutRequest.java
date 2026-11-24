package com.altaira.backend.dto.commercial;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Input for create checkout in the commercial API. */
public class CreateCheckoutRequest {
    /** Checkout amount in the currency’s smallest unit. */
    @Min(value = 100, message = "Payment amount must be at least 1.00")
    @Max(value = 100000000, message = "Payment amount is above the supported limit")
    private long amountMinor;

    /** Currency code for the payment. */
    @Pattern(regexp = "^[A-Za-z]{3}$", message = "Currency must be a three-letter code")
    private String currency = "EUR";

    /** Human-readable description. */
    @Size(max = 500, message = "Payment description must be at most 500 characters")
    private String description;

    /** {@return checkout amount in the currency’s smallest unit} */
    public long getAmountMinor() { return amountMinor; }
    /**
     * Sets the checkout amount in the currency’s smallest unit on this create checkout.
     *
     * @param amountMinor checkout amount in the currency’s smallest unit
     */
    public void setAmountMinor(long amountMinor) { this.amountMinor = amountMinor; }
    /** {@return currency code for the payment} */
    public String getCurrency() { return currency; }
    /**
     * Sets the currency code for the payment on this create checkout.
     *
     * @param currency currency code for the payment
     */
    public void setCurrency(String currency) { this.currency = currency; }
    /** {@return human-readable description} */
    public String getDescription() { return description; }
    /**
     * Sets the human-readable description on this create checkout.
     *
     * @param description human-readable description
     */
    public void setDescription(String description) { this.description = description; }
}
