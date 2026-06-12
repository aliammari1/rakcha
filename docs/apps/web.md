# Web (Symfony 6.4 + Twig)

`apps/web` — a **server-rendered MVC** web app built on **Symfony 6.4**. The
controllers render **Twig** templates to HTML; Doctrine ORM handles persistence.
This is a classic MVC site, **not** an API.

## Prerequisites

- **PHP 8.2+** (CI matrix: 8.2 / 8.3 / 8.4) with `ctype, curl, iconv, intl,
  pdo_sqlite, gd, mbstring, zip`.
- **Composer 2**.

There is no Node/Encore build — the dead Symfony Encore/JS toolchain was removed.
Front-end assets are loaded from a jsDelivr CDN (see `templates/base.html.twig`).

## Run

```bash
cd apps/web
composer install
cp .env.example .env            # set DATABASE_URL etc.
php bin/console doctrine:migrations:migrate
symfony server:start            # or: php -S 127.0.0.1:8000 -t public
# visit http://localhost:8000
```

The default `DATABASE_URL` is SQLite (`data/data_prod.db`); MySQL and PostgreSQL
DSNs are provided (commented) in `.env`.

## Quality gates

```bash
composer test       # PHPUnit (phpunit.xml.dist)
composer phpstan     # PHPStan + phpstan-symfony (baseline in phpstan-baseline.neon)
composer cs-check    # php-cs-fixer dry-run (@Symfony ruleset)
composer cs-fix      # apply fixes
```

CI: `.github/workflows/ci-web.yml` (setup-php 8.2/8.3/8.4 → composer → php -l →
PHPStan → php-cs-fixer via cs2pr → PHPUnit → Codecov).

## Twig page map

The web app is organized into a **front office** (`templates/front/`), a
**back office / admin** (`templates/back/`), and **auth** flows
(`templates/login`, `registration`, `reset_password`, `security`). Across
**36 controllers** rendering **71 templates**, the major route groups are:

| Controller prefix | Purpose | Example routes |
|---|---|---|
| `FilmController` (`/film`) | Film catalog CRUD + show | `app_film_index`, `app_film_show` |
| `CinemaController` | Cinema management | cinema list / detail / dashboard |
| `CategoryController`, `CategoriesController` | Film/genre categories | category tables (admin) |
| `ProduitController`, `CategorieProduitController` | E-commerce products | product client + admin tables |
| `CommandeController`, `CommandeitemController`, `PanierController` | Cart & orders | checkout, cart total (AJAX) |
| `SeanceController`, `SalleController`, `SeatController` | Showtimes & seating | reservation flow |
| `ActorController`, `ActorfilmController`, `SeriesController`, `EpisodesController` | Cast & series | actor tables, series catalog |
| `Avis*`, `Commentaire*`, `Feedback`, `Ratingfilm` | Reviews / ratings | sentiment-scored reviews |
| `FriendshipsController`, `FavorisController` | Social / bookmarks | bookmark toggle (AJAX) |
| `SecurityController`, `RegistrationController`, `ResetPasswordController` | Auth + 2FA | login, register, reset |

!!! note "AJAX helpers, not an API"
    A few endpoints return `JsonResponse` (cart total, bookmark toggle). These
    are page-support helpers for the rendered UI — there is no public REST API.
