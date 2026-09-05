package it.chalmers.gamma

import org.springframework.beans.factory.annotation.Autowired
import java.util.Base64
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class OAuthAuthorizationCodeFlowTest
    @Autowired
    constructor(
        private val dataSource: DataSource,
    ) : SpringApplicationTest() {
        @Test
        fun `authorization code flow supports optional and S256 PKCE and token lifecycle`() {
            val client = dataSource.createOAuthTestClient(port)

            listOf(false, true).forEach { usePkce ->
                val browser = browser(uniqueAddress())
                val authorization = browser.authorize(client, usePkce)
                val tokenResponse = browser.exchangeCode(client, authorization)
                assertEquals(200, tokenResponse.status, tokenResponse.body)
                val accessToken = jsonString(tokenResponse.body, "access_token")
                val idToken = jsonString(tokenResponse.body, "id_token")
                val header = jwtPart(idToken, 0)
                val claims = jwtPart(idToken, 1)
                assertContains(header, "kid")
                assertContains(claims, "nonce-")
                assertContains(claims, "88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")

                val jwks = browser.get("/oauth2/jwks")
                assertEquals(200, jwks.status)
                assertEquals(jsonString(header, "kid"), jsonString(jwks.body, "kid"))
                val modulus = Base64.getUrlDecoder().decode(jsonString(jwks.body, "n"))
                assertTrue(modulus.size == 256 || modulus.size == 257)

                val userInfo = browser.get("/oauth2/userinfo", mapOf("Authorization" to "Bearer $accessToken"))
                assertEquals(200, userInfo.status)
                assertContains(userInfo.body, "mscott")
                assertContains(userInfo.body, "mscott@example.org")

                if (usePkce) {
                    val bareLogout = browser.get("/connect/logout")
                    assertEquals(400, bareLogout.status)
                    assertEquals(200, browser.get("/me").status)

                    val logout = browser.get("/connect/logout?id_token_hint=$idToken")
                    assertEquals(302, logout.status, logout.body)
                    assertTrue(logout.header("Location").orEmpty().endsWith("/"))
                    assertContains(browser.get("/me").header("Location").orEmpty(), "/login")
                }

                val introspection =
                    browser.formMulti(
                        "POST",
                        "/oauth2/introspect",
                        mapOf("token" to listOf(accessToken)),
                        mapOf("Authorization" to client.basicAuthorization()),
                    )
                assertEquals(200, introspection.status)
                assertContains(introspection.body, "\"active\":true")

                val revocation =
                    browser.formMulti(
                        "POST",
                        "/oauth2/revoke",
                        mapOf("token" to listOf(accessToken)),
                        mapOf("Authorization" to client.basicAuthorization()),
                    )
                assertEquals(200, revocation.status)
                val inactive =
                    browser.formMulti(
                        "POST",
                        "/oauth2/introspect",
                        mapOf("token" to listOf(accessToken)),
                        mapOf("Authorization" to client.basicAuthorization()),
                    )
                assertContains(inactive.body, "\"active\":false")
            }
        }

        @Test
        fun `S256 code rejects a missing or incorrect verifier`() {
            val client = dataSource.createOAuthTestClient(port)

            val missingBrowser = browser(uniqueAddress())
            val missing = missingBrowser.authorize(client, usePkce = true)
            val missingResponse = missingBrowser.exchangeCode(client, missing, verifier = null)
            assertEquals(400, missingResponse.status)
            assertContains(missingResponse.body, "invalid_grant")

            val wrongBrowser = browser(uniqueAddress())
            val wrong = wrongBrowser.authorize(client, usePkce = true)
            val wrongResponse =
                wrongBrowser.exchangeCode(
                    client,
                    wrong,
                    verifier = "wrong-verifier-abcdefghijklmnopqrstuvwxyz123456",
                )
            assertEquals(400, wrongResponse.status)
            assertContains(wrongResponse.body, "invalid_grant")
            assertNotEquals(missing.code, wrong.code)
        }
    }
