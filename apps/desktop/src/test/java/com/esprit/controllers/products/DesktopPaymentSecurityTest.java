package com.esprit.controllers.products;

import com.esprit.utils.PaymentProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Desktop Payment Security Boundary Tests")
class DesktopPaymentSecurityTest {

    @Test
    @DisplayName("Desktop client must not expose PAYPAL_CLIENT_SECRET field")
    void testPayPalClientSecretNotPresentInDesktopClient() {
        for (Field field : OrderClientController.class.getDeclaredFields()) {
            assertThat(field.getName())
                .as("Field %s in OrderClientController must not contain CLIENT_SECRET", field.getName())
                .isNotEqualToIgnoringCase("CLIENT_SECRET")
                .isNotEqualToIgnoringCase("PAYPAL_CLIENT_SECRET");
        }
    }

    @Test
    @DisplayName("OrderClientController must report privileged payment execution is disabled")
    void testOrderClientPrivilegedExecutionDisabled() {
        assertThat(OrderClientController.isPrivilegedPaymentExecutionEnabled())
            .as("Privileged payment execution must always be disabled on untrusted desktop client")
            .isFalse();
    }

    @Test
    @DisplayName("PaymentProcessor must report privileged payment execution is disabled")
    void testPaymentProcessorPrivilegedExecutionDisabled() {
        assertThat(PaymentProcessor.isPrivilegedPaymentExecutionEnabled())
            .as("Direct privileged Stripe execution must always be disabled on desktop client")
            .isFalse();
    }

    @Test
    @DisplayName("PaymentProcessor must reject direct payment in standard configuration")
    void testStandardPaymentProcessingRejectsDirectExecution() {
        // In standard configuration (without simulation mode), payments are rejected
        boolean result = PaymentProcessor.processPayment(
            "Jane Doe",
            "jane@example.com",
            50.0f,
            "4242424242424242",
            12,
            2026,
            "123"
        );

        assertThat(result)
            .as("PaymentProcessor.processPayment must return false in standard production configuration")
            .isFalse();
    }
}
