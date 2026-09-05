package it.chalmers.gamma.api

import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyName
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.oauth.AuthorityName
import it.chalmers.gamma.oauth.ClientApiCredential
import it.chalmers.gamma.oauth.ClientApprovals
import it.chalmers.gamma.oauth.ClientAuthorities
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthApiKeyId
import it.chalmers.gamma.oauth.OAuthApiToken
import it.chalmers.gamma.oauth.OAuthClientQueries
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserQueries
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApiReadSnapshotIntegrationTest {
    @Test
    fun `direct info user read holds its snapshot across settings and profile queries`() =
        withDatabase { database, mutation ->
            val api = InfoApi(database, ApiKeyQueries(database), UserQueries(database), OrganizationQueries(database))
            val key = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")
            val before = assertNotNull(api.user(key, userId))
            mutation.beforeSecondRead = {
                database.executeSqlScript(
                    "UPDATE g_user SET nick = 'Changed' WHERE user_id = '${userId.value}'",
                )
            }
            assertEquals(before, api.user(key, userId))
            assertNotEquals(before, api.user(key, userId))
        }

    @Test
    fun `direct scaffold read holds its snapshot across settings and user queries`() =
        withDatabase { database, mutation ->
            val api =
                AccountScaffoldApi(
                    database,
                    ApiKeyQueries(database),
                    UserQueries(database),
                    OrganizationQueries(database),
                )
            val key = ApiKeyId.parse("22222222-2222-4222-8222-222222222222")
            val before = api.superGroups(key)
            mutation.beforeSecondRead = { database.executeSqlScript("UPDATE g_user SET nick = 'Changed'") }
            assertEquals(before, api.superGroups(key))
            assertNotEquals(before, api.superGroups(key))
        }

    @Test
    fun `info blob and managed user list also hold their snapshots`() {
        for (info in listOf(true, false)) {
            withDatabase { database, mutation ->
                database.executeSqlScript(
                    """
                    INSERT INTO g_api_key_to_super_group_type (settings_id, created_at, super_group_type_name)
                    VALUES ('40000000-0000-4000-8000-000000000001', CURRENT_TIMESTAMP, 'committee');
                    """.trimIndent(),
                )
                val users = UserQueries(database)
                val organizations = OrganizationQueries(database)
                val infoApi = InfoApi(database, ApiKeyQueries(database), users, organizations)
                val scaffold = AccountScaffoldApi(database, ApiKeyQueries(database), users, organizations)
                val read: () -> Any = {
                    if (info) infoApi.blob(infoKey) else scaffold.users(scaffoldKey)
                }
                val before = read()
                mutation.beforeSecondRead = { database.executeSqlScript("UPDATE g_user SET nick = 'Changed'") }
                assertEquals(before, read())
                assertNotEquals(before, read())
            }
        }
    }

    @Test
    fun `every client projection owns a consistent snapshot when called directly`() {
        for (projection in 0..6) {
            withDatabase { database, mutation ->
                val key = createClient(database)
                val api =
                    ClientApi(
                        database,
                        OAuthClientQueries(database),
                        UserQueries(database),
                        OrganizationQueries(database),
                    )
                val read: () -> Any? = {
                    when (projection) {
                        0 -> api.groups(key)
                        1 -> api.superGroups(key)
                        2 -> api.approvedUsers(key)
                        3 -> api.approvedUser(key, userId)
                        4 -> api.membershipsForApprovedUser(key, userId)
                        5 -> api.authorities(key)
                        else -> api.authoritiesForUser(key, userId)
                    }
                }
                val before = read()
                mutation.beforeSecondRead = {
                    database.executeSqlScript(
                        """
                        UPDATE g_user SET nick = 'Changed';
                        UPDATE g_group SET pretty_name = 'Changed';
                        UPDATE g_super_group SET pretty_name = 'Changed';
                        DELETE FROM g_client_authority_user;
                        DELETE FROM g_client_authority_super_group;
                        DELETE FROM g_client_authority;
                        """.trimIndent(),
                    )
                }
                assertEquals(before, read(), "Client projection $projection mixed snapshots")
                assertNotEquals(before, read(), "Client projection $projection did not observe the committed change")
            }
        }
    }

    @Test
    fun `complete reads reject ambient transactions and empty queries validate participation`() =
        withDatabase { database, mutation ->
            val source = mutation.source
            val users = UserQueries(database)
            val organizations = OrganizationQueries(database)
            val keys = ApiKeyQueries(database)
            val clients = OAuthClientQueries(database)
            val info = InfoApi(database, keys, users, organizations)
            val scaffold = AccountScaffoldApi(database, keys, users, organizations)
            val client = ClientApi(database, clients, users, organizations)
            val key = createClient(database)
            val reads =
                listOf<() -> Any?>(
                    { info.user(infoKey, userId) },
                    { info.blob(infoKey) },
                    { scaffold.superGroups(scaffoldKey) },
                    { scaffold.users(scaffoldKey) },
                    { client.groups(key) },
                    { client.superGroups(key) },
                    { client.approvedUsers(key) },
                    { client.approvedUser(key, userId) },
                    { client.membershipsForApprovedUser(key, userId) },
                    { client.authorities(key) },
                    { client.authoritiesForUser(key, userId) },
                )
            lateinit var completed: org.jetbrains.exposed.v1.jdbc.JdbcTransaction
            database.commitTransaction {
                completed = this
                for (read in reads) assertFailsWith<IllegalStateException> { read() }
                assertEquals(emptyList(), users.apiUsersByIdsIn(this, emptySet()))
                assertEquals(emptyList(), organizations.groupsByIdsIn(this, emptySet()))
            }
            assertFailsWith<IllegalStateException> { users.apiUsersByIdsIn(completed, emptySet()) }
            assertFailsWith<IllegalStateException> { organizations.groupsByIdsIn(completed, emptySet()) }
            assertFailsWith<IllegalStateException> { keys.infoSettingsIn(completed, infoKey) }
            DatabaseFactory(source).use { other ->
                other.commitTransaction {
                    assertFailsWith<IllegalStateException> { users.apiUsersIn(this) }
                    assertFailsWith<IllegalStateException> { organizations.listMembershipsIn(this) }
                    assertFailsWith<IllegalStateException> { keys.accountScaffoldSettingsIn(this, scaffoldKey) }
                    assertFailsWith<IllegalStateException> {
                        clients.findClientByApiKeyIn(
                            this,
                            OAuthApiKeyId(key.value),
                        )
                    }
                }
            }
        }

    @Test
    fun `scaffold keeps managed privacy filtering and client reads require approval`() =
        withDatabase { database, _ ->
            val users = UserQueries(database)
            val organizations = OrganizationQueries(database)
            val scaffold = AccountScaffoldApi(database, ApiKeyQueries(database), users, organizations)
            val managed = scaffold.users(scaffoldKey)
            assertTrue(managed.isNotEmpty())
            assertTrue(managed.all { it.gdprTrained })
            assertEquals(managed.sortedBy { it.cid.value }, managed)
            assertTrue(
                scaffold
                    .superGroups(scaffoldKey)
                    .flatMap { it.groups }
                    .flatMap { it.members }
                    .all { it.user.gdprTrained },
            )
            database.executeSqlScript("DELETE FROM g_gdpr_trained")
            assertEquals(emptyList(), scaffold.users(scaffoldKey))
            assertTrue(scaffold.superGroups(scaffoldKey).all { group -> group.groups.all { it.members.isEmpty() } })
            val key = createClient(database)
            val client = ClientApi(database, OAuthClientQueries(database), users, organizations)
            assertNotNull(client.approvedUser(key, userId))
            database.executeSqlScript("DELETE FROM g_user_approval")
            assertEquals(emptyList(), client.approvedUsers(key))
            assertNull(client.approvedUser(key, userId))
            assertNull(client.membershipsForApprovedUser(key, userId))
        }

    private fun createClient(database: DatabaseFactory): ApiKeyId {
        val key =
            CreateApiKey(
                database,
                bcryptCost = 10,
            ).create(ApiKeyName("Snapshot client"), LocalizedText.of(), ApiKeyType.CLIENT)
        val creation = CreateClient(database, bcryptCost = 10)
        val prepared =
            creation.prepare(
                NewOAuthClient(
                    RedirectUri("https://example.org/callback"),
                    ClientName("Snapshot client"),
                    LocalizedText.of(),
                    false,
                    ClientOwner.Official,
                ),
            )
        database.commitTransaction {
            val client =
                creation
                    .insertIn(
                        this,
                        prepared,
                        ClientApiCredential(OAuthApiKeyId(key.apiKey.id.value), OAuthApiToken(key.token.value)),
                    ).client
            ClientApprovals(database).approveIn(this, userId, client.uid, client.scopes)
            val authorities = ClientAuthorities(database)
            authorities.createIn(
                this,
                authorities.lockIn(this, client.uid),
                AuthorityName("manage"),
                setOf(userId),
                emptySet(),
            )
        }
        return key.apiKey.id
    }

    private fun withDatabase(test: (DatabaseFactory, ReadMutation) -> Unit) {
        PostgresTestEnvironment().use { postgres ->
            val mutation = ReadMutation(postgres.dataSource)
            DatabaseFactory(postgres.dataSource, listOf(mutation)).use { database -> test(database, mutation) }
        }
    }

    private companion object {
        val infoKey = ApiKeyId.parse("11111111-1111-4111-8111-111111111111")
        val scaffoldKey = ApiKeyId.parse("22222222-2222-4222-8222-222222222222")
        val userId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
    }
}

private class ReadMutation(
    val source: javax.sql.DataSource,
) : StatementInterceptor {
    var beforeSecondRead: (() -> Unit)? = null
    private var reads = 0

    override fun beforeExecution(
        transaction: Transaction,
        context: StatementContext,
    ) {
        if (beforeSecondRead != null && ++reads == 2) {
            val mutation = beforeSecondRead
            beforeSecondRead = null
            reads = 0
            mutation?.invoke()
        }
    }
}
