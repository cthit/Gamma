package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.oauth.AuthorityName
import it.chalmers.gamma.oauth.ClientAuthorities
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthClientNotFound
import it.chalmers.gamma.oauth.OAuthClientQueries
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.UserAccountAccess
import java.sql.SQLException
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class OAuthClientAuthorityIntegrationTest {
    @Test
    fun `stale administrator authority cannot create a client authority`() =
        withDatabase { database ->
            val created = createClient(database)
            assertFailsWith<AccessDenied> {
                creation(database).create(
                    staleAdministrator,
                    created.client.uid,
                    name,
                    emptySet(),
                    emptySet(),
                )
            }
        }

    @Test
    fun `stale administrator authority cannot delete a client authority`() =
        withDatabase { database ->
            val created = createClient(database)
            creation(database).create(administrator, created.client.uid, name, emptySet(), emptySet())
            assertFailsWith<AccessDenied> {
                deletion(database).delete(staleAdministrator, created.client.uid, name)
            }
        }

    @Test
    fun `owners and current administrators create and delete the complete authority`() =
        withDatabase { database ->
            for (personal in listOf(false, true)) {
                val created = createClient(database, personal)
                val actor = if (personal) owner else administrator.copy(isAdministrator = false)
                val operation = creation(database)
                operation.create(actor, created.client.uid, name, setOf(ownerId), setOf(superGroupId))
                val store = OAuthClientQueries(database)
                val authority =
                    database
                        .commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            store.authoritiesIn(this, created.client.uid)
                        }.single()
                assertEquals(name, authority.name)
                assertEquals(setOf(ownerId), authority.userIds)
                assertEquals(setOf(superGroupId), authority.superGroupIds)
                assertFailsWith<IllegalArgumentException> {
                    operation.create(actor, created.client.uid, name, emptySet(), emptySet())
                }
                val otherName = AuthorityName("view")
                operation.create(actor, created.client.uid, otherName, emptySet(), emptySet())
                deletion(database).delete(actor, created.client.uid, name)
                assertEquals(
                    listOf(
                        otherName,
                    ),
                    database
                        .commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            store.authoritiesIn(this, created.client.uid)
                        }.map { it.name },
                )
                assertFailsWith<OAuthClientNotFound> { deletion(database).delete(actor, created.client.uid, name) }
                deletion(database).delete(actor, created.client.uid, otherName)
            }
            assertEquals(listOf(0L, 0L, 0L), authorityRows(database))
        }

    @Test
    fun `denied callers and enclosing transactions cannot change authorities`() =
        withDatabase { database ->
            val created = createClient(database, personal = true)
            val create = creation(database)
            val delete = deletion(database)
            create.create(owner, created.client.uid, name, setOf(ownerId), setOf(superGroupId))
            val before = authorityRows(database)
            database.executeSqlScript("DELETE FROM g_admin_user WHERE user_id = '${administrator.userId.value}'")
            for (actor in listOf(Actor.Anonymous, administrator, Actor.User(ActorUserId(UUID.randomUUID()), true))) {
                assertFailsWith<AccessDenied> { create.create(actor, created.client.uid, name, emptySet(), emptySet()) }
                assertFailsWith<AccessDenied> { delete.delete(actor, created.client.uid, name) }
            }
            database.executeSqlScript("UPDATE g_user SET locked = TRUE WHERE user_id = '${ownerId.value}'")
            assertFailsWith<AccessDenied> { create.create(owner, created.client.uid, name, emptySet(), emptySet()) }
            assertFailsWith<AccessDenied> { delete.delete(owner, created.client.uid, name) }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> {
                    create.create(owner, created.client.uid, name, emptySet(), emptySet())
                }
                assertFailsWith<IllegalStateException> { delete.delete(owner, created.client.uid, name) }
            }
            assertEquals(before, authorityRows(database))
        }

    @Test
    fun `a late assignment failure rolls back authority and earlier assignments`() =
        withDatabase { database ->
            val created = createClient(database)
            val before = authorityRows(database)
            assertFailsWith<SQLException> {
                creation(database).create(
                    administrator,
                    created.client.uid,
                    name,
                    setOf(ownerId),
                    setOf(UUID.randomUUID()),
                )
            }
            assertEquals(before, authorityRows(database))
        }

    @Test
    fun `a rejected parent deletion restores both sets of authority assignments`() =
        withDatabase { database ->
            val created = createClient(database)
            creation(database).create(administrator, created.client.uid, name, setOf(ownerId), setOf(superGroupId))
            val before = authorityRows(database)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_authority_deletion() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RAISE EXCEPTION 'authority deletion rejected'; END $$;
                CREATE TRIGGER reject_authority_deletion BEFORE DELETE ON g_client_authority
                FOR EACH ROW EXECUTE FUNCTION reject_authority_deletion();
                """.trimIndent(),
            )
            assertFailsWith<SQLException> { deletion(database).delete(administrator, created.client.uid, name) }
            assertEquals(before, authorityRows(database))
        }

    @Test
    fun `a skipped assignment cannot report successful creation`() =
        withDatabase { database ->
            val created = createClient(database)
            database.executeSqlScript(
                """
                CREATE FUNCTION skip_authority_assignment() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RETURN NULL; END $$;
                CREATE TRIGGER skip_authority_assignment BEFORE INSERT ON g_client_authority_user
                FOR EACH ROW EXECUTE FUNCTION skip_authority_assignment();
                """.trimIndent(),
            )
            assertFailsWith<IllegalStateException> {
                creation(database).create(administrator, created.client.uid, name, setOf(ownerId), setOf(superGroupId))
            }
            assertEquals(listOf(0L, 0L, 0L), authorityRows(database))
        }

    @Test
    fun `competing creates and deletes each have one winner`() =
        withDatabase { database ->
            val created = createClient(database, personal = true)
            val workers = Executors.newFixedThreadPool(2)
            try {
                for (creating in listOf(true, false)) {
                    val start = CountDownLatch(1)
                    val results =
                        listOf(administrator, owner).map { actor ->
                            workers.submit<Throwable?> {
                                start.await()
                                runCatching {
                                    if (creating) {
                                        creation(database).create(
                                            actor,
                                            created.client.uid,
                                            name,
                                            emptySet(),
                                            setOf(superGroupId),
                                        )
                                    } else {
                                        deletion(database).delete(actor, created.client.uid, name)
                                    }
                                }.exceptionOrNull()
                            }
                        }
                    start.countDown()
                    val failures = results.map { it.get(10, TimeUnit.SECONDS) }
                    assertEquals(1, failures.count { it == null })
                    if (creating) {
                        assertTrue(failures.filterNotNull().single() is IllegalArgumentException)
                    } else {
                        assertTrue(failures.filterNotNull().single() is OAuthClientNotFound)
                    }
                }
            } finally {
                workers.shutdownNow()
            }
            assertEquals(listOf(0L, 0L, 0L), authorityRows(database))
        }

    private fun creation(database: DatabaseFactory) =
        CreateOAuthClientAuthority(database, UserAccountAccess(database), ClientAuthorities(database))

    private fun deletion(database: DatabaseFactory) =
        DeleteOAuthClientAuthority(database, UserAccountAccess(database), ClientAuthorities(database))

    private fun createClient(
        database: DatabaseFactory,
        personal: Boolean = false,
    ) = CreateOAuthClient(
        database,
        UserAccountAccess(database),
        CreateClient(database, bcryptCost = 10),
        CreateApiKey(database, bcryptCost = 10),
    ).create(
        if (personal) owner else administrator,
        NewOAuthClient(
            RedirectUri("https://example.org/callback"),
            ClientName("Authority test"),
            LocalizedText.of(),
            false,
            if (personal) ClientOwner.User(ownerId) else ClientOwner.Official,
        ),
    )

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private companion object {
        val ownerId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
        val owner = Actor.User(ActorUserId(ownerId.value))
        val superGroupId: UUID = UUID.fromString("712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab")
        val administrator = Actor.User(ActorUserId(UUID.fromString("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")), true)
        val staleAdministrator = Actor.User(ActorUserId(UUID.fromString("bc605869-9a4d-46ec-8a29-d00819d4c195")), true)
        val name = AuthorityName("manage")
    }
}

private fun authorityRows(database: DatabaseFactory) =
    listOf(
        "g_client_authority",
        "g_client_authority_user",
        "g_client_authority_super_group",
    ).map(database::tableRowCount)
