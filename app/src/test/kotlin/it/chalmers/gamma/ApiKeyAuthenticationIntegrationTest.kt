package it.chalmers.gamma

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApiKeyAuthenticationIntegrationTest : SpringApplicationTest() {
    @Test
    fun `api authentication distinguishes missing invalid wrong-type and valid credentials`() {
        val browser = browser(uniqueAddress())
        assertEquals(401, browser.get("/api/info/v1/blob").status)
        assertEquals(
            401,
            browser.get("/api/info/v1/blob", mapOf("Authorization" to "pre-shared invalid:invalid")).status,
        )

        val wrongType =
            browser.get(
                "/api/info/v1/blob",
                mapOf("Authorization" to ALLOW_LIST_CREDENTIALS),
            )
        assertEquals(403, wrongType.status)
        assertContains(wrongType.body, "FORBIDDEN")

        val info = browser.get("/api/info/v1/blob", mapOf("Authorization" to INFO_CREDENTIALS))
        assertEquals(200, info.status)
        assertEquals("[]", info.body)

        val scaffold =
            browser.get(
                "/api/account-scaffold/v1/users",
                mapOf("Authorization" to ACCOUNT_SCAFFOLD_CREDENTIALS),
            )
        assertEquals(200, scaffold.status)
        assertContains(scaffold.body, "mscott")

        val allowList =
            browser.json(
                "POST",
                "/api/allow-list/v1",
                "{\"cids\":[]}",
                mapOf("Authorization" to ALLOW_LIST_CREDENTIALS),
            )
        assertEquals(200, allowList.status)
        assertContains(allowList.body, "ALLOW_LIST_ADDED_RESPONSE")
    }

    private companion object {
        const val INFO_CREDENTIALS =
            "pre-shared 11111111-1111-4111-8111-111111111111:gamma-info-regression-token-000001"
        const val ACCOUNT_SCAFFOLD_CREDENTIALS =
            "pre-shared 22222222-2222-4222-8222-222222222222:gamma-info-regression-token-000001"
        const val ALLOW_LIST_CREDENTIALS =
            "pre-shared 33333333-3333-4333-8333-333333333333:gamma-info-regression-token-000001"
    }
}
