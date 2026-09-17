# ADR 0004: OpenAPI Specification and Client Generation Status

## Title
ADR 0004: OpenAPI Specification and Client Generation Status

## Status
Accepted

## Date
2026-09-17

## Context
Early project documentation claimed `shared/api-spec/openapi.yaml` was the contract for code generation across all platforms.
Upon audit:
- The specification was a 144-line incomplete stub detailing only a few unserved paths.
- `openapitools.json` had no generator configurations wired.
- Neither JavaFX nor Flutter utilized generated SDKs or HTTP clients for Symfony.
- Maintaining a disconnected specification resulted in confusion and false assumptions during dependency and security auditing.

## Decision
1. Acknowledge the deletion of the vaporware OpenAPI stub in commit `f733921b` as the correct, truth-aligned action for this branch.
2. Formally declare that OpenAPI code generation is not currently an active contract mechanism in Rakcha.
3. If an authoritative REST API is established in a future architectural phase, the OpenAPI specification must be generated directly from Symfony controller route attributes (e.g., via NelmioApiDocBundle) to guarantee continuous contract synchronization.

## Alternatives
1. **Flesh Out Hand-Written OpenAPI Now**: Spend time drafting a 2000-line OpenAPI YAML file for Symfony's HTML routes. Rejected because Symfony routes return HTML/Twig rather than standard REST JSON, making it deceptive.
2. **Commit Stub Again**: Re-add the 144-line stub. Rejected as dead cruft.

## Consequences
- Clean repository without unused schema files or broken generator configs.
- Clear roadmap for future API contract generation.

## Security implications
Prevents developers from assuming client code is bounded by a documented API contract when clients are actually performing direct database or Firestore operations.

## Operational implications
No OpenAPI generation step required in build pipelines or CI.

## Migration path
When designing Rakcha API v1:
1. Install and configure NelmioApiDocBundle in `apps/web`.
2. Annotate controller actions with schema types.
3. Export generated `openapi.json` during CI.
4. Wire OpenAPI Generator with pinned versioning to build client packages.

## Revisit triggers
- Creation of dedicated `/api/v1/` headless endpoints in Symfony.
