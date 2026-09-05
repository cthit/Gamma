package it.chalmers.gamma.users

import it.chalmers.gamma.media.LocalMediaStore
import it.chalmers.gamma.media.MediaObjectId
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.sql.SQLException
import java.util.Base64
import java.util.UUID
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

class UserAvatarsIntegrationTest {
    @Test
    fun `commit acknowledgement failure cannot delete the new current avatar`() =
        withAvatars {
            UserAvatars(database, storage).replaceMyAvatar(user.profileActor(), upload)
            val failingStorage =
                object : MediaStore by storage {
                    override fun save(
                        objectId: MediaObjectId,
                        bytes: ByteArray,
                        declaredContentType: String?,
                    ): MediaUri {
                        val saved = storage.save(objectId, bytes, declaredContentType)
                        commitFailure.armed = true
                        return saved
                    }
                }
            // The retry sees the committed pointer and conflicts; it must not repeat the mutation.
            assertFailsWith<UserConflict> {
                UserAvatars(database, failingStorage).replaceMyAvatar(user.profileActor(), upload)
            }
            assertEquals(user.version + 2, users.findUser(user.id)?.version)
            assertEquals(1, commitFailure.failures)
            val current = assertNotNull(users.findUser(user.id)?.avatarUri)
            assertTrue(Files.exists(root.resolve(current)), "The database must never reference deleted bytes")
            assertEquals(1L, Files.list(root).use { it.count() })
        }

    @Test
    fun `cleanup cancellation propagates instead of being buried under a storage failure`() =
        withAvatars {
            val original = IOException("upload failed")
            val cancellation = CancellationException("cleanup cancelled")
            val failingStorage =
                object : MediaStore by storage {
                    override fun save(
                        objectId: MediaObjectId,
                        bytes: ByteArray,
                        declaredContentType: String?,
                    ): MediaUri = throw original

                    override fun delete(objectId: MediaObjectId): Unit = throw cancellation
                }
            assertSame(
                cancellation,
                assertFailsWith<CancellationException> {
                    UserAvatars(database, failingStorage).replaceMyAvatar(user.profileActor(), upload)
                },
            )
            assertTrue(original in cancellation.suppressed)
        }

    @Test
    fun `an enclosing transaction is rejected before writing media`() =
        withAvatars {
            database.commitTransaction {
                assertFailsWith<IllegalStateException> {
                    UserAvatars(database, storage).replaceMyAvatar(user.profileActor(), upload)
                }
                assertFailsWith<IllegalStateException> {
                    UserAvatars(
                        database,
                        storage,
                    ).deleteMyAvatar(user.profileActor())
                }
                assertFailsWith<IllegalStateException> {
                    UserAvatars(database, storage).deleteUserAvatarAsAdministrator(user.profileActor(), user.id)
                }
            }
            assertEquals(0L, Files.list(root).use { it.count() })
        }

    @Test
    fun `replacement and both deletion operations persist pointers before removing bytes`() =
        withAvatars {
            val administrator = checkNotNull(users.findUser(Cid(FIXTURE_ADMINISTRATOR_CID)))
            val observingStorage =
                object : MediaStore by storage {
                    override fun save(
                        objectId: MediaObjectId,
                        bytes: ByteArray,
                        declaredContentType: String?,
                    ): MediaUri {
                        assertNull(TransactionManager.currentOrNull())
                        return storage.save(objectId, bytes, declaredContentType)
                    }

                    override fun delete(uri: MediaUri) {
                        assertNull(TransactionManager.currentOrNull())
                        assertTrue(users.findUser(user.id)?.avatarUri != uri.value)
                        storage.delete(uri)
                    }
                }
            val avatars = UserAvatars(database, observingStorage)
            avatars.replaceMyAvatar(user.profileActor(), upload)
            avatars.replaceMyAvatar(user.profileActor(), upload)
            assertEquals(user.version + 2, users.findUser(user.id)?.version)
            assertEquals(1L, Files.list(root).use { it.count() })
            avatars.deleteMyAvatar(user.profileActor())
            assertNull(users.findUser(user.id)?.avatarUri)
            avatars.deleteMyAvatar(user.profileActor())
            avatars.replaceMyAvatar(user.profileActor(), upload)
            avatars.deleteUserAvatarAsAdministrator(administrator.profileActor(), user.id)
            assertNull(users.findUser(user.id)?.avatarUri)
            assertEquals(0L, Files.list(root).use { it.count() })
        }

    @Test
    fun `upload conflicts when another upload replaces its captured avatar`() =
        withAvatars {
            val avatars = UserAvatars(database, storage)
            avatars.replaceMyAvatar(user.profileActor(), upload)
            var winner: String? = null
            val racingStorage =
                object : MediaStore by storage {
                    override fun save(
                        objectId: MediaObjectId,
                        bytes: ByteArray,
                        declaredContentType: String?,
                    ): MediaUri {
                        val saved = storage.save(objectId, bytes, declaredContentType)
                        avatars.replaceMyAvatar(user.profileActor(), upload)
                        winner = users.findUser(user.id)?.avatarUri
                        return saved
                    }
                }
            assertFailsWith<UserConflict> {
                UserAvatars(database, racingStorage).replaceMyAvatar(user.profileActor(), upload)
            }
            assertEquals(winner, users.findUser(user.id)?.avatarUri)
            assertTrue(Files.exists(root.resolve(assertNotNull(winner))))
            assertEquals(1L, Files.list(root).use { it.count() })
        }

    @Test
    fun `failed pointer update rolls back the avatar and version and removes staged bytes`() =
        withAvatars {
            UserAvatars(database, storage).replaceMyAvatar(user.profileActor(), upload)
            val previous = checkNotNull(users.findUser(user.id))
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_avatar_version() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RAISE EXCEPTION 'avatar update rejected'; END; $$;
                CREATE TRIGGER reject_avatar_version BEFORE UPDATE ON g_user
                FOR EACH ROW EXECUTE FUNCTION reject_avatar_version();
                """.trimIndent(),
            )
            assertFails { UserAvatars(database, storage).replaceMyAvatar(user.profileActor(), upload) }
            assertEquals(previous, users.findUser(user.id))
            assertTrue(Files.exists(root.resolve(assertNotNull(previous.avatarUri))))
            assertEquals(1L, Files.list(root).use { it.count() })
        }

    @Test
    fun `ambiguous committed deletion removes the displaced bytes after the retry conflicts`() =
        withAvatars {
            val avatars = UserAvatars(database, storage)
            avatars.replaceMyAvatar(user.profileActor(), upload)
            val previous = assertNotNull(users.findUser(user.id)?.avatarUri)
            commitFailure.afterNextCommit = { commitFailure.armed = true }
            commitFailure.skipArmedCommit = true
            assertFails { avatars.deleteMyAvatar(user.profileActor()) }
            assertEquals(1, commitFailure.failures)
            assertNull(users.findUser(user.id)?.avatarUri)
            assertFalse(Files.exists(root.resolve(previous)))
        }

    @Test
    fun `administrator demoted after capture cannot clear the users avatar`() =
        withAvatars {
            val administrator = checkNotNull(users.findUser(Cid(FIXTURE_ADMINISTRATOR_CID)))
            val avatars = UserAvatars(database, storage)
            avatars.replaceMyAvatar(user.profileActor(), upload)
            val previous = assertNotNull(users.findUser(user.id)?.avatarUri)
            commitFailure.afterNextCommit = {
                database.executeSqlScript("DELETE FROM g_admin_user WHERE user_id = '${administrator.id.value}'")
            }
            assertFailsWith<AccessDenied> {
                avatars.deleteUserAvatarAsAdministrator(administrator.profileActor(true), user.id)
            }
            assertEquals(previous, users.findUser(user.id)?.avatarUri)
            assertTrue(Files.exists(root.resolve(previous)))
        }

    @Test
    fun `unavailable ownership after an ambiguous commit retains both images`() =
        withAvatars {
            UserAvatars(database, storage).replaceMyAvatar(user.profileActor(), upload)
            val previous = assertNotNull(users.findUser(user.id)?.avatarUri)
            val failingStorage =
                object : MediaStore by storage {
                    override fun save(
                        objectId: MediaObjectId,
                        bytes: ByteArray,
                        declaredContentType: String?,
                    ): MediaUri {
                        val saved = storage.save(objectId, bytes, declaredContentType)
                        commitFailure.armed = true
                        commitFailure.blockFollowingStatements = true
                        return saved
                    }
                }
            assertFails { UserAvatars(database, failingStorage).replaceMyAvatar(user.profileActor(), upload) }
            commitFailure.blockFollowingStatements = false
            val current = assertNotNull(users.findUser(user.id)?.avatarUri)
            assertTrue(current != previous)
            assertTrue(Files.exists(root.resolve(current)))
            assertTrue(Files.exists(root.resolve(previous)))
            assertEquals(2L, Files.list(root).use { it.count() })
        }

    @Test
    fun `cleanup interruption takes precedence over an upload failure`() =
        withAvatars {
            val original = IOException("upload failed")
            val interruption = InterruptedException("cleanup interrupted")
            val failingStorage =
                object : MediaStore by storage {
                    override fun save(
                        objectId: MediaObjectId,
                        bytes: ByteArray,
                        declaredContentType: String?,
                    ): MediaUri {
                        storage.save(objectId, bytes, declaredContentType)
                        throw original
                    }

                    override fun delete(objectId: MediaObjectId) {
                        storage.delete(objectId)
                        throw interruption
                    }
                }
            assertSame(
                interruption,
                assertFailsWith<InterruptedException> {
                    UserAvatars(database, failingStorage).replaceMyAvatar(user.profileActor(), upload)
                },
            )
            assertTrue(original in interruption.suppressed)
            assertEquals(0L, Files.list(root).use { it.count() })
        }

    @Test
    fun `anonymous missing and unauthorized users cannot change avatars`() =
        withAvatars {
            val avatars = UserAvatars(database, storage)
            assertFailsWith<AccessDenied> { avatars.replaceMyAvatar(Actor.Anonymous, upload) }
            assertFailsWith<AccessDenied> { avatars.deleteMyAvatar(Actor.Anonymous) }
            assertFailsWith<AccessDenied> { avatars.deleteUserAvatarAsAdministrator(user.profileActor(true), user.id) }
            val missingActor = Actor.User(ActorUserId(UUID.randomUUID()), false)
            assertFailsWith<UserNotFound> { avatars.replaceMyAvatar(missingActor, upload) }
            assertFailsWith<UserNotFound> { avatars.deleteMyAvatar(missingActor) }
            assertEquals(0L, Files.list(root).use { it.count() })
        }

    private fun withAvatars(test: AvatarFixture.() -> Unit) {
        PostgresTestEnvironment().use { postgres ->
            val failure = AvatarCommitFailure()
            DatabaseFactory(postgres.dataSource, listOf(failure)).use { database ->
                val root = Files.createTempDirectory("gamma-user-avatar")
                try {
                    AvatarFixture(database, UserQueries(database), root, LocalMediaStore(root), failure).test()
                } finally {
                    Files.walk(root).use { it.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists) }
                }
            }
        }
    }

    private class AvatarFixture(
        val database: DatabaseFactory,
        val users: UserQueries,
        val root: Path,
        val storage: LocalMediaStore,
        val commitFailure: AvatarCommitFailure,
    ) {
        val user = checkNotNull(users.findUser(Cid("jhalpert")))
    }

    private class AvatarCommitFailure : StatementInterceptor {
        var armed = false
        var failures = 0
        var blockFollowingStatements = false
        var skipArmedCommit = false
        var afterNextCommit: (() -> Unit)? = null

        override fun beforeExecution(
            transaction: Transaction,
            context: StatementContext,
        ) {
            if (blockFollowingStatements && failures > 0) throw SQLException("ownership unavailable")
        }

        override fun afterCommit(transaction: Transaction) {
            val callback = afterNextCommit
            afterNextCommit = null
            callback?.invoke()
            if (armed && skipArmedCommit) {
                skipArmedCommit = false
                return
            }
            if (armed) {
                armed = false
                failures += 1
                throw SQLException("commit acknowledgement failed")
            }
        }
    }

    private companion object {
        val upload =
            UserAvatarUpload(
                Base64.getDecoder().decode(
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=",
                ),
                "image/png",
            )
    }
}
