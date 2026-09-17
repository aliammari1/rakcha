# ADR 0002: Relational Database Source of Truth and Concurrency Management

## Title
ADR 0002: Relational Database Source of Truth and Concurrency Management

## Status
Accepted

## Date
2026-09-17

## Context
Rakcha utilizes relational databases (MySQL 8 / PostgreSQL 16 / SQLite) in `apps/web` and `apps/desktop`, while `apps/mobile` stores overlapping business state in Google Cloud Firestore. Furthermore, cinema seat booking had no transaction boundary or concurrency conflict checks, allowing two users to pay for and reserve the exact same seat concurrently.

## Decision
1. **Canonical Source of Truth**: The relational database schema managed by Doctrine migrations (`apps/web/migrations`) and Desktop schema creator is designated as the canonical system of record for commercial and physical cinema state (cinemas, rooms, showtimes, seat reservations, orders).
2. **Double-Booking Concurrency Protection**: Seat reservations must enforce atomicity:
   - Check existing seat state (`statut !== 'reserve'`).
   - Abort with HTTP 409 Conflict if any seat is already booked before charging customer credit cards.
   - Wrap state modifications in explicit database transactions (`beginTransaction` / `commit` / `rollback`).
3. **SQLite Default**: Desktop defaults to embedded SQLite (`data/rakcha.db`) and Web supports SQLite (`var/data.db`) for testing, development, and standalone execution without requiring external database services.

## Alternatives
1. **Firestore as Primary Database**: Make Firestore authoritative for all platforms. Rejected because Doctrine ORM, desktop HikariCP JDBC, and complex SQL joins depend heavily on relational integrity and foreign keys.
2. **Distributed 2PC / Eventual Consistency Sync**: Build a bidirectional sync daemon between MySQL and Firestore. Rejected as over-engineered for the current project scale.

## Consequences
- Seat double-booking is eliminated at the application and database layer.
- Local development and automated tests run reliably without live MySQL instances.

## Security implications
Prevents financial loss and customer conflicts resulting from race conditions during showtime seat selection and ticket payments.

## Operational implications
Production deployments should utilize MySQL 8 or PostgreSQL 16 with row-level locking enabled on high-concurrency reservation tables.

## Migration path
Add unique composite constraints on showtime seat instances `(seance_id, seat_number)` in future migration scripts to enforce locking at the RDBMS constraint level.

## Revisit triggers
- High booking volume exceeding single RDBMS instance write throughput.
- Full mobile-web database reconciliation initiative.
