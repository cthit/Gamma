package it.chalmers.gamma.oauth

import it.chalmers.gamma.CreateOAuthClient
import it.chalmers.gamma.DeleteOAuthClient
import it.chalmers.gamma.ResetOAuthClientSecret
import it.chalmers.gamma.apiaccess.ApiCredentialAuthenticator
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.apiaccess.DeleteOwnedApiKeys
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.UserAccountAccess
import it.chalmers.gamma.users.UserQueries
import java.nio.file.Path
import java.security.SecureRandom
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OAuthClientLifecycleIntegrationTest {
    @Test
    fun `client administration authorizes administrators and owners before mutation`() {
        val root = Path.of(checkNotNull(System.getProperty("gamma.root")))
        val migrations = root.resolve("app/src/main/resources/db/migration")

        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { postgres ->
                DatabaseFactory(
                    DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
                ).use { database ->
                    val apiAccess = ApiKeyQueries(database)
                    val identities = UserQueries(database)
                    val reset =
                        ResetOAuthClientSecret(
                            database,
                            UserAccountAccess(database),
                            RotateClientSecret(database, bcryptCost = 10),
                        )
                    run {
                        val administrator = assertNotNull(identities.findUser(Cid("mscott")))
                        val owner = assertNotNull(identities.findUser(Cid("jhalpert")))
                        val input =
                            NewOAuthClient(
                                RedirectUri("https://owner.example.org/callback"),
                                ClientName("Owner client"),
                                LocalizedText.of("Ägarklient", "Owner client"),
                                includeEmailScope = false,
                                owner = ClientOwner.User(owner.id),
                                generateApiKey = true,
                            )

                        assertFailsWith<AccessDenied> {
                            CreateOAuthClient(
                                database,
                                UserAccountAccess(database),
                                CreateClient(database, bcryptCost = 10),
                                CreateApiKey(database, bcryptCost = 10),
                            ).create(Actor.Anonymous, input)
                        }
                        val created =
                            CreateOAuthClient(
                                database,
                                UserAccountAccess(database),
                                CreateClient(database, bcryptCost = 10),
                                CreateApiKey(database, bcryptCost = 10),
                            ).create(Actor.User(ActorUserId(owner.id.value)), input)
                        assertFailsWith<AccessDenied> {
                            reset.reset(
                                Actor.User(ActorUserId(java.util.UUID.randomUUID())),
                                created.client.uid,
                            )
                        }
                        assertTrue(clientSecretMatches(database, created.client.clientId, created.secret))

                        reset.reset(Actor.User(ActorUserId(owner.id.value)), created.client.uid)
                        DeleteOAuthClient(
                            database,
                            UserAccountAccess(database),
                            DeleteClient(database),
                            DeleteOwnedApiKeys(database),
                        ).delete(
                            Actor.User(ActorUserId(administrator.id.value), isAdministrator = true),
                            created.client.uid,
                        )
                        assertNull(OAuthProtocolClients(database).serverClient(created.client.uid)?.client)
                        val revokedCredential = assertNotNull(created.apiCredential)
                        assertNull(
                            database.commitTransaction(
                                readOnly = true,
                                isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                            ) {
                                apiAccess.findApiKeyIn(this, ApiKeyId(revokedCredential.id.value))
                            },
                        )
                        assertNull(
                            ApiCredentialAuthenticator(database).authenticate(
                                ApiKeyId(revokedCredential.id.value),
                                it.chalmers.gamma.apiaccess
                                    .RawApiToken(revokedCredential.token.value),
                                requiredType = ApiKeyType.CLIENT,
                            ),
                        )
                    }
                }
            }
    }

    @Test
    fun `persists credentials scopes ownership and approvals on real postgres`() {
        val root = Path.of(checkNotNull(System.getProperty("gamma.root")))
        val migrations = root.resolve("app/src/main/resources/db/migration")

        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { postgres ->
                DatabaseFactory(
                    DatabaseSettings(
                        jdbcUrl = postgres.jdbcUrl,
                        username = postgres.username,
                        password = postgres.password,
                        maximumPoolSize = 2,
                    ),
                ).use { database ->
                    val clients = OAuthClientQueries(database)
                    val michael = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")

                    run {
                        val issued =
                            CreateApiKey(database, bcryptCost = 10).create(
                                it.chalmers.gamma.apiaccess
                                    .ApiKeyName("Regression client"),
                                LocalizedText.of("Testklient", "Test client"),
                                ApiKeyType.CLIENT,
                            )
                        val created =
                            CreateClient(database, bcryptCost = 10).let { creation ->
                                val prepared =
                                    creation.prepare(
                                        NewOAuthClient(
                                            redirectUri = RedirectUri("https://client.example.org/callback"),
                                            name = ClientName("Regression client"),
                                            description = LocalizedText.of("Testklient", "Test client"),
                                            includeEmailScope = true,
                                            owner = ClientOwner.User(michael),
                                            generateApiKey = true,
                                            restrictedSuperGroupIds =
                                                setOf(
                                                    java.util.UUID.fromString("712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab"),
                                                ),
                                        ),
                                    )
                                database.commitTransaction {
                                    creation.insertIn(
                                        this,
                                        prepared,
                                        ClientApiCredential(
                                            OAuthApiKeyId(issued.apiKey.id.value),
                                            OAuthApiToken(issued.token.value),
                                        ),
                                    )
                                }
                            }
                        assertNotEquals(created.secret.value, created.secret.toString())
                        assertEquals(setOf(Scope.OPENID, Scope.PROFILE, Scope.EMAIL), created.client.scopes)
                        assertPersistedScopes(
                            postgres,
                            created.client.uid,
                            setOf("PROFILE", "EMAIL"),
                        )
                        val apiCredential = assertNotNull(created.apiCredential)
                        assertEquals(
                            created.client,
                            database.commitTransaction(readOnly = true) {
                                clients.findClientByApiKeyIn(this, apiCredential.id)
                            },
                        )
                        assertEquals(
                            setOf(java.util.UUID.fromString("712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab")),
                            created.client.restrictedSuperGroupIds,
                        )
                        assertEquals(
                            created.client,
                            OAuthProtocolClients(database).serverClient(created.client.uid)?.client,
                        )
                        assertEquals(
                            created.client,
                            OAuthProtocolClients(database).serverClient(created.client.clientId)?.client,
                        )
                        assertEquals(
                            listOf(created.client),
                            database.commitTransaction(readOnly = true) {
                                clients.listClientsIn(this, michael)
                            },
                        )
                        assertTrue(clientSecretMatches(database, created.client.clientId, created.secret))
                        assertFalse(
                            clientSecretMatches(
                                database,
                                created.client.clientId,
                                RawClientSecret("this-is-not-the-right-secret-but-long-enough"),
                            ),
                        )

                        assertNull(ClientApprovals(database).approvedScopes(michael, created.client.uid))
                        database.commitTransaction {
                            ClientApprovals(
                                database,
                            ).approveIn(this, michael, created.client.uid, created.client.scopes)
                        }
                        database.commitTransaction {
                            ClientApprovals(
                                database,
                            ).approveIn(this, michael, created.client.uid, created.client.scopes)
                        }
                        assertNotNull(ClientApprovals(database).approvedScopes(michael, created.client.uid))
                        assertEquals(
                            listOf(created.client),
                            database.commitTransaction(readOnly = true) {
                                clients.approvedClientsIn(this, michael)
                            },
                        )
                        assertEquals(
                            listOf(michael),
                            database.commitTransaction(readOnly = true) {
                                clients.approvedUserIdsIn(this, created.client.uid)
                            },
                        )

                        val authority = AuthorityName("calendarread")
                        database.commitTransaction {
                            val authorities = ClientAuthorities(database)
                            authorities.createIn(
                                this,
                                authorities.lockIn(this, created.client.uid),
                                authority,
                                setOf(michael),
                                emptySet(),
                            )
                        }
                        assertEquals(
                            listOf(authority),
                            database.commitTransaction(readOnly = true) {
                                clients.authoritiesForUserIn(this, created.client.uid, michael)
                            },
                        )
                        assertEquals(
                            setOf(
                                michael,
                            ),
                            database
                                .commitTransaction(
                                    readOnly = true,
                                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                                ) {
                                    clients.authoritiesIn(this, created.client.uid)
                                }.single()
                                .userIds,
                        )
                        database.commitTransaction {
                            val authorities = ClientAuthorities(database)
                            authorities.deleteIn(this, authorities.lockIn(this, created.client.uid), authority)
                        }
                        assertTrue(
                            database
                                .commitTransaction(
                                    readOnly = true,
                                    isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                                ) {
                                    clients.authoritiesIn(this, created.client.uid)
                                }.isEmpty(),
                        )
                        ClientApprovals(database).revoke(michael, created.client.uid)
                        assertNull(ClientApprovals(database).approvedScopes(michael, created.client.uid))

                        val actor = Actor.User(ActorUserId(michael.value))
                        val reset =
                            ResetOAuthClientSecret(
                                database,
                                UserAccountAccess(database),
                                RotateClientSecret(database, bcryptCost = 10),
                            )
                        val replacement = reset.reset(actor, created.client.uid).secret
                        assertFalse(clientSecretMatches(database, created.client.clientId, created.secret))
                        assertTrue(clientSecretMatches(database, created.client.clientId, replacement))

                        val blockingRandom = BlockingFirstSecretRandom()
                        val concurrentReset =
                            ResetOAuthClientSecret(
                                database,
                                UserAccountAccess(database),
                                RotateClientSecret(database, bcryptCost = 10, random = blockingRandom),
                            )
                        val workers = Executors.newFixedThreadPool(2)
                        val first =
                            workers.submit<RawClientSecret> {
                                concurrentReset.reset(actor, created.client.uid).secret
                            }
                        val concurrentReplacement =
                            try {
                                check(blockingRandom.awaitFirstSecret()) { "First secret rotation did not start" }
                                val second =
                                    workers.submit<Throwable?> {
                                        runCatching {
                                            concurrentReset.reset(actor, created.client.uid).secret
                                        }.exceptionOrNull()
                                    }
                                assertTrue(second.get() is OAuthClientConflict)
                                blockingRandom.releaseFirstSecret()
                                first.get()
                            } finally {
                                blockingRandom.releaseFirstSecret()
                                workers.shutdownNow()
                            }
                        assertTrue(clientSecretMatches(database, created.client.clientId, concurrentReplacement))

                        database.commitTransaction {
                            val deletion = DeleteClient(database)
                            deletion.deleteIn(this, deletion.lockIn(this, created.client.uid))
                        }
                        assertNull(OAuthProtocolClients(database).serverClient(created.client.uid)?.client)
                        assertNull(
                            database.commitTransaction(
                                readOnly = true,
                            ) { clients.findClientByApiKeyIn(this, apiCredential.id) },
                        )
                        assertFailsWith<OAuthClientNotFound> { reset.reset(actor, created.client.uid) }
                    }
                }
            }
    }

    private fun assertPersistedScopes(
        postgres: PostgresTestEnvironment,
        clientUid: ClientUid,
        expected: Set<String>,
    ) {
        postgres.connection { connection ->
            connection
                .prepareStatement("SELECT scope FROM g_client_scope WHERE client_uid = ?")
                .use { statement ->
                    statement.setObject(1, clientUid.value)
                    statement.executeQuery().use { result ->
                        val actual =
                            buildSet {
                                while (result.next()) add(result.getString(1))
                            }
                        assertEquals(expected, actual)
                    }
                }
        }
    }

    @Test
    fun `concurrent client deletion cannot leave its credential usable`() {
        val root = Path.of(checkNotNull(System.getProperty("gamma.root")))
        val migrations = root.resolve("app/src/main/resources/db/migration")

        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { postgres ->
                DatabaseFactory(
                    DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
                ).use { database ->
                    val apiAccess = ApiKeyQueries(database)
                    val administratorId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
                    val actor = Actor.User(ActorUserId(administratorId.value), isAdministrator = true)

                    run {
                        val created =
                            CreateOAuthClient(
                                database,
                                UserAccountAccess(database),
                                CreateClient(database, bcryptCost = 10),
                                CreateApiKey(database, bcryptCost = 10),
                            ).create(
                                actor,
                                NewOAuthClient(
                                    RedirectUri("https://concurrent.example.org/callback"),
                                    ClientName("Concurrent retirement client"),
                                    LocalizedText.of("Samtidig klient", "Concurrent client"),
                                    includeEmailScope = false,
                                    owner = ClientOwner.Official,
                                    generateApiKey = true,
                                ),
                            )
                        val credential = assertNotNull(created.apiCredential)
                        val deletion =
                            DeleteOAuthClient(
                                database,
                                UserAccountAccess(database),
                                DeleteClient(database),
                                DeleteOwnedApiKeys(database),
                            )

                        val workers = Executors.newFixedThreadPool(2)
                        val attempts =
                            List(2) {
                                workers.submit<OAuthClientNotFound?> {
                                    try {
                                        deletion.delete(actor, created.client.uid)
                                        null
                                    } catch (failure: OAuthClientNotFound) {
                                        failure
                                    }
                                }
                            }
                        val results =
                            try {
                                attempts.map { it.get() }
                            } finally {
                                workers.shutdownNow()
                            }

                        assertEquals(1, results.count { it == null })
                        assertEquals(1, results.count { it is OAuthClientNotFound })
                        assertNull(OAuthProtocolClients(database).serverClient(created.client.uid)?.client)
                        assertNull(
                            database.commitTransaction(
                                readOnly = true,
                                isolationLevel = java.sql.Connection.TRANSACTION_REPEATABLE_READ,
                            ) {
                                apiAccess.findApiKeyIn(this, ApiKeyId(credential.id.value))
                            },
                        )
                        assertNull(
                            ApiCredentialAuthenticator(database).authenticate(
                                ApiKeyId(credential.id.value),
                                it.chalmers.gamma.apiaccess
                                    .RawApiToken(credential.token.value),
                                requiredType = ApiKeyType.CLIENT,
                            ),
                        )
                    }
                }
            }
    }
}

private class BlockingFirstSecretRandom : SecureRandom() {
    private val firstSecretStarted = CountDownLatch(1)
    private val firstSecretRelease = CountDownLatch(1)

    override fun nextBytes(bytes: ByteArray) {
        bytes.fill(11)
        firstSecretStarted.countDown()
        check(firstSecretRelease.await(5, TimeUnit.SECONDS)) { "Timed out waiting to release the first secret" }
    }

    fun awaitFirstSecret(): Boolean = firstSecretStarted.await(5, TimeUnit.SECONDS)

    fun releaseFirstSecret() = firstSecretRelease.countDown()
}
