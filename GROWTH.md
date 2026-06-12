# GROWTH.md — discoverability & launch kit for RAKCHA

This is the founder/maintainer playbook for making RAKCHA discoverable. Nothing
here is marketing fluff: the honest hook is the **rare polyglot stack** (one
cinema domain across JavaFX desktop + Symfony/Twig web + FlutterFlow mobile +
Firebase AI concierge) plus the **downloadable jpackage native installers**.
There is intentionally **no "API-first / one-spec-many-clients"** claim — the
old `openapi.yaml` was a stub and was removed; the apps are independent.

> ⭐ If you build cross-platform or polyglot apps, **star the repo** — it's the
> reference for running one product across three independent stacks.

---

## GitHub repo settings (do these once, in the web UI)

**Topics** (Settings → Topics — copy/paste):

```
javafx  symfony  flutter  firebase  cinema  monorepo  jpackage  langchain
twig  php  flutterflow  langgraph  anthropic  doctrine  maven  desktop-app
mkdocs  movies  polyglot
```

**About** (Settings → repo description):

> One cinema platform, three independent apps: a JavaFX 21 desktop client
> (downloadable .msi/.dmg/.deb), a Symfony/Twig web app, and a FlutterFlow
> mobile app — sharing a Firebase AI concierge. Polyglot monorepo.

**Social preview** (Settings → Social preview): generate the 1280×640 PNG from
the prompt in [`BANNER.md`](BANNER.md) and upload it.

---

## The hook, one line per surface

- **Repo headline:** "A polyglot cinema platform — JavaFX desktop + Symfony/Twig
  web + FlutterFlow mobile, sharing one Firebase AI concierge."
- **Desktop:** "Download a real native installer — .msi / .dmg / .deb — built by
  jpackage and SLSA-attested." (the strongest demo magnet here)
- **Web:** "Classic server-rendered Symfony/Twig MVC — 36 controllers, 71
  templates — deployable to Render's free tier."
- **Mobile:** "A FlutterFlow-exported app wired to Firebase + the AI concierge."
- **AI:** "A LangGraph + Anthropic film concierge, App Check-enforced, consumed
  independently by the web and mobile apps — no shared REST API."

---

## Content / launch plan (you post these)

### Flagship dev.to / Hashnode post
**Title:** "I run a cinema platform across 3 independent stacks (JavaFX, Symfony,
Flutter) — here's why I *didn't* build one shared API."
Personal-story framing (cited as ~3× the upvotes of feature-list titles). Cover:
why one shared API was the wrong call for independent products; the JDBC desktop
vs Twig web vs Firebase mobile split; how the one real cross-app integration (the
AI concierge callable) works; shipping native installers with jpackage. End with
the ⭐ CTA and `canonical_url` back to the repo.

### Subreddits (build karma first; "I built X because Y frustrated me")
- **r/SymfonyFramework** — the Twig MVC app + NelmioSecurityBundle CSP writeup.
- **r/JavaFX** — "shipping JavaFX as downloadable .msi/.dmg/.deb with jpackage."
- **r/FlutterDev** — the FlutterFlow → Firebase → AI-concierge wiring.
- (secondary) r/PHP, r/java.

### Show HN
**Title:** "Show HN: JavaFX cinema app you can download as a native installer
(.msi/.dmg/.deb)". Seed the first comment with the jpackage matrix + SLSA
provenance detail; reply within the hour. Post Tue–Thu ~13:00–16:00 UTC.

### Awesome-list submission lines (open the PRs)
- **awesome-javafx** — `RAKCHA — a polyglot cinema platform whose JavaFX 21
  desktop client ships as downloadable, SLSA-attested .msi/.dmg/.deb installers
  (jpackage matrix).`
- **awesome-flutter** (Apps / Open Source) — `RAKCHA — the FlutterFlow mobile
  app of a polyglot cinema platform, wired to Firebase and a LangGraph+Anthropic
  AI concierge.`
- (optional) **awesome-symfony** — `RAKCHA web — a server-rendered Symfony/Twig
  cinema MVC (36 controllers, 71 templates) with a NelmioSecurityBundle CSP.`

### FlutterFlow showcase
Submit the mobile app to the **FlutterFlow community showcase** (screens + the
AI-concierge flow) — a discovery channel unique to FlutterFlow projects.

---

## Cross-link hub

Add RAKCHA to your profile README's project hub and give it a "Related projects"
footer (other AI / full-stack repos). Internal backlinks are the cheapest
compounding star lever — your second repo makes your first discoverable.

---

## What's deliberately NOT claimed

- ❌ "API-first" / "one spec, many clients" — the apps are independent; the
  OpenAPI stub was removed.
- ❌ "Runs on Cloudflare" for the web app — Workers run JS/WASM, not PHP; only
  the **docs** go to CF Pages, the Symfony app targets **Render**.

Keeping these honest is part of the pitch: the interesting story is the polyglot
reality, not a synthetic unified API.
