## 2024-05-27 - [Optimization of Users and Cart Controllers]
**Learning:** Found a pervasive N+1 anti-pattern in the codebase where `findAll()` was called multiple times within the same controller method, even inside loop conditions. This leads to $O(N)$ redundant database queries and hydration overhead.
**Action:** Always fetch entity collections once into a variable before using them in loops or multiple operations. Use `findBy` with an array of IDs to batch fetch entities and eliminate N+1 query problems in loops.
