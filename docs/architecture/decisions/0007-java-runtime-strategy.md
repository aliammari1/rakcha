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

While Java 25 LTS and JavaFX 25 LTS (such as 25.0.4) are officially released and bytecode tools like JaCoCo 0.8.15 provide Java 26 support, evaluating a direct runtime baseline bump from Java 21 to Java 25 revealed critical ecosystem stability factors:
1. **Headless Test Framework Compatibility**: Headless automated UI testing relies on TestFX and Monocle (`org.pdfsam:javafx-monocle:21`). The Monocle headless glass implementation is heavily coupled to JavaFX 21 internal APIs, and running headless tests against JavaFX 25 causes initialization errors in CI virtual framebuffers.
2. **Third-Party UI Library Validation**: Key third-party UI components (ControlsFX 11.2.1, FormsFX, AnimateFX, JFoenix) were compiled and rigorously validated against the Java 21 LTS module system.
3. **Cross-Platform Native Packaging (`jpackage`)**: The current deployment pipeline produces self-contained native bundles (Windows MSI, Linux DEB/RPM, macOS DMG/PKG) using BellSoft Liberica JDK 21 Full (bundling JavaFX 21). Upgrading the target runtime requires re-qualifying all platform-specific native runtime images and runtime installer generation.
4. **Long-Term Support Horizon**: Java 21 LTS receives active enterprise and vendor support through at least September 2029, providing a stable, production-grade foundation without requiring premature runtime churn.

## Decision
Retain **Java 21 LTS** and **JavaFX 21 LTS (21.0.7)** as the active compilation, testing, and packaging baseline for `apps/desktop`. Defer major runtime baseline upgrade to Java 25 until the headless test matrix (TestFX/Monocle), third-party UI libraries, and platform-specific `jpackage` bundling have been comprehensively qualified on a dedicated branch.

## Alternatives
1. **Immediate Upgrade to Java 25 / JavaFX 25**: Upgrade `apps/desktop` compiler target to 25. Deferred due to test matrix instability with Monocle headless test execution and the requirement to re-verify native `jpackage` packaging across all target operating systems.
2. **Downgrade to Java 17**: Rejected since Java 21 LTS features (virtual threads, record patterns, modern switch) are actively utilized in the codebase.

## Consequences
- Deterministic builds and proven native packaging across Windows (MSI), Linux (DEB), and macOS (DMG/PKG).
- Fully stable CI testing with xvfb and Monocle headless runners.
- Elimination of regressions in third-party JavaFX component libraries.

## Security implications
Java 21 LTS receives ongoing security patches from OpenJDK vendors through at least September 2029.

## Operational implications
Developers and CI runners must use JDK 21 (Liberica Full recommended) for local execution, test suite runs, and native jpackage bundle builds.

## Migration path
Track TestFX Monocle headless support for JavaFX 25. Once qualified, perform the upgrade in a dedicated stabilization branch (`chore/java-25-qualification`) with full cross-platform installer testing.

## Revisit triggers
- General availability of a mature TestFX / Monocle headless runner supporting JavaFX 25+.
- Scheduled annual review of the desktop packaging and test matrix.
- End-of-life or deprecation notices for JavaFX 21 community patches.
