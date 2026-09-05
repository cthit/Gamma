package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyName
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.apiaccess.DeleteApiKey
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApiKeyWriteEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired private lateinit var database: DatabaseFactory

    @Autowired
    private lateinit var creation: CreateApiKey

    @Autowired
    private lateinit var deletion: DeleteApiKey

    @Autowired
    private lateinit var keys: ApiKeyQueries

    @Test
    fun `reset and delete forms preserve credentials redirects version and administrator access`() {
        val created = creation.create(ApiKeyName("HTTP writes"), LocalizedText.of(), ApiKeyType.INFO)
        val id = created.apiKey.id
        val path = "/api-keys/${id.value}"
        val browser = browser(uniqueAddress())
        assertEquals(302, browser.login().status)
        val (_, csrf) = browser.csrf(path)
        try {
            val details = browser.get(path)
            assertEquals(200, details.status)
            assertTrue(details.body.contains("HTTP writes"))
            assertTrue(details.body.contains("INFO"))
            assertFalse(details.body.contains(created.token.value))
            val listed = browser.get("/api-keys")
            assertEquals(200, listed.status)
            assertTrue(listed.body.contains("HTTP writes"))
            assertFalse(listed.body.contains(created.token.value))
            val member = browser(uniqueAddress())
            assertEquals(302, member.login("jhalpert").status)
            val (_, memberCsrf) = member.csrf("/me")
            assertEquals(403, member.get("/api-keys").status)
            assertEquals(403, member.get(path).status)
            assertEquals(403, member.form("POST", "$path/reset", mapOf("_csrf" to memberCsrf)).status)
            assertEquals(403, member.form("DELETE", path, mapOf("_csrf" to memberCsrf)).status)
            assertEquals(
                0,
                database
                    .commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        keys.findApiKeyIn(this, id)
                    }?.version,
            )
            val rotated = browser.form("POST", "$path/reset", mapOf("_csrf" to csrf))
            assertEquals(200, rotated.status)
            val credential =
                assertNotNull(Regex("Authorization: pre-shared ([a-f0-9-]+):([A-Za-z0-9_-]+)").find(rotated.body))
            assertEquals(id.value.toString(), credential.groupValues[1])
            val authorization = "pre-shared ${id.value}:${credential.groupValues[2]}"
            assertEquals(
                1,
                database
                    .commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        keys.findApiKeyIn(this, id)
                    }?.version,
            )
            assertEquals(200, browser.get("/api/info/v1/blob", mapOf("Authorization" to authorization)).status)
            assertEquals(
                401,
                browser
                    .get(
                        "/api/info/v1/blob",
                        mapOf("Authorization" to "pre-shared ${id.value}:${created.token.value}"),
                    ).status,
            )
            val deleted = browser.form("DELETE", path, mapOf("_csrf" to csrf))
            assertEquals(302, deleted.status)
            assertEquals("/api-keys", deleted.header("Location"))
            assertNull(
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.findApiKeyIn(this, id)
                },
            )
            assertEquals(401, browser.get("/api/info/v1/blob", mapOf("Authorization" to authorization)).status)
            assertEquals(404, browser.get(path).status)
            assertEquals(404, browser.form("DELETE", path, mapOf("_csrf" to csrf)).status)
            assertEquals(404, browser.form("POST", "$path/reset", mapOf("_csrf" to csrf)).status)
        } finally {
            if (database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    keys.findApiKeyIn(this, id)
                } !=
                null
            ) {
                database.commitTransaction { deletion.deleteIn(this, id) }
            }
        }
    }
}
