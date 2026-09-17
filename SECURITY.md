# Security Policy

## Reporting a vulnerability

**Please do not open a public GitHub issue for security vulnerabilities.**

Report privately via either:

- **GitHub Security Advisories** — use the "Report a vulnerability" button under
  the repository's **Security** tab (preferred — keeps the report private).
- **Email** — `contact@aliammari.com` with subject `[SECURITY] <component>`.

Please include the affected component (desktop / web / mobile / functions),
steps to reproduce, and the impact. We aim to acknowledge within a few days.
There is no bug-bounty program; responsible disclosure is appreciated and we're
happy to credit you.

## Scope

RAKCHA is a portfolio/capstone project maintained on a best-effort basis. The
**latest commit on the default branch** is what receives fixes — there are no
back-ported releases or formal LTS lines.

| Component | Notes |
|---|---|
| Desktop (`apps/desktop`) | JavaFX client; reads the DB directly over JDBC |
| Web (`apps/web`) | Symfony 6.4 / Twig server-rendered MVC |
| Mobile (`apps/mobile`) | FlutterFlow-exported app on Firebase |
| Functions | Node Firebase callables (AI concierge) |

## Automated checks in CI

The following run in CI and gate merges (see `.github/workflows/`):

- **CodeQL** — `java-kotlin` + `javascript`. (PHP has no CodeQL support, so the
  Symfony app is covered by **PHPStan + phpstan-symfony** instead.)
- **Trivy** — container image and lockfile scanning (SHA-pinned action).
- **php-cs-fixer / PHPStan** — static analysis for the web app.

## Notes for contributors

- Never commit real secrets. The committed `apps/web/.env` contains only Symfony
  **development defaults** (e.g. a placeholder `APP_SECRET`, `MAILER_DSN=null`);
  use `.env.local` (git-ignored) for real credentials.
- Use parameterized queries / the ORM — never string-concatenate user input into
  SQL. The desktop app uses prepared statements; the web app uses Doctrine.
- Escape output in Twig (auto-escaping is on by default — don't disable it
  without reason).
