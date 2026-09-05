package it.chalmers.gamma

import it.chalmers.gamma.organization.CreateGroup
import it.chalmers.gamma.organization.DeleteGroup
import it.chalmers.gamma.organization.GroupId
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
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class GroupReadEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var database: DatabaseFactory

    @Autowired
    private lateinit var creation: CreateGroup

    @Autowired
    private lateinit var deletion: DeleteGroup

    @Autowired
    private lateinit var organizations: OrganizationQueries

    @Test
    fun `group details show an existing member beyond the first directory page`() =
        withLastPageMember { groupId, _ ->
            val admin = browser(uniqueAddress())
            assertEquals(302, admin.login().status)
            for (path in listOf("/groups/${groupId.value}", "/groups/${groupId.value}/cancel-edit")) {
                val response = admin.get(path)
                assertEquals(200, response.status)
                assertContains(response.body, "Beyond directory page")
            }
        }

    @Test
    fun `group editor retains and saves an existing member beyond the first directory page`() =
        withLastPageMember { groupId, memberId ->
            val admin = browser(uniqueAddress())
            assertEquals(302, admin.login().status)
            val newMember = admin.get("/groups/new-member")
            assertEquals(200, newMember.status)
            val candidates =
                assertNotNull(
                    Regex("""<select\b[^>]*name="userId"[^>]*>(.*?)</select>""", RegexOption.DOT_MATCHES_ALL)
                        .find(newMember.body),
                ).groupValues[1]
            assertEquals(200, Regex("""<option\b""").findAll(candidates).count())
            assertFalse(candidates.contains(memberId.value.toString()))
            val (page, csrf) = admin.csrf("/groups/${groupId.value}/edit")
            val option = assertNotNull(Regex("""<option\b[^>]*value="${memberId.value}"[^>]*>""").find(page.body))
            assertContains(option.value, "selected")
            val group = assertNotNull(organizations.findGroup(groupId))
            val saved =
                admin.form(
                    "PUT",
                    "/groups/${groupId.value}",
                    mapOf(
                        "name" to group.name.value,
                        "prettyName" to group.prettyName.value,
                        "superGroupId" to
                            group.superGroup.id.value
                                .toString(),
                        "version" to group.version.toString(),
                        "userId" to memberId.value.toString(),
                        "postId" to POST_ID,
                        "unofficialPostName" to "Still the same member",
                        "_csrf" to csrf,
                    ),
                )
            assertEquals(302, saved.status)
            assertEquals(
                memberId,
                database
                    .commitTransaction(readOnly = true) {
                        organizations.membershipsForGroupIn(this, groupId)
                    }.single()
                    .userId,
            )
        }

    private fun withLastPageMember(test: (GroupId, UserId) -> Unit) {
        val memberId = UserId.generate()
        val ids = List(200) { UserId.generate() } + memberId
        val rows =
            ids.mapIndexed { index, id ->
                val cid = if (id == memberId) "zzreadmember" else "aread${'a' + index / 26}${'a' + index % 26}"
                val nick = if (id == memberId) "Beyond directory page" else "Candidate $index"
                "('${id.value}', '$cid', '$nick', 'Read', 'Fixture', '$cid@example.org', 2020, 0, NOW(), NOW())"
            }
        database.executeSqlScript(
            "INSERT INTO g_user (user_id, cid, nick, first_name, last_name, email, acceptance_year, " +
                "version, created_at, updated_at) VALUES ${rows.joinToString(",")}",
        )
        var groupId: GroupId? = null
        try {
            val created =
                creation.create(
                    administrator,
                    NewGroup(
                        OrganizationName("read-members"),
                        PrettyName("Read members"),
                        SuperGroupId.parse(SUPER_GROUP_ID),
                    ),
                    listOf(NewGroupMembership(memberId, PostId.parse(POST_ID), UnofficialPostName(null))),
                )
            groupId = created
            test(created, memberId)
        } finally {
            groupId?.let { deletion.delete(administrator, it) }
            database.executeSqlScript(
                "DELETE FROM g_user WHERE user_id IN (${ids.joinToString(",") { "'${it.value}'" }})",
            )
        }
    }

    private companion object {
        val administrator = Actor.User(ActorUserId(UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f").value), true)
        const val SUPER_GROUP_ID = "aed27030-ad90-4526-855c-1e909b1dcecb"
        const val POST_ID = "7bb1db15-730d-4864-bfc3-99abe7c0ccf8"
    }
}
