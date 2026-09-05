package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.DeleteApiKey
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class CreateApiKeyEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired private lateinit var database: DatabaseFactory

    @Autowired
    private lateinit var keys: ApiKeyQueries

    @Autowired
    private lateinit var deletion: DeleteApiKey

    @Test
    fun `creation form returns a usable credential once and rejects unauthorized or client-only requests`() {
        val browser = browser(uniqueAddress())
        assertEquals(302, browser.login().status)
        val (_, csrf) = browser.csrf("/api-keys/create")
        val fields =
            mapOf(
                "_csrf" to csrf,
                "prettyName" to "HTTP creation",
                "svDescription" to "Svenska",
                "enDescription" to "English",
                "keyType" to "INFO",
            )
        var createdId: ApiKeyId? = null
        try {
            val response = browser.form("POST", "/api-keys/create", fields)
            assertEquals(200, response.status)
            val credential =
                assertNotNull(Regex("Authorization: pre-shared ([a-f0-9-]+):([A-Za-z0-9_-]+)").find(response.body))
            val id = ApiKeyId.parse(credential.groupValues[1])
            createdId = id
            val token = credential.groupValues[2]
            val key =
                assertNotNull(
                    database.commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        keys.findApiKeyIn(this, id)
                    },
                )
            assertEquals(ApiKeyType.INFO, key.type)
            assertEquals("Svenska", key.description.sv.value)
            assertEquals("English", key.description.en.value)
            assertEquals(
                0,
                database
                    .commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        keys.infoSettingsIn(this, id)
                    }?.version,
            )
            assertEquals(
                200,
                browser.get("/api/info/v1/blob", mapOf("Authorization" to "pre-shared ${id.value}:$token")).status,
            )
            assertFalse(browser.get("/api-keys/${id.value}").body.contains(token))
            val before =
                database
                    .commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        keys.listApiKeysIn(this)
                    }.size
            assertEquals(400, browser.form("POST", "/api-keys/create", fields + ("keyType" to "CLIENT")).status)
            val member = browser(uniqueAddress())
            assertEquals(302, member.login("jhalpert").status)
            val (_, memberCsrf) = member.csrf("/me")
            assertEquals(403, member.form("POST", "/api-keys/create", fields + ("_csrf" to memberCsrf)).status)
            assertEquals(
                before,
                database
                    .commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        keys.listApiKeysIn(this)
                    }.size,
            )
        } finally {
            createdId?.let { id -> database.commitTransaction { deletion.deleteIn(this, id) } }
        }
    }
}
