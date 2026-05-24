## 2026-05-24 - Redundant Entity Collection Hydration
**Learning:** Calling `$repository->findAll()` multiple times within a single controller method (e.g., once for a loop count, once inside the loop, and once for the template) leads to redundant database queries and expensive object hydration. Even if Doctrine's identity map skips some work, the overhead of calling the method and processing the results remains.
**Action:** Always fetch entity collections once into a local variable at the beginning of the controller method and reuse that variable for all subsequent operations.
