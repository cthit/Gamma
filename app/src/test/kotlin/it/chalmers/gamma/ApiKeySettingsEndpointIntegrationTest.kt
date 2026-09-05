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
import kotlin.test.assertNotNull

class ApiKeySettingsEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired private lateinit var database: DatabaseFactory

    @Autowired
    private lateinit var apiKeys: ApiKeyQueries

    @Autowired
    private lateinit var creation: CreateApiKey

    @Autowired
    private lateinit var deletion: DeleteApiKey

    @Test
    fun `settings forms persist type options reject stale edits and require administrator access`() {
        val info = creation.create(ApiKeyName("HTTP info settings"), LocalizedText.of(), ApiKeyType.INFO)
        val scaffold =
            creation.create(
                ApiKeyName("HTTP scaffold settings"),
                LocalizedText.of(),
                ApiKeyType.ACCOUNT_SCAFFOLD,
            )
        val browser = browser(uniqueAddress())
        assertEquals(302, browser.login().status)
        val (_, csrf) = browser.csrf("/api-keys")
        try {
            val infoPath = "/api-keys/${info.apiKey.id.value}/info-settings"
            val infoFields = mapOf("_csrf" to csrf, "version" to "0", "superGroupType" to "committee")
            val updated = browser.form("PUT", infoPath, infoFields)
            assertEquals(302, updated.status)
            assertEquals("/api-keys/${info.apiKey.id.value}", updated.header("Location"))
            val savedInfo =
                assertNotNull(
                    database.commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        apiKeys.infoSettingsIn(this, info.apiKey.id)
                    },
                )
            assertEquals(1, savedInfo.version)
            assertEquals(listOf("committee"), savedInfo.superGroupTypes.map { it.value })
            assertEquals(409, browser.form("PUT", infoPath, infoFields).status)
            assertEquals(
                savedInfo,
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    apiKeys.infoSettingsIn(this, info.apiKey.id)
                },
            )

            val scaffoldFields =
                mapOf(
                    "_csrf" to csrf,
                    "version" to "0",
                    "superGroupTypes[0].type" to "committee",
                    "superGroupTypes[0].requiresManaged" to "on",
                    "superGroupTypes[1].type" to "society",
                )
            assertEquals(
                302,
                browser
                    .form(
                        "PUT",
                        "/api-keys/${scaffold.apiKey.id.value}/account-scaffold-settings",
                        scaffoldFields,
                    ).status,
            )
            val savedScaffold =
                assertNotNull(
                    database.commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        apiKeys.accountScaffoldSettingsIn(this, scaffold.apiKey.id)
                    },
                )
            assertEquals(1, savedScaffold.version)
            assertEquals(listOf("committee", "society"), savedScaffold.superGroupTypes.map { it.type.value })
            assertEquals(listOf(true, false), savedScaffold.superGroupTypes.map { it.requiresManaged })

            val member = browser(uniqueAddress())
            assertEquals(302, member.login("jhalpert").status)
            val (_, memberCsrf) = member.csrf("/me")
            assertEquals(
                403,
                member.form("PUT", infoPath, infoFields + mapOf("_csrf" to memberCsrf, "version" to "1")).status,
            )
            assertEquals(
                savedInfo,
                database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    apiKeys.infoSettingsIn(this, info.apiKey.id)
                },
            )
        } finally {
            database.commitTransaction { deletion.deleteIn(this, info.apiKey.id) }
            database.commitTransaction { deletion.deleteIn(this, scaffold.apiKey.id) }
        }
    }
}
