package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyName
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.ApiTokenVerificationCache
import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.apiaccess.RawApiToken
import it.chalmers.gamma.apiaccess.StoredApiCredential
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.AdministratorAccess
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.security.SecureRandom
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class CreateAdministrativeApiKeyIntegrationTest {
    @Test
    fun `a stale administrator flag cannot create a key`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val before = database.tableRowCount("g_api_key")
                val actor = Actor.User(ActorUserId(UUID.fromString("bc605869-9a4d-46ec-8a29-d00819d4c195")), true)
                assertFailsWith<AccessDenied> {
                    CreateAdministrativeApiKey(
                        database,
                        AdministratorAccess(database),
                        CreateApiKey(database, bcryptCost = 10),
                    ).create(actor, ApiKeyName("stale authority"), LocalizedText.of(), ApiKeyType.INFO)
                }
                assertEquals(before, database.tableRowCount("g_api_key"))
            }
        }
    }

    @Test
    fun `current administrator authority is checked again after credential preparation`() =
        withDatabase { database ->
            var publications = 0
            val random =
                object : SecureRandom() {
                    override fun nextBytes(bytes: ByteArray) {
                        assertNull(TransactionManager.currentOrNull())
                        database.executeSqlScript(
                            "DELETE FROM g_admin_user WHERE user_id = '88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f'",
                        )
                        super.nextBytes(bytes)
                    }
                }
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ) {
                        publications++
                    }
                }
            val operation =
                CreateAdministrativeApiKey(
                    database,
                    AdministratorAccess(database),
                    CreateApiKey(database, cache, bcryptCost = 10, random = random),
                )
            val before = database.tableRowCount("g_api_key")
            assertFailsWith<AccessDenied> {
                operation.create(administrator, ApiKeyName("revoked authority"), LocalizedText.of(), ApiKeyType.INFO)
            }
            assertEquals(before, database.tableRowCount("g_api_key"))
            assertEquals(0, publications)
        }

    @Test
    fun `a current administrator with a false cached flag creates an independent key`() =
        withDatabase { database ->
            val operation =
                CreateAdministrativeApiKey(
                    database,
                    AdministratorAccess(database),
                    CreateApiKey(database, bcryptCost = 10),
                )
            val created =
                operation.create(
                    administrator,
                    ApiKeyName("current authority"),
                    LocalizedText.of("Svenska", "English"),
                    ApiKeyType.INFO,
                )
            assertEquals(
                created.apiKey,
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    ApiKeyQueries(database).findApiKeyIn(this, created.apiKey.id)
                },
            )
        }

    @Test
    fun `denial client-only type and enclosing transactions reject before credential work`() =
        withDatabase { database ->
            var generations = 0
            val random =
                object : SecureRandom() {
                    override fun nextBytes(bytes: ByteArray) {
                        generations++
                        super.nextBytes(bytes)
                    }
                }
            val operation =
                CreateAdministrativeApiKey(
                    database,
                    AdministratorAccess(database),
                    CreateApiKey(database, bcryptCost = 10, random = random),
                )
            val name = ApiKeyName("invalid creation")
            assertFailsWith<AccessDenied> {
                operation.create(
                    Actor.Anonymous,
                    name,
                    LocalizedText.of(),
                    ApiKeyType.CLIENT,
                )
            }
            assertFailsWith<IllegalArgumentException> {
                operation.create(administrator, name, LocalizedText.of(), ApiKeyType.CLIENT)
            }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> {
                    operation.create(administrator, name, LocalizedText.of(), ApiKeyType.INFO)
                }
            }
            assertEquals(0, generations)
        }

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private companion object {
        val administrator = Actor.User(ActorUserId(UUID.fromString("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")))
    }
}
