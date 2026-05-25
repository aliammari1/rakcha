## 2026-05-25 - Redundant findAll() Performance Anti-pattern
**Learning:** A pervasive performance anti-pattern in this codebase is calling `$repository->findAll()` multiple times within a single controller method (e.g., for loop counts, indexing inside a loop, and view rendering), which leads to significant redundant hydration overhead and unnecessary O(N) database queries.
**Action:** Always fetch entity collections once into a variable before using them in loops or multiple times in a method. This is especially critical when generating multiple form views for a list of entities.
