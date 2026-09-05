package it.chalmers.gamma

import it.chalmers.gamma.media.LocalMediaStore
import it.chalmers.gamma.media.MediaObjectId
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.organization.CreateGroup
import it.chalmers.gamma.organization.DeleteGroup
import it.chalmers.gamma.organization.DeletePost
import it.chalmers.gamma.organization.DeleteSuperGroup
import it.chalmers.gamma.organization.GroupId
import it.chalmers.gamma.organization.GroupImageKind
import it.chalmers.gamma.organization.GroupImageUpload
import it.chalmers.gamma.organization.GroupImages
import it.chalmers.gamma.organization.GroupUpdate
import it.chalmers.gamma.organization.NewGroup
import it.chalmers.gamma.organization.OrganizationName
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.PostId
import it.chalmers.gamma.organization.PrettyName
import it.chalmers.gamma.organization.ReorderPosts
import it.chalmers.gamma.organization.SuperGroupId
import it.chalmers.gamma.organization.SuperGroupType
import it.chalmers.gamma.organization.SuperGroupTypes
import it.chalmers.gamma.organization.UpdateGroup
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.UserAccountAccess
import java.nio.file.Files
import java.util.Base64
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class OrganizationAuthorityIntegrationTest {
    @Test
    fun `demoted administrator cannot create edit delete or reorder organization records`() =
        withDatabase { database ->
            val access = CurrentOrganizationAccess(UserAccountAccess(database))
            val queries = OrganizationQueries(database)
            val group = assertNotNull(queries.findGroup(groupId))
            database.executeSqlScript("DELETE FROM g_admin_user WHERE user_id = '${administrator.userId.value}'")
            val stale = administrator.copy(isAdministrator = true)
            assertFailsWith<AccessDenied> { CreateGroup(database, access).create(stale, newGroup, emptyList()) }
            assertFailsWith<AccessDenied> { UpdateGroup(database, access).update(stale, edit(group.version)) }
            assertFailsWith<AccessDenied> { DeleteGroup(database, access).delete(stale, groupId) }
            assertFailsWith<AccessDenied> { DeletePost(database, access).delete(stale, PostId.generate()) }
            assertFailsWith<AccessDenied> { DeleteSuperGroup(database, access).delete(stale, superGroupId) }
            assertFailsWith<AccessDenied> { SuperGroupTypes(database, access).create(stale, SuperGroupType("denied")) }
            assertFailsWith<AccessDenied> { ReorderPosts(database, access).reorder(stale, emptyList()) }
            assertEquals(group, queries.findGroup(groupId))
        }

    @Test
    fun `current administrator can create and edit with a stale false request flag`() =
        withDatabase { database ->
            val access = CurrentOrganizationAccess(UserAccountAccess(database))
            val queries = OrganizationQueries(database)
            val id = CreateGroup(database, access).create(administrator, newGroup, emptyList())
            assertEquals(newGroup.name, assertNotNull(queries.findGroup(id)).name)
            val original = assertNotNull(queries.findGroup(groupId))
            UpdateGroup(database, access).update(administrator, edit(original.version))
            assertEquals(original.version + 1, assertNotNull(queries.findGroup(groupId)).version)
        }

    @Test
    fun `demotion during an image upload rejects publication and removes the uploaded object`() =
        withDatabase { database ->
            val access = CurrentOrganizationAccess(UserAccountAccess(database))
            val queries = OrganizationQueries(database)
            // Use a new group so the administrator has no independent membership authority.
            val id = CreateGroup(database, access).create(administrator, newGroup, emptyList())
            val original = assertNotNull(queries.findGroup(id))
            val root = Files.createTempDirectory("organization-authority-images")
            try {
                val storage = LocalMediaStore(root)
                val demotingStorage =
                    object : MediaStore by storage {
                        override fun save(
                            objectId: MediaObjectId,
                            bytes: ByteArray,
                            declaredContentType: String?,
                        ): MediaUri {
                            val saved = storage.save(objectId, bytes, declaredContentType)
                            database.executeSqlScript(
                                "DELETE FROM g_admin_user WHERE user_id = '${administrator.userId.value}'",
                            )
                            return saved
                        }
                    }
                assertFailsWith<AccessDenied> {
                    GroupImages(database, demotingStorage, access).replace(
                        administrator.copy(isAdministrator = true),
                        id,
                        GroupImageKind.AVATAR,
                        GroupImageUpload(Base64.getDecoder().decode(IMAGE), "image/png"),
                    )
                }
                assertEquals(original, queries.findGroup(id))
                assertEquals(0L, Files.list(root).use { it.count() })
            } finally {
                root.toFile().deleteRecursively()
            }
        }

    @Test
    fun `administrator demotion waits for an authorized group edit to commit`() {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource).use { database ->
                val access = CurrentOrganizationAccess(UserAccountAccess(database))
                val queries = OrganizationQueries(database)
                val original = assertNotNull(queries.findGroup(groupId))
                postgres.connection { blocker ->
                    blocker.createStatement().use {
                        it
                            .executeQuery(
                                "SELECT group_id FROM g_group WHERE group_id = '${groupId.value}' FOR UPDATE",
                            ).close()
                    }
                    val workers = Executors.newFixedThreadPool(2)
                    try {
                        val update =
                            workers.submit {
                                UpdateGroup(
                                    database,
                                    access,
                                ).update(administrator, edit(original.version))
                            }
                        waitForLock(postgres, "g_group")
                        val demotion =
                            workers.submit {
                                postgres.connection { connection ->
                                    connection.createStatement().use {
                                        it.executeUpdate(
                                            "DELETE FROM g_admin_user WHERE user_id = '${administrator.userId.value}'",
                                        )
                                    }
                                    connection.commit()
                                }
                            }
                        waitForLock(postgres, "g_admin_user")
                        blocker.commit()
                        update.get(20, TimeUnit.SECONDS)
                        demotion.get(20, TimeUnit.SECONDS)
                        assertEquals(original.version + 1, assertNotNull(queries.findGroup(groupId)).version)
                        assertFailsWith<AccessDenied> {
                            UpdateGroup(database, access).update(
                                administrator.copy(isAdministrator = true),
                                edit(original.version + 1),
                            )
                        }
                    } finally {
                        blocker.rollback()
                        workers.shutdownNow()
                    }
                }
            }
        }
    }

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private fun edit(version: Int) =
        GroupUpdate(
            groupId,
            version,
            OrganizationName("authority-edited"),
            newGroup.prettyName,
            superGroupId,
            emptyList(),
        )

    private fun waitForLock(
        postgres: PostgresTestEnvironment,
        table: String,
    ) {
        repeat(200) {
            val blocked =
                postgres.connection { connection ->
                    connection.createStatement().use { statement ->
                        statement
                            .executeQuery(
                                "SELECT COUNT(*) FROM pg_stat_activity WHERE datname = current_database() " +
                                    "AND cardinality(pg_blocking_pids(pid)) > 0 AND query LIKE '%$table%'",
                            ).use { result ->
                                check(result.next())
                                result.getInt(1)
                            }
                    }
                }
            if (blocked > 0) return
            Thread.sleep(25)
        }
        error("Timed out waiting for the $table lock")
    }

    private companion object {
        val administrator = Actor.User(ActorUserId(UUID.fromString("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")))
        val groupId = GroupId.parse("047ac437-a789-4cc5-bb6e-ba50efd7c509")
        val superGroupId = SuperGroupId.parse("aed27030-ad90-4526-855c-1e909b1dcecb")
        val newGroup = NewGroup(OrganizationName("authority-group"), PrettyName("Authority group"), superGroupId)
        const val IMAGE = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII="
    }
}
