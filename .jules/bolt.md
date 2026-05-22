## 2025-05-22 - [Optimized entity fetching in loops]
**Learning:** Calling $repository->findAll() within a loop to generate forms for each entity is an O(N^2) anti-pattern in Doctrine, as it may trigger redundant hydrations or queries depending on the cache.
**Action:** Always fetch the collection once into a variable and iterate over the local variable.
