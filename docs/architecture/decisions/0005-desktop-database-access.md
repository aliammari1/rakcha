# ADR 0005: Desktop Direct Database Access and Embedded SQLite Strategy

## Title
ADR 0005: Desktop Direct Database Access and Embedded SQLite Strategy

## Status
Accepted

## Date
2026-09-17

## Context
`apps/desktop` contains a complete JavaFX desktop application with deep DAO/service implementations built on direct JDBC queries (`UserService`, `CinemaService`, `FilmService`, `OrderService`, etc.).
Shipping direct database credentials to end-user desktop installations introduces severe risks:
- RDBMS credentials can be extracted from binaries or memory.
- The production database must be exposed to the public internet or corporate VPN.
- Client-side business logic can be altered or bypassed by tampering with the desktop client.

However, completely replacing the entire JDBC service layer of 194 classes in JavaFX with REST calls is outside the feasible scope of `feat/upgrade`.

## Decision
1. **Multi-Database Support & Local Default**: Update `DataSource.java` to default gracefully to local embedded SQLite (`jdbc:sqlite:data/rakcha.db`) when `DB_URL` is unset or empty.
2. **Untrusted Client Classification**: Formally classify the desktop client as an untrusted tier:
   - For standalone or offline usage, embedded SQLite operates locally with zero network exposure.
   - For production environments, remote database credentials must never be embedded in packaged binaries.
3. **Graceful Initialization**: Remove fatal `ExceptionInInitializerError` crashes in payment and utility classes when external secrets are omitted.

## Alternatives
1. **Immediate Total API Migration**: Rewrite all 194 classes to call HTTP endpoints. Rejected as too risky and massive for a single stabilization branch.
2. **Hardcode MySQL Only**: Disallow SQLite. Rejected because local development, testing, and CI become dependent on live MySQL daemons.

## Consequences
- The desktop app runs cleanly out-of-the-box on developer workstations and test environments without configuring external databases.
- Clear architectural documentation of known debt regarding thick-client JDBC connections.

## Security implications
Prevents accidental deployment of production database passwords inside distributed client packages.

## Operational implications
Simplifies CI verification and automated testing by leveraging embedded SQLite.

## Migration path
Design a thin API proxy service for high-privilege operations (user registration, payment processing, administrative changes) in a follow-up branch `refactor/desktop-api-client`.

## Revisit triggers
- Native installer packaging for public customer distribution.
