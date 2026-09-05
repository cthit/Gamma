package it.chalmers.gamma

import it.chalmers.gamma.organization.CreateGroup
import it.chalmers.gamma.organization.DeleteGroup
import it.chalmers.gamma.organization.GroupId
import it.chalmers.gamma.organization.Membership
import it.chalmers.gamma.organization.NewGroup
import it.chalmers.gamma.organization.NewGroupMembership
import it.chalmers.gamma.organization.OrganizationName
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.PostId
import it.chalmers.gamma.organization.PrettyName
import it.chalmers.gamma.organization.SuperGroupId
import it.chalmers.gamma.organization.UnofficialPostName
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GroupOperationEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var database: DatabaseFactory

    @Autowired
    private lateinit var organizations: OrganizationQueries

    @Autowired
    private lateinit var groupDeletion: DeleteGroup

    @Autowired
    private lateinit var groupCreation: CreateGroup

    @Test
    fun `group forms create edit and reject stale replacement through the real operation`() {
        val browser = browser(uniqueAddress())
        assertEquals(302, browser.login().status)
        val (_, csrf) = browser.csrf("/groups/create")
        val response =
            browser.form(
                "POST",
                "/groups/create",
                mapOf(
                    "name" to "endpoint-group",
                    "prettyName" to "Endpoint group",
                    "superGroupId" to SUPER_GROUP_ID,
                    "_csrf" to csrf,
                ),
            )
        assertEquals(302, response.status)
        val location = assertNotNull(response.header("Location"))
        val groupId = GroupId.parse(location.substringAfterLast('/'))
        try {
            val created = assertNotNull(organizations.findGroup(groupId))
            assertEquals("endpoint-group", created.name.value)
            assertEquals(
                emptyList(),
                database.commitTransaction(readOnly = true) {
                    organizations.membershipsForGroupIn(this, groupId)
                },
            )

            val (_, editCsrf) = browser.csrf("/groups/${groupId.value}/edit")
            val fields =
                mapOf(
                    "name" to "endpoint-edited",
                    "prettyName" to "Endpoint edited",
                    "superGroupId" to SUPER_GROUP_ID,
                    "version" to created.version.toString(),
                    "userId" to MEMBER_ID,
                    "postId" to POST_ID,
                    "unofficialPostName" to "Endpoint member",
                    "_csrf" to editCsrf,
                )
            val edited = browser.form("PUT", "/groups/${groupId.value}", fields)
            assertEquals(302, edited.status)
            assertEquals(location, edited.header("Location"))
            val saved = assertNotNull(organizations.findGroup(groupId))
            assertEquals("endpoint-edited", saved.name.value)
            assertEquals(created.version + 1, saved.version)
            val memberships =
                database.commitTransaction(
                    readOnly = true,
                ) { organizations.membershipsForGroupIn(this, groupId) }
            assertEquals(
                listOf(
                    Membership(
                        UserId.parse(MEMBER_ID),
                        groupId,
                        PostId.parse(POST_ID),
                        UnofficialPostName("Endpoint member"),
                    ),
                ),
                memberships,
            )

            val duplicate =
                browser.formMulti(
                    "PUT",
                    "/groups/${groupId.value}",
                    fields.mapValues { listOf(it.value) } +
                        mapOf(
                            "version" to listOf(saved.version.toString()),
                            "userId" to listOf(MEMBER_ID, MEMBER_ID),
                            "postId" to listOf(POST_ID, POST_ID),
                            "unofficialPostName" to listOf("First name", "Second name"),
                        ),
                )
            assertEquals(409, duplicate.status)
            assertEquals(saved, organizations.findGroup(groupId))
            assertEquals(
                memberships,
                database.commitTransaction(readOnly = true) {
                    organizations.membershipsForGroupIn(this, groupId)
                },
            )

            val stale = browser.form("PUT", "/groups/${groupId.value}", fields - "userId" - "postId")
            assertEquals(409, stale.status)
            assertEquals(saved, organizations.findGroup(groupId))
            assertEquals(
                memberships,
                database.commitTransaction(readOnly = true) {
                    organizations.membershipsForGroupIn(this, groupId)
                },
            )
        } finally {
            groupDeletion.delete(
                administrator,
                groupId,
            )
        }
    }

    @Test
    fun `personal post form is atomic and administrator deletion cascades memberships`() {
        val posts = organizations.listPosts().take(2)
        val groupId =
            groupCreation.create(
                administrator,
                NewGroup(
                    OrganizationName("endpoint-posts"),
                    PrettyName("Endpoint posts"),
                    SuperGroupId.parse(SUPER_GROUP_ID),
                ),
                posts.map { NewGroupMembership(UserId.parse(MEMBER_ID), it.id, UnofficialPostName("Original")) },
            )
        try {
            val member = browser(uniqueAddress())
            assertEquals(302, member.login("jhalpert").status)
            val (_, csrf) = member.csrf("/")
            val path = "/groups/${groupId.value}/my-posts"
            val saved =
                member.formMulti(
                    "PUT",
                    path,
                    mapOf(
                        "postId" to posts.map { it.id.value.toString() },
                        "unofficialPostName" to listOf("Personal name", ""),
                        "_csrf" to listOf(csrf),
                    ),
                )
            assertEquals(302, saved.status)
            assertEquals("/groups/${groupId.value}", saved.header("Location"))
            val committed =
                database.commitTransaction(
                    readOnly = true,
                ) { organizations.membershipsForGroupIn(this, groupId) }
            assertEquals("Personal name", committed.single { it.postId == posts[0].id }.unofficialPostName.value)
            assertNull(committed.single { it.postId == posts[1].id }.unofficialPostName.value)

            val rejected =
                member.form(
                    "PUT",
                    path,
                    linkedMapOf(
                        "postNames[${posts[0].id.value}]" to "Must roll back",
                        "postNames[${PostId.generate().value}]" to "Denied",
                        "_csrf" to csrf,
                    ),
                )
            assertEquals(403, rejected.status)
            assertEquals(
                committed,
                database.commitTransaction(readOnly = true) {
                    organizations.membershipsForGroupIn(this, groupId)
                },
            )

            val admin = browser(uniqueAddress())
            assertEquals(302, admin.login().status)
            val (_, adminCsrf) = admin.csrf("/groups")
            val deleted = admin.form("DELETE", "/groups/${groupId.value}", mapOf("_csrf" to adminCsrf))
            assertEquals(302, deleted.status)
            assertEquals("/groups", deleted.header("Location"))
            assertNull(organizations.findGroup(groupId))
            assertEquals(
                emptyList(),
                database.commitTransaction(readOnly = true) {
                    organizations.membershipsForGroupIn(this, groupId)
                },
            )
        } finally {
            if (organizations.findGroup(groupId) != null) groupDeletion.delete(administrator, groupId)
        }
    }

    private companion object {
        val administrator = Actor.User(ActorUserId(UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f").value), true)
        const val SUPER_GROUP_ID = "aed27030-ad90-4526-855c-1e909b1dcecb"
        const val MEMBER_ID = "bc605869-9a4d-46ec-8a29-d00819d4c195"
        const val POST_ID = "7bb1db15-730d-4864-bfc3-99abe7c0ccf8"
    }
}
