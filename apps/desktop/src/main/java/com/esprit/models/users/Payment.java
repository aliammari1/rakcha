package com.esprit.models.users;

import com.esprit.models.films.Ticket;
import com.esprit.models.products.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Payment {

    @Builder.Default
    private final java.sql.Timestamp createdAt = new java.sql.Timestamp(System.currentTimeMillis());
    private Long id;
    /**
     * The user who made the payment.
     */
    private User user;
    private Double amount;
    /**
     * Payment method: STRIPE, PAYPAL, CASH, CREDIT_CARD
     */
    private String paymentMethod;
    /**
     * Payment status: COMPLETED, PENDING, FAILED, REFUNDED
     */
    private String status;
    private String transactionId;
    /**
     * The order this payment is for (if applicable).
     */
    private Order order;
    /**
     * The ticket this payment is for (if applicable).
     */
    private Ticket ticket;

}

