# ADR 0003: Firebase Responsibilities and Security Rules

## Title
ADR 0003: Firebase Responsibilities and Security Rules

## Status
Accepted

## Date
2026-09-17

## Context
Mobile (`apps/mobile`) uses Firebase for authentication, Cloud Firestore, Cloud Storage, and Cloud Functions (AI Concierge).
Auditing `apps/mobile/firebase/firestore.rules` revealed that almost all collection rules were wide open:
`allow create, read, write, delete: if true;`
This permitted unauthenticated public users to read, overwrite, and delete cinema records, series, films, carts, and reservations across the entire project.

## Decision
1. **Rule Hardening**: Completely lock down `firestore.rules` using least-privilege principles:
   - Public read-only access for catalog collections (`Film`, `serie`, `cinema`, `salle`, `Seance`, `CategoryProduct`, `produit`, `Event`).
   - Authenticated write protection: write operations require valid authentication tokens.
   - User document isolation: users can only modify their own profiles (`users/{userId}` where `request.auth.uid == userId`).
   - Personal collections (`cart`, `Reservation`, `Favoris`): restricted to authenticated user contexts.
2. **Cloud Functions AI Concierge**: The `cinemaConcierge` LangGraph/Anthropic callable is enforced via Firebase App Check (`runWith({ enforceAppCheck: true })`) to prevent unauthorized API quota consumption.

## Alternatives
1. **Remove Firebase Completely**: Migrate mobile to relational DB/REST API. Rejected for current branch scope due to FlutterFlow client coupling.
2. **Leave Firestore Rules Permissive**: Rejected as an unacceptable P0 security vulnerability.

## Consequences
- Data integrity of Firestore collections is safeguarded against malicious tampering or deletion.
- Mobile client operations conform to authenticated session requirements.

## Security implications
Closes critical unauthenticated data tampering and denial-of-service vulnerabilities.

## Operational implications
Developers working with the mobile app must ensure test users authenticate via Firebase Auth before performing write operations.

## Migration path
In a subsequent milestone, transition administrative updates to a trusted backend proxy rather than direct client writes.

## Revisit triggers
- Transition of mobile backend to unified REST API.
- Firebase project quota or billing adjustments.
