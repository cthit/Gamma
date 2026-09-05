package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiCredentialAuthenticator
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiTokenVerificationCache
import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.apiaccess.RawApiToken
import it.chalmers.gamma.apiaccess.StoredApiCredential
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthClientQueries
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.oauth.Scope
import it.chalmers.gamma.oauth.clientSecretMatches
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.UserAccountAccess
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.security.SecureRandom
import java.sql.SQLException
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateOAuthClientIntegrationTest {
    @Test
    fun `a stale administrator flag cannot create an official client`() =
        withDatabase { database ->
            val actor = Actor.User(ActorUserId(UUID.fromString("bc605869-9a4d-46ec-8a29-d00819d4c195")), true)
            assertFailsWith<AccessDenied> { operation(database).create(actor, input) }
        }

    @Test
    fun `API credential publication observes the client and its link already committed`() =
        withDatabase { database ->
            val beforeClients = database.tableRowCount("g_client")
            val beforeLinks = database.tableRowCount("g_client_api_key")
            var observedClients: Long? = null
            var observedLinks: Long? = null
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ) {
                        observedClients = database.tableRowCount("g_client")
                        observedLinks = database.tableRowCount("g_client_api_key")
                    }
                }
            operation(database, cache).create(administrator, input)
            assertEquals(beforeClients + 1, observedClients)
            assertEquals(beforeLinks + 1, observedLinks)
        }

    @Test
    fun `personal and official creation persist their requested scopes restrictions and credentials`() =
        withDatabase { database ->
            val operation = operation(database)
            val personal =
                operation.create(
                    owner,
                    input.copy(owner = ClientOwner.User(ownerId), generateApiKey = false),
                )
            assertNull(personal.apiCredential)
            assertEquals(setOf(Scope.OPENID, Scope.PROFILE), personal.client.scopes)
            assertEquals(ClientOwner.User(ownerId), personal.client.owner)
            val official =
                operation.create(
                    administrator.copy(isAdministrator = false),
                    input.copy(
                        includeEmailScope = true,
                        restrictedSuperGroupIds = setOf(superGroupId),
                    ),
                )
            assertEquals(setOf(Scope.OPENID, Scope.PROFILE, Scope.EMAIL), official.client.scopes)
            assertEquals(setOf(superGroupId), official.client.restrictedSuperGroupIds)
            assertEquals(personal.client, OAuthProtocolClients(database).serverClient(personal.client.uid)?.client)
            assertEquals(official.client, OAuthProtocolClients(database).serverClient(official.client.uid)?.client)
            assertTrue(clientSecretMatches(database, personal.client.clientId, personal.secret))
            assertTrue(clientSecretMatches(database, official.client.clientId, official.secret))
            val key = assertNotNull(official.apiCredential)
            assertNotNull(
                ApiCredentialAuthenticator(database).authenticate(ApiKeyId(key.id.value), RawApiToken(key.token.value)),
            )
        }

    @Test
    fun `ownership restrictions and enclosing transactions reject before either credential is prepared`() =
        withDatabase { database ->
            val random = OAuthCreationRandom()
            val operation =
                CreateOAuthClient(
                    database,
                    UserAccountAccess(database),
                    CreateClient(database, bcryptCost = 10, random = random),
                    CreateApiKey(database, bcryptCost = 10, random = random),
                )
            assertFailsWith<AccessDenied> { operation.create(Actor.Anonymous, input) }
            assertFailsWith<AccessDenied> {
                operation.create(
                    owner,
                    input.copy(owner = ClientOwner.User(administratorId)),
                )
            }
            assertFailsWith<IllegalArgumentException> {
                operation.create(
                    owner,
                    input.copy(owner = ClientOwner.User(ownerId), restrictedSuperGroupIds = setOf(superGroupId)),
                )
            }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> {
                    operation.create(
                        administrator,
                        input,
                    )
                }
            }
            assertEquals(0, random.secrets)
        }

    @Test
    fun `administrator demotion during preparation rejects before creating either record`() =
        withDatabase { database ->
            val before = creationRows(database)
            val random =
                OAuthCreationRandom {
                    database.executeSqlScript("DELETE FROM g_admin_user WHERE user_id = '${administratorId.value}'")
                }
            val operation =
                CreateOAuthClient(
                    database,
                    UserAccountAccess(database),
                    CreateClient(database, bcryptCost = 10, random = random),
                    CreateApiKey(database, bcryptCost = 10),
                )
            assertFailsWith<AccessDenied> { operation.create(administrator, input) }
            assertEquals(before, creationRows(database))
        }

    @Test
    fun `locking the owner during API credential preparation rejects before creating either record`() =
        withDatabase { database ->
            val before = creationRows(database)
            val random =
                OAuthCreationRandom {
                    database.executeSqlScript("UPDATE g_user SET locked = TRUE WHERE user_id = '${ownerId.value}'")
                }
            val operation =
                CreateOAuthClient(
                    database,
                    UserAccountAccess(database),
                    CreateClient(database, bcryptCost = 10),
                    CreateApiKey(database, bcryptCost = 10, random = random),
                )
            assertFailsWith<AccessDenied> { operation.create(owner, input.copy(owner = ClientOwner.User(ownerId))) }
            assertEquals(before, creationRows(database))
        }

    @Test
    fun `a late restriction failure rolls back both credentials and every related row`() =
        withDatabase { database ->
            val before = creationRows(database)
            var publications = 0
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ) {
                        publications++
                    }
                }
            assertFailsWith<SQLException> {
                operation(
                    database,
                    cache,
                ).create(administrator, input.copy(restrictedSuperGroupIds = setOf(UUID.randomUUID())))
            }
            assertEquals(before, creationRows(database))
            assertEquals(0, publications)
        }

    @Test
    fun `a retry after inserting the client reuses both prepared credentials`() =
        withDatabase { database ->
            database.executeSqlScript(
                """
                CREATE SEQUENCE client_scope_attempts;
                CREATE FUNCTION retry_client_scope_once() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN
                    IF nextval('client_scope_attempts') = 1 THEN RAISE EXCEPTION 'retry client scope'; END IF;
                    RETURN NEW;
                END $$;
                CREATE TRIGGER retry_client_scope_once BEFORE INSERT ON g_client_scope
                FOR EACH ROW EXECUTE FUNCTION retry_client_scope_once();
                """.trimIndent(),
            )
            val clientRandom = OAuthCreationRandom()
            val keyRandom = OAuthCreationRandom()
            var publications = 0
            val cache =
                object : ApiTokenVerificationCache by ApiTokenVerificationCache.Disabled {
                    override fun remember(
                        id: ApiKeyId,
                        storedCredential: StoredApiCredential,
                        token: RawApiToken,
                    ) {
                        publications++
                    }
                }
            val operation =
                CreateOAuthClient(
                    database,
                    UserAccountAccess(database),
                    CreateClient(database, bcryptCost = 10, random = clientRandom),
                    CreateApiKey(database, cache, bcryptCost = 10, random = keyRandom),
                )
            val beforeClients = database.tableRowCount("g_client")
            val result = operation.create(administrator, input)
            assertEquals(1, clientRandom.secrets)
            assertEquals(1, keyRandom.secrets)
            assertEquals(1, publications)
            assertEquals(beforeClients + 1, database.tableRowCount("g_client"))
            assertTrue(clientSecretMatches(database, result.client.clientId, result.secret))
            val credential = assertNotNull(result.apiCredential)
            assertNotNull(
                ApiCredentialAuthenticator(
                    database,
                ).authenticate(ApiKeyId(credential.id.value), RawApiToken(credential.token.value)),
            )
        }

    @Test
    fun `lost commit acknowledgement retains the committed client and credential together`() {
        PostgresTestEnvironment().use { postgres ->
            var armed = false
            val interceptor =
                object : StatementInterceptor {
                    override fun afterCommit(transaction: Transaction) {
                        if (!armed) return
                        armed = false
                        throw SQLException("lost client creation acknowledgement")
                    }
                }
            DatabaseFactory(postgres.dataSource, listOf(interceptor)).use { database ->
                val beforeClients = database.tableRowCount("g_client")
                val beforeKeys = database.tableRowCount("g_api_key")
                val random = OAuthCreationRandom { armed = true }
                assertFailsWith<SQLException> {
                    CreateOAuthClient(
                        database,
                        UserAccountAccess(database),
                        CreateClient(database, bcryptCost = 10),
                        CreateApiKey(database, bcryptCost = 10, random = random),
                    ).create(administrator, input)
                }
                assertEquals(beforeClients + 1, database.tableRowCount("g_client"))
                assertEquals(beforeKeys + 1, database.tableRowCount("g_api_key"))
                val client =
                    database
                        .commitTransaction(
                            readOnly = true,
                        ) { OAuthClientQueries(database).listClientsIn(this) }
                        .single {
                            it.name ==
                                input.name
                        }
                val key = assertNotNull(client.apiKeyId)
                assertNotNull(
                    ApiCredentialAuthenticator(
                        database,
                    ).authenticate(ApiKeyId(key.value), assertNotNull(random.lastToken)),
                )
            }
        }
    }

    @Test
    fun `mutable restriction input cannot change the authorized creation during preparation`() =
        withDatabase { database ->
            val restrictions = mutableSetOf<UUID>()
            val random = OAuthCreationRandom { restrictions.add(superGroupId) }
            val result =
                CreateOAuthClient(
                    database,
                    UserAccountAccess(database),
                    CreateClient(database, bcryptCost = 10, random = random),
                    CreateApiKey(database, bcryptCost = 10),
                ).create(owner, input.copy(owner = ClientOwner.User(ownerId), restrictedSuperGroupIds = restrictions))
            assertEquals(emptySet(), result.client.restrictedSuperGroupIds)
            assertEquals(
                emptySet(),
                OAuthProtocolClients(database).serverClient(result.client.uid)?.client?.restrictedSuperGroupIds,
            )
        }

    private fun operation(
        database: DatabaseFactory,
        cache: ApiTokenVerificationCache = ApiTokenVerificationCache.Disabled,
    ) = CreateOAuthClient(
        database,
        UserAccountAccess(database),
        CreateClient(database, bcryptCost = 10),
        CreateApiKey(database, cache, bcryptCost = 10),
    )

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private companion object {
        val administratorId = UserId.parse("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")
        val ownerId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
        val owner = Actor.User(ActorUserId(ownerId.value))
        val superGroupId: UUID = UUID.fromString("712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab")
        val administrator = Actor.User(ActorUserId(UUID.fromString("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")), true)
        val input =
            NewOAuthClient(
                RedirectUri("https://example.org/callback"),
                ClientName("Creation test"),
                LocalizedText.of(),
                false,
                ClientOwner.Official,
                generateApiKey = true,
            )
    }
}

private fun creationRows(database: DatabaseFactory): List<Long> =
    listOf(
        "g_client",
        "g_client_scope",
        "g_client_api_key",
        "g_client_restriction",
        "g_client_restriction_super_group",
        "g_api_key",
        "g_text",
    ).map(database::tableRowCount)

private class OAuthCreationRandom(
    private val duringSecret: () -> Unit = {},
) : SecureRandom() {
    var secrets = 0
    var lastToken: RawApiToken? = null

    override fun nextBytes(bytes: ByteArray) {
        assertNull(TransactionManager.currentOrNull())
        super.nextBytes(bytes)
        if (bytes.size == 32) {
            secrets++
            lastToken =
                RawApiToken(
                    java.util.Base64
                        .getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(bytes),
                )
            duringSecret()
        }
    }
}
