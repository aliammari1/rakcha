## 2025-05-15 - Redundant findAll() calls in Controllers
**Learning:** Multiple calls to `$repository->findAll()` within a single controller method, especially inside loops or for both count and iteration, lead to redundant database queries and expensive entity hydration.
**Action:** Always fetch the entity collection into a variable once if it's needed multiple times in a method.
