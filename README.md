# Gamma

Gamma is a **membership and identity management system** built for [Chalmers IT student division](https://chalmers.it). It serves as the central directory for members, groups, OAuth2 clients, and API keys.

- **Language:** Java 25
- **Framework:** Spring Boot 4.0.6
- **Build:** Gradle 9.5.1 (wrapper)
- **Database:** PostgreSQL 16+ with Flyway migrations
- **Cache/Sessions:** Redis 7+
- **UI:** Thymeleaf + HTMX, styled by a single hand-written stylesheet (`app/src/main/resources/static/css/main.css`, BEM naming)
- **Auth:** Spring Security, OAuth2 Authorization Server, API keys


## Quick Start

### Prerequisites

- JDK 25 (Temurin recommended)
- Docker (for Testcontainers dev services and E2E tests)
- Node.js 20+ and pnpm (for E2E tests)

### Run locally

In bash:

```bash
# Starts Spring Boot with auto-started PostgreSQL and Redis via Testcontainers
DEV_SERVICES_ENABLED=true PRODUCTION=false ./gradlew bootRun
```

In PowerShell:

```powershell
$env:DEV_SERVICES_ENABLED="true"; $env:PRODUCTION="false"; ./gradlew bootRun
```

The app starts on `http://localhost:8081`. On first run, an admin user is auto-created (credentials logged to stdout).

### Mock data

When `PRODUCTION=false` or `IS_MOCKING=true`, Gamma loads mock data from `MOCK_DATA_RESOURCE` (default [`mock.json`](app/src/main/resources/mock/mock.json)) on startup. Each kind of data is only created if none of it exists yet. All mock users sign in with the password `password1337`.

The mock data can also specify OAuth2 clients and their authorities. The default `mock.json` has none, so add a `clients` section to your mock file to have your clients created on startup:

```json
"clients": [ {
  "id": "8e54391c-ec36-4054-a1a9-0a6f5c2aa948",
  "clientId": "MYAPPCLIENT0000000000000000001",
  "clientSecret": "my-app-secret",
  "redirectUrl": "http://localhost:3000/api/auth/callback/gamma",
  "prettyName": "My app",
  "description": { "sv": "Min app", "en": "My app" },
  "emailScope": true,
  "ownerId": "bc605869-9a4d-46ec-8a29-d00819d4c195",
  "apiKey": { "id": "ea917118-ce7c-47f2-b024-37e131c33e86", "token": "my-app-api-key" },
  "authorities": [ {
    "name": "admin",
    "superGroupIds": [ "aed27030-ad90-4526-855c-1e909b1dcecb" ],
    "superGroupPosts": [ {
      "superGroupId": "aed27030-ad90-4526-855c-1e909b1dcecb",
      "postId": "7bb1db15-730d-4864-bfc3-99abe7c0ccf8"
    } ],
    "userIds": [ "88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f" ]
  } ]
} ]
```

| Field | Description |
|---|---|
| `id` | Client UUID |
| `clientId` | OAuth2 client ID, exactly 30 upper case letters and digits |
| `clientSecret` | Raw client secret, encoded when the client is created |
| `redirectUrl` | Redirect URL of your application |
| `emailScope` | Whether the client gets the `email` scope in addition to `openid profile` |
| `ownerId` | Id of the user that owns the client. Leave it out for an official client |
| `apiKey` | Optional client API key. Use it with `Authorization: pre-shared <id>:<token>` |
| `authorities` | Authority names (2-30 lower case letters and digits) with the ids of the super groups and users that have them. `superGroupPosts` gives the authority only to the members of a super group's groups who hold a post, such as the chairman of digIT |

The client IDs, secrets and API keys are also logged on startup. Clients are only created if the database has no clients yet.

### Run with custom services

```bash
export DB_HOST=localhost DB_PORT=5432 DB_NAME=gamma \
       DB_USER=gamma DB_PASSWORD=secret \
       REDIS_HOST=localhost REDIS_PORT=6379 \
       PRODUCTION=false
./gradlew bootRun
```

### Build Docker image

```bash
make build-image      # tags as gamma-app:test
# or
./gradlew bootBuildImage && docker image tag app:latest gamma-app:test
```

### Run E2E tests

Install the test dependencies and Chromium before the first run (and reinstall Chromium after updating Playwright):

```bash
(cd e2e && pnpm install && pnpm exec playwright install --with-deps chromium)
```

Then, from the repository root:

```bash
make e2e              # builds image then runs tests
# or with a specific version from GHCR
make test-e2e GAMMA_VERSION=1.2.3
```

When developing E2E tests, after the setup above and with `gamma-app:test` already built or pulled:

```bash
cd e2e && pnpm test
```


## Configuration

All configuration is environment-variable-driven via `application.yml`.

| Variable | Default | Description |
|---|---|---|
| `SERVER_PORT` | `8081` | HTTP port |
| `BASE_URL` | `http://localhost:8081` | Public base URL (used for redirect URIs, etc.) |
| `DB_HOST` | `db` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `postgres` | Database name |
| `DB_USER` | `postgres` | Database user |
| `DB_PASSWORD` | `postgres` | Database password |
| `REDIS_HOST` | `redis` | Redis host |
| `REDIS_PORT` | `6379` | Redis port |
| `REDIS_PASSWORD` | *(empty)* | Redis password |
| `PRODUCTION` | `true` | Production mode (disables Thymeleaf caching, dev services, etc.) |
| `OAUTH2_SIGNING_KEY` | *(empty)* | RSA private key that signs OAuth2 tokens. Will be required in production from v3, see [OAuth2 signing keys](#oauth2-signing-keys) |
| `OAUTH2_VERIFICATION_KEYS` | *(empty)* | Comma separated keys that are still published so the tokens they signed can be verified, but that no longer sign tokens |
| `PASSKEYS_RP_ID` | host of `BASE_URL` | The domain passkeys are registered for, see [Signing in](#signing-in). Changing it makes every registered passkey stop working |
| `PASSKEYS_ALLOWED_ORIGINS` | origin of `BASE_URL` | Comma separated origins that passkeys may be used on |
| `DEV_SERVICES_ENABLED` | `false` | Auto-start PostgreSQL + Redis via Testcontainers |
| `IS_MOCKING` | `false` | Load mock data on startup |
| `MOCK_DATA_RESOURCE` | `classpath:/mock/mock.json` | Path to mock data JSON |
| `ADMIN_SETUP` | `true` | Auto-create admin user on first run |
| `UPLOAD_FOLDER` | `./uploads/` | File upload directory |
| `GOTIFY_KEY` | `123abc` | Gotify pre-shared key |
| `GOTIFY_BASE_URL` | `http://gotify:80` | Gotify service URL |
| `SESSION_TIMEOUT` | `43200` | Session timeout in seconds |
| `ROOT_DEBUG_LEVEL` | `INFO` | Root log level |
| `SERVER_SSL_ENABLED` | *(varies)* | Enable HTTPS |


## Signing in

Users sign in with a **passkey**, or with their **password** and, if they have set one up, a code from an **authenticator app** (TOTP). Admins must sign in with a passkey: an admin who signs in with their password can only add a passkey until they have one, and can't use Gamma or sign in to other clients before that. A user who is made admin gets the same on their next request.

Everything is built on Spring Security:

- Passkeys use Spring Security's WebAuthn support (`http.webAuthn()`), which stores them in the `user_entities` and `user_credentials` tables. Passkeys must verify the user, for example with a fingerprint or PIN.
- Each way of signing in is a Spring Security factor (`FACTOR_PASSWORD`, `FACTOR_WEBAUTHN` and Gamma's `FACTOR_TOTP`), and the factors each user needs are required factors (`AllRequiredFactorsAuthorizationManager`). Changing how you sign in needs a sign in from the last 10 minutes.
- Authenticator app codes are checked with [java-otp](https://github.com/jchambers/java-otp) (HMAC-SHA1, six digits, 30 second steps), and each code can only be used once.

Users manage this under **Sign in and security** (`/me/security`): add and remove passkeys, add or remove their password once they have a passkey, set up an authenticator app (QR code, or the key typed in by hand), and turn email recovery off.

### Getting back in

- **Email recovery**: the forgot password link lets the user set a new password, or set up a passkey instead. For users with an authenticator app the link only resets the password, so the app is still needed. Admins can never recover by email.
- **Recovery key**: users with an authenticator app, and users who turn off email recovery, get a 64 character recovery key. It is shown once, and has to be typed back on the next page before it is saved, as a hash. Signing in with it (`/login/recovery`) lets the user set up a new passkey or password once, and removes their authenticator app.
- **Admin reset**: an admin can reset how another user signs in from the user's page. That removes the user's passkeys, authenticator app and recovery key and turns email recovery on. A reset admin signs in with their password and adds a new passkey.

If the last admin loses all their passkeys, remove them in the database, so the admin can sign in with their password and add a new one:

```sql
DELETE FROM user_entities WHERE name = '<user id of the admin>';
```

If they have forgotten their password too, remove their admin rights (`DELETE FROM g_admin_user WHERE user_id = '<user id>';`), let them reset their password through the forgot password page, and give the rights back (`INSERT INTO g_admin_user (user_id, created_at) VALUES ('<user id>', now());`).

## OAuth2 signing keys

Gamma signs ID tokens and access tokens with the RSA key in `OAUTH2_SIGNING_KEY` and publishes its public key at `/oauth2/jwks`. Without one, a temporary key is generated on every start, so tokens stop working when Gamma restarts and instances can't verify each other's tokens. Running in production without `OAUTH2_SIGNING_KEY` is deprecated, and Gamma v3 will refuse to start without it ([#1004](https://github.com/cthit/Gamma/issues/1004)).

Both settings take PEM content directly or the location of a PEM file, such as `/run/secrets/gamma-signing-key.pem` or `file:/run/secrets/gamma-signing-key.pem`. Each key gets its [RFC 7638](https://www.rfc-editor.org/rfc/rfc7638) thumbprint as key id (`kid`), so every instance that uses the same key publishes the same key id. Keys must be RSA keys of at least 2048 bits.

Create a key with:

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:3072 -out gamma-signing-key.pem
```

### Rotating the signing key

1. Create a new key as above.
2. Add the new key to `OAUTH2_VERIFICATION_KEYS` and deploy that to every instance. Every instance now publishes and accepts the new key before any of them signs with it. Give clients that cache the published keys time to fetch them again.
3. Set `OAUTH2_SIGNING_KEY` to the new key, remove the new key from `OAUTH2_VERIFICATION_KEYS` and add the previous key there instead, then deploy. The previous key's public key is enough: `openssl pkey -in previous-key.pem -pubout`. Tokens signed by the previous key still verify.
4. Once the tokens signed by the previous key have expired, remove it from `OAUTH2_VERIFICATION_KEYS` and deploy. ID tokens live for 30 minutes and access tokens for 5 minutes, so waiting a day is plenty.

Gamma refuses to start if the same key is configured twice, for example as both the signing key and a verification key.


## API Keys

Gamma issues scoped API keys for service-to-service access. On first startup with mock data, example keys are created and logged.

| Key Type | Purpose |
|---|---|
| `INFO` | Read public info (`/api/info/v1`): groups, super groups, members |
| `ACCOUNT_SCAFFOLD` | Google account sync data (`/api/account-scaffold/v1`) |
| `ALLOW_LIST` | Registration allow-list management (`/api/allow-list/v1`) |
| `CLIENT` | OAuth2 client-level API access |

When developing a service that consumes Gamma APIs, obtain the appropriate key type and pass its ID and token via the `Authorization` header using the `pre-shared` scheme:

```http
GET /api/info/v1/groups
Authorization: pre-shared <api_key_id>:<api_key_token>
```


## Developing

### Code style

Google Java Format is enforced by Spotless. Run before committing:

```bash
./gradlew spotlessApply
```

### Database migrations

Flyway migrations live in `app/src/main/resources/db/migration/`. Migration files follow the `V<version>__<description>.sql` naming convention.

### Modifying JPA entities

- Domain records are immutable Java records with `@RecordBuilder` for builders.
- JPA entities live in `adapter/secondary/jpa/` and are mapped to/from domain records via entity converters.

To validate entity mappings against the Flyway migrations, start the application with development services:

```bash
DEV_SERVICES_ENABLED=true PRODUCTION=false ./gradlew bootRun
```

Wait for successful application startup. Flyway applies the migrations to the development database, then Hibernate validates the entity mappings (`ddl-auto: validate`). `./gradlew build` compiles and checks the project but does not start the application or validate the database schema.

### Adding a new REST endpoint

1. Create a domain service in the appropriate `app/` sub-package (e.g. `app/user/`).
2. Create an adapter in `adapter/primary/api/` (`@RestController` with request/response DTOs).
3. Wire in the facade and use `AccessGuard` for authorization.
4. Add E2E tests in `e2e/tests/api/`.

### Adding UI pages

1. Create a Thymeleaf controller in `adapter/primary/web/`.
2. Create templates in `app/src/main/resources/templates/`.
3. Use HTMX for dynamic partial updates.


## Architecture

Gamma follows **Domain-Driven Design** with a **hexagonal (ports & adapters)** architecture.

```
src/main/java/it/chalmers/gamma/
├── GammaApplication.java          # Spring Boot entry point
├── BootstrapRunner.java           # Startup bootstrapper (admin setup, mock data)
├── ScheduledTasks.java            # Periodic cleanup tasks
├── adapter/
│   ├── primary/                   # Inbound ports (driving adapters)
│   │   ├── api/                   #   REST controllers
│   │   ├── web/                   #   Thymeleaf MVC controllers
│   │   └── images/                #   Image serving controller
│   └── secondary/                 # Outbound ports (driven adapters)
│       ├── jpa/                   #   JPA persistence (entities, repositories, converters)
│       ├── redis/                 #   Redis-backed OAuth2 authorization store
│       ├── image/                 #   Local filesystem image service
│       └── mail/                  #   Gotify-based notification service
├── app/                           # Domain + application services
│   ├── Facade.java                # Base facade with AccessGuard support
│   ├── <domain>/                  # Each bounded context has:
│   │   ├── domain/                #   Pure domain records and interfaces
│   │   └── <Domain>Facade.java    #   Application service orchestrating domain + adapters
│   ├── user/                      # User management
│   ├── group/                     # Group management
│   ├── supergroup/                # Super group management
│   ├── post/                      # Post/role management
│   ├── apikey/                    # API key management
│   ├── client/                    # OAuth2 client management
│   ├── oauth2/                    # OAuth2 business logic
│   ├── authentication/            # Authentication domain
│   ├── admin/                     # Admin operations
│   ├── image/                     # Image domain
│   ├── mail/                      # Mail domain
│   ├── throttling/                # Rate-limit management
│   ├── validation/                # Shared validation
│   └── common/                    # Shared value objects / utilities
├── bootstrap/                     # Seed data bootstrapping
└── security/                      # Spring Security config, filters, JWK
```

### Key patterns

- **Domain records:** Business objects are immutable Java records with `@RecordBuilder`.
- **Facades:** Each domain has a Facade that orchestrates domain logic and adapters, with `AccessGuard` for authorization.
- **AccessGuard:** Composable authorization checks (`isAdmin()`, `isMe()`, `isSignedIn()`, `isApi()`, etc.).
- **Separated entity model:** JPA entities are distinct from domain records; entity converters handle mapping.
- **AbstractEntity hierarchy:** `ImmutableEntity` / `MutableEntity` base classes with automatic `created_at` timestamps.

## Testing

### Java tests

There are currently no Java unit/integration tests. Spotless formatting checks run via `./gradlew check`.

### E2E tests (Playwright + TypeScript)

The E2E suite lives in `e2e/` and uses Playwright with Testcontainers to orchestrate PostgreSQL, Redis, Gotify, and Gamma Docker containers.

Complete the [E2E setup](#run-e2e-tests), including installing Chromium and building or pulling `gamma-app:test`, before running these commands from the repository root.

```bash
# Run all E2E tests
cd e2e && pnpm test

# Run with UI mode
cd e2e && pnpm test:ui

# Checks
cd e2e && pnpm run format:check && pnpm run typecheck && pnpm run lint
```

Test files follow the `**/*.it.ts` naming convention:
- `tests/api/`: API-level integration tests
- `tests/ui/`: Browser-level UI tests


## Project links

- GitHub: https://github.com/cthit/gamma
- Docker images: `ghcr.io/cthit/gamma`

## License

Zero-Clause BSD (0BSD). See [LICENSE](LICENSE).
