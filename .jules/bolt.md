## 2024-05-27 - [Optimization of Users and Cart Controllers]
**Learning:** Found a pervasive N+1 anti-pattern in the codebase where `findAll()` was called multiple times within the same controller method, even inside loop conditions. This leads to $O(N)$ redundant database queries and hydration overhead.
**Action:** Always fetch entity collections once into a variable before using them in loops or multiple operations. Use `findBy` with an array of IDs to batch fetch entities and eliminate N+1 query problems in loops.

## 2024-06-12 - [Resolving CI Duplication and Security Issues]
**Learning:** Applying the same optimization across multiple controller methods can trigger SonarCloud duplication errors. Also, batch queries using user-supplied IDs must include ownership checks to maintain security ratings.
**Action:** Extract shared data preparation logic into private helper methods. Always include ownership filters (e.g., `'idclient' => $this->getUser()`) in batch queries.
