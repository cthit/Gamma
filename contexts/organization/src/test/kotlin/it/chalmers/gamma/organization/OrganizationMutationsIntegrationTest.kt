package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OrganizationMutationsIntegrationTest {
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
                    val commands = OrganizationMutations(database)
                    val queries = OrganizationStore(database)

                    run {
                        val superGroup = assertNotNull(queries.findSuperGroup(superGroupId))
                        assertEquals(0, superGroup.version)
                        assertEquals(LocalizedText.of(), superGroup.description)
                        commands.updateSuperGroup(
                            superGroup.copy(description = LocalizedText.of(en = "Restored description")),
                        )
                        assertEquals(1, queries.findSuperGroup(superGroupId)?.version)

                        val group = assertNotNull(queries.findGroup(groupId))
                        assertEquals(0, group.version)
                        assertNull(group.avatarUri)
                        assertNull(group.bannerUri)
                        commands.updateGroup(group.copy(prettyName = PrettyName("Updated group")))
                        assertEquals(1, queries.findGroup(groupId)?.version)

                        val post = assertNotNull(queries.findPost(postId))
                        assertEquals(0, post.version)
                        assertEquals("", post.emailPrefix.value)
                        assertEquals(0, post.order.value)
                        commands.updatePost(post.copy(emailPrefix = EmailPrefix("updated")))
                        assertEquals(1, queries.findPost(postId)?.version)

                        val nullableMemberships =
                            queries.membershipsForGroup(
                                GroupId.parse("ee4153d5-830d-445f-acb3-ec09c53e7c0c"),
                            )
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
                    val commands = OrganizationStore(database)
                    val queries = commands

                    run {
                        val customType = SuperGroupType("testtype")
                        commands.createSuperGroupType(customType)
                        assertEquals(true, customType in queries.listSuperGroupTypes())
                        val superGroupId =
                            commands.createSuperGroup(
                                NewSuperGroup(
                                    name = OrganizationName("test-group"),
                                    prettyName = PrettyName("Test group"),
                                    type = customType,
                                    description = LocalizedText.of("Svenska", "English"),
                                ),
                            )
                        val created = assertNotNull(queries.findSuperGroup(superGroupId))
                        assertEquals("English", created.description.en.value)

                        val edited =
                            created.copy(
                                prettyName = PrettyName("Edited group"),
                                description = LocalizedText.of("Redigerad", "Edited"),
                            )
                        commands.updateSuperGroup(edited)
                        val updated = assertNotNull(queries.findSuperGroup(superGroupId))
                        assertEquals(1, updated.version)
                        assertEquals("Edited", updated.description.en.value)
                        assertFailsWith<OrganizationConflict> { commands.updateSuperGroup(edited) }

                        val groupId =
                            commands.createGroup(
                                NewGroup(
                                    name = OrganizationName("test-group-2026"),
                                    prettyName = PrettyName("Test group 2026"),
                                    superGroupId = superGroupId,
                                ),
                            )
                        assertEquals(superGroupId, assertNotNull(queries.findGroup(groupId)).superGroup.id)

                        val postId =
                            commands.createPost(
                                NewPost(
                                    name = LocalizedText.of("Testare", "Tester"),
                                    emailPrefix = EmailPrefix("tester"),
                                ),
                            )
                        assertEquals(4, assertNotNull(queries.findPost(postId)).order.value)

                        val reorderedPostIds = queries.listPosts().map { it.id }.reversed()
                        commands.reorderPosts(reorderedPostIds)
                        assertEquals(reorderedPostIds, queries.listPosts().map { it.id })
                        assertFails { commands.reorderPosts(reorderedPostIds.dropLast(1)) }
                        assertEquals(
                            reorderedPostIds,
                            queries.listPosts().map { it.id },
                            "a rejected partial order must not mutate persisted ordering",
                        )

                        val membership =
                            Membership(
                                userId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f"),
                                groupId = groupId,
                                postId = postId,
                                unofficialPostName = UnofficialPostName("Regression lead"),
                            )
                        commands.replaceMemberships(groupId, listOf(membership))
                        assertEquals(listOf(membership), queries.membershipsForGroup(groupId))
                        commands.changeUnofficialPostName(
                            membership.userId,
                            groupId,
                            postId,
                            UnofficialPostName("Changed"),
                        )
                        assertEquals(
                            "Changed",
                            queries
                                .membershipsForGroup(groupId)
                                .single()
                                .unofficialPostName.value,
                        )

                        commands.replaceMemberships(groupId, listOf(membership))
                        val invalidMembership =
                            membership.copy(
                                userId = UserId(UUID.fromString("00000000-0000-0000-0000-000000000000")),
                            )
                        assertFails { commands.replaceMemberships(groupId, listOf(invalidMembership)) }
                        assertEquals(
                            listOf(membership),
                            queries.membershipsForGroup(groupId),
                            "failed replacement must roll the deletion back",
                        )

                        commands.replaceMemberships(groupId, emptyList())
                        commands.deleteGroup(groupId)
                        commands.deletePost(postId)
                        commands.deleteSuperGroup(superGroupId)
                        commands.deleteSuperGroupType(customType)
                        assertEquals(false, customType in queries.listSuperGroupTypes())
                        assertEquals(null, queries.findSuperGroup(superGroupId))
                    }
                }
            }
    }
}
