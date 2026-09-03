package it.chalmers.gamma.oauth

import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyStore
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.OAuthClientCredentials
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.BcryptPasswordHasher
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.UserStore
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

class OAuthClientStoreIntegrationTest {
    @Test
    fun `client administration authorizes administrators and owners before mutation`() {
        val root = Path.of(checkNotNull(System.getProperty("gamma.root")))
        val migrations = root.resolve("app/src/main/resources/db/migration")

        PostgresTestEnvironment(listOf("filesystem:${migrations.toAbsolutePath()}"))
            .use { postgres ->
                DatabaseFactory(
                    DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password, maximumPoolSize = 2),
                ).use { database ->
                    val clients = OAuthClientStore(database, bcryptCost = 10)
                    val apiAccess = ApiKeyStore(database, bcryptCost = 10)
                    val identities = UserStore(database, BcryptPasswordHasher(cost = 10))
                    val administration =
                        OAuthClientAdministration(
                            clients,
                            OAuthClientCredentials(apiAccess),
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
                            administration.createMyClient(Actor.Anonymous, input)
                        }
                        val created = administration.createMyClient(Actor.User(ActorUserId(owner.id.value)), input)
                        assertFailsWith<AccessDenied> {
                            administration.resetSecret(
                                Actor.User(ActorUserId(java.util.UUID.randomUUID())),
                                created.client.uid,
                            )
                        }
                        assertNotNull(clients.authenticate(created.client.clientId, created.secret))

                        administration.resetSecret(Actor.User(ActorUserId(owner.id.value)), created.client.uid)
                        administration.deleteClient(
                            Actor.User(ActorUserId(administrator.id.value), isAdministrator = true),
                            created.client.uid,
                        )
                        assertNull(clients.findClient(created.client.uid))
                        val revokedCredential = assertNotNull(created.apiCredential)
                        assertNull(apiAccess.findApiKey(ApiKeyId(revokedCredential.id.value)))
                        assertNull(
                            apiAccess.authenticate(
                                ApiKeyType.CLIENT,
                                ApiKeyId(revokedCredential.id.value),
                                it.chalmers.gamma.apiaccess
                                    .RawApiToken(revokedCredential.token.value),
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
                    val clients = OAuthClientStore(database, bcryptCost = 10)
                    val apiAccess = ApiKeyStore(database, bcryptCost = 10)
                    val michael = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")

                    run {
                        val issued =
                            OAuthClientCredentials(apiAccess).issue(
                                it.chalmers.gamma.apiaccess
                                    .ApiKeyName("Regression client"),
                                LocalizedText.of("Testklient", "Test client"),
                            )
                        val created =
                            clients.createClient(
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
                                ClientApiCredential(OAuthApiKeyId(issued.id.value), OAuthApiToken(issued.token.value)),
                            )
                        assertNotEquals(created.secret.value, created.secret.toString())
                        assertEquals(setOf(Scope.OPENID, Scope.PROFILE, Scope.EMAIL), created.client.scopes)
                        assertPersistedScopes(
                            postgres,
                            created.client.uid,
                            setOf("PROFILE", "EMAIL"),
                        )
                        val apiCredential = assertNotNull(created.apiCredential)
                        assertEquals(created.client, clients.findClientByApiKey(apiCredential.id))
                        assertEquals(
                            setOf(java.util.UUID.fromString("712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab")),
                            created.client.restrictedSuperGroupIds,
                        )
                        assertEquals(created.client, clients.findClient(created.client.uid))
                        assertEquals(created.client, clients.findClient(created.client.clientId))
                        assertEquals(listOf(created.client), clients.listClients(michael))
                        assertEquals(created.client, clients.authenticate(created.client.clientId, created.secret))
                        assertNull(
                            clients.authenticate(
                                created.client.clientId,
                                RawClientSecret("this-is-not-the-right-secret-but-long-enough"),
                            ),
                        )

                        assertFalse(clients.isApproved(michael, created.client.uid))
                        clients.approve(michael, created.client.uid)
                        clients.approve(michael, created.client.uid)
                        assertTrue(clients.isApproved(michael, created.client.uid))
                        assertEquals(listOf(created.client), clients.approvedClients(michael))
                        assertEquals(listOf(michael), clients.approvedUserIds(created.client.uid))

                        val authority = AuthorityName("calendarread")
                        clients.createAuthority(created.client.uid, authority, setOf(michael))
                        assertEquals(listOf(authority), clients.authoritiesForUser(created.client.uid, michael))
                        assertEquals(setOf(michael), clients.authorities(created.client.uid).single().userIds)
                        clients.deleteAuthority(created.client.uid, authority)
                        assertTrue(clients.authorities(created.client.uid).isEmpty())
                        clients.revokeApproval(michael, created.client.uid)
                        assertFalse(clients.isApproved(michael, created.client.uid))

                        val replacement = clients.resetSecret(created.client.uid)
                        assertNull(clients.authenticate(created.client.clientId, created.secret))
                        assertNotNull(clients.authenticate(created.client.clientId, replacement))

                        val blockingRandom = BlockingFirstSecretRandom()
                        val concurrentClients = OAuthClientStore(database, bcryptCost = 10, random = blockingRandom)
                        val workers = Executors.newFixedThreadPool(2)
                        val first =
                            workers.submit<RawClientSecret> {
                                concurrentClients.resetSecret(created.client.uid)
                            }
                        val concurrentReplacement =
                            try {
                                check(blockingRandom.awaitFirstSecret()) { "First secret rotation did not start" }
                                val second =
                                    workers.submit<Throwable?> {
                                        runCatching {
                                            concurrentClients.resetSecret(created.client.uid)
                                        }.exceptionOrNull()
                                    }
                                assertTrue(second.get() is OAuthClientConflict)
                                blockingRandom.releaseFirstSecret()
                                first.get()
                            } finally {
                                blockingRandom.releaseFirstSecret()
                                workers.shutdownNow()
                            }
                        assertNotNull(clients.authenticate(created.client.clientId, concurrentReplacement))

                        clients.deleteClient(created.client.uid)
                        assertNull(clients.findClient(created.client.uid))
                        assertNull(clients.findClientByApiKey(apiCredential.id))
                        assertFailsWith<OAuthClientNotFound> { clients.resetSecret(created.client.uid) }
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
                    val clients = OAuthClientStore(database, bcryptCost = 10)
                    val apiAccess = ApiKeyStore(database, bcryptCost = 10)
                    val administratorId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
                    val actor = Actor.User(ActorUserId(administratorId.value), isAdministrator = true)
                    val administration =
                        OAuthClientAdministration(
                            clients,
                            OAuthClientCredentials(apiAccess),
                        )

                    run {
                        val created =
                            administration.createOfficialClient(
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
                        val concurrentAdministration =
                            OAuthClientAdministration(
                                clients,
                                OAuthClientCredentials(apiAccess),
                            )

                        val workers = Executors.newFixedThreadPool(2)
                        val attempts =
                            List(2) {
                                workers.submit<OAuthClientNotFound?> {
                                    try {
                                        concurrentAdministration.deleteClient(actor, created.client.uid)
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
                        assertNull(clients.findClient(created.client.uid))
                        assertNull(apiAccess.findApiKey(ApiKeyId(credential.id.value)))
                        assertNull(
                            apiAccess.authenticate(
                                ApiKeyType.CLIENT,
                                ApiKeyId(credential.id.value),
                                it.chalmers.gamma.apiaccess
                                    .RawApiToken(credential.token.value),
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
