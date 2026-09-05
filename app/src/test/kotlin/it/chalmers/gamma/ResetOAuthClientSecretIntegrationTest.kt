package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.oauth.AuthorityName
import it.chalmers.gamma.oauth.ClientAuthorities
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthClientConflict
import it.chalmers.gamma.oauth.OAuthClientNotFound
import it.chalmers.gamma.oauth.OAuthClientQueries
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.oauth.RawClientSecret
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.oauth.RotateClientSecret
import it.chalmers.gamma.oauth.clientSecretMatches
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.UserAccountAccess
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.security.SecureRandom
import java.sql.SQLException
import java.util.UUID
import java.util.concurrent.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ResetOAuthClientSecretIntegrationTest {
    @Test
    fun `stale administrator authority cannot reset an official client secret`() =
        withDatabase { database ->
            val uid = create(database).client.uid
            assertFailsWith<AccessDenied> { reset(database, staleAdministrator, uid) }
        }

    @Test
    fun `secret preparation runs outside a transaction`() =
        withDatabase { database ->
            val uid = create(database).client.uid
            val random =
                object : SecureRandom() {
                    override fun nextBytes(bytes: ByteArray) {
                        assertNull(TransactionManager.currentOrNull(), "Secret preparation must be outside SQL")
                        super.nextBytes(bytes)
                    }
                }
            reset(database, administrator, uid, random)
        }

    @Test
    fun `administrators and owners receive a usable replacement without a remaining reservation`() =
        withDatabase { database ->
            for (personal in listOf(false, true)) {
                val created = create(database, personal)
                val actor = if (personal) owner else administrator.copy(isAdministrator = false)
                val clients = OAuthClientQueries(database)
                database.commitTransaction {
                    val authorities = ClientAuthorities(database)
                    authorities.createIn(
                        this,
                        authorities.lockIn(this, created.client.uid),
                        AuthorityName("manage"),
                        setOf(UserId(owner.userId.value)),
                        emptySet(),
                    )
                }
                val result = reset(database, actor, created.client.uid)
                assertEquals(
                    database.commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        clients.authoritiesIn(this, created.client.uid)
                    },
                    result.authorities,
                )
                assertEquals(created.client, result.client)
                assertFalse(clientSecretMatches(database, created.client.clientId, created.secret))
                assertTrue(clientSecretMatches(database, created.client.clientId, result.secret))
                assertTrue(result.secret.value !in result.toString())
                assertEquals(0, database.tableRowCount("g_client_secret_rotation"))
            }
        }

    @Test
    fun `demotion during preparation denies replacement and releases the reservation`() =
        withDatabase { database ->
            val created = create(database)
            val random =
                SecretRotationRandom {
                    database.executeSqlScript(
                        "DELETE FROM g_admin_user WHERE user_id = '${administrator.userId.value}'",
                    )
                }
            assertFailsWith<AccessDenied> { reset(database, administrator, created.client.uid, random) }
            assertTrue(clientSecretMatches(database, created.client.clientId, created.secret))
            assertEquals(0, database.tableRowCount("g_client_secret_rotation"))
        }

    @Test
    fun `denied callers and enclosing transactions cannot prepare a secret`() =
        withDatabase { database ->
            val created = create(database, personal = true)
            val random = SecretRotationRandom()
            assertFailsWith<AccessDenied> { reset(database, Actor.Anonymous, created.client.uid, random) }
            assertFailsWith<AccessDenied> {
                reset(database, Actor.User(ActorUserId(UUID.randomUUID()), true), created.client.uid, random)
            }
            database.executeSqlScript("DELETE FROM g_admin_user WHERE user_id = '${administrator.userId.value}'")
            assertFailsWith<AccessDenied> { reset(database, administrator, created.client.uid, random) }
            database.executeSqlScript("UPDATE g_user SET locked = TRUE WHERE user_id = '${owner.userId.value}'")
            assertFailsWith<AccessDenied> { reset(database, owner, created.client.uid, random) }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> { reset(database, owner, created.client.uid, random) }
            }
            assertEquals(0, random.secrets)
            assertEquals(0, database.tableRowCount("g_client_secret_rotation"))
        }

    @Test
    fun `a competing operation on another database factory rejects while the first prepares`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                DatabaseFactory(postgres.dataSource).use { other ->
                    val created = create(database)
                    val competingRandom = SecretRotationRandom()
                    val random =
                        SecretRotationRandom {
                            assertFailsWith<OAuthClientConflict> {
                                reset(other, administrator, created.client.uid, competingRandom)
                            }
                            assertEquals(1, database.tableRowCount("g_client_secret_rotation"))
                        }
                    val result = reset(database, administrator, created.client.uid, random)
                    assertEquals(1, random.secrets)
                    assertEquals(0, competingRandom.secrets)
                    assertTrue(clientSecretMatches(database, created.client.clientId, result.secret))
                }
            }
        }
    }

    @Test
    fun `an expired preparation cannot overwrite a newer reset`() =
        withDatabase { database ->
            val created = create(database)
            var winner: RawClientSecret? = null
            val random =
                SecretRotationRandom {
                    database.executeSqlScript("UPDATE g_client_secret_rotation SET expires_at = '2000-01-01'")
                    winner = reset(database, administrator, created.client.uid).secret
                }
            assertFailsWith<OAuthClientConflict> { reset(database, administrator, created.client.uid, random) }
            assertTrue(clientSecretMatches(database, created.client.clientId, assertNotNull(winner)))
            assertEquals(0, database.tableRowCount("g_client_secret_rotation"))
        }

    @Test
    fun `failure cleanup cannot remove another request reservation`() =
        withDatabase { database ->
            val created = create(database)
            val replacementId = UUID.randomUUID()
            val random =
                SecretRotationRandom {
                    database.executeSqlScript("UPDATE g_client_secret_rotation SET reservation_id = '$replacementId'")
                }
            assertFailsWith<OAuthClientConflict> { reset(database, administrator, created.client.uid, random) }
            assertEquals(1, database.tableRowCount("g_client_secret_rotation"))
            assertFailsWith<OAuthClientConflict> { reset(database, administrator, created.client.uid) }
            assertTrue(clientSecretMatches(database, created.client.clientId, created.secret))
        }

    @Test
    fun `cancellation during preparation propagates and frees the client for another reset`() =
        withDatabase { database ->
            val created = create(database)
            val cancellation = CancellationException("secret preparation cancelled")
            val random = SecretRotationRandom { throw cancellation }
            assertSame(
                cancellation,
                assertFailsWith<CancellationException> {
                    reset(database, administrator, created.client.uid, random)
                },
            )
            assertEquals(0, database.tableRowCount("g_client_secret_rotation"))
            assertTrue(clientSecretMatches(database, created.client.clientId, created.secret))
            reset(database, administrator, created.client.uid)
        }

    @Test
    fun `SQL rejection keeps the original secret and releases the reservation`() =
        withDatabase { database ->
            val created = create(database)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_client_secret() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RAISE EXCEPTION 'secret update rejected'; END $$;
                CREATE TRIGGER reject_client_secret BEFORE UPDATE ON g_client
                FOR EACH ROW EXECUTE FUNCTION reject_client_secret();
                """.trimIndent(),
            )
            val random = SecretRotationRandom()
            assertFailsWith<SQLException> { reset(database, administrator, created.client.uid, random) }
            assertEquals(1, random.secrets)
            assertEquals(0, database.tableRowCount("g_client_secret_rotation"))
            assertTrue(clientSecretMatches(database, created.client.clientId, created.secret))
        }

    @Test
    fun `SQL retry and lost commit acknowledgements reuse one prepared secret`() {
        for (loseReservationAcknowledgement in listOf(false, true)) {
            PostgresTestEnvironment().use { postgres ->
                var armed = false
                val interceptor =
                    object : StatementInterceptor {
                        override fun afterCommit(transaction: Transaction) {
                            if (armed) {
                                armed = false
                                throw SQLException("lost secret reset acknowledgement")
                            }
                        }
                    }
                DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { database ->
                    val created = create(database)
                    if (loseReservationAcknowledgement) armed = true
                    val random = SecretRotationRandom { if (!loseReservationAcknowledgement) armed = true }
                    val result = reset(database, administrator, created.client.uid, random)
                    assertEquals(1, random.secrets)
                    assertEquals(0, database.tableRowCount("g_client_secret_rotation"))
                    assertTrue(clientSecretMatches(database, created.client.clientId, result.secret))
                }
            }
        }
    }

    @Test
    fun `single connection reset works and a deleted client cannot be recreated`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(
                DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 1),
            ).use { database ->
                val created = create(database)
                reset(database, administrator, created.client.uid)
                val random =
                    SecretRotationRandom {
                        DeleteOAuthClient(
                            database,
                            UserAccountAccess(database),
                            it.chalmers.gamma.oauth
                                .DeleteClient(database),
                            it.chalmers.gamma.apiaccess
                                .DeleteOwnedApiKeys(database),
                        ).delete(administrator, created.client.uid)
                    }
                assertFailsWith<OAuthClientNotFound> { reset(database, administrator, created.client.uid, random) }
                assertEquals(0, database.tableRowCount("g_client_secret_rotation"))
                assertNull(OAuthProtocolClients(database).serverClient(created.client.uid)?.client)
            }
        }
    }

    @Test
    fun `a skipped UPDATE cannot return a replacement secret`() =
        withDatabase { database ->
            val created = create(database)
            database.executeSqlScript(
                """
                CREATE FUNCTION skip_secret_update() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RETURN NULL; END $$;
                CREATE TRIGGER skip_secret_update BEFORE UPDATE ON g_client
                FOR EACH ROW EXECUTE FUNCTION skip_secret_update();
                """.trimIndent(),
            )
            assertFailsWith<OAuthClientNotFound> { reset(database, administrator, created.client.uid) }
            assertTrue(clientSecretMatches(database, created.client.clientId, created.secret))
            assertEquals(0, database.tableRowCount("g_client_secret_rotation"))
        }

    @Test
    fun `failed cleanup preserves cancellation and expiry permits recovery`() =
        withDatabase { database ->
            val created = create(database)
            val cancellation = CancellationException("preparation cancelled")
            val random =
                SecretRotationRandom {
                    database.executeSqlScript(
                        """
                        CREATE FUNCTION reject_reservation_cleanup() RETURNS trigger LANGUAGE plpgsql AS $$
                        BEGIN RAISE EXCEPTION 'reservation cleanup unavailable'; END $$;
                        CREATE TRIGGER reject_reservation_cleanup BEFORE DELETE ON g_client_secret_rotation
                        FOR EACH ROW EXECUTE FUNCTION reject_reservation_cleanup();
                        """.trimIndent(),
                    )
                    throw cancellation
                }
            val failure =
                assertFailsWith<CancellationException> { reset(database, administrator, created.client.uid, random) }
            assertSame(cancellation, failure)
            assertTrue(failure.suppressed.single() is SQLException)
            assertEquals(1, database.tableRowCount("g_client_secret_rotation"))
            assertTrue(clientSecretMatches(database, created.client.clientId, created.secret))
            database.executeSqlScript(
                """
                DROP TRIGGER reject_reservation_cleanup ON g_client_secret_rotation;
                UPDATE g_client_secret_rotation SET expires_at = '2000-01-01';
                """.trimIndent(),
            )
            reset(database, administrator, created.client.uid)
            assertEquals(0, database.tableRowCount("g_client_secret_rotation"))
        }

    private fun reset(
        database: DatabaseFactory,
        actor: Actor,
        uid: ClientUid,
        random: SecureRandom = SecureRandom(),
    ) = ResetOAuthClientSecret(
        database,
        UserAccountAccess(database),
        RotateClientSecret(database, bcryptCost = 10, random = random),
    ).reset(actor, uid)

    private fun create(
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
            ClientName("Secret reset test"),
            LocalizedText.of(),
            false,
            if (personal) ClientOwner.User(UserId(owner.userId.value)) else ClientOwner.Official,
        ),
    )

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private companion object {
        val owner = Actor.User(ActorUserId(UUID.fromString("bc605869-9a4d-46ec-8a29-d00819d4c195")))
        val administrator = Actor.User(ActorUserId(UUID.fromString("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")), true)
        val staleAdministrator = Actor.User(ActorUserId(UUID.fromString("bc605869-9a4d-46ec-8a29-d00819d4c195")), true)
    }
}

private class SecretRotationRandom(
    private val duringPreparation: () -> Unit = {},
) : SecureRandom() {
    var secrets = 0

    override fun nextBytes(bytes: ByteArray) {
        assertNull(TransactionManager.currentOrNull())
        super.nextBytes(bytes)
        secrets++
        duringPreparation()
    }
}
