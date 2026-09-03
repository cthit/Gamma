package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ApiKeyStoreCacheFailureIntegrationTest {
    @Test
    fun `ordinary cache failures do not change credential results`() {
        postgresEnvironment().use { postgres ->
            database(postgres).use { database ->
                val api = ApiKeyStore(database, FailingApiTokenVerificationCache(), bcryptCost = 10)
                val authoritativeApi = ApiKeyStore(database, bcryptCost = 10)
                val fixtureId = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")

                run {
                    assertNotNull(
                        api.authenticate(
                            ApiKeyType.INFO,
                            fixtureId,
                            RawApiToken("gamma-info-regression-token-000001"),
                        ),
                    )
                    assertNull(
                        api.authenticate(
                            ApiKeyType.INFO,
                            fixtureId,
                            RawApiToken("wrong-token-value-that-is-definitely-long-enough"),
                        ),
                    )

                    val created =
                        api.createApiKey(
                            ApiKeyName("cache-outage"),
                            LocalizedText.of("Cachefel", "Cache outage"),
                            ApiKeyType.ALLOW_LIST,
                        )
                    assertNotNull(
                        authoritativeApi.authenticate(ApiKeyType.ALLOW_LIST, created.apiKey.id, created.token),
                    )

                    val replacement = api.resetToken(created.apiKey.id)
                    assertNotNull(authoritativeApi.authenticate(ApiKeyType.ALLOW_LIST, created.apiKey.id, replacement))
                    assertNull(authoritativeApi.authenticate(ApiKeyType.ALLOW_LIST, created.apiKey.id, created.token))
                }
            }
        }
    }
}

private class FailingApiTokenVerificationCache : ApiTokenVerificationCache {
    override fun match(
        id: ApiKeyId,
        storedCredential: StoredApiCredential,
        presentedToken: RawApiToken,
    ): CachedApiTokenMatch = error("Redis is unavailable")

    override fun remember(
        id: ApiKeyId,
        storedCredential: StoredApiCredential,
        token: RawApiToken,
    ): Unit = error("Redis is unavailable")
}

private fun postgresEnvironment(): PostgresTestEnvironment {
    val root = Path.of(checkNotNull(System.getProperty("gamma.root")))
    return PostgresTestEnvironment(listOf("filesystem:${root.resolve("app/src/main/resources/db/migration")}"))
}

private fun database(postgres: PostgresTestEnvironment) =
    DatabaseFactory(
        DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
    )
