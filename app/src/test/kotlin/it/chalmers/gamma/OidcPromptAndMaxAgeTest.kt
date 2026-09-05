package it.chalmers.gamma

import org.springframework.beans.factory.annotation.Autowired
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class OidcPromptAndMaxAgeTest
    @Autowired
    constructor(
        private val dataSource: DataSource,
    ) : SpringApplicationTest() {
        @Test
        fun `prompt login forces an authenticated browser through login again`() {
            val client = dataSource.createOAuthTestClient(port)
            val browser = browser(uniqueAddress())
            assertEquals(302, browser.login().status)

            val response = browser.get(client.authorizationPath(prompt = "login"))

            assertEquals(302, response.status)
            assertContains(response.header("Location").orEmpty(), "/login")
        }

        @Test
        fun `max age zero forces an authenticated browser through login again`() {
            val client = dataSource.createOAuthTestClient(port)
            val browser = browser(uniqueAddress())
            assertEquals(302, browser.login().status)

            val response = browser.get(client.authorizationPath(maxAge = 0))

            assertEquals(302, response.status)
            assertContains(response.header("Location").orEmpty(), "/login")
        }

        @Test
        fun `prompt none returns login required for an anonymous browser`() {
            val client = dataSource.createOAuthTestClient(port)
            val response = browser(uniqueAddress()).get(client.authorizationPath(prompt = "none"))

            assertEquals(302, response.status)
            assertEquals("login_required", response.redirectParameters()["error"])
        }
    }
