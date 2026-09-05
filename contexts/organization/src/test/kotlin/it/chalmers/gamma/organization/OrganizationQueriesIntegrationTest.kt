package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.nio.file.Path
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OrganizationQueriesIntegrationTest {
    @Test
    fun `legacy memberships retain their absent unofficial names`() =
        withGroupDatabase { database, queries ->
            val memberships =
                database.commitTransaction(readOnly = true) {
                    queries.membershipsForGroupIn(this, GroupId.parse("ee4153d5-830d-445f-acb3-ec09c53e7c0c"))
                }
            assertEquals(4, memberships.size)
            assertEquals(setOf(null), memberships.map { it.unofficialPostName.value }.toSet())
        }

    @Test
    fun `reads the deterministic organization snapshot through Exposed`() {
        val root = Path.of(checkNotNull(System.getProperty("gamma.root")))
        val migrations = root.resolve("app/src/main/resources/db/migration")

        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { postgres ->
                DatabaseFactory(
                    settings =
                        DatabaseSettings(
                            jdbcUrl = postgres.jdbcUrl,
                            username = postgres.username,
                            password = postgres.password,
                            maximumPoolSize = 2,
                        ),
                ).use { database ->
                    val organizations = OrganizationQueries(database)
                    val membershipQueries = organizations

                    run {
                        assertEquals(
                            listOf("alumni", "board", "committee", "functionaries", "society"),
                            organizations.listSuperGroupTypes().map { it.value },
                        )
                        assertEquals(9, organizations.listSuperGroups().size)
                        assertEquals(
                            listOf("digit", "prit"),
                            organizations.listSuperGroups(SuperGroupType("committee")).map { it.name.value },
                        )

                        val digit =
                            assertNotNull(
                                organizations
                                    .superGroupDetails(
                                        SuperGroupId.parse("aed27030-ad90-4526-855c-1e909b1dcecb"),
                                    )?.superGroup,
                            )
                        assertEquals("digIT", digit.prettyName.value)
                        assertEquals(1, organizations.listGroups(digit.id).size)
                        assertEquals(9, organizations.listGroups().size)
                        val digit2026 = organizations.listGroups(digit.id).single()
                        val digit2026Member =
                            UserId.parse("858e5acc-c289-40d3-9422-d6d317f40299")
                        assertTrue(
                            database.commitTransaction(readOnly = true) {
                                membershipQueries.isMemberOfAnySuperGroupIn(
                                    this,
                                    digit2026Member,
                                    setOf(digit.id),
                                )
                            },
                        )
                        assertFalse(
                            database.commitTransaction(readOnly = true) {
                                membershipQueries.isMemberOfAnySuperGroupIn(
                                    this,
                                    digit2026Member,
                                    setOf(SuperGroupId.generate()),
                                )
                            },
                        )
                        assertFalse(
                            database.commitTransaction(readOnly = true) {
                                membershipQueries.isMemberOfAnySuperGroupIn(
                                    this,
                                    digit2026Member,
                                    emptySet(),
                                )
                            },
                        )

                        val group =
                            assertNotNull(
                                organizations.findGroup(
                                    GroupId.parse("047ac437-a789-4cc5-bb6e-ba50efd7c509"),
                                ),
                            )
                        assertEquals("digit2025", group.name.value)
                        assertEquals("didit", group.superGroup.name.value)
                        assertNull(group.avatarUri)

                        val pointers = GroupImagePointers(database, organizationAccess(database))
                        pointers.change(
                            groupAdministrator,
                            GroupImageChange(
                                group.id,
                                GroupImageKind.AVATAR,
                                null,
                                "11111111-1111-4111-8111-111111111111.png",
                            ),
                        )
                        pointers.change(
                            groupAdministrator,
                            GroupImageChange(
                                group.id,
                                GroupImageKind.BANNER,
                                null,
                                "22222222-2222-4222-8222-222222222222.jpg",
                            ),
                        )
                        val imagedGroup = assertNotNull(organizations.findGroup(group.id))
                        assertEquals("11111111-1111-4111-8111-111111111111.png", imagedGroup.avatarUri)
                        assertEquals("22222222-2222-4222-8222-222222222222.jpg", imagedGroup.bannerUri)
                        assertEquals(group.version + 2, imagedGroup.version)
                        pointers.change(
                            groupAdministrator,
                            GroupImageChange(group.id, GroupImageKind.AVATAR, imagedGroup.avatarUri, null),
                        )
                        pointers.change(
                            groupAdministrator,
                            GroupImageChange(group.id, GroupImageKind.BANNER, imagedGroup.bannerUri, null),
                        )
                        assertNull(organizations.findGroup(group.id)?.avatarUri)
                        assertNull(organizations.findGroup(group.id)?.bannerUri)

                        val posts = organizations.listPosts()
                        assertEquals(listOf(0, 1, 2, 3), posts.map { it.order.value })
                        assertEquals(
                            "Chairman",
                            posts
                                .first()
                                .name.en.value,
                        )
                        assertEquals(
                            "Ordförande",
                            posts
                                .first()
                                .name.sv.value,
                        )
                        assertEquals(posts.first(), organizations.findPost(posts.first().id))

                        val jimMemberships =
                            database.commitTransaction(readOnly = true) {
                                organizations.membershipsForUserIn(
                                    this,
                                    UserId(UUID.fromString("bc605869-9a4d-46ec-8a29-d00819d4c195")),
                                )
                            }
                        assertEquals(4, jimMemberships.size)
                        assertEquals(
                            3,
                            database
                                .commitTransaction(readOnly = true) {
                                    organizations.membershipsForGroupIn(this, group.id)
                                }.size,
                        )
                        assertNull(
                            organizations.findGroup(
                                GroupId.parse("00000000-0000-0000-0000-000000000000"),
                            ),
                        )
                        UpdateGroup(database, organizationAccess(database)).update(
                            groupAdministrator,
                            GroupUpdate(
                                digit2026.id,
                                digit2026.version,
                                digit2026.name,
                                digit2026.prettyName,
                                digit2026.superGroup.id,
                                emptyList(),
                            ),
                        )
                        assertFalse(
                            database.commitTransaction(readOnly = true) {
                                membershipQueries.isMemberOfAnySuperGroupIn(
                                    this,
                                    digit2026Member,
                                    setOf(digit.id),
                                )
                            },
                        )
                    }
                }
            }
    }
}
