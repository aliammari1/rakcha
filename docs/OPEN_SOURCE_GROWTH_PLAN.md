# Open Source Growth Plan

This plan turns the repository review into a practical backlog for Rakcha.

## Quick wins

- Add screenshots and demo videos for cinema management, streaming, marketplace, QR flows, and admin tools.
- Add one-command Docker/local setup with seeded demo data.
- Add architecture documentation for backend, desktop, mobile, web, and shared data contracts.
- Add CI checks for the main frameworks in the monorepo.
- Add observability notes for logs, metrics, and tracing.
- Add `good first issue` tasks for documentation, sample data, tests, and UI polish.

## Bugs and bad practices to watch

- Duplicate business logic across desktop, mobile, web, and backend clients.
- Tight coupling between domain modules such as cinema, streaming, and commerce.
- Inconsistent permissions or role checks across clients.
- QR code and ticket state transitions that are not idempotent.
- Missing monitoring for payment, notification, and streaming failures.

## Star growth strategy

1. Add a public demo with synthetic cinema, users, products, and media metadata.
2. Show the platform modules visually above the fold in the README.
3. Add GitHub topics for cinema, streaming, e-commerce, Flutter, Symfony, and Java.
4. Publish architecture notes to make the large codebase approachable.
5. Share individual module demos with relevant communities.

## Trending-library opportunities

- Use Morphik-Core-style document/media search for streaming metadata exploration.
- Use ChainForge-style prompt testing for recommendation or chatbot experiments.
- Use Polars and Data-Formulator-style dashboards for commerce and engagement analytics.

## Suggested next PRs

- Add `docs/ARCHITECTURE.md` with bounded contexts and module ownership.
- Add CI for the primary build/test paths.
- Add sample data and demo environment setup.
