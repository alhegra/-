# Production session contract

`ProductionSessionManager` is the boundary between Firebase identity and application authorization.

## Rules

1. Firebase UID is the only client identity authority.
2. The role is loaded from `users/{uid}` in Firestore.
3. Local preferences must never grant or change a role.
4. A missing profile or missing role is treated as unauthorized.
5. Sensitive operations must still be enforced by Firestore/backend rules; client checks are UX only.
6. `setUserRole()` and role-switcher UI must not be used for production authorization.
7. Legacy local password authentication must be removed after UI migration and data migration are complete.
