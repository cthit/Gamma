package it.chalmers.gamma.organization

import it.chalmers.gamma.media.LocalMediaStore
import it.chalmers.gamma.media.MediaObjectId
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
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

class GroupImagesIntegrationTest {
    @Test
    fun `commit failure followed by a retry conflict never deletes the committed image`() =
        withImages {
            val old = storage.save(imageBytes, "image/png")
            GroupImagePointers(
                database,
            ).change(groupAdministrator, GroupImageChange(existingGroupId, GroupImageKind.AVATAR, null, old.value))
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

            assertFails {
                GroupImages(
                    database,
                    failingStorage,
                ).replace(groupAdministrator, existingGroupId, GroupImageKind.AVATAR, upload)
            }

            assertEquals(1, commitFailure.failures)
            val current = assertNotNull(queries.findGroup(existingGroupId)?.avatarUri)
            assertTrue(Files.isRegularFile(root.resolve(current)), "committed pointer must retain its image bytes")
            assertEquals(listOf(current), Files.list(root).use { it.map { path -> path.fileName.toString() }.toList() })
        }

    @Test
    fun `committed cleanup propagates cancellation and interruption without undoing the pointer`() =
        withImages {
            for (failure in listOf(
                CancellationException("cancel cleanup"),
                InterruptedException("interrupt cleanup"),
            )) {
                GroupImages(
                    database,
                    storage,
                ).replace(groupAdministrator, existingGroupId, GroupImageKind.AVATAR, upload)
                val failingStorage =
                    object : MediaStore by storage {
                        override fun delete(uri: MediaUri): Unit = throw failure
                    }
                val actual =
                    assertFails {
                        GroupImages(
                            database,
                            failingStorage,
                        ).replace(groupAdministrator, existingGroupId, GroupImageKind.AVATAR, upload)
                    }
                assertSame(failure, actual)
                val current = assertNotNull(queries.findGroup(existingGroupId)?.avatarUri)
                assertTrue(Files.isRegularFile(root.resolve(current)))
                val deletionFailure =
                    assertFails {
                        GroupImages(
                            database,
                            failingStorage,
                        ).delete(groupAdministrator, existingGroupId, GroupImageKind.AVATAR)
                    }
                assertSame(failure, deletionFailure)
                assertNull(queries.findGroup(existingGroupId)?.avatarUri)
            }
        }

    @Test
    fun `membership revoked during upload rejects the image write and cleans staged bytes`() =
        withImages {
            val groupId =
                CreateGroup(database).create(
                    groupAdministrator,
                    NewGroup(OrganizationName("revoked-image"), PrettyName("Revoked image"), existingSuperGroupId),
                    listOf(groupMembership),
                )
            val revokingStorage =
                object : MediaStore by storage {
                    override fun save(
                        objectId: MediaObjectId,
                        bytes: ByteArray,
                        declaredContentType: String?,
                    ): MediaUri {
                        val saved = storage.save(objectId, bytes, declaredContentType)
                        val current = assertNotNull(queries.findGroup(groupId))
                        UpdateGroup(database).update(
                            groupAdministrator,
                            GroupUpdate(
                                groupId,
                                current.version,
                                current.name,
                                current.prettyName,
                                current.superGroup.id,
                                emptyList(),
                            ),
                        )
                        return saved
                    }
                }
            assertFailsWith<AccessDenied> {
                GroupImages(
                    database,
                    revokingStorage,
                ).replace(ordinaryGroupUser, groupId, GroupImageKind.AVATAR, upload)
            }
            assertNull(queries.findGroup(groupId)?.avatarUri)
            assertEquals(0L, Files.list(root).use { it.count() })
        }

    @Test
    fun `ambiguous committed deletion cleans only the displaced image`() =
        withImages {
            GroupImages(database, storage).replace(groupAdministrator, existingGroupId, GroupImageKind.AVATAR, upload)
            val old = assertNotNull(queries.findGroup(existingGroupId)?.avatarUri)
            commitFailure.armed = true
            commitFailure.commitsBeforeFailure = 1
            assertFails {
                GroupImages(
                    database,
                    storage,
                ).delete(groupAdministrator, existingGroupId, GroupImageKind.AVATAR)
            }
            assertEquals(1, commitFailure.failures)
            assertNull(queries.findGroup(existingGroupId)?.avatarUri)
            assertFalse(Files.exists(root.resolve(old)))
        }

    @Test
    fun `unavailable ownership read retains both staged and previously stored bytes`() =
        withImages {
            GroupImages(database, storage).replace(groupAdministrator, existingGroupId, GroupImageKind.AVATAR, upload)
            val old = assertNotNull(queries.findGroup(existingGroupId)?.avatarUri)
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
            assertFails {
                GroupImages(
                    database,
                    failingStorage,
                ).replace(groupAdministrator, existingGroupId, GroupImageKind.AVATAR, upload)
            }
            commitFailure.blockFollowingStatements = false
            assertEquals(1, commitFailure.failures)
            val current = assertNotNull(queries.findGroup(existingGroupId)?.avatarUri)
            assertTrue(Files.exists(root.resolve(current)))
            assertTrue(Files.exists(root.resolve(old)))
            assertEquals(2L, Files.list(root).use { it.count() })
        }

    @Test
    fun `failed upload removes bytes written before the storage error and preserves the pointer`() =
        withImages {
            GroupImages(database, storage).replace(groupAdministrator, existingGroupId, GroupImageKind.AVATAR, upload)
            val old = assertNotNull(queries.findGroup(existingGroupId)?.avatarUri)
            val expected = IOException("save failed after writing bytes")
            val failingStorage =
                object : MediaStore by storage {
                    override fun save(
                        objectId: MediaObjectId,
                        bytes: ByteArray,
                        declaredContentType: String?,
                    ): MediaUri {
                        storage.save(objectId, bytes, declaredContentType)
                        throw expected
                    }
                }
            val failure =
                assertFails {
                    GroupImages(
                        database,
                        failingStorage,
                    ).replace(groupAdministrator, existingGroupId, GroupImageKind.AVATAR, upload)
                }
            assertSame(expected, failure)
            assertEquals(old, queries.findGroup(existingGroupId)?.avatarUri)
            assertEquals(listOf(old), Files.list(root).use { it.map { path -> path.fileName.toString() }.toList() })
        }

    @Test
    fun `cleanup cancellation takes precedence over an ordinary upload failure`() =
        withImages {
            val saveFailure = IOException("save failed")
            val cancellation = CancellationException("cancel compensation")
            val failingStorage =
                object : MediaStore by storage {
                    override fun save(
                        objectId: MediaObjectId,
                        bytes: ByteArray,
                        declaredContentType: String?,
                    ): MediaUri = throw saveFailure

                    override fun delete(objectId: MediaObjectId): Unit = throw cancellation
                }
            val failure =
                assertFails {
                    GroupImages(
                        database,
                        failingStorage,
                    ).replace(groupAdministrator, existingGroupId, GroupImageKind.AVATAR, upload)
                }
            assertSame(cancellation, failure)
            assertTrue(saveFailure in failure.suppressed)
            assertNull(queries.findGroup(existingGroupId)?.avatarUri)
        }

    @Test
    fun `ordinary cleanup failure preserves a successful committed replacement`() =
        withImages {
            GroupImages(database, storage).replace(groupAdministrator, existingGroupId, GroupImageKind.AVATAR, upload)
            val failingStorage =
                object : MediaStore by storage {
                    override fun delete(uri: MediaUri): Unit = throw IOException("cleanup unavailable")
                }
            GroupImages(
                database,
                failingStorage,
            ).replace(groupAdministrator, existingGroupId, GroupImageKind.AVATAR, upload)
            val current = assertNotNull(queries.findGroup(existingGroupId)?.avatarUri)
            assertTrue(Files.exists(root.resolve(current)))
        }

    private fun withImages(test: ImageFixture.() -> Unit) {
        val migrations =
            Path
                .of(
                    checkNotNull(System.getProperty("gamma.root")),
                ).resolve("app/src/main/resources/db/migration")
        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { postgres ->
                val failure = ImageCommitFailure()
                DatabaseFactory(postgres.dataSource, listOf(failure)).use { database ->
                    val root = Files.createTempDirectory("gamma-image-operation")
                    try {
                        ImageFixture(
                            database,
                            OrganizationQueries(database),
                            root,
                            LocalMediaStore(root),
                            failure,
                        ).test()
                    } finally {
                        Files.walk(root).use { it.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists) }
                    }
                }
            }
    }

    private class ImageFixture(
        val database: DatabaseFactory,
        val queries: OrganizationQueries,
        val root: Path,
        val storage: LocalMediaStore,
        val commitFailure: ImageCommitFailure,
    )

    private class ImageCommitFailure : StatementInterceptor {
        var armed = false
        var failures = 0
        var commitsBeforeFailure = 0
        var blockFollowingStatements = false

        override fun beforeExecution(
            transaction: Transaction,
            context: StatementContext,
        ) {
            if (blockFollowingStatements && failures > 0) throw SQLException("ownership unavailable")
        }

        override fun afterCommit(transaction: Transaction) {
            if (armed && commitsBeforeFailure > 0) {
                commitsBeforeFailure -= 1
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
        val imageBytes: ByteArray =
            Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=",
            )
        val upload = GroupImageUpload(imageBytes, "image/png")
    }
}
