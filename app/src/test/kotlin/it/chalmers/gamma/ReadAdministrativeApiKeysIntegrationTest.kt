package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.UserAccountAccess
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReadAdministrativeApiKeysIntegrationTest {
    @Test
    fun `API key lists reject a stale administrator flag`() =
        withDatabase { database ->
            assertFailsWith<AccessDenied> {
                ReadAdministrativeApiKeys(
                    database,
                    UserAccountAccess(database),
                    ApiKeyQueries(database),
                ).listApiKeys(staleAdministrator)
            }
        }

    @Test
    fun `API key details reject a stale administrator flag`() =
        withDatabase { database ->
            assertFailsWith<AccessDenied> {
                ReadAdministrativeApiKeys(
                    database,
                    UserAccountAccess(database),
                    ApiKeyQueries(database),
                ).findApiKey(staleAdministrator, keyId)
            }
        }

    @Test
    fun `current administrators read sorted metadata and unavailable accounts are denied`() =
        withDatabase { database ->
            val reads = ReadAdministrativeApiKeys(database, UserAccountAccess(database), ApiKeyQueries(database))
            val administrator = Actor.User(ActorUserId(adminId.value), false)
            val keys = reads.listApiKeys(administrator)
            assertEquals(3, keys.size)
            assertEquals(keys.sortedBy { it.name.value }, keys)
            assertEquals(keys.single { it.id == keyId }, reads.findApiKey(administrator, keyId))
            assertNull(reads.findApiKey(administrator, ApiKeyId(UUID.randomUUID())))
            database.executeSqlScript(
                "UPDATE g_api_key SET description = NULL, version = NULL WHERE api_key_id = '${keyId.value}'",
            )
            val legacy = assertNotNull(reads.findApiKey(administrator, keyId))
            assertEquals(0, legacy.version)
            assertEquals(
                it.chalmers.gamma.platform.core.LocalizedText
                    .of(),
                legacy.description,
            )
            database.executeSqlScript("UPDATE g_user SET locked = TRUE WHERE user_id = '${adminId.value}'")
            for (actor in listOf(administrator, Actor.Anonymous, Actor.User(ActorUserId(UUID.randomUUID()), true))) {
                assertFailsWith<AccessDenied> { reads.listApiKeys(actor) }
                assertFailsWith<AccessDenied> { reads.findApiKey(actor, keyId) }
            }
            database.executeSqlScript(
                "UPDATE g_user SET locked = FALSE WHERE user_id = '${adminId.value}'; DELETE FROM g_admin_user",
            )
            assertFailsWith<AccessDenied> { reads.listApiKeys(administrator.copy(isAdministrator = true)) }
            assertFailsWith<AccessDenied> { reads.findApiKey(administrator.copy(isAdministrator = true), keyId) }
        }

    @Test
    fun `both administrative reads retain the snapshot established during authorization`() {
        for (details in listOf(false, true)) {
            PostgresTestEnvironment().use { postgres ->
                var beforeKey: (() -> Unit)? = null
                var fired = false
                val observer =
                    object : StatementInterceptor {
                        override fun beforeExecution(
                            transaction: Transaction,
                            context: StatementContext,
                        ) {
                            if (context.statement.targets.any { it.tableName == "g_api_key" }) {
                                val mutation = beforeKey ?: return
                                beforeKey = null
                                try {
                                    mutation()
                                } catch (failure: java.sql.SQLException) {
                                    throw AssertionError("Concurrent fixture mutation failed", failure)
                                }
                                fired = true
                            }
                        }
                    }
                DatabaseFactory(postgres.dataSource, listOf(observer)).use { database ->
                    val reads =
                        ReadAdministrativeApiKeys(database, UserAccountAccess(database), ApiKeyQueries(database))
                    val administrator = Actor.User(ActorUserId(adminId.value), true)
                    val read: () -> Any? = {
                        if (details) reads.findApiKey(administrator, keyId) else reads.listApiKeys(administrator)
                    }
                    val before = read()
                    beforeKey = {
                        database.executeSqlScript(
                            "UPDATE g_api_key SET pretty_name = 'Changed' WHERE api_key_id = '${keyId.value}'",
                        )
                    }
                    assertEquals(before, read())
                    assertTrue(fired)
                    assertNotEquals(before, read())
                }
            }
        }
    }

    @Test
    fun `read owners reject ambient transactions and queries require the exact active handle`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val keys = ApiKeyQueries(database)
                val reads = ReadAdministrativeApiKeys(database, UserAccountAccess(database), keys)
                val actor = Actor.User(ActorUserId(adminId.value))
                lateinit var completed: JdbcTransaction
                database.commitTransaction {
                    completed = this
                    assertFailsWith<IllegalStateException> { reads.listApiKeys(actor) }
                    assertFailsWith<IllegalStateException> { reads.findApiKey(actor, keyId) }
                    assertEquals(3, keys.listApiKeysIn(this).size)
                }
                val participants =
                    listOf<(JdbcTransaction) -> Any?>(
                        { keys.findApiKeyIn(it, keyId) },
                        { keys.listApiKeysIn(it) },
                        { keys.infoSettingsIn(it, keyId) },
                        { keys.accountScaffoldSettingsIn(it, ApiKeyId(UUID.randomUUID())) },
                    )
                for (read in participants) assertFailsWith<IllegalStateException> { read(completed) }
                DatabaseFactory(postgres.dataSource).use { foreign ->
                    foreign.commitTransaction {
                        for (read in participants) assertFailsWith<IllegalStateException> { read(this) }
                    }
                }
            }
        }
    }

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private companion object {
        val adminId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
        val keyId = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")
        val staleAdministrator =
            Actor.User(
                ActorUserId(UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195").value),
                true,
            )
    }
}
