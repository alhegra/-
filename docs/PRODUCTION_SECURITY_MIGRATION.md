# Lo2ma Production Security Migration

This branch starts the P0 production-hardening work. The current Android app still contains local/demo authorization and local order/payment behavior, so the app must not be released to production until the gates below are complete.

## Current blockers

- Local SharedPreferences session/role cannot be the authority for identity or permissions.
- Customer-facing role switching must be removed from production.
- Restaurant approval must be an admin/backend operation.
- Passwords must be handled by Firebase Authentication (or an equivalent audited auth service), never stored as plaintext or client-generated password hashes.
- Order totals, delivery fees, discounts, coupons and payment state must be calculated/verified server-side.
- Paymob success must come from a verified server webhook/transaction lookup, not from a client-supplied success flag or generated transaction ID.
- Demo/seed customers, restaurants, reviews, addresses, couriers and sample orders must not ship in production.
- Order status transitions must be authorized server-side.
- Reviews must require a verified delivered order and one review per eligible order.
- Firestore must use deny-by-default security rules. `firestore.rules` is the initial baseline and must be tested against the final schema before deployment.

## Migration order

1. Enable Firebase Authentication and map the authenticated UID to the application user.
2. Introduce server-authoritative role claims for CUSTOMER, RESTAURANT_OWNER, COURIER and ADMIN.
3. Remove local role switching and local authorization decisions from the UI/repository.
4. Move order creation to a trusted backend function/API. The client sends product IDs, quantities, address ID, coupon code and payment method; the backend resolves prices and calculates the final amount.
5. Move order status transitions to trusted backend operations with role/state validation.
6. Implement Paymob server-side payment creation and webhook verification. Never accept a client-provided transaction ID as proof of payment.
7. Remove production execution paths for SeedData and sample-order generation.
8. Add verified-purchase review creation and server-side rating aggregation.
9. Test Firestore rules with authenticated users for every role and unauthorized access case.
10. Only then enable production release configuration and store rollout.

## Release gate

The production build is **blocked** until every P0 item above has an automated or documented verification test. A successful local build alone is not sufficient evidence of production readiness.
