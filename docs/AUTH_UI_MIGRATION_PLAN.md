# Authentication UI migration

## Production contract

The authentication UI must use Firebase Authentication as the identity provider. The client must never choose or persist privileged roles as an authority.

### Login
- Collect email and password.
- Call ProductionAuth.signIn(email, password).
- Resolve the role from the authenticated user's server-side profile (`users/{uid}`).
- Route the user only to the role returned by the trusted profile.

### Customer registration
- Collect name, email, phone, city, and password.
- Call ProductionAuth.registerCustomer(...).
- New public registrations are always CUSTOMER.

### Restaurant registration
- Create a Firebase-authenticated account, but create the restaurant application as PENDING.
- Do not allow the client to mark it APPROVED.
- A future backend/admin action must transition PENDING -> APPROVED.

### Courier registration
- Create a Firebase-authenticated account, but do not allow the client to grant itself courier privileges. Courier activation must be controlled by an admin/backend workflow.

## Legacy removal rule
Do not remove the existing UI callbacks until Login/Register/Session restoration are wired to ProductionAuth and verified. After that migration, repository login/register/session methods must no longer be used as authentication authority.

## Required tests
- Wrong password fails.
- Unknown email fails.
- New customer cannot select ADMIN/COURIER/RESTAURANT_OWNER.
- Local storage changes cannot elevate the authenticated role.
- Signing out clears Firebase identity.
- Reopening the app restores Firebase identity and server-side role only.
