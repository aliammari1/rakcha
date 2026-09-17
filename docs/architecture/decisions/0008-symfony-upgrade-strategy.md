# ADR 0008: Symfony 6.4 LTS Retention vs. Symfony 7.4 Migration

## Title
ADR 0008: Symfony 6.4 LTS Retention vs. Symfony 7.4 Migration

## Status
Accepted

## Date
2026-09-17

## Context
`apps/web` is built on Symfony `6.4.42` LTS with Doctrine ORM 2.20 and Twig 3.28.
Evaluating an immediate major upgrade to Symfony 7.4 or 8.x revealed:
1. Symfony 6.4 LTS is maintained for bug fixes until November 2026 and security fixes until November 2027.
2. The web application contains 36 controllers, dozens of Form types, and community bundles (`scheb/2fa-bundle`, `knpuniversity/oauth2-client-bundle`, `liip/imagine-bundle`, `spatie/browsershot`, `madcoda/php-youtube-api`).
3. Moving to Symfony 7.4 drops deprecated type hints, removes deprecated methods, and requires bundle upgrades that introduce breaking changes across form handlers and authentication subscribers.
4. Performing a major framework upgrade inside `feat/upgrade` alongside multi-platform security and CI stabilization would make the pull request unreviewable and introduce high regression risks.

## Decision
Retain **Symfony 6.4 LTS** for `feat/upgrade`, keeping dependencies updated to their latest stable 6.4 patch releases (`v6.4.42`). Defer Symfony 7.4 LTS migration to a dedicated follow-up branch (`chore/symfony-7.4`).

Security vulnerabilities (such as Guzzle CVEs) and schema mapping issues are patched immediately within the 6.4 release line.

## Alternatives
1. **Immediate Migration to Symfony 7.4**: Upgrade all packages to 7.4. Rejected due to scope inflation, bundle compatibility hurdles, and risk of breaking Twig/Form integrations.
2. **Upgrade to Symfony 8**: Rejected as unstable/non-LTS for production commerce systems.

## Consequences
- The web application remains on an active, battle-tested LTS foundation with predictable behavior.
- PR diff size remains focused on security, correctness, and architecture fixes.

## Security implications
Symfony 6.4 LTS receives security advisories and CVE patches through November 2027. Security headers and CSP are already enforced via `nelmio/security-bundle`.

## Operational implications
PHP runtime constraint `>=8.2` is satisfied by modern hosts (environment runs PHP 8.4).

## Migration path
1. Run `composer phpstan` and resolve deprecation notices logged in dev mode.
2. Upgrade Doctrine ORM 2 to ORM 3 on a separate test branch.
3. Open a dedicated PR `chore/symfony-7.4` after `feat/upgrade` merges.

## Revisit triggers
- Approaching Symfony 6.4 security maintenance end date (mid-2027).
- Upstream bundle dropping support for Symfony 6.4.
