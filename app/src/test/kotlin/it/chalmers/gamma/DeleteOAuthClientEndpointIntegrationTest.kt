package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiCredentialAuthenticator
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.RawApiToken
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DeleteOAuthClientEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var creation: CreateOAuthClient

    @Autowired
    private lateinit var deletion: DeleteOAuthClient

    @Autowired
    private lateinit var database: DatabaseFactory

    @Autowired
    private lateinit var authentication: ApiCredentialAuthenticator

    @Test
    fun `deletion checks ownership revokes credentials redirects by owner and returns missing after deletion`() {
        val administrator = Actor.User(ActorUserId(UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f").value), true)
        val ownerId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
        val member = browser(uniqueAddress())
        val admin = browser(uniqueAddress())
        assertEquals(302, member.login("jhalpert").status)
        assertEquals(302, admin.login("mscott").status)
        val (_, memberCsrf) = member.csrf("/my-clients")
        val (_, adminCsrf) = admin.csrf("/clients")
        val pending = mutableListOf<ClientUid>()
        try {
            for (owner in listOf(ClientOwner.Official, ClientOwner.User(ownerId))) {
                val actor = if (owner is ClientOwner.Official) administrator else Actor.User(ActorUserId(ownerId.value))
                val created =
                    creation.create(
                        actor,
                        NewOAuthClient(
                            RedirectUri("https://example.org/callback"),
                            ClientName("HTTP deletion"),
                            LocalizedText.of(),
                            false,
                            owner,
                            generateApiKey = true,
                        ),
                    )
                pending.add(created.client.uid)
                val path = "/clients/${created.client.uid.value}"
                if (owner is ClientOwner.Official) {
                    assertEquals(403, member.form("DELETE", path, mapOf("_csrf" to memberCsrf)).status)
                    assertNotNull(OAuthProtocolClients(database).serverClient(created.client.uid)?.client)
                }
                val requester = if (owner is ClientOwner.Official) admin else member
                val csrf = if (owner is ClientOwner.Official) adminCsrf else memberCsrf
                val response = requester.form("DELETE", path, mapOf("_csrf" to csrf))
                assertEquals(302, response.status)
                assertEquals(
                    if (owner is ClientOwner.Official) "/clients" else "/my-clients",
                    response.header("Location"),
                )
                pending.remove(created.client.uid)
                assertNull(OAuthProtocolClients(database).serverClient(created.client.uid)?.client)
                val key = assertNotNull(created.apiCredential)
                assertNull(authentication.authenticate(ApiKeyId(key.id.value), RawApiToken(key.token.value)))
                assertEquals(404, requester.form("DELETE", path, mapOf("_csrf" to csrf)).status)
            }
        } finally {
            for (id in pending) {
                if (OAuthProtocolClients(database).serverClient(id)?.client !=
                    null
                ) {
                    deletion.delete(administrator, id)
                }
            }
        }
    }
}
