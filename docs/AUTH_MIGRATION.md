# Authentication migration

## Production rules

1. Firebase Authentication is the identity provider.
2. Passwords are never stored in Room, SharedPreferences, Firestore, or application logs.
3. A newly registered account is always `CUSTOMER`.
4. `RESTAURANT_OWNER`, `COURIER`, and `ADMIN` are elevated roles and cannot be selected by the client.
5. The authoritative role lives in `users/{uid}.role` and must eventually be protected by trusted backend/Admin tooling.
6. UI navigation is not an authorization boundary. Firestore/backend authorization must enforce every sensitive operation.
7. The legacy `lo2ma_session_prefs` session must be removed after the UI is migrated to Firebase Auth.

## Current implementation

`ProductionAuth.kt` provides:

- Firebase email/password registration.
- Firebase email/password sign-in.
- Automatic customer-only account creation.
- Server-side role lookup from `users/{uid}`.
- Firebase sign-out.

## Required next integration

- Replace the legacy repository login/session code with `ProductionAuth`.
- Load the user profile from `users/{uid}` after authentication.
- Remove client-side role switching.
- Add backend-controlled role elevation for restaurant owners, couriers, and admins.
- Add App Check and authentication-aware Firestore rules before production rollout.
- Add automated tests for every role and sensitive Firestore operation.
