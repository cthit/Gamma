package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiAccessNotFound
import it.chalmers.gamma.apiaccess.ApiCredentialAuthenticator
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.apiaccess.DeleteApiKey
import it.chalmers.gamma.apiaccess.DeleteOwnedApiKeys
import it.chalmers.gamma.apiaccess.RawApiToken
import it.chalmers.gamma.oauth.AuthorityName
import it.chalmers.gamma.oauth.ClientApprovals
import it.chalmers.gamma.oauth.ClientAuthorities
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.DeleteClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthClientNotFound
import it.chalmers.gamma.oauth.OAuthClientQueries
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.oauth.clientSecretMatches
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.UserAccountAccess
import java.sql.SQLException
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeleteOAuthClientIntegrationTest {
    @Test
    fun `stale administrator authority cannot delete an official client`() =
        withDatabase { database ->
            val created = create(database)
            assertFailsWith<AccessDenied> {
                operation(database).delete(staleAdministrator, created.client.uid)
            }
        }

    @Test
    fun `a late deletion failure retains the client and credential together`() =
        withDatabase { database ->
            val created = create(database)
            val before = deletionRows(database)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_client_deletion() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RAISE EXCEPTION 'client deletion rejected'; END $$;
                CREATE TRIGGER reject_client_deletion BEFORE DELETE ON g_client
                FOR EACH ROW EXECUTE FUNCTION reject_client_deletion();
                """.trimIndent(),
            )
            assertFailsWith<SQLException> { operation(database).delete(administrator, created.client.uid) }
            assertEquals(before, deletionRows(database))
        }

    @Test
    fun `owner and current administrator deletion remove all related rows and invalidate both credentials`() =
        withDatabase { database ->
            val before = deletionRows(database)
            val personal = create(database, ClientOwner.User(ownerId))
            assertEquals(ClientOwner.User(ownerId), operation(database).delete(ownerActor, personal.client.uid))
            val official = create(database)
            database.commitTransaction {
                ClientApprovals(database).approveIn(this, ownerId, official.client.uid, official.client.scopes)
            }
            database.commitTransaction {
                val authorities = ClientAuthorities(database)
                authorities.createIn(
                    this,
                    authorities.lockIn(this, official.client.uid),
                    AuthorityName("manage"),
                    setOf(ownerId),
                    setOf(superGroupId),
                )
            }
            assertEquals(
                ClientOwner.Official,
                operation(database).delete(administrator.copy(isAdministrator = false), official.client.uid),
            )
            assertEquals(before, deletionRows(database))
            for (created in listOf(personal, official)) {
                assertNull(OAuthProtocolClients(database).serverClient(created.client.uid)?.client)
                assertFalse(clientSecretMatches(database, created.client.clientId, created.secret))
                val key = assertNotNull(created.apiCredential)
                assertNull(
                    ApiCredentialAuthenticator(
                        database,
                    ).authenticate(ApiKeyId(key.id.value), RawApiToken(key.token.value)),
                )
            }
            assertFailsWith<OAuthClientNotFound> { operation(database).delete(administrator, official.client.uid) }
        }

    @Test
    fun `an API deletion failure restores the client authorities approvals restrictions and key`() =
        withDatabase { database ->
            val created = create(database)
            database.commitTransaction {
                ClientApprovals(database).approveIn(this, ownerId, created.client.uid, created.client.scopes)
            }
            database.commitTransaction {
                val authorities = ClientAuthorities(database)
                authorities.createIn(
                    this,
                    authorities.lockIn(this, created.client.uid),
                    AuthorityName("manage"),
                    setOf(ownerId),
                    setOf(superGroupId),
                )
            }
            val before = deletionRows(database)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_key_deletion() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RAISE EXCEPTION 'key deletion rejected'; END $$;
                CREATE TRIGGER reject_key_deletion BEFORE DELETE ON g_api_key
                FOR EACH ROW EXECUTE FUNCTION reject_key_deletion();
                """.trimIndent(),
            )
            assertFailsWith<SQLException> { operation(database).delete(administrator, created.client.uid) }
            assertEquals(before, deletionRows(database))
            assertEquals(created.client, OAuthProtocolClients(database).serverClient(created.client.uid)?.client)
            assertTrue(clientSecretMatches(database, created.client.clientId, created.secret))
            val key = assertNotNull(created.apiCredential)
            assertNotNull(
                ApiCredentialAuthenticator(database).authenticate(ApiKeyId(key.id.value), RawApiToken(key.token.value)),
            )
        }

    @Test
    fun `a skipped key deletion cannot report successful client deletion`() =
        withDatabase { database ->
            val created = create(database)
            val before = deletionRows(database)
            database.executeSqlScript(
                """
                CREATE FUNCTION skip_key_deletion() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN RETURN NULL; END $$;
                CREATE TRIGGER skip_key_deletion BEFORE DELETE ON g_api_key
                FOR EACH ROW EXECUTE FUNCTION skip_key_deletion();
                """.trimIndent(),
            )
            assertFailsWith<ApiAccessNotFound> { operation(database).delete(administrator, created.client.uid) }
            assertEquals(before, deletionRows(database))
        }

    @Test
    fun `unavailable callers unrelated owners and enclosing transactions leave the client intact`() =
        withDatabase { database ->
            val personal = create(database, ClientOwner.User(ownerId))
            val before = deletionRows(database)
            val deletion = operation(database)
            assertFailsWith<AccessDenied> { deletion.delete(Actor.Anonymous, personal.client.uid) }
            assertFailsWith<AccessDenied> {
                deletion.delete(Actor.User(ActorUserId(UUID.randomUUID()), true), personal.client.uid)
            }
            database.executeSqlScript("DELETE FROM g_admin_user WHERE user_id = '${administrator.userId.value}'")
            assertFailsWith<AccessDenied> { deletion.delete(administrator, personal.client.uid) }
            database.executeSqlScript("UPDATE g_user SET locked = TRUE WHERE user_id = '${ownerId.value}'")
            assertFailsWith<AccessDenied> { deletion.delete(ownerActor, personal.client.uid) }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> { deletion.delete(ownerActor, personal.client.uid) }
            }
            assertEquals(before, deletionRows(database))
        }

    @Test
    fun `clients without keys and clients whose key was already revoked can still be deleted`() =
        withDatabase { database ->
            val before = deletionRows(database)
            val withoutKey = create(database, generateApiKey = false)
            operation(database).delete(administrator, withoutKey.client.uid)
            val revoked = create(database)
            database.commitTransaction {
                DeleteApiKey(database).deleteIn(this, ApiKeyId(assertNotNull(revoked.apiCredential).id.value))
            }
            assertNull(OAuthProtocolClients(database).serverClient(revoked.client.uid)?.client?.apiKeyId)
            operation(database).delete(administrator, revoked.client.uid)
            assertEquals(before, deletionRows(database))
        }

    @Test
    fun `a retry after client removal still deletes the client and key together`() =
        withDatabase { database ->
            val before = deletionRows(database)
            val created = create(database)
            database.executeSqlScript(
                """
                CREATE SEQUENCE key_deletion_attempts;
                CREATE FUNCTION retry_key_deletion_once() RETURNS trigger LANGUAGE plpgsql AS $$
                BEGIN
                    IF nextval('key_deletion_attempts') = 1 THEN RAISE EXCEPTION 'retry key deletion'; END IF;
                    RETURN OLD;
                END $$;
                CREATE TRIGGER retry_key_deletion_once BEFORE DELETE ON g_api_key
                FOR EACH ROW EXECUTE FUNCTION retry_key_deletion_once();
                """.trimIndent(),
            )
            assertEquals(ClientOwner.Official, operation(database).delete(administrator, created.client.uid))
            assertEquals(before, deletionRows(database))
        }

    private fun operation(database: DatabaseFactory) =
        DeleteOAuthClient(
            database,
            UserAccountAccess(database),
            DeleteClient(database),
            DeleteOwnedApiKeys(database),
        )

    private fun create(
        database: DatabaseFactory,
        owner: ClientOwner = ClientOwner.Official,
        generateApiKey: Boolean = true,
    ) = CreateOAuthClient(
        database,
        UserAccountAccess(database),
        CreateClient(database, bcryptCost = 10),
        CreateApiKey(database, bcryptCost = 10),
    ).create(
        if (owner is ClientOwner.User) Actor.User(ActorUserId(owner.userId.value)) else administrator,
        NewOAuthClient(
            RedirectUri("https://example.org/callback"),
            ClientName("Deletion test"),
            LocalizedText.of(),
            false,
            owner,
            generateApiKey = generateApiKey,
            restrictedSuperGroupIds = if (owner is ClientOwner.Official) setOf(superGroupId) else emptySet(),
        ),
    )

    private fun withDatabase(test: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres -> DatabaseFactory(postgres.dataSource).use(test) }
    }

    private companion object {
        val superGroupId: UUID = UUID.fromString("712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab")
        val ownerId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
        val ownerActor = Actor.User(ActorUserId(ownerId.value))
        val administrator = Actor.User(ActorUserId(UUID.fromString("88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f")), true)
        val staleAdministrator = Actor.User(ActorUserId(UUID.fromString("bc605869-9a4d-46ec-8a29-d00819d4c195")), true)
    }
}

private fun deletionRows(database: DatabaseFactory) =
    listOf(
        "g_client",
        "g_client_scope",
        "g_client_api_key",
        "g_api_key",
        "g_text",
        "g_user_approval",
        "g_client_authority",
        "g_client_authority_user",
        "g_client_authority_super_group",
        "g_client_restriction",
        "g_client_restriction_super_group",
    ).map(database::tableRowCount)
