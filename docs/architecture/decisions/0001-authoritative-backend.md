# ADR 0001: Independent Monorepo Client Architectures vs. Unified REST Backend

## Title
ADR 0001: Independent Monorepo Client Architectures vs. Unified REST Backend

## Status
Accepted

## Date
2026-09-17

## Context
Historical documentation claimed Rakcha was a unified client-server architecture where JavaFX Desktop, Flutter Mobile, and Symfony Web all communicated with a central Symfony REST API contract defined by `shared/api-spec/openapi.yaml`.

Inspection of the actual codebase reveals:
1. **Desktop (`apps/desktop`)** communicates directly with relational databases (MySQL, PostgreSQL, SQLite) via JDBC and HikariCP connection pooling. It makes zero HTTP calls to Symfony.
2. **Mobile (`apps/mobile`)** is a FlutterFlow application backed directly by Cloud Firestore collections (`Film`, `cinema`, `cart`, `Reservation`, etc.) and Firebase Authentication. It makes zero HTTP calls to Symfony.
3. **Web (`apps/web`)** is a server-rendered MVC application (Symfony 6.4 + Twig) serving 36 controllers and 71 Twig templates. It provides HTML views with minimal AJAX helpers, not a public REST API.
4. **OpenAPI (`shared/api-spec/openapi.yaml`)** was a 144-line unimplemented stub that mapped to no active Symfony controllers and generated no client code.

Attempting to force all three platforms behind a unified Symfony REST backend in a single upgrade branch would require rewriting all three client applications, destabilizing working systems.

## Decision
Maintain the three independent application runtimes in the monorepo for the `feat/upgrade` branch, treating the repository honestly as three separate products addressing a shared cinema domain. Do not attempt a mass rewrite of client data layers to a non-existent REST API in this branch.

## Alternatives
1. **Force Immediate Rewrite to Symfony REST**: Rewrite Flutter mobile and JavaFX desktop data access layers to call Symfony. Rejected due to extreme scope, lack of existing REST endpoints in Symfony, and high risk of regressions.
2. **Split into Separate Repositories**: De-monorepo into three separate git repositories. Rejected to preserve shared tooling, licensing, documentation, and coordinated issue tracking.

## Consequences
- Documentation and architecture diagrams reflect reality rather than fictional architecture.
- Maintenance is divided cleanly along runtime boundaries (PHP, Java, Dart).
- Follow-up initiatives can migrate sensitive domain operations (payments, seat holds) behind unified APIs iteratively.

## Security implications
Client applications connecting directly to datastores require strict trust boundary controls (see ADR 0003 and ADR 0005). Desktop clients must not receive production credentials; mobile must use least-privilege Firestore rules.

## Operational implications
Each application has independent deployment artifacts (jpackage desktop installers, Dockerized web app, Flutter APK/AAB).

## Migration path
1. Stabilize `feat/upgrade` with security patches and accurate documentation.
2. Introduce authenticated backend endpoints for high-risk operations (payments, double-booking prevention).
3. Gradually migrate read/write queries from thick clients to API endpoints as dedicated follow-up branches.

## Revisit triggers
- Requirement for centralized multi-tenant authorization.
- Schema divergence between SQL and Firestore causing data corruption.
- Expansion of public third-party API requirements.
