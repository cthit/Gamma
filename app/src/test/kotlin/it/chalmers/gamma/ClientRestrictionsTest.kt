package it.chalmers.gamma

import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals

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
    }
