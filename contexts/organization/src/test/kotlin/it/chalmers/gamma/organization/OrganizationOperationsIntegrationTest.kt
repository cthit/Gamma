package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OrganizationOperationsIntegrationTest {
    @Test
    fun `reads and updates nullable organization values`() {
        val root = Path.of(checkNotNull(System.getProperty("gamma.root")))
        val migrations = root.resolve("app/src/main/resources/db/migration")

        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { postgres ->
                DatabaseFactory(
                    DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
                ).use { database ->
                    val superGroupId = SuperGroupId.parse("aed27030-ad90-4526-855c-1e909b1dcecb")
                    val groupId = GroupId.parse("2abe2264-fd61-4899-ba46-851279d85229")
                    val postId = PostId.parse("7bb1db15-730d-4864-bfc3-99abe7c0ccf8")
                    database.executeSqlScript(
                        """
                        UPDATE g_super_group SET description = NULL, version = NULL
                        WHERE super_group_id = '${superGroupId.value}';
                        UPDATE g_group SET version = NULL WHERE group_id = '${groupId.value}';
                        INSERT INTO g_group_images_uri (
                            created_at, updated_at, group_id, avatar_uri, banner_uri, version
                        ) VALUES (NOW(), NOW(), '${groupId.value}', NULL, NULL, NULL);
                        UPDATE g_post SET email_prefix = NULL, version = NULL, post_order = NULL
                        WHERE post_id = '${postId.value}'
                        """.trimIndent(),
                    )
                    val queries = OrganizationQueries(database)

                    run {
                        val superGroup = assertNotNull(queries.superGroupDetails(superGroupId)?.superGroup)
                        assertEquals(0, superGroup.version)
                        assertEquals(LocalizedText.of(), superGroup.description)
                        UpdateSuperGroup(database).update(
                            groupAdministrator,
                            SuperGroupUpdate(
                                superGroup.id,
                                superGroup.version,
                                superGroup.name,
                                superGroup.prettyName,
                                superGroup.type,
                                LocalizedText.of(en = "Restored description"),
                            ),
                        )
                        assertEquals(1, queries.superGroupDetails(superGroupId)?.superGroup?.version)

                        val group = assertNotNull(queries.findGroup(groupId))
                        assertEquals(0, group.version)
                        assertNull(group.avatarUri)
                        assertNull(group.bannerUri)
                        UpdateGroup(database).update(
                            groupAdministrator,
                            GroupUpdate(
                                group.id,
                                group.version,
                                group.name,
                                PrettyName("Updated group"),
                                group.superGroup.id,
                                database
                                    .commitTransaction(
                                        readOnly = true,
                                    ) { queries.membershipsForGroupIn(this, group.id) }
                                    .map {
                                        NewGroupMembership(it.userId, it.postId, it.unofficialPostName)
                                    },
                            ),
                        )
                        assertEquals(1, queries.findGroup(groupId)?.version)

                        val post = assertNotNull(queries.findPost(postId))
                        assertEquals(0, post.version)
                        assertEquals("", post.emailPrefix.value)
                        assertEquals(0, post.order.value)
                        UpdatePost(database).update(
                            groupAdministrator,
                            PostUpdate(post.id, post.version, post.name, EmailPrefix("updated")),
                        )
                        assertEquals(1, queries.findPost(postId)?.version)

                        val nullableMemberships =
                            database.commitTransaction(readOnly = true) {
                                queries.membershipsForGroupIn(
                                    this,
                                    GroupId.parse("ee4153d5-830d-445f-acb3-ec09c53e7c0c"),
                                )
                            }
                        assertEquals(4, nullableMemberships.size)
                        assertEquals(setOf(null), nullableMemberships.map { it.unofficialPostName.value }.toSet())
                    }
                }
            }
    }

    @Test
    fun `writes atomically and enforces optimistic versions on real postgres`() {
        val root = Path.of(checkNotNull(System.getProperty("gamma.root")))
        val migrations = root.resolve("app/src/main/resources/db/migration")

        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { postgres ->
                DatabaseFactory(
                    DatabaseSettings(
                        jdbcUrl = postgres.jdbcUrl,
                        username = postgres.username,
                        password = postgres.password,
                        maximumPoolSize = 2,
                    ),
                ).use { database ->
                    val queries = OrganizationQueries(database)

                    run {
                        val customType = SuperGroupType("testtype")
                        SuperGroupTypes(database).create(groupAdministrator, customType)
                        assertEquals(true, customType in queries.listSuperGroupTypes())
                        val superGroupId =
                            CreateSuperGroup(database).create(
                                groupAdministrator,
                                NewSuperGroup(
                                    name = OrganizationName("test-group"),
                                    prettyName = PrettyName("Test group"),
                                    type = customType,
                                    description = LocalizedText.of("Svenska", "English"),
                                ),
                            )
                        val created = assertNotNull(queries.superGroupDetails(superGroupId)?.superGroup)
                        assertEquals("English", created.description.en.value)

                        val edited =
                            SuperGroupUpdate(
                                created.id,
                                created.version,
                                created.name,
                                PrettyName("Edited group"),
                                created.type,
                                LocalizedText.of("Redigerad", "Edited"),
                            )
                        UpdateSuperGroup(database).update(groupAdministrator, edited)
                        val updated = assertNotNull(queries.superGroupDetails(superGroupId)?.superGroup)
                        assertEquals(1, updated.version)
                        assertEquals("Edited", updated.description.en.value)
                        assertFailsWith<OrganizationConflict> {
                            UpdateSuperGroup(database).update(groupAdministrator, edited)
                        }

                        val groupId =
                            CreateGroup(database).create(
                                groupAdministrator,
                                NewGroup(
                                    name = OrganizationName("test-group-2026"),
                                    prettyName = PrettyName("Test group 2026"),
                                    superGroupId = superGroupId,
                                ),
                                emptyList(),
                            )
                        assertEquals(superGroupId, assertNotNull(queries.findGroup(groupId)).superGroup.id)

                        val postId =
                            CreatePost(database).create(
                                groupAdministrator,
                                NewPost(
                                    name = LocalizedText.of("Testare", "Tester"),
                                    emailPrefix = EmailPrefix("tester"),
                                ),
                            )
                        assertEquals(4, assertNotNull(queries.findPost(postId)).order.value)

                        val reorderedPostIds = queries.listPosts().map { it.id }.reversed()
                        ReorderPosts(database).reorder(groupAdministrator, reorderedPostIds)
                        assertEquals(reorderedPostIds, queries.listPosts().map { it.id })
                        assertFails { ReorderPosts(database).reorder(groupAdministrator, reorderedPostIds.dropLast(1)) }
                        assertEquals(
                            reorderedPostIds,
                            queries.listPosts().map { it.id },
                            "a rejected partial order must not mutate persisted ordering",
                        )

                        DeleteGroup(database).delete(groupAdministrator, groupId)
                        DeletePost(database).delete(groupAdministrator, postId)
                        DeleteSuperGroup(database).delete(groupAdministrator, superGroupId)
                        SuperGroupTypes(database).delete(groupAdministrator, customType)
                        assertEquals(false, customType in queries.listSuperGroupTypes())
                        assertEquals(null, queries.superGroupDetails(superGroupId)?.superGroup)
                    }
                }
            }
    }
}
