package it.chalmers.gamma

import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.UserId
import org.springframework.beans.factory.annotation.Autowired
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class OAuthClientCredentialEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var reset: ResetOAuthClientSecret

    @Autowired
    private lateinit var deletion: DeleteOAuthClient

    @Test
    fun `token endpoint accepts the current secret and rejects replaced or deleted client credentials`() {
        val administrator = Actor.User(ActorUserId(UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f").value), true)
        val client = dataSource.createOAuthTestClient(port)
        val uid = ClientUid(client.uid)
        var deleted = false
        try {
            val browser = browser(uniqueAddress())
            val firstCode = browser.authorize(client, usePkce = true)
            val wrong = browser.exchangeCode(client.copy(clientSecret = "wrong-client-secret"), firstCode)
            assertEquals(401, wrong.status)
            assertContains(wrong.body, "invalid_client")
            assertEquals(200, browser.exchangeCode(client, firstCode).status)
            val nextCode = browser.authorize(client, usePkce = true)
            val replacement = reset.reset(administrator, uid)
            val old = browser.exchangeCode(client, nextCode)
            assertEquals(401, old.status)
            assertContains(old.body, "invalid_client")
            val updated = client.copy(clientSecret = replacement.secret.value)
            assertEquals(200, browser.exchangeCode(updated, nextCode).status)
            val finalCode = browser.authorize(updated, usePkce = true)
            deletion.delete(administrator, uid)
            deleted = true
            val removed = browser.exchangeCode(updated, finalCode)
            assertEquals(401, removed.status)
            assertContains(removed.body, "invalid_client")
        } finally {
            if (!deleted) deletion.delete(administrator, uid)
        }
    }
}
