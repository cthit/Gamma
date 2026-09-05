package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiCredentialAuthenticator
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.RawApiToken
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.oauth.RawClientSecret
import it.chalmers.gamma.oauth.Scope
import it.chalmers.gamma.oauth.clientSecretMatches
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CreateOAuthClientEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var database: DatabaseFactory

    @Autowired
    private lateinit var deletion: DeleteOAuthClient

    @Autowired
    private lateinit var apiAuthentication: ApiCredentialAuthenticator

    @Test
    fun `official and personal forms create usable credentials and preserve ownership scope and restrictions`() {
        val createdIds = mutableListOf<ClientUid>()
        val administratorId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
        val administrator = Actor.User(ActorUserId(administratorId.value), true)
        val ownerId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
        val restriction = UUID.fromString("712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab")
        try {
            for (personal in listOf(false, true)) {
                val browser = browser(uniqueAddress())
                assertEquals(302, browser.login(if (personal) "jhalpert" else "mscott").status)
                val (_, csrf) = browser.csrf(if (personal) "/my-clients/create" else "/clients/create")
                val fields =
                    mapOf(
                        "_csrf" to csrf,
                        "prettyName" to "HTTP client creation",
                        "svDescription" to "Svenska",
                        "enDescription" to "English",
                        "redirectUrl" to "https://example.org/callback",
                        "emailScope" to "true",
                        "generateApiKey" to "true",
                        "restrictions" to restriction.toString(),
                    )
                val response = browser.form("POST", if (personal) "/my-clients" else "/clients/create", fields)
                assertEquals(200, response.status)
                val uid =
                    ClientUid.parse(
                        assertNotNull(Regex("/clients/([a-f0-9-]{36})").find(response.body)).groupValues[1],
                    )
                createdIds.add(uid)
                val client = assertNotNull(OAuthProtocolClients(database).serverClient(uid)?.client)
                assertEquals(if (personal) ClientOwner.User(ownerId) else ClientOwner.Official, client.owner)
                assertEquals(if (personal) emptySet() else setOf(restriction), client.restrictedSuperGroupIds)
                assertEquals(setOf(Scope.OPENID, Scope.PROFILE, Scope.EMAIL), client.scopes)
                val secret = assertNotNull(Regex("<code>([A-Za-z0-9_-]+)</code>").find(response.body)).groupValues[1]
                assertTrue(clientSecretMatches(database, client.clientId, RawClientSecret(secret)))
                val api =
                    assertNotNull(Regex("Authorization: pre-shared ([a-f0-9-]+):([A-Za-z0-9_-]+)").find(response.body))
                assertNotNull(
                    apiAuthentication.authenticate(ApiKeyId.parse(api.groupValues[1]), RawApiToken(api.groupValues[2])),
                )
                val details = browser.get("/clients/${uid.value}")
                assertEquals(200, details.status)
                assertFalse(details.body.contains(secret))
                assertFalse(details.body.contains(api.groupValues[2]))
                if (personal) assertEquals(403, browser.form("POST", "/clients/create", fields).status)
            }
        } finally {
            for (id in createdIds) deletion.delete(administrator, id)
        }
    }
}
