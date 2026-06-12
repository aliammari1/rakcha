# Desktop (JavaFX 21)

`apps/desktop` — a JavaFX 21 desktop client built with **Maven**. It connects to
the database directly via **JDBC** (HikariCP connection pool; MySQL / SQLite /
PostgreSQL drivers are all on the classpath).

## Prerequisites

- **JDK 21** with JavaFX. The CI uses **BellSoft Liberica Full** / Liberica
  `jdk+fx`, which bundles JavaFX modules.
- **Maven 3.9+**.

## Build & run

```bash
cd apps/desktop
mvn clean compile
mvn javafx:run          # launch the app (mainClass com.esprit.MainApp)
```

## Test

The `pom.xml` is already wired for headless UI testing:

- **JUnit 5** (`junit-jupiter`)
- **TestFX** + **Monocle** (headless JavaFX via the Surefire `argLine`:
  `-Dtestfx.headless=true -Dglass.platform=Monocle -Dprism.order=sw`)
- **JaCoCo** for coverage (`target/site/jacoco/jacoco.xml`)

```bash
mvn verify              # compiles, runs JUnit5/TestFX headless, writes coverage
```

CI runs this under `xvfb-run` (`.github/workflows/ci-desktop.yml`).

## Native installers (jpackage)

The `jpackage-maven-plugin` produces native installers from the bundled JDK.
The release workflow (`.github/workflows/release-desktop.yml`) builds all three
on an OS matrix and attaches them to a GitHub Release:

| OS | Installer |
|---|---|
| Windows | `.msi` |
| macOS | `.dmg` |
| Linux | `.deb` |

Trigger by pushing a tag like `desktop-v1.0.10`, or run the workflow manually.
Per-OS packaging resources live in `src/packaging/{windows,macos,linux}`.
