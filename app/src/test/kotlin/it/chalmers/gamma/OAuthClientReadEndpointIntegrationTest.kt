package it.chalmers.gamma

import it.chalmers.gamma.oauth.AuthorityName
import it.chalmers.gamma.oauth.ClientApprovals
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OAuthClientReadEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired private lateinit var creation: CreateOAuthClient

    @Autowired private lateinit var deletion: DeleteOAuthClient

    @Autowired private lateinit var authorities: CreateOAuthClientAuthority

    @Autowired private lateinit var approvals: ClientApprovals

    @Autowired private lateinit var database: DatabaseFactory

    @Test
    fun `client pages preserve lists ownership authorities approval and missing client responses`() {
        val adminId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
        val ownerId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
        val administrator = Actor.User(ActorUserId(adminId.value), true)
        val owner = Actor.User(ActorUserId(ownerId.value))
        val pending = mutableListOf<ClientUid>()
        val adminBrowser = browser(uniqueAddress())
        val member = browser(uniqueAddress())
        assertEquals(302, adminBrowser.login("mscott").status)
        assertEquals(302, member.login("jhalpert").status)
        try {
            for (personal in listOf(false, true)) {
                val actor = if (personal) owner else administrator
                val name = if (personal) "Personal read endpoint" else "Official read endpoint"
                val created =
                    creation.create(
                        actor,
                        NewOAuthClient(
                            RedirectUri("https://example.org/callback"),
                            ClientName(name),
                            LocalizedText.of(),
                            false,
                            if (personal) ClientOwner.User(ownerId) else ClientOwner.Official,
                        ),
                    )
                pending.add(created.client.uid)
                authorities.create(actor, created.client.uid, AuthorityName("manage"), setOf(ownerId), emptySet())
                database.commitTransaction {
                    approvals.approveIn(
                        this,
                        ownerId,
                        created.client.uid,
                        created.client.scopes,
                    )
                }
                val path = "/clients/${created.client.uid.value}"
                val details = adminBrowser.get(path)
                assertEquals(200, details.status)
                assertTrue(details.body.contains(name))
                assertTrue(details.body.contains("manage"))
                assertFalse(details.body.contains(created.secret.value))
                assertEquals(200, adminBrowser.get("$path/authorities").status)
                assertEquals(if (personal) 200 else 403, member.get(path).status)
                assertEquals(403, member.get("$path/authorities").status)
                val official = adminBrowser.get("/clients")
                val mine = member.get("/my-clients")
                val owned = adminBrowser.get("/user-clients")
                val approved = member.get("/me/accepted-clients")
                for (response in listOf(official, mine, owned, approved)) assertEquals(200, response.status)
                assertEquals(!personal, official.body.contains(name))
                assertEquals(personal, mine.body.contains(name))
                assertEquals(personal, owned.body.contains(name))
                if (personal) assertTrue(owned.body.contains("Jim"))
                assertTrue(approved.body.contains(name))
                assertEquals(403, member.get("/clients").status)
                assertEquals(403, member.get("/user-clients").status)
                deletion.delete(administrator, created.client.uid)
                pending.remove(created.client.uid)
                assertEquals(404, adminBrowser.get(path).status)
            }
        } finally {
            for (uid in pending) deletion.delete(administrator, uid)
        }
    }
}
