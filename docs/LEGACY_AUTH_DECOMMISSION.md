# Legacy authentication decommission gate

The legacy session in `MinyooRepository` must not be the production authority.

## Current legacy sources to remove
- `lo2ma_session_prefs`
- `is_logged_in`
- `user_role`
- `user_name`
- `user_phone`
- `rest_name`, `rest_phone`, `rest_area`, `rest_cuisine`, `rest_logo`, `rest_status`
- `setUserRole()` as an authorization mechanism
- `login()` / `registerCustomer()` / `registerCourier()` as identity authorities
- `logoutSession()` as the production sign-out authority

## Removal gate
The legacy paths can be deleted only after:
1. `ProductionAuthScreen` is the only auth UI entry point.
2. `ProductionAuthGate` is the app entry/session gate.
3. Firebase Auth persistence restores sessions after process death/relaunch.
4. `users/{uid}` is created for every registered user.
5. The role is resolved from the trusted profile and cannot be changed by the client.
6. Customer, restaurant, courier, and admin routing has been verified.
7. Firestore rules are enabled in the production Firebase project.
8. Existing local users have a documented migration path or are required to sign in again.

Do not delete the legacy code before all eight conditions are satisfied.
