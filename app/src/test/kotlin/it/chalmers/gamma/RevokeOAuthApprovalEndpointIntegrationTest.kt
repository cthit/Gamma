package it.chalmers.gamma

import it.chalmers.gamma.oauth.ClientApprovals
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.UserId
import org.springframework.beans.factory.annotation.Autowired
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RevokeOAuthApprovalEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var approvals: ClientApprovals

    @Autowired
    private lateinit var deletion: DeleteOAuthClient

    @Test
    fun `an account can repeatedly revoke its approval without changing another account approval`() {
        val adminId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
        val memberId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
        val client = dataSource.createOAuthTestClient(port)
        val uid = ClientUid(client.uid)
        try {
            val administrator = browser(uniqueAddress())
            assertEquals(302, administrator.login("mscott").status)
            administrator.authorize(client, usePkce = false)
            val member = browser(uniqueAddress())
            assertEquals(302, member.login("jhalpert").status)
            member.authorize(client, usePkce = false)
            assertNotNull(approvals.approvedScopes(adminId, uid))
            assertNotNull(approvals.approvedScopes(memberId, uid))
            val (_, csrf) = member.csrf("/me/accepted-clients")
            repeat(2) {
                val response = member.form("DELETE", "/me/accepted-clients/${uid.value}", mapOf("_csrf" to csrf))
                assertEquals(302, response.status)
                assertEquals("/me/accepted-clients", response.header("Location"))
            }
            assertNull(approvals.approvedScopes(memberId, uid))
            assertNotNull(approvals.approvedScopes(adminId, uid))
        } finally {
            deletion.delete(Actor.User(ActorUserId(adminId.value), true), uid)
        }
    }
}
