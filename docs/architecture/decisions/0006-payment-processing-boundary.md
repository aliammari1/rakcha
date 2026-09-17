# ADR 0006: Payment Processing Trust Boundaries and Client Secret Externalization

## Title
ADR 0006: Payment Processing Trust Boundaries and Client Secret Externalization

## Status
Accepted

## Date
2026-09-17

## Context
Auditing Stripe and PayPal integrations across all three platforms revealed critical vulnerabilities:
1. **Desktop Secrets**: `OrderClientController.java` read `PAYPAL_CLIENT_SECRET` directly from environment and executed payments using the deprecated `com.paypal.sdk:rest-api-sdk:1.14.0`. `PaymentProcessor.java` and `Paymentuser.java` initialized `Stripe.apiKey` with secret keys (`STRIPE_API_KEY`) and created charges directly from client code.
2. **Client Amount Manipulation**:
   - In web `paymentStripeController.php`, the amount charged was computed from client input (`$data["prix"] * 10`).
   - In web `CommandeController.php`, PayPal purchase amount was read from user input (`$request->request->get('amount')`).
   - In desktop, `totalPrix` was passed directly from client variables.
3. **Broken Object-Level Authorization (BOLA)**:
   - Web `CommandeController::success` updated orders to `payé` without verifying that the currently logged-in user owned the order.

## Decision
1. **Strict Server-Side Authority**:
   - Payment credentials (`STRIPE_SECRET_KEY`, `PAYPAL_SECRET_KEY`) belong strictly on trusted server environments and must never be distributed in client binaries.
   - Price calculation must be server-authoritative; client requests must never dictate payment amounts.
2. **Web Hardening**:
   - `paymentStripeController.php` requires authenticated user sessions (`IS_AUTHENTICATED_REMEMBERED`).
   - `paymentStripeController.php` verifies seat availability prior to charge creation and wraps updates in a database transaction to prevent double booking.
   - `CommandeController.php` and `PanierController.php` enforce strict ownership checks before modifying orders or cart items.
   - Debug statements (`var_dump`) removed from payment controllers.
3. **Desktop Hardening**:
   - Deprecate direct client-side Stripe charges in `PaymentProcessor.java`; eliminate fatal initializers when `STRIPE_API_KEY` is not present.
   - Document the migration path to delegate all desktop payment requests to backend checkout sessions.

## Alternatives
1. **Keep Client-Side Stripe/PayPal in Desktop**: Rejected as a severe P0 security violation for production releases.

## Consequences
- Protects the business from tampered checkout prices and fraudulent unpaid order validations.
- Prevents cross-user order hijacking and unauthorized cart modifications.

## Security implications
Resolves P0 vulnerabilities in payment execution and object-level authorization.

## Operational implications
Payment gateways operate under test credentials in development and strictly server-side credentials in production.

## Migration path
Implement Stripe Elements / Checkout Sessions and PayPal v2 Orders API on the backend with webhook listeners (`checkout.session.completed`, `PAYMENT.CAPTURE.COMPLETED`) for asynchronous status reconciliation.

## Revisit triggers
- Implementation of dedicated Stripe/PayPal webhook endpoints.
