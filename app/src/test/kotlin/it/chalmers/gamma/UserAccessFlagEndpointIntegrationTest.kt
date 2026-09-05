package it.chalmers.gamma

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.UserAccessFlag
import it.chalmers.gamma.users.UserAccessFlagKind
import it.chalmers.gamma.users.UserAccessFlags
import it.chalmers.gamma.users.UserAuthentication
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserQueries
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserAccessFlagEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired private lateinit var authentication: UserAuthentication

    @Autowired
    private lateinit var flags: UserAccessFlags

    @Autowired
    private lateinit var users: UserQueries

    @Test
    fun `access forms rotate responsibility refresh sessions and preserve the final administrator`() {
        val administrator = assertNotNull(users.findUser(Cid("mscott"))).id
        val member = assertNotNull(users.findUser(Cid("jhalpert"))).id
        val originalAdmins = enabled(administrator, UserAccessFlagKind.ADMINISTRATOR)
        val originalGdpr = enabled(administrator, UserAccessFlagKind.GDPR_TRAINED)
        try {
            val adminBrowser = browser(uniqueAddress())
            val memberBrowser = browser(uniqueAddress())
            assertEquals(302, adminBrowser.login().status)
            assertEquals(302, memberBrowser.login("jhalpert").status)
            assertEquals(403, memberBrowser.get("/admins").status)
            val (_, csrf) = adminBrowser.csrf("/admins")

            val gdpr = adminBrowser.form("PUT", "/gdpr", mapOf("userId" to member.value.toString(), "_csrf" to csrf))
            assertEquals(302, gdpr.status)
            assertEquals("/gdpr", gdpr.header("Location"))
            assertEquals(setOf(member), enabled(administrator, UserAccessFlagKind.GDPR_TRAINED))

            val promote =
                adminBrowser.form(
                    "PUT",
                    "/admins",
                    mapOf("userId" to member.value.toString(), "_csrf" to csrf),
                )
            assertEquals(302, promote.status)
            assertEquals("/admins", promote.header("Location"))
            assertEquals(setOf(member), enabled(member, UserAccessFlagKind.ADMINISTRATOR))
            assertEquals(403, adminBrowser.get("/admins").status)
            val (page, memberCsrf) = memberBrowser.csrf("/admins")
            assertEquals(200, page.status)
            assertEquals(409, memberBrowser.form("PUT", "/admins", mapOf("_csrf" to memberCsrf)).status)
            assertEquals(setOf(member), enabled(member, UserAccessFlagKind.ADMINISTRATOR))
            assertEquals(403, adminBrowser.form("PUT", "/gdpr", mapOf("_csrf" to csrf)).status)

            assertEquals(302, memberBrowser.form("PUT", "/gdpr", mapOf("_csrf" to memberCsrf)).status)
            assertEquals(emptySet(), enabled(member, UserAccessFlagKind.GDPR_TRAINED))
            val restore =
                memberBrowser.form(
                    "PUT",
                    "/admins",
                    mapOf("userId" to administrator.value.toString(), "_csrf" to memberCsrf),
                )
            assertEquals(302, restore.status)
            assertEquals(200, adminBrowser.get("/admins").status)
            assertEquals(403, memberBrowser.get("/admins").status)
        } finally {
            val currentAdministrator =
                listOf(administrator, member).first {
                    authentication.sessionAccess(it)?.administrator ==
                        true
                }
            flags.replace(currentAdministrator.actor(), UserAccessFlagKind.ADMINISTRATOR, originalAdmins)
            flags.replace(administrator.actor(), UserAccessFlagKind.GDPR_TRAINED, originalGdpr)
        }
    }

    private fun enabled(
        administrator: UserId,
        kind: UserAccessFlagKind,
    ): Set<UserId> =
        flags
            .list(
                administrator.actor(),
                kind,
            ).filter(UserAccessFlag::enabled)
            .mapTo(mutableSetOf(), UserAccessFlag::userId)

    private fun UserId.actor() = Actor.User(ActorUserId(value), true)
}
