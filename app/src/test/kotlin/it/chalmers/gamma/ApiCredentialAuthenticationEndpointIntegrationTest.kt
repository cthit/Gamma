package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyName
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.apiaccess.RawApiToken
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiCredentialAuthenticationEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired private lateinit var database: DatabaseFactory

    @Autowired
    private lateinit var apiKeys: ApiKeyQueries

    @Autowired
    private lateinit var creation: CreateApiKey

    @Autowired
    private lateinit var deletion: DeleteAdministrativeApiKey

    @Autowired
    private lateinit var rotation: RotateAdministrativeApiKey

    @Test
    fun `API requests reject rotated and deleted credentials`() {
        val administrator = Actor.User(ActorUserId(UUID.fromString("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")), true)
        val created =
            creation.create(
                ApiKeyName("HTTP authentication"),
                LocalizedText.of("Test", "Test"),
                ApiKeyType.ALLOW_LIST,
            )
        val id = created.apiKey.id
        val browser = browser(uniqueAddress())

        fun request(token: RawApiToken) =
            browser.json(
                "POST",
                "/api/allow-list/v1",
                "{\"cids\":[]}",
                mapOf("Authorization" to "pre-shared ${id.value}:${token.value}"),
            )
        try {
            assertEquals(200, request(created.token).status)
            val replacement = rotation.rotate(administrator, id).token
            assertEquals(401, request(created.token).status)
            assertEquals(200, request(replacement).status)
            deletion.delete(administrator, id)
            assertEquals(401, request(replacement).status)
        } finally {
            if (database.commitTransaction(
                    readOnly = true,
                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                ) {
                    apiKeys.findApiKeyIn(this, id)
                } !=
                null
            ) {
                deletion.delete(administrator, id)
            }
        }
    }
}
