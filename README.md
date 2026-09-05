# Gamma

Gamma is the account and organization service for the Chalmers Student Union IT Division. It runs
as one Spring Boot application and stores persistent data in PostgreSQL, browser sessions and OAuth
state in Redis, and uploaded media on the local filesystem.

## Run locally

Use Java 25 and start PostgreSQL and Redis before launching the application. With both services on
localhost and their default ports:

```shell
DB_HOST=localhost REDIS_HOST=localhost PRODUCTION=false ./gradlew bootRun
```

Gamma listens on `http://localhost:8081` by default. On a fresh database, the development admin is
`admin` with password `password1337` when `PRODUCTION=false` and `ADMIN_SETUP=true`.

Configuration uses Spring Boot environment-variable binding. The commonly needed settings are:

| Environment variable | Purpose | Default |
| --- | --- | --- |
| `SERVER_PORT` | HTTP listener port | `8081` |
| `SERVER_SERVLET_CONTEXT_PATH` | Optional servlet context path | empty |
| `GAMMA_TRUSTED_PROXIES` | Regex matching proxy addresses trusted to supply `X-Forwarded-For` | empty (trust none) |
| `DB_HOST`, `DB_PORT`, `DB_NAME` | PostgreSQL address and database | `db`, `5432`, `postgres` |
| `DB_USER`, `DB_PASSWORD` | PostgreSQL credentials | `postgres`, `postgres` |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD` | Redis connection | `redis`, `6379`, empty |
| `SESSION_TIMEOUT` | Spring Session timeout | `43200s` |
| `BASE_URL` | Public application URL and OAuth issuer | `http://localhost:8081` |
| `OAUTH_SIGNING_KEY_PEM` | PKCS#8 RSA private key used to sign OAuth tokens | ephemeral key (development only) |
| `PRODUCTION` | Production behavior | `true` |
| `IS_MOCKING`, `MOCK_DATA_RESOURCE` | Optional mock-data bootstrap | `false`, classpath fixture |
| `ADMIN_SETUP` | Create the initial administrator when absent | `true` |
| `UPLOAD_FOLDER` | Media storage directory | `./uploads/` |
| `GOTIFY_BASE_URL`, `GOTIFY_KEY`, `GOTIFY_FROM` | Outbound mail gateway | disabled when URL/key are empty |
| `ROOT_DEBUG_LEVEL` | Root log level | `INFO` |

## Verify and package

Run the complete Kotlin checks and tests with `./gradlew check` (PostgreSQL and Redis containers
require a running Docker-compatible engine). Use `./gradlew unitTest` for container-free tests and
`./gradlew integrationTest` for all container-backed tests. To run one operation's regressions:

```shell
./gradlew :contexts:organization:test --tests '*UpdateGroupIntegrationTest'
```

New Kotlin tests belong in Git alongside the operations they cover; avoid local ignore rules for
`src/test/kotlin` or test-support fixtures. Build the production image with
`./gradlew bootBuildImage`; it creates `app:latest` with Java 25. Run the complete Playwright suite
against the image with `make build-image e2e`.
