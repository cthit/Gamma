package it.chalmers.gamma

import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ClientRestrictionsTest
    @Autowired
    constructor(
        private val dataSource: DataSource,
    ) : SpringApplicationTest() {
        @Test
        fun `user outside a restricted super group cannot authorize the client`() {
            val restrictedClient =
                dataSource.createOAuthTestClient(
                    port,
                    restrictedSuperGroupId = UUID.fromString("712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab"),
                )
            val browser = browser(uniqueAddress())
            assertEquals(302, browser.login().status)

            val response = browser.get(restrictedClient.authorizationPath())

            assertEquals(302, response.status)
            assertEquals("access_denied", response.redirectParameters()["error"])
        }

        @Test
        fun `a matching member authorizes but a changed restriction blocks a later request`() {
            val client =
                dataSource.createOAuthTestClient(
                    port,
                    restrictedSuperGroupId = UUID.fromString("aed27030-ad90-4526-855c-1e909b1dcecb"),
                )
            val browser = browser(uniqueAddress())
            val authorization = browser.authorize(client, usePkce = true)
            assertEquals(200, browser.exchangeCode(client, authorization).status)
            dataSource.connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate(
                        """
                        UPDATE g_client_restriction_super_group
                        SET super_group_id = '712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab'
                        WHERE restriction_id = '${client.uid}'
                        """.trimIndent(),
                    )
                }
                if (!connection.autoCommit) connection.commit()
            }
            val response = browser.get(client.authorizationPath())
            assertEquals(302, response.status)
            assertEquals("access_denied", response.redirectParameters()["error"])
        }

        @Test
        fun `consent page preserves official and personal warnings and scope errors`() {
            val client = dataSource.createOAuthTestClient(port)
            val browser = browser(uniqueAddress())
            assertEquals(302, browser.login().status)
            val path = "/oauth2/consent?client_id=${client.clientId}&scope=openid%20profile%20email&state=test-state"
            val official = browser.get(path)
            assertEquals(200, official.status)
            assertFalse(official.body.contains("not an approved"))
            dataSource.connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate(
                        """
                        UPDATE g_client SET official = FALSE, created_by = '88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f'
                        WHERE client_uid = '${client.uid}'
                        """.trimIndent(),
                    )
                }
                if (!connection.autoCommit) connection.commit()
            }
            val personal = browser.get(path)
            assertEquals(200, personal.status)
            assertTrue(personal.body.contains("not an approved"))
            assertTrue(personal.body.contains("Michael"))
            assertTrue(personal.body.contains("confirm-authorization"))
            assertTrue(personal.body.contains("deny-authorization"))
            assertTrue(browser.get(path.replace("openid%20profile%20email", "openid")).body.contains("Mismatch scopes"))
            assertTrue(browser.get(path.replace(client.clientId, "bad")).body.contains("Client not found"))
        }
    }
