package it.chalmers.gamma

import it.chalmers.gamma.organization.DeletePost
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.PostId
import it.chalmers.gamma.organization.ReorderPosts
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.UserId
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PostEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var organizations: OrganizationQueries

    @Autowired
    private lateinit var deletion: DeletePost

    @Autowired
    private lateinit var ordering: ReorderPosts

    @Test
    fun `post forms preserve translations order version checks and redirects`() {
        val originalOrder = organizations.listPosts().map { it.id }
        val actor = Actor.User(ActorUserId(UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f").value), true)
        val browser = browser(uniqueAddress())
        assertEquals(302, browser.login().status)
        val (_, csrf) = browser.csrf("/posts/create")
        var postId: PostId? = null
        try {
            val fields =
                mapOf(
                    "svName" to "Tillfällig",
                    "enName" to "Temporary",
                    "emailPrefix" to "endpoint",
                    "_csrf" to csrf,
                )
            val created = browser.form("POST", "/posts", fields)
            assertEquals(302, created.status)
            val location = assertNotNull(created.header("Location"))
            val id = PostId.parse(location.substringAfterLast('/'))
            postId = id
            val original = assertNotNull(organizations.findPost(id))
            assertEquals("Tillfällig", original.name.sv.value)
            assertEquals("Temporary", original.name.en.value)
            assertEquals(200, browser.get(location).status)

            val reorderedIds = (originalOrder + id).reversed()
            val reordered =
                browser.formMulti(
                    "PUT",
                    "/posts/order",
                    mapOf("list" to reorderedIds.map { it.value.toString() }, "_csrf" to listOf(csrf)),
                )
            assertEquals(302, reordered.status)
            assertEquals("/posts", reordered.header("Location"))
            val edit = fields + mapOf("version" to original.version.toString(), "enName" to "Edited post")
            val updated = browser.form("PUT", "/posts/${id.value}", edit)
            assertEquals(302, updated.status)
            assertEquals(location, updated.header("Location"))
            val committed = assertNotNull(organizations.findPost(id))
            assertEquals(original.version + 1, committed.version)
            assertEquals("Edited post", committed.name.en.value)
            assertEquals(reorderedIds, organizations.listPosts().map { it.id })
            assertEquals(409, browser.form("PUT", "/posts/${id.value}", edit).status)
            assertEquals(committed, organizations.findPost(id))

            val partialOrder =
                browser.formMulti(
                    "PUT",
                    "/posts/order",
                    mapOf("list" to listOf(id.value.toString()), "_csrf" to listOf(csrf)),
                )
            assertEquals(400, partialOrder.status)
            assertEquals(reorderedIds, organizations.listPosts().map { it.id })
            val deleted = browser.form("DELETE", "/posts/${id.value}", mapOf("_csrf" to csrf))
            assertEquals(302, deleted.status)
            assertEquals("/posts", deleted.header("Location"))
            assertNull(organizations.findPost(id))
        } finally {
            postId?.let { if (organizations.findPost(it) != null) deletion.delete(actor, it) }
            ordering.reorder(actor, originalOrder)
        }
    }
}
