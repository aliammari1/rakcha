# RAKCHA

A **polyglot cinema platform** built as four independent components that share a
problem domain (cinema operations, film catalog, e-commerce) but **not** a
codebase or a runtime API:

| Component | Stack | Data access |
|---|---|---|
| **Desktop** (`apps/desktop`) | JavaFX 21 + Maven | **JDBC directly to the DB** |
| **Web** (`apps/web`) | Symfony 6.4 server-rendered **MVC (Twig)** | Doctrine ORM |
| **Mobile** (`apps/mobile`) | **FlutterFlow**-exported Flutter | Firebase / Firestore |
| **Functions** (`apps/mobile/firebase/functions`) | Node | Firebase Admin |

!!! warning "No shared REST API"
    These are **three independent apps**. The desktop client talks to the
    database over JDBC; it does **not** call the web app over HTTP. There is no
    "one spec → many clients" code-generation pipeline — `shared/api-spec/openapi.yaml`
    is an unimplemented stub that no controller serves. Read these docs with that
    reality in mind.

## What's here

- **[Architecture](architecture.md)** — how the three apps relate (and don't).
- **[Desktop](apps/desktop.md)** — build/run the JavaFX app, native installers.
- **[Web](apps/web.md)** — run the Symfony app + the Twig page map.
- **[Mobile](apps/mobile.md)** — the FlutterFlow app and what's hand-written.
- **[Firebase functions & AI](apps/functions.md)** — the AI cinema concierge.
- **[Database schema](database.md)** — entities and relationships.
- **[Engineering decisions](decisions.md)** — the why behind the stack.

## License

RAKCHA ships under the **RAKCHA Commercial Use License**
(`SPDX-License-Identifier: LicenseRef-RAKCHA-Commercial`). Free for educational
and personal use; commercial use requires a license. See `LICENSE`.
