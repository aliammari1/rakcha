package com.esprit.exceptions;

/**
 * Exception thrown when a ticket cannot be refunded.
 */

import lombok.extern.log4j.Log4j2;

@Log4j2
public class TicketNotRefundableException extends RuntimeException {

    public TicketNotRefundableException(String message) {
        super(message);
    }
}
