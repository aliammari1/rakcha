## 2026-05-26 - [Redundant findAll() hydration overhead]
**Learning:** Calling `$repository->findAll()` multiple times within a single controller method (e.g., for loop counts, indexing, and template rendering) causes redundant O(N) database queries and hydration overhead. Fetching once into a variable is a significant performance win.
**Action:** Always fetch entity collections once into a variable before using them in loops or passing them to views. Use `array_map` for concise logic when generating multiple form views.
