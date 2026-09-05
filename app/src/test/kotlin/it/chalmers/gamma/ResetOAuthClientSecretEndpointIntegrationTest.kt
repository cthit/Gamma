package it.chalmers.gamma

import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.RawClientSecret
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.oauth.clientSecretMatches
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResetOAuthClientSecretEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var creation: CreateOAuthClient

    @Autowired
    private lateinit var deletion: DeleteOAuthClient

    @Autowired
    private lateinit var database: DatabaseFactory

    @Test
    fun `reset forms enforce ownership show a usable secret once and reject missing clients`() {
        val adminId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
        val ownerId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
        val administrator = Actor.User(ActorUserId(adminId.value), true)
        val pending = mutableListOf<ClientUid>()
        try {
            for (personal in listOf(false, true)) {
                val actor = if (personal) Actor.User(ActorUserId(ownerId.value)) else administrator
                val created =
                    creation.create(
                        actor,
                        NewOAuthClient(
                            RedirectUri("https://example.org/callback"),
                            ClientName("HTTP secret reset"),
                            LocalizedText.of(),
                            false,
                            if (personal) ClientOwner.User(ownerId) else ClientOwner.Official,
                        ),
                    )
                pending.add(created.client.uid)
                val browser = browser(uniqueAddress())
                assertEquals(302, browser.login(if (personal) "jhalpert" else "mscott").status)
                val path = "/clients/${created.client.uid.value}"
                val (_, csrf) = browser.csrf(path)
                val response = browser.form("POST", "$path/reset", mapOf("_csrf" to csrf))
                assertEquals(200, response.status)
                val secret = assertNotNull(Regex("<code>([A-Za-z0-9_-]+)</code>").find(response.body)).groupValues[1]
                assertTrue(clientSecretMatches(database, created.client.clientId, RawClientSecret(secret)))
                assertFalse(clientSecretMatches(database, created.client.clientId, created.secret))
                assertFalse(browser.get(path).body.contains(secret))
                if (!personal) {
                    val member = browser(uniqueAddress())
                    assertEquals(302, member.login("jhalpert").status)
                    val (_, memberCsrf) = member.csrf("/my-clients")
                    assertEquals(403, member.form("POST", "$path/reset", mapOf("_csrf" to memberCsrf)).status)
                    assertTrue(clientSecretMatches(database, created.client.clientId, RawClientSecret(secret)))
                }
                deletion.delete(administrator, created.client.uid)
                pending.remove(created.client.uid)
                assertEquals(404, browser.form("POST", "$path/reset", mapOf("_csrf" to csrf)).status)
            }
        } finally {
            for (id in pending) deletion.delete(administrator, id)
        }
    }
}
