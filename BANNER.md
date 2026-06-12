# RAKCHA — Brand & Banner direction

> **Status: TODO.** The final hero/social images are not yet generated. This
> file is the art-direction brief; `assets/banner.svg` is a committed local
> placeholder so the README hero never 404s or rate-limits.

## Concept

**Cinema letterbox.** A 2.39:1 anamorphic frame — the widescreen "scope" ratio —
with theatre-red and charcoal. RAKCHA runs a cinema across three independent
stacks (JavaFX desktop · Symfony/Twig web · FlutterFlow mobile · Firebase), so
the banner should read like the opening title card of a film: bold wordmark,
deep letterbox bars, a hint of projector glow.

## Palette

| Role | Hex |
|---|---|
| Theatre red (primary) | `#B11226` |
| Deep charcoal (background) | `#16181D` |
| Curtain shadow | `#2A0E12` |
| Screen glow / off-white | `#F4F1EA` |

## Deliverables (to generate)

- **Social preview** — 1280×640 (GitHub Settings → Social preview).
- **README hero** — wide 2.39:1 letterbox with the RAKCHA wordmark.
- Use the `brandkit` skill for the identity board and the
  `imagegen-frontend-web` / `-mobile` skills for the hero + device mockups.

## Current placeholder

`assets/banner.svg` — a self-contained SVG in the palette above, used as the
README hero until the generated PNGs land. Replace it (and set the social
preview in repo settings) when the brandkit assets are ready.
