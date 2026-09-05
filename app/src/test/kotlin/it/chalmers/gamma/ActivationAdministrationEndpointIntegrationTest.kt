package it.chalmers.gamma

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.ActivationCodeAdministration
import it.chalmers.gamma.users.ActivationCodes
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.UserQueries
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ActivationAdministrationEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var database: DatabaseFactory

    @Autowired
    private lateinit var operations: ActivationCodeAdministration

    @Autowired
    private lateinit var tokens: ActivationCodes

    @Autowired
    private lateinit var users: UserQueries

    @Test
    fun `administrator forms preserve duplicate rejection activation deletion and atomic retraction`() {
        val cid = Cid("admstudent")
        val administrator = Actor.User(ActorUserId(assertNotNull(users.findUser(Cid("mscott"))).id.value))
        val admin = browser(uniqueAddress())
        assertEquals(302, admin.login().status)
        val (_, csrf) = admin.csrf("/allow-list")
        try {
            val added = admin.form("PUT", "/allow-list", mapOf("cid" to cid.value, "_csrf" to csrf))
            assertEquals(302, added.status)
            assertEquals("/allow-list", added.header("Location"))
            assertTrue(tokens.allowedCids().contains(cid))
            assertContains(admin.get("/allow-list").body, cid.value)
            assertEquals(409, admin.form("PUT", "/allow-list", mapOf("cid" to cid.value, "_csrf" to csrf)).status)
            assertEquals(409, admin.form("PUT", "/allow-list", mapOf("cid" to "mscott", "_csrf" to csrf)).status)
            assertFalse(tokens.allowedCids().contains(Cid("mscott")))

            val token = database.seedActivationForTest(cid)
            assertContains(admin.get("/activation-codes").body, cid.value)
            val deleted = admin.form("DELETE", "/activation-codes/${cid.value}", mapOf("_csrf" to csrf))
            assertEquals(302, deleted.status)
            assertEquals("/activation-codes", deleted.header("Location"))
            assertNull(tokens.findCid(token))
            assertTrue(tokens.allowedCids().contains(cid))

            val replacement = database.seedActivationForTest(cid)
            val member = browser(uniqueAddress())
            assertEquals(302, member.login("jhalpert").status)
            val (_, memberCsrf) = member.csrf("/me/edit")
            assertEquals(403, member.get("/allow-list").status)
            assertEquals(403, member.get("/activation-codes").status)
            assertEquals(
                403,
                member.form("PUT", "/allow-list", mapOf("cid" to "deniedcid", "_csrf" to memberCsrf)).status,
            )
            assertEquals(
                403,
                member.form("DELETE", "/activation-codes/${cid.value}", mapOf("_csrf" to memberCsrf)).status,
            )
            assertEquals(403, member.form("DELETE", "/allow-list/${cid.value}", mapOf("_csrf" to memberCsrf)).status)
            assertEquals(cid, tokens.findCid(replacement))
            val retracted = admin.form("DELETE", "/allow-list/${cid.value}", mapOf("_csrf" to csrf))
            assertEquals(302, retracted.status)
            assertEquals("/allow-list", retracted.header("Location"))
            assertFalse(tokens.allowedCids().contains(cid))
            assertNull(tokens.findCid(replacement))
            assertEquals(404, admin.form("DELETE", "/allow-list/${cid.value}", mapOf("_csrf" to csrf)).status)
            assertEquals(404, admin.form("DELETE", "/activation-codes/${cid.value}", mapOf("_csrf" to csrf)).status)
        } finally {
            if (tokens.allowedCids().contains(cid)) operations.retractCid(administrator, cid)
        }
    }
}
