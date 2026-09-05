package it.chalmers.gamma

import it.chalmers.gamma.oauth.AuthorityName
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthClientQueries
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class ClientAuthorityEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired private lateinit var creation: CreateOAuthClient

    @Autowired private lateinit var deletion: DeleteOAuthClient

    @Autowired private lateinit var database: DatabaseFactory

    @Autowired private lateinit var clients: OAuthClientQueries

    @Test
    fun `authority forms preserve owner and administrator policy and assignment persistence`() {
        val adminId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
        val ownerId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
        val groupId = UUID.fromString("712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab")
        val administrator = Actor.User(ActorUserId(adminId.value), true)
        val pending = mutableListOf<ClientUid>()
        val adminBrowser = browser(uniqueAddress())
        val member = browser(uniqueAddress())
        assertEquals(302, adminBrowser.login("mscott").status)
        assertEquals(302, member.login("jhalpert").status)
        try {
            for (personal in listOf(false, true)) {
                val created =
                    creation.create(
                        if (personal) Actor.User(ActorUserId(ownerId.value)) else administrator,
                        NewOAuthClient(
                            RedirectUri("https://example.org/callback"),
                            ClientName("HTTP authority"),
                            LocalizedText.of(),
                            false,
                            if (personal) ClientOwner.User(ownerId) else ClientOwner.Official,
                        ),
                    )
                pending.add(created.client.uid)
                val path = "/clients/${created.client.uid.value}"
                for (helper in listOf(
                    "$path/new-authority",
                    "/clients/authority/new-user",
                    "/clients/authority/new-super-group",
                )) {
                    assertEquals(200, adminBrowser.get(helper).status)
                    assertEquals(403, member.get(helper).status)
                }
                val browser = if (personal) member else adminBrowser
                val (_, csrf) = browser.csrf("/my-clients")
                val fields =
                    mapOf(
                        "_csrf" to listOf(csrf),
                        "authority" to listOf("manage"),
                        "users" to listOf(ownerId.value.toString()),
                        "superGroups" to listOf(groupId.toString()),
                    )
                if (!personal) {
                    val (_, memberCsrf) = member.csrf("/my-clients")
                    assertEquals(
                        403,
                        member
                            .formMulti(
                                "POST",
                                "$path/authority",
                                fields + ("_csrf" to listOf(memberCsrf)),
                            ).status,
                    )
                    assertEquals(
                        403,
                        member.form("DELETE", "$path/authority/manage", mapOf("_csrf" to memberCsrf)).status,
                    )
                }
                assertEquals(302, browser.formMulti("POST", "$path/authority", fields).status)
                val authority =
                    database
                        .commitTransaction(
                            readOnly = true,
                            isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                        ) {
                            clients.authoritiesIn(this, created.client.uid)
                        }.single()
                assertEquals(AuthorityName("manage"), authority.name)
                assertEquals(setOf(ownerId), authority.userIds)
                assertEquals(setOf(groupId), authority.superGroupIds)
                assertEquals(400, browser.formMulti("POST", "$path/authority", fields).status)
                assertEquals(302, browser.form("DELETE", "$path/authority/manage", mapOf("_csrf" to csrf)).status)
                assertEquals(
                    emptyList(),
                    database.commitTransaction(
                        readOnly = true,
                        isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                    ) {
                        clients.authoritiesIn(this, created.client.uid)
                    },
                )
                assertEquals(404, browser.form("DELETE", "$path/authority/manage", mapOf("_csrf" to csrf)).status)
            }
        } finally {
            for (uid in pending) deletion.delete(administrator, uid)
        }
    }
}
