# Engineering decisions

Short, honest notes on why RAKCHA is built the way it is.

## Why three independent apps, not one API + clients?

The original "one OpenAPI spec → Java/PHP/Dart clients" framing was **never
implemented** — the old `shared/api-spec/openapi.yaml` was a 144-line stub that
no controller served, with no generator wired, so it (and `openapitools.json`)
were **removed**. Rather than pretend, the apps are
treated as what they actually are: three independent products against a shared
domain. The desktop app uses **JDBC** (best ergonomics for a thick JavaFX
client), the web app uses **Symfony/Twig** server-side rendering (SEO-friendly,
no SPA build), and mobile uses **FlutterFlow** (rapid visual development).

## Why keep the commercial license?

RAKCHA is a capstone product the owner may monetize. It stays under the
**RAKCHA Commercial Use License** with a valid SPDX identifier
(`LicenseRef-RAKCHA-Commercial`) so GitHub shows a recognized chip while keeping
commercial rights reserved.

## Why delete the Symfony Encore/JS?

There was no `webpack.config.js`, no `assets/` directory, and the two
`encore_entry_*` Twig calls were commented out or dead — assets load from a
jsDelivr CDN. The `package.json` only carried decorative, unused front-end libs.
Deleting it removes a misleading build step; the server-rendered site is
unaffected.

## Why CodeQL excludes PHP

CodeQL has no PHP analyzer. The Symfony app's static-analysis gate is
**PHPStan + phpstan-symfony** (with a baseline so legacy code doesn't block new
code). CodeQL covers the Java desktop app and the Node functions.

## Why one Renovate instead of Dependabot + per-app configs

The repo previously ran Dependabot (with a `/app/web` path typo) **and** two
per-app `renovate.json` files — colliding update sources. Now a single root
`renovate.json` covers composer (web) / maven (desktop) / pub (mobile) / npm
(functions) / docker, with per-ecosystem grouping and SHA-pinned actions.

## Why the AI concierge is a Firebase callable, not a REST endpoint

It is built on the LangGraph + `@langchain/anthropic` dependencies already in
the functions package, and both the mobile and web apps can reach a Firebase
callable directly over HTTPS. A callable keeps the integration consistent with
the "independent apps" reality — no new shared REST surface to maintain. Model:
**`claude-haiku-4-5`** (fast, cheap, ample for short recommendation prompts).

## Why SLSA provenance + OSV-Scanner + CycloneDX SBOM

The CI already scanned (CodeQL, Trivy) but emitted no **attestations**. The
release workflow now signs **SLSA build provenance**
(`actions/attest-build-provenance`) for each jpackage installer, so a downloader
can prove a `.msi`/`.dmg`/`.deb` came from this repo's workflow
(`gh attestation verify <installer> --repo aliammari1/rakcha`). A single
**OSV-Scanner** pass covers all four lockfiles (`pom.xml`, `composer.lock`,
`pubspec.lock`, `package-lock.json`), and **cdxgen** emits one aggregated
**CycloneDX** SBOM across the Maven/Composer/npm/pub ecosystems.

## Why a CSP via NelmioSecurityBundle (with `unsafe-inline`, for now)

The Symfony app shipped **no CSP** while loading from several CDNs (jsDelivr
dominant). NelmioSecurityBundle now adds a Content-Security-Policy plus HSTS,
nosniff, and Referrer-Policy. The 71 Twig templates contain ~46 inline
`<script>` blocks and many inline styles, so a strict nonce-only CSP would
require rewriting every template; the baseline CSP instead **locks the host
allowlist** to the CDNs actually used and keeps `'unsafe-inline'` as a
documented, temporary allowance. Dropping `'unsafe-inline'` via nonces is a
tracked backlog item. Even so, this blocks unlisted hosts/objects/frames — a
large improvement over no CSP.

## Why App Check is enforced on the concierge callable

The `cinemaConcierge` callable fans out to the paid Anthropic API. It now runs
with `runWith({ enforceAppCheck: true })` (gen-1) and additionally asserts
`context.app`, so only the real apps (with valid App Check tokens) can spend the
budget — anti-abuse on a money-touching AI endpoint. (gen-1 → gen-2 migration is
a tracked follow-up alongside Symfony 6.4→7.4 and PIT/Infection mutation
testing.)

## Cloudflare / hosting honesty

Symfony (PHP) cannot run on Cloudflare Workers. The docs site (this MkDocs build)
deploys to **Cloudflare Pages**; the PHP web app needs a PHP host (CF Containers
beta or any PHP host). The desktop app ships as native installers via GitHub
Releases. These constraints are stated rather than glossed over.
