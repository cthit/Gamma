package it.chalmers.gamma

import it.chalmers.gamma.oauth.ClientApprovals
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthClientQueries
import it.chalmers.gamma.oauth.ReadOAuthClientLists
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.UserAccountAccess
import it.chalmers.gamma.users.UserQueries
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ReadOAuthClientListsIntegrationTest {
    @Test
    fun `lists use current account authority and retain official ownership and approval filters`() =
        withDatabase { database, _ ->
            val official = createClient(database, ClientOwner.Official, "Official")
            val personal = createClient(database, ClientOwner.User(ownerId), "Personal")
            val another = createClient(database, ClientOwner.User(adminId), "Another")
            database.commitTransaction {
                ClientApprovals(database).approveIn(this, ownerId, official.uid, official.scopes)
            }
            val reads = reads(database)
            assertEquals(listOf(official), reads.officialClients(administrator.copy(isAdministrator = false)))
            assertEquals(listOf(personal), reads.myClients(owner))
            assertEquals(listOf(another), reads.myClients(administrator))
            assertEquals(listOf(official), reads.approvedClients(owner))
            assertEquals(emptyList(), reads.approvedClients(administrator))
            val owned = reads.personalClientsForAdministration(administrator)
            assertEquals(listOf(another, personal), owned.map { it.client })
            assertEquals(listOf(adminId, ownerId), owned.map { it.owner?.id })
            assertTrue(owned.all { it.toString() == "PersonalOAuthClient(<redacted>)" })
            val stale = owner.copy(isAdministrator = true)
            assertFailsWith<AccessDenied> { reads.officialClients(stale) }
            assertFailsWith<AccessDenied> { reads.personalClientsForAdministration(stale) }
            database.executeSqlScript("DELETE FROM g_admin_user WHERE user_id = '${adminId.value}'")
            assertFailsWith<AccessDenied> { reads.officialClients(administrator) }
            assertFailsWith<AccessDenied> { reads.personalClientsForAdministration(administrator) }
            database.executeSqlScript("UPDATE g_user SET locked = TRUE WHERE user_id = '${ownerId.value}'")
            for (actor in listOf(owner, Actor.Anonymous, Actor.User(ActorUserId(UUID.randomUUID()), true))) {
                assertFailsWith<AccessDenied> { reads.myClients(actor) }
                assertFailsWith<AccessDenied> { reads.approvedClients(actor) }
                assertFailsWith<AccessDenied> { reads.officialClients(actor) }
                assertFailsWith<AccessDenied> { reads.personalClientsForAdministration(actor) }
            }
        }

    @Test
    fun `personal clients and batched owner profiles retain one snapshot`() =
        withDatabase { database, observation ->
            repeat(3) { createClient(database, ClientOwner.User(ownerId), "Personal $it") }
            val reads = reads(database)
            val before = reads.personalClientsForAdministration(administrator)
            observation.userReads = 0
            observation.beforeOwnerRead = {
                database.executeSqlScript(
                    """
                    UPDATE g_client SET pretty_name = 'Changed' WHERE created_by = '${ownerId.value}';
                    UPDATE g_user SET nick = 'Changed' WHERE user_id = '${ownerId.value}';
                    """.trimIndent(),
                )
            }
            assertEquals(before, reads.personalClientsForAdministration(administrator))
            assertTrue(observation.fired)
            assertEquals(2, observation.userReads, "One account check and one owner query regardless of client count")
            assertNotEquals(before, reads.personalClientsForAdministration(administrator))
        }

    @Test
    fun `official owned and approved lists keep scopes and metadata in one snapshot`() {
        for (projection in 0..2) {
            withDatabase { database, observation ->
                val client =
                    createClient(
                        database,
                        if (projection == 1) ClientOwner.User(ownerId) else ClientOwner.Official,
                        "Snapshot client",
                    )
                database.commitTransaction {
                    ClientApprovals(database).approveIn(this, ownerId, client.uid, client.scopes)
                }
                val reads = reads(database)
                val read = {
                    when (projection) {
                        0 -> reads.officialClients(administrator)
                        1 -> reads.myClients(owner)
                        else -> reads.approvedClients(owner)
                    }
                }
                val before = read()
                observation.beforeClientLinks = {
                    database.executeSqlScript(
                        """
                        UPDATE g_client SET pretty_name = 'Changed' WHERE client_uid = '${client.uid.value}';
                        DELETE FROM g_client_scope WHERE client_uid = '${client.uid.value}';
                        """.trimIndent(),
                    )
                }
                assertEquals(before, read())
                assertTrue(observation.fired)
                assertNotEquals(before, read())
            }
        }
    }

    @Test
    fun `list owners reject ambient transactions and participants validate every handle including empty queries`() =
        withDatabase { database, observation ->
            val reads = reads(database)
            val clients = OAuthClientQueries(database)
            val users = UserQueries(database)
            lateinit var completed: JdbcTransaction
            database.commitTransaction {
                completed = this
                assertFailsWith<IllegalStateException> { reads.officialClients(administrator) }
                assertFailsWith<IllegalStateException> { reads.myClients(owner) }
                assertFailsWith<IllegalStateException> { reads.personalClientsForAdministration(administrator) }
                assertFailsWith<IllegalStateException> { reads.approvedClients(owner) }
                assertEquals(emptyList(), users.usersByIdsIn(this, emptySet()))
            }
            val participants =
                listOf<(JdbcTransaction) -> Any?>(
                    { clients.listClientsIn(it) },
                    { clients.listClientsIn(it, ownerId) },
                    { clients.approvedClientsIn(it, ownerId) },
                    { users.usersByIdsIn(it, emptySet()) },
                    { users.usersByIdsIn(it, setOf(ownerId)) },
                )
            for (read in participants) assertFailsWith<IllegalStateException> { read(completed) }
            DatabaseFactory(observation.source).use { foreign ->
                foreign.commitTransaction {
                    for (read in participants) assertFailsWith<IllegalStateException> { read(this) }
                }
            }
        }

    private fun reads(database: DatabaseFactory) =
        ReadOAuthClientLists(database, UserAccountAccess(database), OAuthClientQueries(database), UserQueries(database))

    private fun createClient(
        database: DatabaseFactory,
        owner: ClientOwner,
        name: String,
    ) = CreateClient(database, bcryptCost = 10).let { creation ->
        val prepared =
            creation.prepare(
                NewOAuthClient(
                    RedirectUri("https://example.org/callback"),
                    ClientName(name),
                    LocalizedText.of(),
                    false,
                    owner,
                ),
            )
        database.commitTransaction { creation.insertIn(this, prepared).client }
    }

    private fun withDatabase(test: (DatabaseFactory, OwnerReadObservation) -> Unit) {
        PostgresTestEnvironment().use { postgres ->
            val observation = OwnerReadObservation(postgres.dataSource)
            DatabaseFactory(postgres.dataSource, listOf(observation)).use { database -> test(database, observation) }
        }
    }

    private companion object {
        val ownerId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
        val adminId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
        val owner = Actor.User(ActorUserId(ownerId.value))
        val administrator = Actor.User(ActorUserId(adminId.value), true)
    }
}

private class OwnerReadObservation(
    val source: javax.sql.DataSource,
) : StatementInterceptor {
    var beforeClientLinks: (() -> Unit)? = null
    var beforeOwnerRead: (() -> Unit)? = null
    var userReads = 0
    var fired = false

    override fun beforeExecution(
        transaction: Transaction,
        context: StatementContext,
    ) {
        if (context.statement.targets.any { it.tableName == "g_client_api_key" }) {
            val mutation = beforeClientLinks
            beforeClientLinks = null
            if (mutation != null) {
                mutation()
                fired = true
            }
        }
        if (context.statement.targets.any { it.tableName == "g_user" }) {
            userReads++
            if (userReads == 2) {
                val mutation = beforeOwnerRead ?: return
                beforeOwnerRead = null
                fired = true
                mutation()
            }
        }
    }
}
