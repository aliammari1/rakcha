# ADR 0009: Monorepo CI/CD Architecture and Supply Chain Strategy

## Title
ADR 0009: Monorepo CI/CD Architecture and Supply Chain Strategy

## Status
Accepted

## Date
2026-09-17

## Context
Rakcha historically had fragmented and non-functional CI:
- Workflows nested under `apps/desktop/.github/workflows/` (`build-and-deploy.yml`, `ci.yml`, `codeql.yml`, etc.) were ignored by GitHub Actions, which only parses `.github/workflows/` at repository root.
- Uncoordinated dependency automation: Dependabot and multiple per-app `renovate.json` files collided.
- Absence of supply chain attestations for compiled desktop binaries.

## Decision
1. **Centralized Workflows**: Maintain all 10 active workflows strictly under root `.github/workflows/`:
   - `ci-web.yml`: Validates composer dependencies, PHP 8.2-8.4 matrix, PHPStan, php-cs-fixer, and PHPUnit.
   - `ci-desktop.yml`: Validates Maven build, headless JUnit5 / Monocle tests with xvfb, and JaCoCo coverage.
   - `ci-mobile.yml`: Validates Flutter analyze and unit tests on hand-written code.
   - `supply-chain.yml`: OSV-Scanner vulnerability detection across Maven, Composer, npm, and pub lockfiles + CycloneDX SBOM generation (`cdxgen`).
   - `codeql.yml`: Static security analysis for Java desktop and Node functions.
   - `trivy.yml`: Container and filesystem vulnerability scanning.
   - `release-desktop.yml`: Multi-platform jpackage installer build with SLSA build provenance attestations (`actions/attest-build-provenance`).
   - `release-please.yml`: Semantic versioning and changelog automation.
   - `docs.yml`: MkDocs documentation site deployment to Cloudflare Pages.
   - `claude-review.yml`: Automated pull request reviews.
2. **Unified Renovate**: Single root `renovate.json` replaces fragmented Dependabot configurations, grouping updates by ecosystem with SHA-pinned actions.
3. **No Stranded Workflows**: Ensure all nested `.github/` directories remain removed.

## Alternatives
1. **Single Monolithic Workflow**: Combine all three apps into a single 500-line `ci.yml`. Rejected because path filtering and runtime setups (PHP + JavaFX + Flutter) are cleaner and run faster as decoupled parallel jobs.
2. **Re-introduce Dependabot**: Rejected because Renovate provides superior multi-ecosystem lockfile grouping and SHA-pinning support.

## Consequences
- GitHub executes all CI pipelines predictably on every PR.
- Downloader can cryptographically verify native installers via SLSA build provenance (`gh attestation verify`).

## Security implications
Enforces GitHub Actions least-privilege permissions (`contents: read` default) and stops supply-chain tampering.

## Operational implications
CI time is optimized by running path-filtered parallel checks for web, desktop, and mobile.

## Migration path
Periodically update action SHAs via Renovate pull requests.

## Revisit triggers
- Adoption of additional apps or microservices in the monorepo.
- Transition from Cloudflare Pages to alternative docs hosting.
