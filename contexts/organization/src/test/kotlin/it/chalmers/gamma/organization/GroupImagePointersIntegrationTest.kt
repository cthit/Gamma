package it.chalmers.gamma.organization

import it.chalmers.gamma.media.DefaultMedia
import it.chalmers.gamma.media.LocalMediaStore
import it.chalmers.gamma.media.MediaContent
import it.chalmers.gamma.media.MediaObjectId
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.nio.file.Files
import java.nio.file.Path
import java.util.Base64
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GroupImagePointersIntegrationTest {
    @Test
    fun `group image writes preserve legacy row and version rules`() =
        withGroupImageCommands {
            val groupId = GroupId.parse(DIGIT_GROUP_ID)
            val originalGroup = assertNotNull(queries.findGroup(groupId))
            val unknownGroupId = GroupId.generate()

            val avatarValidation =
                assertFailsWith<IllegalArgumentException> {
                    commands.change(
                        groupAdministrator,
                        GroupImageChange(unknownGroupId, GroupImageKind.AVATAR, null, "a".repeat(256)),
                    )
                }
            assertEquals("Avatar URI is too long", avatarValidation.message)
            val bannerValidation =
                assertFailsWith<IllegalArgumentException> {
                    commands.change(
                        groupAdministrator,
                        GroupImageChange(unknownGroupId, GroupImageKind.BANNER, null, "b".repeat(256)),
                    )
                }
            assertEquals("Banner URI is too long", bannerValidation.message)

            val missingGroup =
                assertFailsWith<OrganizationNotFound> {
                    commands.change(
                        groupAdministrator,
                        GroupImageChange(unknownGroupId, GroupImageKind.AVATAR, null, "valid-avatar"),
                    )
                }
            assertEquals("Group does not exist", missingGroup.message)

            commands.change(groupAdministrator, GroupImageChange(groupId, GroupImageKind.AVATAR, null, "stored-avatar"))
            assertEquals(
                StoredImageRow("stored-avatar", null, 0),
                imageRow(groupId),
            )
            assertEquals(originalGroup.version + 1, queries.findGroup(groupId)?.version)

            commands.change(groupAdministrator, GroupImageChange(groupId, GroupImageKind.BANNER, null, "stored-banner"))
            assertEquals(
                StoredImageRow("stored-avatar", "stored-banner", originalGroup.version + 2),
                imageRow(groupId),
            )
            assertEquals(originalGroup.version + 2, queries.findGroup(groupId)?.version)
        }

    @Test
    fun `group image write rolls back its inserted row when group version update fails`() =
        withGroupImageCommands {
            val groupId = GroupId.parse(DIGIT_GROUP_ID)
            val originalGroup = assertNotNull(queries.findGroup(groupId))
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_group_image_version() RETURNS trigger LANGUAGE plpgsql AS ${'$'}${'$'}
                BEGIN
                    IF OLD.group_id = '$DIGIT_GROUP_ID'::UUID THEN
                        RAISE EXCEPTION 'forced group image version failure';
                    END IF;
                    RETURN NEW;
                END;
                ${'$'}${'$'};
                CREATE TRIGGER reject_group_image_version
                    BEFORE UPDATE ON g_group
                    FOR EACH ROW EXECUTE FUNCTION reject_group_image_version();
                """.trimIndent(),
            )

            assertFails {
                commands.change(
                    groupAdministrator,
                    GroupImageChange(groupId, GroupImageKind.AVATAR, null, "rolled-back-avatar"),
                )
            }

            assertNull(imageRow(groupId))
            assertEquals(originalGroup, queries.findGroup(groupId))
        }

    @Test
    fun `same pointer compare and set writers allow exactly one winner`() =
        withGroupImageCommands {
            val groupId = GroupId.parse(DIGIT_GROUP_ID)
            commands.change(groupAdministrator, GroupImageChange(groupId, GroupImageKind.AVATAR, null, "old-avatar"))
            val writersReady = CountDownLatch(2)

            Executors.newFixedThreadPool(2).use { workers ->
                val first =
                    workers.submit<OrganizationConflict?> {
                        writersReady.countDown()
                        writersReady.await()
                        try {
                            commands.change(
                                groupAdministrator,
                                GroupImageChange(groupId, GroupImageKind.AVATAR, "old-avatar", "first-avatar"),
                            )
                            null
                        } catch (conflict: OrganizationConflict) {
                            conflict
                        }
                    }
                val second =
                    workers.submit<OrganizationConflict?> {
                        writersReady.countDown()
                        writersReady.await()
                        try {
                            commands.change(
                                groupAdministrator,
                                GroupImageChange(groupId, GroupImageKind.AVATAR, "old-avatar", "second-avatar"),
                            )
                            null
                        } catch (conflict: OrganizationConflict) {
                            conflict
                        }
                    }

                val outcomes = listOf(first.get(), second.get())
                assertEquals(1, outcomes.count { it == null })
                assertEquals(1, outcomes.count { it is OrganizationConflict })
                assertTrue(queries.findGroup(groupId)?.avatarUri in setOf("first-avatar", "second-avatar"))
            }
        }

    @Test
    fun `serialization retry reapplies only the database pointer change`() =
        withGroupImageCommands {
            val groupId = GroupId.parse(DIGIT_GROUP_ID)
            val originalVersion = assertNotNull(queries.findGroup(groupId)).version
            commands.change(groupAdministrator, GroupImageChange(groupId, GroupImageKind.AVATAR, null, "old-avatar"))
            database.executeSqlScript(
                """
                CREATE SEQUENCE group_image_retry_attempt;
                CREATE FUNCTION retry_first_group_image_update() RETURNS trigger LANGUAGE plpgsql AS ${'$'}${'$'}
                BEGIN
                    IF nextval('group_image_retry_attempt') = 1 THEN
                        RAISE EXCEPTION 'retry group image update' USING ERRCODE = '40001';
                    END IF;
                    RETURN NEW;
                END;
                ${'$'}${'$'};
                CREATE TRIGGER retry_first_group_image_update
                    BEFORE UPDATE OF avatar_uri ON g_group_images_uri
                    FOR EACH ROW EXECUTE FUNCTION retry_first_group_image_update();
                """.trimIndent(),
            )

            commands.change(
                groupAdministrator,
                GroupImageChange(groupId, GroupImageKind.AVATAR, "old-avatar", "retried-avatar"),
            )

            assertEquals("retried-avatar", queries.findGroup(groupId)?.avatarUri)
            assertEquals(originalVersion + 2, queries.findGroup(groupId)?.version)
            assertEquals(originalVersion + 2, imageRow(groupId)?.version)
        }

    @Test
    fun `concurrent replacements with postgres and filesystem retain only the winner`() =
        withGroupImageCommands {
            val groupId = GroupId.parse(DIGIT_GROUP_ID)
            val mediaRoot = Files.createTempDirectory("gamma-concurrent-group-images")
            try {
                val mediaStore = LocalMediaStore(mediaRoot)
                val oldOperationId = GroupImageOperationId.generate()
                val oldImage = mediaStore.save(MediaObjectId(oldOperationId.value), PNG_BYTES, "image/png")
                commands.change(
                    groupAdministrator,
                    GroupImageChange(groupId, GroupImageKind.AVATAR, null, oldImage.value),
                )
                val storage = BarrierMediaStore(mediaStore)
                val administratorId = UserId(UUID.fromString("20000000-0000-0000-0000-000000000001"))
                val images = GroupImages(database, storage)
                val actor = Actor.User(ActorUserId(administratorId.value), isAdministrator = true)

                Executors.newFixedThreadPool(2).use { workers ->
                    val first =
                        workers.submit<OrganizationConflict?> {
                            try {
                                images.replace(
                                    actor,
                                    groupId,
                                    GroupImageKind.AVATAR,
                                    GroupImageUpload(PNG_BYTES, "image/png"),
                                )
                                null
                            } catch (conflict: OrganizationConflict) {
                                conflict
                            }
                        }
                    val second =
                        workers.submit<OrganizationConflict?> {
                            try {
                                images.replace(
                                    actor,
                                    groupId,
                                    GroupImageKind.AVATAR,
                                    GroupImageUpload(PNG_BYTES, "image/png"),
                                )
                                null
                            } catch (conflict: OrganizationConflict) {
                                conflict
                            }
                        }

                    storage.bothUploadsSaved.await()
                    val outcomes = listOf(first.get(), second.get())
                    assertEquals(1, outcomes.count { it == null })
                    assertEquals(1, outcomes.count { it is OrganizationConflict })
                    val winner = assertNotNull(queries.findGroup(groupId)?.avatarUri)
                    assertTrue(Files.isRegularFile(mediaRoot.resolve(winner)))
                    assertEquals(
                        listOf(winner),
                        Files.list(mediaRoot).use { paths -> paths.map { it.fileName.toString() }.toList() },
                    )
                    assertEquals(1, storage.stagedCleanupAttempts.get())
                }
            } finally {
                Files.walk(mediaRoot).use { paths ->
                    paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
                }
            }
        }

    private fun withGroupImageCommands(test: GroupImageCommandsFixture.() -> Unit) {
        val migrations =
            Path
                .of(checkNotNull(System.getProperty("gamma.root")))
                .resolve("app/src/main/resources/db/migration")
        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { postgres ->
                DatabaseFactory(
                    DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
                ).use { database ->
                    GroupImageCommandsFixture(
                        database,
                        GroupImagePointers(database),
                        OrganizationQueries(database),
                    ).test()
                }
            }
    }

    private class GroupImageCommandsFixture(
        val database: DatabaseFactory,
        val commands: GroupImagePointers,
        val queries: OrganizationQueries,
    ) {
        fun imageRow(groupId: GroupId): StoredImageRow? =
            database.commitTransaction(readOnly = true) {
                GroupImagesTable
                    .selectAll()
                    .where { GroupImagesTable.groupId eq groupId.value }
                    .limit(1)
                    .firstOrNull()
                    ?.let { row ->
                        StoredImageRow(
                            avatarUri = row[GroupImagesTable.avatarUri],
                            bannerUri = row[GroupImagesTable.bannerUri],
                            version = row[GroupImagesTable.version],
                        )
                    }
            }
    }

    private data class StoredImageRow(
        val avatarUri: String?,
        val bannerUri: String?,
        val version: Int?,
    )

    private companion object {
        const val DIGIT_GROUP_ID = "047ac437-a789-4cc5-bb6e-ba50efd7c509"
        val PNG_BYTES: ByteArray =
            Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=",
            )
    }
}

private class BarrierMediaStore(
    private val delegate: MediaStore,
) : MediaStore {
    private val savedCount = AtomicInteger()
    val bothUploadsSaved = CountDownLatch(2)
    val stagedCleanupAttempts = AtomicInteger()

    override fun save(
        bytes: ByteArray,
        declaredContentType: String?,
    ): MediaUri = delegate.save(bytes, declaredContentType)

    override fun save(
        objectId: MediaObjectId,
        bytes: ByteArray,
        declaredContentType: String?,
    ): MediaUri {
        val saved = delegate.save(objectId, bytes, declaredContentType)
        savedCount.incrementAndGet()
        bothUploadsSaved.countDown()
        bothUploadsSaved.await()
        return saved
    }

    override fun read(
        uri: MediaUri?,
        fallback: DefaultMedia,
    ): MediaContent = delegate.read(uri, fallback)

    override fun delete(uri: MediaUri) = delegate.delete(uri)

    override fun delete(objectId: MediaObjectId) {
        stagedCleanupAttempts.incrementAndGet()
        delegate.delete(objectId)
    }
}

class MediaGroupImageStorageIntegrationTest {
    @Test
    fun `staged cleanup removes every extension owned by the upload operation`() =
        run {
            withGroupImageStorage { root, storage ->
                val operationId =
                    GroupImageOperationId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                val stored = storage.save(MediaObjectId(operationId.value), PNG_BYTES, "image/png")
                val storedFile = root.resolve(stored.value)

                assertTrue(stored.value.startsWith(operationId.value.toString()))
                assertTrue(Files.isRegularFile(storedFile))

                storage.delete(MediaObjectId(operationId.value))

                assertFalse(Files.exists(storedFile))
            }
        }

    @Test
    fun `committed image deletion targets its exact stored URI`() =
        run {
            withGroupImageStorage { root, storage ->
                val operationId =
                    GroupImageOperationId(UUID.fromString("223e4567-e89b-12d3-a456-426614174000"))
                val stored = storage.save(MediaObjectId(operationId.value), PNG_BYTES, "image/png")
                val storedFile = root.resolve(stored.value)

                storage.delete(stored)

                assertFalse(Files.exists(storedFile))
            }
        }

    private fun withGroupImageStorage(test: (Path, LocalMediaStore) -> Unit) {
        val root = Files.createTempDirectory("gamma-group-image-storage-test")
        try {
            test(root, LocalMediaStore(root))
        } finally {
            Files.walk(root).use { paths ->
                paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
            }
        }
    }

    private companion object {
        val PNG_BYTES: ByteArray =
            Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=",
            )
    }
}
