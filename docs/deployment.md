# Deployment & live demos

RAKCHA is **three independent apps + Firebase functions**, so there is no single
"deploy" button — each app ships its own way. This page documents a concrete,
**free-tier** path for each.

!!! warning "Why not Cloudflare for the web app?"
    Cloudflare Workers/Pages run **JavaScript/WASM, not PHP**. The Symfony web
    app therefore **cannot** run on Cloudflare. Only the **docs site** (static
    mkdocs build) goes to **Cloudflare Pages**
    (`.github/workflows/docs.yml`, gated on `CLOUDFLARE_*` secrets). The PHP app
    needs a real PHP host — see below.

---

## Web app (Symfony / Twig) — the hard one

The web app needs **PHP 8.2+ + a relational DB**. The 2026 free-tier landscape
changed: **Fly.io** and **Koyeb** dropped free compute, **Railway** is now a
30-day **$5 trial** (no permanent free tier), and **Render** has no *native* PHP
runtime. The practical, honest options:

### Recommended: Render (Docker web service + free Postgres)

Render runs PHP via a **Docker image** — and this repo already ships
[`apps/web/Dockerfile`](https://github.com/aliammari1/rakcha/blob/main/apps/web/Dockerfile)
(`php:8.2-fpm` + nginx). Free tier (2026): **750 instance-hours/month**, no
credit card, free TLS, free **PostgreSQL** (1 GB; note it **expires 30 days**
after creation, 14-day grace). Symfony's `DATABASE_URL` already supports
PostgreSQL out of the box.

Steps:

1. Push the repo to GitHub; sign in to [render.com](https://render.com) with GitHub.
2. **New → Web Service**, point it at the repo, set **Root Directory** to
   `apps/web`, runtime **Docker** (Render auto-detects the Dockerfile).
3. **New → PostgreSQL** (free). Copy its **Internal Database URL**.
4. On the web service, add env vars:
   - `APP_ENV=prod`, `APP_SECRET=<generate one>`
   - `DATABASE_URL=postgresql://<...from step 3...>?serverVersion=16&charset=utf8`
5. Add a **Pre-Deploy Command** (or a startup hook) to migrate:
   `php bin/console doctrine:migrations:migrate --no-interaction`.
6. Deploy. First request after 15 min idle cold-starts (~1 min) — fine for a demo.

> Trade-off: free Render web services **spin down after 15 minutes** of
> inactivity, and the free Postgres **expires after 30 days** (re-create it for a
> long-lived demo). Acceptable for a portfolio demo; not for production.

### One-click alternative: Railway (best Symfony DX, but trial-only)

Railway has a [first-class Symfony guide](https://docs.railway.com/guides/symfony)
and a **MySQL** plugin, deployable by one-click template, GitHub repo, or CLI.
The catch in 2026: the "free" tier is a **one-time $5 / 30-day trial**, then
Hobby is **$5/mo**. Use it for a quick MySQL-backed demo; document it honestly as
trial-grade.

```bash
# Railway CLI path
railway login
railway init
railway add --plugin mysql
railway up                      # builds via the apps/web Dockerfile / nixpacks
railway run php bin/console doctrine:migrations:migrate --no-interaction
```

### Zero-Docker, permanently-free fallback: InfinityFree

[InfinityFree](https://infinityfree.com) is genuinely free forever (no card,
PHP 8.3, 400 MySQL databases, free Let's Encrypt SSL). **Caveats that matter for
Symfony:** no SSH, no Composer, no cron, and you **cannot change the document
root**. You must build `vendor/` locally, upload `htdocs/`, and add `.htaccess`
rewrites to route everything into `public/index.php` (install
`composer require symfony/apache-pack` locally first). Workable for a static-ish
demo, clunky for anything dynamic — listed for completeness, not recommended.

---

## Desktop (JavaFX) — downloadable installers via GitHub Releases

The desktop "demo" is a **native installer** users download and run. This is
already wired:
[`.github/workflows/release-desktop.yml`](https://github.com/aliammari1/rakcha/blob/main/.github/workflows/release-desktop.yml)
runs a `jpackage` OS matrix and attaches the artifacts to a GitHub Release:

| OS | Installer |
|---|---|
| Windows | `.msi` |
| macOS | `.dmg` |
| Linux | `.deb` |

Trigger by pushing a tag like `desktop-v1.0.10` (or run the workflow manually),
then link the **Releases** page from the README. See
[Desktop docs](apps/desktop.md#native-installers-jpackage).

---

## Mobile (FlutterFlow) — APK + TestFlight

- **Android:** `flutter build apk --release` (or `appbundle`) and attach the
  `.apk` to a GitHub Release, or distribute via **Firebase App Distribution**
  (the app is already a Firebase project). A QR/download link in the README is
  the demo.
- **iOS:** `flutter build ipa` then upload to **App Store Connect → TestFlight**
  for an invite-only beta (requires an Apple Developer account). FlutterFlow can
  also publish builds directly.
- The app talks to **Firebase/Firestore** and the `cinemaConcierge` callable —
  no separate backend to host for the mobile demo.

---

## Docs (this site) — Cloudflare Pages

The mkdocs-material site builds with `mkdocs build --strict` and deploys to
**Cloudflare Pages** via `.github/workflows/docs.yml` (gated on
`CLOUDFLARE_API_TOKEN` / `CLOUDFLARE_ACCOUNT_ID`). This is the only RAKCHA
component that fits Cloudflare's free tier, because it's pure static output.

---

## Summary

| Component | Demo mechanism | Free? |
|---|---|---|
| Web (Symfony) | **Render** Docker + free Postgres (or Railway trial / InfinityFree) | Render: yes (with spin-down + 30-day DB) |
| Desktop (JavaFX) | **GitHub Releases** jpackage installers | yes |
| Mobile (FlutterFlow) | **APK** on Releases / Firebase App Distribution; **TestFlight** for iOS | yes (iOS needs Apple Dev account) |
| Docs | **Cloudflare Pages** | yes |

*Hosting research current as of June 2026.*
