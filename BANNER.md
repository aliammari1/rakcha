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

- **README hero** — wide **2.39:1** anamorphic letterbox with the RAKCHA wordmark.
- **Social preview** — **1280×640** (GitHub Settings → Social preview).
- Generate with the `brandkit` / `imagegen-frontend-web` skills, then commit the
  PNGs to `assets/banner.png` (hero) and `assets/social-1280x640.png`.

## The image-gen prompt (use this verbatim)

> Cinematic 2.39:1 anamorphic letterbox title card for "RAKCHA", a cinema
> platform. Deep charcoal background (#16181D) with thick black letterbox bars
> top and bottom. Centered bold modern sans-serif wordmark "RAKCHA" in
> theatre-red (#B11226) with a soft off-white (#F4F1EA) projector glow behind it,
> as if lit by a film projector beam cutting through faint atmospheric haze. A
> subtle dark-red velvet curtain (#2A0E12) hinted at the left and right edges.
> Tiny, tasteful row of four monochrome platform glyphs beneath the wordmark —
> desktop monitor, browser window, smartphone, cloud/flame — suggesting a
> polyglot stack, evenly spaced, low-contrast so the wordmark stays dominant.
> Premium, minimal, editorial; no photo-real faces, no clutter, no extra text.
> Theatre-red and charcoal palette only. High detail, crisp vector-like edges.

For the **1280×640 social card**, reuse the same prompt but request a tighter
crop (wordmark + glow fills the frame, drop the platform glyph row) so it stays
legible as a small thumbnail.

## Current placeholder

`assets/banner.svg` — a self-contained SVG in the palette above, used as the
README hero until the generated PNGs land. Replace it (and set the social
preview in repo settings) when the brandkit assets are ready.
