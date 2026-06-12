# Mobile (FlutterFlow)

`apps/mobile` — a Flutter app **exported from FlutterFlow**. Most of `lib/` is
generated; CI scopes lint and coverage to **hand-written** code only.

## Prerequisites

- **Flutter** (stable channel) + Dart SDK `^3.5.0`.

## Run

```bash
cd apps/mobile
flutter pub get
flutter run
```

## Generated vs hand-written

`analysis_options.yaml` excludes the generated trees so `flutter analyze` and
coverage reflect maintained code:

```yaml
analyzer:
  exclude:
    - lib/flutter_flow/**     # FlutterFlow runtime
    - lib/custom_code/**      # FlutterFlow custom code slots
    - lib/backend/**          # generated Firestore record classes
    - lib/index.dart
    - "**/*.g.dart"
```

Hand-written, tested code lives in `lib/concierge/` (the AI concierge client).

## Test

```bash
flutter test --coverage
```

CI: `.github/workflows/ci-mobile.yml` (flutter analyze + test + Codecov,
`dart format` check on the hand-written dirs).

## AI concierge client

`lib/concierge/concierge_client.dart` is a hand-written HTTP client for the
`cinemaConcierge` Firebase callable (see
[Firebase functions & AI](functions.md)). It posts a prompt + favorite genres
and parses the film recommendations. It calls the callable **directly** — the
mobile app does not go through the web app.
