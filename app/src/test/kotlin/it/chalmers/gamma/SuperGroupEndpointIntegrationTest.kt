package it.chalmers.gamma

import it.chalmers.gamma.organization.DeleteSuperGroup
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.SuperGroupId
import it.chalmers.gamma.organization.SuperGroupType
import it.chalmers.gamma.organization.SuperGroupTypes
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.UserId
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SuperGroupEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var organizations: OrganizationQueries

    @Autowired
    private lateinit var deletion: DeleteSuperGroup

    @Autowired
    private lateinit var types: SuperGroupTypes

    @Test
    fun `type and super group forms preserve translations versions and deletion restrictions`() {
        val browser = browser(uniqueAddress())
        assertEquals(302, browser.login().status)
        val (_, csrf) = browser.csrf("/types")
        val type = SuperGroupType("endpointtype")
        val actor = Actor.User(ActorUserId(UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f").value), true)
        var groupId: SuperGroupId? = null
        try {
            assertEquals(302, browser.form("POST", "/types", mapOf("type" to type.value, "_csrf" to csrf)).status)
            val fields =
                mapOf(
                    "name" to "endpoint-super-group",
                    "prettyName" to "Endpoint super group",
                    "type" to type.value,
                    "svDescription" to "Beskrivning",
                    "enDescription" to "Description",
                    "_csrf" to csrf,
                )
            val created = browser.form("POST", "/super-groups", fields)
            assertEquals(302, created.status)
            val location = assertNotNull(created.header("Location"))
            val id = SuperGroupId.parse(location.substringAfterLast('/'))
            groupId = id
            val original = assertNotNull(organizations.superGroupDetails(id)?.superGroup)
            assertEquals("Beskrivning", original.description.sv.value)
            assertEquals("Description", original.description.en.value)
            val details = browser.get(location)
            assertEquals(200, details.status)
            assertContains(details.body, original.prettyName.value)
            val editor = browser.get("$location/edit")
            assertEquals(200, editor.status)
            assertContains(editor.body, type.value)
            assertContains(editor.body, original.prettyName.value)
            val member = browser(uniqueAddress())
            assertEquals(302, member.login("jhalpert").status)
            assertEquals(200, member.get(location).status)
            assertEquals(403, member.get("$location/edit").status)

            val edit = fields + mapOf("version" to original.version.toString(), "enDescription" to "Edited description")
            val updated = browser.form("PUT", "/super-groups/${id.value}", edit)
            assertEquals(302, updated.status)
            assertEquals(location, updated.header("Location"))
            val committed = assertNotNull(organizations.superGroupDetails(id)?.superGroup)
            assertEquals(original.version + 1, committed.version)
            assertEquals("Beskrivning", committed.description.sv.value)
            assertEquals("Edited description", committed.description.en.value)
            assertEquals(409, browser.form("PUT", "/super-groups/${id.value}", edit).status)
            assertEquals(committed, organizations.superGroupDetails(id)?.superGroup)

            assertEquals(409, browser.form("DELETE", "/types/${type.value}", mapOf("_csrf" to csrf)).status)
            val deleted = browser.form("DELETE", "/super-groups/${id.value}", mapOf("_csrf" to csrf))
            assertEquals(302, deleted.status)
            assertEquals("/super-groups", deleted.header("Location"))
            assertNull(organizations.superGroupDetails(id)?.superGroup)
            assertEquals(404, browser.get(location).status)
            assertEquals(404, browser.get("$location/edit").status)
            assertEquals(302, browser.form("DELETE", "/types/${type.value}", mapOf("_csrf" to csrf)).status)
            assertFalse(type in organizations.listSuperGroupTypes())
        } finally {
            groupId?.let { if (organizations.superGroupDetails(it)?.superGroup != null) deletion.delete(actor, it) }
            if (type in organizations.listSuperGroupTypes()) types.delete(actor, type)
        }
    }
}
