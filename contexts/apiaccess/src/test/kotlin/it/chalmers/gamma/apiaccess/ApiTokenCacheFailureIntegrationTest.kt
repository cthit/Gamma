package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ApiTokenCacheFailureIntegrationTest {
    @Test
    fun `ordinary cache failures do not change credential results`() {
        postgresEnvironment().use { postgres ->
            database(postgres).use { database ->
                val authenticator = ApiCredentialAuthenticator(database, FailingApiTokenVerificationCache())
                val authoritativeApi = ApiCredentialAuthenticator(database)
                val fixtureId = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")

                run {
                    assertNotNull(
                        authenticator.authenticate(
                            fixtureId,
                            RawApiToken("gamma-info-regression-token-000001"),
                            requiredType = ApiKeyType.INFO,
                        ),
                    )
                    assertNull(
                        authenticator.authenticate(
                            fixtureId,
                            RawApiToken("wrong-token-value-that-is-definitely-long-enough"),
                            requiredType = ApiKeyType.INFO,
                        ),
                    )

                    val created =
                        CreateApiKey(database, FailingApiTokenVerificationCache(), bcryptCost = 10).create(
                            ApiKeyName("cache-outage"),
                            LocalizedText.of("Cachefel", "Cache outage"),
                            ApiKeyType.ALLOW_LIST,
                        )
                    assertNotNull(
                        authoritativeApi.authenticate(
                            created.apiKey.id,
                            created.token,
                            requiredType = ApiKeyType.ALLOW_LIST,
                        ),
                    )

                    val replacement =
                        RotateApiKey(
                            database,
                            FailingApiTokenVerificationCache(),
                            bcryptCost = 10,
                        ).rotateForTest(database, created.apiKey.id).token
                    assertNotNull(
                        authoritativeApi.authenticate(
                            created.apiKey.id,
                            replacement,
                            requiredType = ApiKeyType.ALLOW_LIST,
                        ),
                    )
                    assertNull(
                        authoritativeApi.authenticate(
                            created.apiKey.id,
                            created.token,
                            requiredType = ApiKeyType.ALLOW_LIST,
                        ),
                    )
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
