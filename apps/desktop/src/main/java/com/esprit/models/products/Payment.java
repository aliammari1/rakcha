package com.esprit.models.products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Log4j2
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Payment {
    private LocalDateTime createdAt;
    private Long id;
    /**
     * The order this payment is for.
     */
    private Order order;
    private double amount;
    /**
     * Payment method: CREDIT_CARD, PAYPAL, CASH, etc.
     */
    private String paymentMethod;
    /**
     * Payment status: PENDING, COMPLETED, FAILED, REFUNDED
     */
    private String status;
    private String transactionId;
    private LocalDateTime processedAt;

    private String refundReason;

    @Override
    public String toString() {
        return "Payment{" +
            "id=" + id +
            ", orderId=" + (order != null ? order.getId() : null) +
            ", amount=" + amount +
            ", paymentMethod='" + paymentMethod + '\'' +
            ", status='" + status + '\'' +
            ", transactionId='" + transactionId + '\'' +
            ", createdAt=" + createdAt +
            ", processedAt=" + processedAt +
            ", refundReason='" + refundReason + '\'' +
            '}';
    }
}
