package it.chalmers.gamma

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CsrfIntegrationTest : SpringApplicationTest() {
    @Test
    fun `browser mutations require csrf while api requests do not`() {
        val browser = browser(uniqueAddress())
        assertEquals(302, browser.login().status)

        assertEquals(403, browser.form("POST", "/allow-list", mapOf("cid" to "csrf-test")).status)
        assertEquals(
            403,
            browser.form("PUT", "/gdpr", mapOf("userId" to "88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")).status,
        )
        assertEquals(403, browser.form("DELETE", "/allow-list/csrf-test", emptyMap<String, String>()).status)

        val csrf = extractCsrf(browser.get("/allow-list").body)
        val methodOverride =
            browser.form(
                "POST",
                "/gdpr",
                mapOf(
                    "_method" to "PUT",
                    "_csrf" to csrf,
                    "userId" to "88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f",
                ),
            )
        assertNotEquals(403, methodOverride.status)

        val api = browser(uniqueAddress())
        val response =
            api.json(
                "POST",
                "/api/allow-list/v1",
                "{\"cids\":[]}",
                mapOf(
                    "Authorization" to
                        "pre-shared 33333333-3333-4333-8333-333333333333:gamma-info-regression-token-000001",
                ),
            )
        assertEquals(200, response.status)
    }
}
