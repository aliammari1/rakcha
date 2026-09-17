# ADR 0007: Java and JavaFX Runtime LTS Strategy

## Title
ADR 0007: Java and JavaFX Runtime LTS Strategy

## Status
Accepted

## Date
2026-09-17

## Context
Rakcha Desktop is built with JavaFX and Maven. The project currently targets:
- `maven.compiler.source`: 21
- `maven.compiler.target`: 21
- `org.openjfx:javafx-*`: 21.0.7 (JavaFX 21 LTS patch)

Evaluating an upgrade to Java 25 LTS revealed:
1. JavaFX ecosystem and third-party UI libraries (ControlsFX, JFoenix, AnimateFX, FormsFX) have established stability on Java 21 LTS.
2. Bytecode instrumentation tools such as JaCoCo 0.8.12 fail with `IllegalArgumentException: Unsupported class file major version 69` when executed against Java 25 class files.
3. TestFX and Monocle headless test frameworks are specifically verified with JavaFX 21 (`org.pdfsam:javafx-monocle:21`).
4. CI uses BellSoft Liberica JDK 21 Full (with bundled JavaFX), which is an officially supported LTS distribution with multi-year enterprise maintenance.

## Decision
Retain **Java 21 LTS** and **JavaFX 21 LTS (21.0.7)** as the active compilation and packaging baseline for `apps/desktop`. Defer major runtime upgrade to Java 25 until JavaFX 25, JaCoCo, and downstream UI/TestFX plugins offer full out-of-the-box compatibility.

## Alternatives
1. **Force Upgrade to Java 25**: Upgrade compiler to 25. Rejected due to JaCoCo test failures, breaking changes in byte-code tools, and lack of mature JavaFX 25 release artifacts.
2. **Downgrade to Java 17**: Rejected since Java 21 LTS features (virtual threads, record patterns, modern switch) are already utilized and supported.

## Consequences
- Deterministic builds and packaging across Windows (MSI), Linux (DEB), and macOS (DMG/PKG).
- Stable CI testing with xvfb and Monocle headless runners.
- Low maintenance overhead on desktop build plugins.

## Security implications
Java 21 LTS receives ongoing security patches from OpenJDK and vendors through at least September 2029.

## Operational implications
Developers and CI runners must use JDK 21 (Liberica Full recommended) for local execution and native jpackage bundle builds.

## Migration path
Track JaCoCo >=0.8.13 and JavaFX 25 releases. When stable, evaluate in follow-up branch `chore/java-25`.

## Revisit triggers
- End-of-life for JavaFX 21 community patches.
- Release of official JavaFX 25 LTS and compatible TestFX releases.
