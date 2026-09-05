# Working on Gamma

Gamma is the single sign-on authorization server for the IT student division at
Chalmers in Gothenburg. It provides shared identity and access infrastructure for
the [CTHIT GitHub organization](https://github.com/cthit). Changes here affect the
services students use and the accounts, groups, and permissions those services
rely on.

## Compatibility and consumers

Backward compatibility is the default. Preserve public APIs, authentication and
authorization flows, and compatibility with existing persisted database and media
data. Consider upgrades from existing installations as well as fresh setups.
Intentional breaking changes require an explicit major release, documented impact,
and migration guidance that explains how operators and consumers should adapt.

Assess proposed changes through static analysis of how Gamma's critical consumers
use it: [chalmers.it](https://github.com/cthit/chalmers.it),
[bookIT-node](https://github.com/cthit/bookIT-node),
[SyncIT](https://github.com/cthit/SyncIT), and
[hubbit](https://github.com/cthit/hubbit). Clone the relevant repositories when
needed to inspect their authentication flows, API calls, and assumptions about
account and group data. Trace the affected contracts through consumer code and
use that evidence to judge compatibility and choose validation for the change.

## Code and module boundaries

Write code that the next student can read, understand, and maintain. Prefer
ordinary data, straightforward control flow, and consistent domain names. Keep
business decisions, authorization, mutations, and failures visible in the operation
that owns them. Keep related rules and data operations together, with explicit
dependencies. Introduce abstractions when they remove real complexity; avoid
clever machinery and layers that only forward calls.

Gamma is a modular monolith with high cohesion and low coupling. Users,
organization, OAuth, and API access are independent business contexts. The app/API
composition layer and Spring authorization-server adapter compose these contexts;
the build enforces that the four independent business contexts have no direct
dependencies on one another. Preserve these boundaries when coordinating work
across contexts.

Prefer official Spring libraries for sessions, security, and authorization-server
functionality. Use custom code for Gamma's business requirements, avoiding needless
reimplementation of protocol behavior already provided by Spring.

## Transactions and effects

An operation owns its transaction or explicitly participates in its caller's
transaction. Make that contract clear at the call site and keep related updates
atomic. Database phases may retry, so keep external effects such as media writes
and mail outside those phases. Make commit-dependent effects and failure handling
explicit so a retry or rollback cannot silently produce an inconsistent outcome.

## Validation and completion

See the [README](README.md) for Gamma’s purpose. Use the
[GitHub Actions workflow](.github/workflows/workflow.yml) as the source of truth
for validation commands and requirements. All applicable GitHub Actions PR checks,
including lint, tests, E2E, and other specified validation, must pass before work
is considered complete. Report which checks ran, any failures, and any checks
that remain unrun honestly; do not present unverified validation as passing.
