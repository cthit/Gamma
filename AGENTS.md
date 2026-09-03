# Gamma coding style

Write code for the student who will maintain Gamma after you leave. The code
should feel boring, direct, and under control.

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

## Keep lint useful

- Preserve cognitive-complexity, cyclomatic-complexity, nesting, and large-class
  checks.
- A cohesive core “crux” function may use a local, documented `LongMethod`
  suppression.
- Do not disable a rule repository-wide to accommodate one function.
- Never restructure clear code into scattered indirection solely to satisfy a
  line-count rule.

## Review standard

Before considering code finished, ask:

- Can a student explain the important path after reading one function?
- Are domain terms used consistently?
- Are authorization, mutations, failures, and external effects obvious?
- Did an abstraction remove real complexity, or merely move it elsewhere?
- Is the code as simple as the problem permits?
