# Gamma

Gamma is a **membership and identity management system** built for [Chalmers IT student division](https://chalmers.it). It serves as the central directory for members, groups, OAuth2 clients, and API keys.

- **Language:** Java 25
- **Framework:** Spring Boot 4.0.6
- **Build:** Gradle 9.5.1 (wrapper)
- **Database:** PostgreSQL 16+ with Flyway migrations
- **Cache/Sessions:** Redis 7+
- **UI:** Thymeleaf + HTMX + Pico CSS
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

```bash
make e2e              # builds image then runs tests
# or with a specific version from GHCR
make test-e2e GAMMA_VERSION=v1.2.3
```

For development of E2E tests:

```bash
cd e2e && pnpm install && pnpm test
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


## API Keys

Gamma issues scoped API keys for service-to-service access. On first startup with mock data, example keys are created and logged.

| Key Type | Purpose |
|---|---|
| `INFO` | Read public info (`/api/info/v1`): groups, super groups, members |
| `ACCOUNT_SCAFFOLD` | Google account sync data (`/api/account-scaffold/v1`) |
| `ALLOW_LIST` | Registration allow-list management (`/api/allow-list/v1`) |
| `CLIENT` | OAuth2 client-level API access |

When developing a service that consumes Gamma APIs, obtain the appropriate key type and pass it via the `X-API-Key` header:

```http
GET /api/info/v1/groups
X-API-Key: <your_info_key>
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
- Run `./gradlew build` to verify DDL validation against Flyway migrations (`ddl-auto: validate`).

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
