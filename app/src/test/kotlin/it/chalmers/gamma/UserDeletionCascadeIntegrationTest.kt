package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyName
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.apiaccess.DeleteOwnedApiKeys
import it.chalmers.gamma.media.LocalMediaStore
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.oauth.ClientApiCredential
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.DeleteOwnedOAuthClients
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthApiKeyId
import it.chalmers.gamma.oauth.OAuthApiToken
import it.chalmers.gamma.oauth.OAuthClientQueries
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.BcryptPasswordHasher
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.PasswordHash
import it.chalmers.gamma.users.PasswordHasher
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.UserAvatarUpload
import it.chalmers.gamma.users.UserAvatars
import it.chalmers.gamma.users.UserConflict
import it.chalmers.gamma.users.UserDeletion
import it.chalmers.gamma.users.UserQueries
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.springframework.session.FindByIndexNameSessionRepository
import org.springframework.session.MapSession
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.sql.SQLException
import java.util.Base64
import java.util.concurrent.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class UserDeletionCascadeIntegrationTest {
    @Test
    fun `administrator deletion removes owned clients keys and user before external cleanup`() =
        withDeletion {
            val keys = ApiKeyQueries(database)
            val key =
                CreateApiKey(
                    database,
                    bcryptCost = 10,
                ).create(ApiKeyName("Deletion test"), LocalizedText.of("Test", "Test"), ApiKeyType.INFO)
            val client =
                CreateClient(database, bcryptCost = 10).let { creation ->
                    val prepared =
                        creation.prepare(
                            NewOAuthClient(
                                RedirectUri("https://example.org/callback"),
                                ClientName("Deletion test"),
                                LocalizedText.of("Test", "Test"),
                                false,
                                ClientOwner.User(user.id),
                                true,
                            ),
                        )
                    database.commitTransaction {
                        creation.insertIn(
                            this,
                            prepared,
                            ClientApiCredential(OAuthApiKeyId(key.apiKey.id.value), OAuthApiToken(key.token.value)),
                        )
                    }
                }
            val official =
                CreateClient(database, bcryptCost = 10).let { creation ->
                    val prepared =
                        creation.prepare(
                            NewOAuthClient(
                                RedirectUri("https://example.org/callback"),
                                ClientName("Official test"),
                                LocalizedText.of("Test", "Test"),
                                false,
                                ClientOwner.Official,
                            ),
                        )
                    database.commitTransaction { creation.insertIn(this, prepared) }
                }
            assertTrue(operation().delete(AccountDeletion.Administrator(deletionTestAdministrator, user.id)))
            assertNull(users.findUser(user.id))
            assertNull(OAuthProtocolClients(database).serverClient(client.client.uid)?.client)
            assertNull(
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.findApiKeyIn(this, key.apiKey.id)
                },
            )
            assertNotNull(OAuthProtocolClients(database).serverClient(official.client.uid)?.client)
            assertTrue(sessions.deleted)
            assertFalse(Files.exists(root.resolve(avatar)))
        }

    @Test
    fun `failure deleting the user rolls back owned clients keys and pointer without cleanup`() =
        withDeletion {
            val keys = ApiKeyQueries(database)
            val key =
                CreateApiKey(
                    database,
                    bcryptCost = 10,
                ).create(ApiKeyName("Rollback test"), LocalizedText.of("Test", "Test"), ApiKeyType.INFO)
            val client =
                CreateClient(database, bcryptCost = 10).let { creation ->
                    val prepared =
                        creation.prepare(
                            NewOAuthClient(
                                RedirectUri("https://example.org/callback"),
                                ClientName("Rollback test"),
                                LocalizedText.of("Test", "Test"),
                                false,
                                ClientOwner.User(user.id),
                                true,
                            ),
                        )
                    database.commitTransaction {
                        creation.insertIn(
                            this,
                            prepared,
                            ClientApiCredential(OAuthApiKeyId(key.apiKey.id.value), OAuthApiToken(key.token.value)),
                        )
                    }
                }
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_user_deletion() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RAISE EXCEPTION 'deletion rejected'; END; $$;
                CREATE TRIGGER reject_user_deletion BEFORE DELETE ON g_user
                FOR EACH ROW EXECUTE FUNCTION reject_user_deletion();
                """.trimIndent(),
            )
            assertFails { operation().delete(AccountDeletion.Administrator(deletionTestAdministrator, user.id)) }
            assertNotNull(users.findUser(user.id))
            assertNotNull(OAuthProtocolClients(database).serverClient(client.client.uid)?.client)
            assertNotNull(
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.findApiKeyIn(this, key.apiKey.id)
                },
            )
            assertFalse(sessions.deleted)
            assertEquals(0, sessions.lookups)
            assertTrue(Files.exists(root.resolve(avatar)))
        }

    @Test
    fun `cached administrator flag cannot authorize deletion and final administrator survives`() =
        withDeletion {
            val staleAdministrator = Actor.User(ActorUserId(user.id.value), true)
            assertFailsWith<AccessDenied> {
                operation().delete(
                    AccountDeletion.Administrator(staleAdministrator, user.id),
                )
            }
            val administrator = assertNotNull(users.findUser(Cid("mscott")))
            assertFailsWith<UserConflict> {
                operation().delete(AccountDeletion.Administrator(deletionTestAdministrator, administrator.id))
            }
            assertNotNull(users.findUser(user.id))
            assertNotNull(users.findUser(administrator.id))
            assertEquals(0, sessions.lookups)
        }

    @Test
    fun `personal deletion rejects a changed password after verification`() =
        withDeletion {
            val racingHasher =
                object : PasswordHasher by hasher {
                    override fun verify(
                        password: PlainTextPassword,
                        hash: PasswordHash,
                    ): Boolean {
                        assertNull(TransactionManager.currentOrNull())
                        database.executeSqlScript(
                            "UPDATE g_user SET password = NULL WHERE user_id = '${user.id.value}'",
                        )
                        return true
                    }
                }
            assertFailsWith<UserConflict> { operation(racingHasher).delete(AccountDeletion.Personal(actor, password)) }
            assertNotNull(users.findUser(user.id))
            assertEquals(0, sessions.lookups)
            assertTrue(Files.exists(root.resolve(avatar)))
        }

    @Test
    fun `personal deletion rejects an invalid password and accepts the real password`() =
        withDeletion {
            assertFalse(operation().delete(AccountDeletion.Personal(actor, PlainTextPassword("an incorrect password"))))
            assertNotNull(users.findUser(user.id))
            assertEquals(0, sessions.lookups)
            assertTrue(operation().delete(AccountDeletion.Personal(actor, password)))
            assertNull(users.findUser(user.id))
            assertTrue(sessions.deleted)
            assertFalse(Files.exists(root.resolve(avatar)))
        }

    @Test
    fun `ambient transaction is rejected before verification or external effects`() =
        withDeletion {
            database.commitTransaction {
                assertFailsWith<IllegalStateException> { operation().delete(AccountDeletion.Personal(actor, password)) }
                assertFailsWith<IllegalStateException> {
                    operation().delete(AccountDeletion.Administrator(deletionTestAdministrator, user.id))
                }
            }
            assertNotNull(users.findUser(user.id))
            assertEquals(0, sessions.lookups)
            assertTrue(Files.exists(root.resolve(avatar)))
        }

    @Test
    fun `ambiguous committed deletion still evicts sessions and removes unreferenced media`() =
        withDeletion {
            commitFailure.armed = true
            assertFails { operation().delete(AccountDeletion.Administrator(deletionTestAdministrator, user.id)) }
            assertEquals(1, commitFailure.failures)
            assertNull(users.findUser(user.id))
            assertTrue(sessions.deleted)
            assertFalse(Files.exists(root.resolve(avatar)))
        }

    @Test
    fun `unavailable deletion confirmation retains media and sessions after an ambiguous commit`() =
        withDeletion {
            commitFailure.armed = true
            commitFailure.blockFollowingStatements = true
            assertFails { operation().delete(AccountDeletion.Administrator(deletionTestAdministrator, user.id)) }
            commitFailure.blockFollowingStatements = false
            assertNull(users.findUser(user.id))
            assertFalse(sessions.deleted)
            assertEquals(0, sessions.lookups)
            assertTrue(Files.exists(root.resolve(avatar)))
        }

    @Test
    fun `cleanup cancellation is not buried and session failure does not skip media cleanup`() =
        withDeletion {
            val original = IOException("session storage unavailable")
            val cancellation = CancellationException("media cleanup cancelled")
            sessions.failure = original
            val cancellingMedia =
                object : MediaStore by storage {
                    override fun delete(uri: MediaUri) {
                        assertNull(TransactionManager.currentOrNull())
                        assertNull(users.findUser(user.id))
                        storage.delete(uri)
                        throw cancellation
                    }
                }
            val result =
                assertFails {
                    operation(
                        media = cancellingMedia,
                    ).delete(AccountDeletion.Administrator(deletionTestAdministrator, user.id))
                }
            assertSame(cancellation, result)
            assertTrue(original in result.suppressed)
            assertFalse(Files.exists(root.resolve(avatar)))
        }

    private fun withDeletion(test: Fixture.() -> Unit) {
        PostgresTestEnvironment().use { postgres ->
            val interceptor = CommitFailure()
            DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { database ->
                val root = Files.createTempDirectory("gamma-account-deletion")
                try {
                    Fixture(database, root, interceptor).test()
                } finally {
                    Files.walk(root).use { it.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists) }
                }
            }
        }
    }

    private class Fixture(
        val database: DatabaseFactory,
        val root: Path,
        val commitFailure: CommitFailure,
    ) {
        val hasher = BcryptPasswordHasher(cost = 10)
        val users = UserQueries(database)
        val user = assertNotNull(users.findUser(Cid("jhalpert")))
        val actor = Actor.User(ActorUserId(user.id.value), false)
        val password = PlainTextPassword("password1337")
        val storage = LocalMediaStore(root)
        val sessions =
            DeletionSessions {
                assertNull(TransactionManager.currentOrNull())
                assertNull(users.findUser(user.id))
            }
        val avatar: String

        init {
            UserAvatars(database, storage).replaceMyAvatar(
                actor,
                UserAvatarUpload(
                    Base64.getDecoder().decode(
                        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=",
                    ),
                    "image/png",
                ),
            )
            avatar = assertNotNull(users.findUser(user.id)?.avatarUri)
        }

        fun operation(
            passwordHasher: PasswordHasher = hasher,
            media: MediaStore = storage,
        ) = UserDeletionCascade(
            database,
            DeleteOwnedOAuthClients(database),
            DeleteOwnedApiKeys(database),
            UserDeletion(database, passwordHasher),
            sessions,
            media,
        )
    }

    private class CommitFailure : StatementInterceptor {
        var armed = false
        var failures = 0
        var blockFollowingStatements = false

        override fun beforeExecution(
            transaction: Transaction,
            context: StatementContext,
        ) {
            if (blockFollowingStatements && failures > 0) throw SQLException("ownership unavailable")
        }

        override fun afterCommit(transaction: Transaction) {
            if (armed) {
                armed = false
                failures += 1
                throw SQLException("commit acknowledgement failed")
            }
        }
    }

    private class DeletionSessions(
        private val beforeCleanup: () -> Unit,
    ) : FindByIndexNameSessionRepository<MapSession> {
        private val session = MapSession()
        var deleted = false
        var lookups = 0
        var failure: Throwable? = null

        override fun createSession() = MapSession()

        override fun save(session: MapSession) = Unit

        override fun findById(id: String): MapSession? = if (!deleted && id == session.id) session else null

        override fun deleteById(id: String) {
            deleted = true
        }

        override fun findByIndexNameAndIndexValue(
            indexName: String,
            indexValue: String,
        ): Map<String, MapSession> {
            beforeCleanup()
            lookups += 1
            failure?.let { throw it }
            return if (deleted) emptyMap() else mapOf(session.id to session)
        }
    }
}
