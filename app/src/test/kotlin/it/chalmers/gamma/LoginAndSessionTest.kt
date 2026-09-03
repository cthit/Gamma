package it.chalmers.gamma

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class LoginAndSessionTest : SpringApplicationTest() {
    @Test
    fun `login changes the session id and logout clears browser state`() {
        val browser = browser(uniqueAddress())
        val anonymous = browser.get("/")
        assertEquals(302, anonymous.status)
        assertContains(assertNotNull(anonymous.header("Location")), "/login")

        val (_, csrf) = browser.csrf()
        val anonymousSession = assertNotNull(browser.sessionCookie)
        val failed =
            browser.form(
                "POST",
                "/login",
                mapOf("username" to "mscott", "password" to "wrong-password", "_csrf" to csrf),
            )
        assertEquals(302, failed.status)
        assertContains(assertNotNull(failed.header("Location")), "/login?error")

        val loginPage = browser.get("/login")
        val authenticated =
            browser.form(
                "POST",
                "/login",
                mapOf(
                    "username" to "mscott",
                    "password" to "password1337",
                    "_csrf" to extractCsrf(loginPage.body),
                ),
            )
        assertEquals(302, authenticated.status)
        assertNotEquals(anonymousSession, browser.sessionCookie)
        assertEquals(200, browser.get("/").status)

        val home = browser.get("/")
        val logout = browser.form("POST", "/logout", mapOf("_csrf" to extractCsrf(home.body)))
        assertEquals(302, logout.status)
        assertContains(assertNotNull(logout.header("Location")), "/login?logout")
        assertEquals(302, browser.get("/").status)
    }

    @Test
    fun `login is throttled after fifty attempts`() {
        val browser = browser(uniqueAddress())
        repeat(50) {
            val page = browser.get("/login")
            val response =
                browser.form(
                    "POST",
                    "/login",
                    mapOf("username" to "nobody", "password" to "wrong-password", "_csrf" to extractCsrf(page.body)),
                )
            assertEquals(302, response.status)
        }
        val page = browser.get("/login")
        val throttled =
            browser.form(
                "POST",
                "/login",
                mapOf("username" to "nobody", "password" to "wrong-password", "_csrf" to extractCsrf(page.body)),
            )
        assertContains(assertNotNull(throttled.header("Location")), "/login?throttle=true")
    }

    @Test
    fun `login throttle uses the address resolved by the trusted proxy`() {
        val browser = browser()
        val resolvedAddress = uniqueAddress()
        repeat(50) { attempt ->
            val page = browser.get("/login")
            val response =
                browser.form(
                    "POST",
                    "/login",
                    mapOf("username" to "nobody", "password" to "wrong-password", "_csrf" to extractCsrf(page.body)),
                    headers = mapOf("X-Forwarded-For" to "203.0.113.${attempt + 1}, $resolvedAddress"),
                )
            assertEquals(302, response.status)
        }

        val page = browser.get("/login")
        val throttled =
            browser.form(
                "POST",
                "/login",
                mapOf("username" to "nobody", "password" to "wrong-password", "_csrf" to extractCsrf(page.body)),
                headers = mapOf("X-Forwarded-For" to "203.0.113.200, $resolvedAddress"),
            )
        assertContains(assertNotNull(throttled.header("Location")), "/login?throttle=true")
    }
}
