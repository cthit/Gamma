package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientApprovals
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.DeleteClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.organization.LocalizedText
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.AcceptanceYear
import it.chalmers.gamma.users.BcryptPasswordHasher
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.CreateUser
import it.chalmers.gamma.users.Email
import it.chalmers.gamma.users.FirstName
import it.chalmers.gamma.users.Language
import it.chalmers.gamma.users.LastName
import it.chalmers.gamma.users.NewUser
import it.chalmers.gamma.users.Nick
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.UserAccountAccess
import it.chalmers.gamma.users.UserId
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApprovalOAuth2AuthorizationConsentServiceIntegrationTest {
    @Test
    fun `approvals are bidirectional scope-current idempotent and cascade with users and clients`() {
        val migrations =
            Path
                .of(checkNotNull(System.getProperty("gamma.root")))
                .resolve("app/src/main/resources/db/migration")
        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { postgres ->
                DatabaseFactory(
                    DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 3),
                ).use { database ->
                    val service =
                        ApprovalOAuth2AuthorizationConsentService(
                            database,
                            UserAccountAccess(database),
                            ClientApprovals(database),
                        )
                    val michael = UserId.parse(MICHAEL_SCOTT)
                    val firstClient =
                        run {
                            CreateClient(
                                database,
                                bcryptCost = 10,
                            ).let { creation ->
                                val prepared = creation.prepare(newClient("Approved client", michael))
                                database.commitTransaction { creation.insertIn(this, prepared) }
                            }.client
                        }

                    insertApproval(postgres, michael, firstClient.uid.value)
                    val storedApproval =
                        assertNotNull(service.findById(firstClient.uid.value.toString(), MICHAEL_SCOTT))
                    assertEquals(setOf("openid", "profile", "email"), storedApproval.scopes)

                    service.remove(storedApproval)
                    assertNull(ClientApprovals(database).approvedScopes(michael, firstClient.uid))
                    val openidOnly = consent(firstClient.uid.value.toString(), MICHAEL_SCOTT, setOf("openid"))
                    assertFailsWith<IllegalArgumentException> { service.save(openidOnly) }
                    val completeConsent =
                        consent(
                            firstClient.uid.value.toString(),
                            MICHAEL_SCOTT,
                            setOf("openid", "profile", "email"),
                        )
                    service.save(completeConsent)
                    service.save(completeConsent)
                    assertEquals(1, approvalCount(postgres, michael, firstClient.uid.value))
                    assertNotNull(ClientApprovals(database).approvedScopes(michael, firstClient.uid))

                    removeEmailScope(postgres, firstClient.uid.value)
                    assertEquals(
                        setOf("openid", "profile"),
                        assertNotNull(service.findById(firstClient.uid.value.toString(), MICHAEL_SCOTT)).scopes,
                    )
                    assertFailsWith<IllegalArgumentException> {
                        service.save(consent(firstClient.uid.value.toString(), MICHAEL_SCOTT, setOf("admin")))
                    }
                    assertNull(service.findById("not-a-client-uid", MICHAEL_SCOTT))

                    database.commitTransaction {
                        val deletion = DeleteClient(database)
                        deletion.deleteIn(this, deletion.lockIn(this, firstClient.uid))
                    }
                    assertEquals(0, approvalCount(postgres, michael, firstClient.uid.value))

                    val disposableUser =
                        run {
                            CreateUser(database, BcryptPasswordHasher(cost = 10)).create(
                                Actor.User(ActorUserId(michael.value), true),
                                NewUser(
                                    cid = Cid("oauthcascade"),
                                    nick = Nick("Cascade"),
                                    firstName = FirstName("OAuth"),
                                    lastName = LastName("Cascade"),
                                    acceptanceYear = AcceptanceYear.of(2024, currentYear = 2026),
                                    language = Language.EN,
                                    email = Email("oauth-cascade@example.org"),
                                    password = PlainTextPassword("correct horse battery staple"),
                                ),
                            )
                        }
                    val secondClient =
                        run {
                            CreateClient(
                                database,
                                bcryptCost = 10,
                            ).let { creation ->
                                val prepared = creation.prepare(newClient("User cascade client", michael))
                                database.commitTransaction { creation.insertIn(this, prepared) }
                            }.client
                        }
                    service.save(
                        consent(
                            secondClient.uid.value.toString(),
                            disposableUser.value.toString(),
                            setOf("openid", "profile", "email"),
                        ),
                    )
                    assertEquals(1, approvalCount(postgres, disposableUser, secondClient.uid.value))
                    deleteUser(postgres, disposableUser)
                    assertEquals(0, approvalCount(postgres, disposableUser, secondClient.uid.value))
                    assertNull(service.findById(secondClient.uid.value.toString(), disposableUser.value.toString()))
                }
            }
    }

    private fun newClient(
        name: String,
        owner: UserId,
    ) = NewOAuthClient(
        redirectUri = RedirectUri("https://${name.lowercase().replace(' ', '-')}.example/callback"),
        name = ClientName(name),
        description = LocalizedText.of(en = name),
        includeEmailScope = true,
        owner = ClientOwner.User(owner),
    )

    private fun consent(
        clientId: String,
        principalName: String,
        scopes: Set<String>,
    ): OAuth2AuthorizationConsent {
        val builder = OAuth2AuthorizationConsent.withId(clientId, principalName)
        scopes.forEach(builder::scope)
        return builder.build()
    }

    private fun insertApproval(
        postgres: PostgresTestEnvironment,
        userId: UserId,
        clientUid: java.util.UUID,
    ) {
        postgres.connection { connection ->
            connection
                .prepareStatement(
                    "INSERT INTO g_user_approval (user_id, client_uid, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)",
                ).use { statement ->
                    statement.setObject(1, userId.value)
                    statement.setObject(2, clientUid)
                    assertEquals(1, statement.executeUpdate())
                }
            connection.commit()
        }
    }

    private fun removeEmailScope(
        postgres: PostgresTestEnvironment,
        clientUid: java.util.UUID,
    ) {
        postgres.connection { connection ->
            connection
                .prepareStatement("DELETE FROM g_client_scope WHERE client_uid = ? AND scope = 'EMAIL'")
                .use { statement ->
                    statement.setObject(1, clientUid)
                    assertEquals(1, statement.executeUpdate())
                }
            connection.commit()
        }
    }

    private fun approvalCount(
        postgres: PostgresTestEnvironment,
        userId: UserId,
        clientUid: java.util.UUID,
    ): Int =
        postgres.connection { connection ->
            connection
                .prepareStatement("SELECT COUNT(*) FROM g_user_approval WHERE user_id = ? AND client_uid = ?")
                .use { statement ->
                    statement.setObject(1, userId.value)
                    statement.setObject(2, clientUid)
                    statement.executeQuery().use { result ->
                        assertTrue(result.next())
                        result.getInt(1)
                    }
                }
        }

    private fun deleteUser(
        postgres: PostgresTestEnvironment,
        userId: UserId,
    ) {
        postgres.connection { connection ->
            connection.prepareStatement("DELETE FROM g_user WHERE user_id = ?").use { statement ->
                statement.setObject(1, userId.value)
                assertEquals(1, statement.executeUpdate())
            }
            connection.commit()
        }
    }

    private companion object {
        const val MICHAEL_SCOTT = "88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f"
    }
}
