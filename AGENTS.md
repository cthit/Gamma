# Gamma coding style

Write code for the student who will maintain Gamma after you leave. The code
should feel boring, direct, and under control.

Favor the "grug brain" approach: ordinary data, explicit steps, and as little
machinery as the problem permits. A reader should spend their effort understanding
the business operation, not reconstructing a framework or a chain of wrappers.

## Optimize for the reader

- Prefer clarity at the call site over cleverness in the implementation.
- Keep behavior close to the data and context needed to understand it.
- Let important behavior occupy visible space. Do not hide a critical decision
  behind layers of forwarding methods.
- Write code that can be read from top to bottom without repeatedly jumping
  between files.
- Use comments to explain non-obvious reasons, constraints, or tradeoffs. Do not
  narrate code that is already obvious.

## Use domain language

- Name types, functions, parameters, and errors with the bounded context's
  ubiquitous language.
- Prefer complete domain names over abbreviations and generic technical names.
- Use distinct types for domain concepts that must not be confused.
- Prefer named data classes and value objects to maps, tuples, primitive
  collections, or strings with undocumented formats.
- Keep the same domain concept named consistently across code and tests.

## Keep important functions cohesive

- A long cohesive function is acceptable when it preserves one complete
  business story.
- Arrange important operations in the order a reader reasons about them:
  authorize, validate, load, enforce, decide, persist, record effects, return.
- Keep security decisions, mutations, transaction boundaries, and external
  effects visually obvious.
- Extract code when it is reused, represents an independently meaningful domain
  rule, or crosses a real boundary.
- Do not extract tiny private functions merely to reduce line count.
- Do not combine unrelated stories in one function just because long functions
  are allowed.

## Organize operations within their business context

- Keep operations within the existing users, organization, OAuth, and API access
  contexts. Preserve the build's boundaries between those contexts.
- Prefer a file per meaningful operation when it keeps a longer function and its
  input types easy to find, such as `UpdateGroup.kt` and `AddMembership.kt`.
  Related small operations may share a file; do not enforce one file per function.
- Let an operation own its authorization, business validation, transaction, and
  persistence directly. Exposed queries may live beside the rules they implement.
- Remove facade/store/mutations layers that merely forward the same operation.
  Inject the actual operation or meaningful query component into its caller.
- Controllers translate HTTP requests and responses. They must not load domain
  records merely to construct an update or secretly establish business transactions.
- Coordinate genuinely cross-context operations in the application composition
  layer. Sharing one PostgreSQL transaction does not require contexts to depend
  directly on one another.

## Make database operation boundaries explicit

- Retain Exposed's typed queries and the existing domain value types. Use explicit,
  parameterized SQL where it makes a database operation clearer.
- A complete database command owns its transaction. It must not silently inherit
  an outer transaction and then claim that returning means it committed.
- Where cross-context composition requires participation in a caller's transaction,
  make that contract explicit. Participating code must not commit independently or
  perform effects that assume the caller has committed.
- Keep all changes belonging to one user action atomic. An operation that saves a
  group and its memberships must not call independently committing member commands.
- Put consistent-read snapshot boundaries in the operation that requires them,
  preserving the required isolation level.
- Catch per-item batch failures outside each item's transaction when the operation
  promises partial success. Report the actual committed outcome.
- Keep password hashing, media writes, mail, and other external work outside
  retryable database phases. Preserve version checks, locks, token consumption,
  compensation, and deliberate retry behavior.
- Distinguish request-time identity from database-backed authority. Moving a stale
  flag into a transaction does not make it current.

## Test the real operation

- Give database operations directly corresponding integration tests, such as
  `UpdateGroupIntegrationTest.kt`, using a real PostgreSQL database and the shipped
  migrations.
- Call the actual operation and assert persisted state and returned outcomes.
  Do not substitute mocked stores or tests that only verify forwarding calls.
- Keep tests focused on business guarantees. Cover success and relevant rejection,
  rollback, stale-version, or concurrency cases. One or two cases may suffice for
  a simple operation; do not impose that limit on a more demanding operation.
- Test external-effect ordering and failure handling where they matter. Narrow
  controllable adapters are appropriate for filesystem, mail, hashing, and other
  boundaries while PostgreSQL remains real.
- Preserve endpoint contract tests and broader application tests where composition
  can change behavior even when an operation passes independently.

## Prefer straightforward control flow

- Prefer explicit `if` statements, `when` expressions, loops, and early returns
  to clever polymorphism, DSLs, pipelines, reflection, or metaprogramming.
- Keep nesting shallow.
- Handle failure paths explicitly and near the operation that can fail.
- Avoid boolean parameters whose meaning is unclear at the call site. Prefer a
  named operation or domain type.
- Avoid invisible control flow through global state, thread-local state, service
  locators, callbacks, or implicit framework behavior.
- Never swallow coroutine cancellation or other control-flow exceptions.

## Resist unnecessary abstraction

- Do not generalize from one example.
- A small amount of obvious duplication is cheaper than the wrong abstraction.
- Introduce an abstraction only when it removes demonstrated complexity and the
  resulting code is easier to read locally.
- Avoid generic `Repository<T>`, `CrudService<T>`, base services, manager
  classes, command buses, event buses, policy engines, and utility grab bags
  without a concrete need.
- Prefer explicit constructor dependencies and ordinary Kotlin calls.
- Do not introduce a framework to remove a small amount of visible wiring.

## Make state and failure unsurprising

- Prefer immutable values. Make mutation deliberate and easy to locate.
- Validate important invariants at construction or at the operation that owns
  them.
- Use domain-specific errors when callers need to distinguish outcomes.
- Keep secrets and personal data out of logs, exception messages, debugging
  output, and `toString` implementations.
- Redact sensitive values by default.
- Do not make a failed or denied operation look successful.
- Make partial-failure and compensation behavior readable where the external
  effect occurs.

## Keep lint subordinate to readability

- Longer functions and higher measured complexity are acceptable when they keep
  one business operation boring, explicit, and readable from top to bottom.
- Lint thresholds must not force extraction, forwarding layers, clever pipelines,
  or fragmented control flow that make the operation harder to understand.
- Keep complexity and size checks as review prompts. A cohesive operation may use
  a local, documented suppression for `LongMethod`, `CognitiveComplexMethod`,
  `CyclomaticComplexMethod`, `NestedBlockDepth`, or another relevant size or
  complexity rule when the explicit implementation is easier to read.
- Explain why keeping the operation together helps the reader. A suppression is
  not permission to combine unrelated operations or retain avoidable nesting.
- Prefer a local exception to disabling a rule repository-wide for one operation.
  If a threshold systematically conflicts with this style, adjust that threshold
  deliberately rather than distorting every operation to satisfy it.

## Review standard

Before considering code finished, ask:

- Can a student explain the important path after reading one function?
- Are domain terms used consistently?
- Are authorization, mutations, failures, and external effects obvious?
- Did an abstraction remove real complexity, or merely move it elsewhere?
- Is the code as simple as the problem permits?
