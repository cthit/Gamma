package it.chalmers.gamma.oauth

import it.chalmers.gamma.apiaccess.ApiKeyName
import it.chalmers.gamma.apiaccess.ClientCredentialId
import it.chalmers.gamma.apiaccess.OAuthClientCredentials
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import java.util.UUID

data class OAuthApiCredentialSummary(
    val id: OAuthApiKeyId,
    val name: String,
    val type: String,
    val swedishDescription: String,
    val englishDescription: String,
)

class OAuthClientAdministration(
    private val clients: OAuthClientStore,
    private val apiCredentials: OAuthClientCredentials,
) {
    private companion object {
        const val CLIENT_DOES_NOT_EXIST = "Client does not exist"
    }

    fun listOfficialClients(actor: Actor): List<OAuthClient> {
        requireAdministrator(actor)
        return clients.listClients().filter { it.owner is ClientOwner.Official }
    }

    fun listMyClients(actor: Actor): List<OAuthClient> = clients.listClients(actor.userId())

    fun listPersonalClientsForAdministration(actor: Actor): List<OAuthClient> {
        requireAdministrator(actor)
        return clients.listClients().filter { it.owner is ClientOwner.User }
    }

    fun approvedClients(actor: Actor): List<OAuthClient> = clients.approvedClients(actor.userId())

    fun authorities(
        actor: Actor,
        uid: ClientUid,
    ): List<ClientAuthority> {
        requireCanManage(actor, clients.findClient(uid) ?: throw OAuthClientNotFound(CLIENT_DOES_NOT_EXIST))
        return clients.authorities(uid)
    }

    fun approvedUserIds(
        actor: Actor,
        uid: ClientUid,
    ): List<UserId> {
        requireCanManage(actor, clients.findClient(uid) ?: throw OAuthClientNotFound(CLIENT_DOES_NOT_EXIST))
        return clients.approvedUserIds(uid)
    }

    fun apiCredential(
        actor: Actor,
        uid: ClientUid,
    ): OAuthApiCredentialSummary? {
        val client = clients.findClient(uid) ?: throw OAuthClientNotFound(CLIENT_DOES_NOT_EXIST)
        requireCanManage(actor, client)
        return client.apiKeyId?.let { id ->
            apiCredentials.find(ClientCredentialId(id.value))?.let { credential ->
                OAuthApiCredentialSummary(
                    id = id,
                    name = credential.name.value,
                    type = "CLIENT",
                    swedishDescription = credential.description.sv.value,
                    englishDescription = credential.description.en.value,
                )
            }
        }
    }

    fun createOfficialClient(
        actor: Actor,
        input: NewOAuthClient,
    ): CreatedOAuthClient {
        requireAdministrator(actor)
        if (input.owner !is ClientOwner.Official) throw AccessDenied()
        return createClient(input)
    }

    fun createMyClient(
        actor: Actor,
        input: NewOAuthClient,
    ): CreatedOAuthClient {
        val userId = actor.userId()
        if ((input.owner as? ClientOwner.User)?.userId != userId) throw AccessDenied()
        require(input.restrictedSuperGroupIds.isEmpty()) { "user client cannot have restrictions" }
        return createClient(input)
    }

    fun manageableClient(
        actor: Actor,
        uid: ClientUid,
    ): OAuthClient {
        val client = clients.findClient(uid) ?: throw OAuthClientNotFound(CLIENT_DOES_NOT_EXIST)
        requireCanManage(actor, client)
        return client
    }

    fun deleteClient(
        actor: Actor,
        uid: ClientUid,
    ) {
        val client = clients.findClient(uid) ?: throw OAuthClientNotFound(CLIENT_DOES_NOT_EXIST)
        requireCanManage(actor, client)

        // API Access owns the credential and OAuth owns the client/link. Revoke first so a failed
        // OAuth deletion leaves a discoverable client linked to an unusable credential. The
        // idempotent revocation lets a later request finish that deletion safely.
        client.apiKeyId?.let { apiCredentials.revokeIfPresent(ClientCredentialId(it.value)) }
        clients.deleteClient(uid)
    }

    fun resetSecret(
        actor: Actor,
        uid: ClientUid,
    ): RawClientSecret {
        requireCanManage(actor, clients.findClient(uid) ?: throw OAuthClientNotFound(CLIENT_DOES_NOT_EXIST))
        return clients.resetSecret(uid)
    }

    fun resetApiCredential(
        actor: Actor,
        uid: ClientUid,
    ): String {
        val client = clients.findClient(uid) ?: throw OAuthClientNotFound(CLIENT_DOES_NOT_EXIST)
        requireCanManage(actor, client)
        val apiKeyId = client.apiKeyId ?: throw OAuthClientNotFound("Client has no API key")
        return "${apiKeyId.value}:${apiCredentials.rotate(ClientCredentialId(apiKeyId.value)).value}"
    }

    fun createAuthority(
        actor: Actor,
        clientUid: ClientUid,
        name: AuthorityName,
        userIds: Set<UserId>,
        superGroupIds: Set<UUID>,
    ) {
        requireCanManage(
            actor,
            clients.findClient(clientUid) ?: throw OAuthClientNotFound(CLIENT_DOES_NOT_EXIST),
        )
        clients.createAuthority(clientUid, name, userIds, superGroupIds)
    }

    fun deleteAuthority(
        actor: Actor,
        clientUid: ClientUid,
        name: AuthorityName,
    ) {
        requireCanManage(
            actor,
            clients.findClient(clientUid) ?: throw OAuthClientNotFound(CLIENT_DOES_NOT_EXIST),
        )
        clients.deleteAuthority(clientUid, name)
    }

    fun revokeMyApproval(
        actor: Actor,
        clientUid: ClientUid,
    ) {
        clients.revokeApproval(actor.userId(), clientUid)
    }

    // Client persistence is an adapter boundary with intentionally untyped failures. Once API
    // Access has issued a credential, every persistence failure must revoke it before escaping.
    @Suppress("TooGenericExceptionCaught")
    private fun createClient(input: NewOAuthClient): CreatedOAuthClient {
        if (!input.generateApiKey) return clients.createClient(input)

        val description =
            LocalizedText.of(
                "Api nyckel för klienten: ${input.name.value}",
                "Api key for client: ${input.name.value}",
            )
        val issued = apiCredentials.issue(ApiKeyName(input.name.value), description)
        val credential =
            ClientApiCredential(
                OAuthApiKeyId(issued.id.value),
                OAuthApiToken(issued.token.value),
            )
        return try {
            clients.createClient(input, credential)
        } catch (failure: Exception) {
            // The issued credential must be removed when client persistence fails.
            val revocationFailure = runCredentialRevocation { apiCredentials.revokeIfPresent(issued.id) }
            if (revocationFailure == null) throw failure
            throw combineCredentialFailures(failure, revocationFailure)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun runCredentialRevocation(revoke: () -> Unit): Throwable? =
        try {
            revoke()
            null
        } catch (failure: Exception) {
            failure
        }

    private fun combineCredentialFailures(
        first: Throwable,
        second: Throwable,
    ): Throwable {
        if (second !== first) first.addSuppressed(second)
        return first
    }

    private fun requireCanManage(
        actor: Actor,
        client: OAuthClient,
    ) {
        val userId = actor.userId()
        val ownsClient = (client.owner as? ClientOwner.User)?.userId == userId
        val user = actor as Actor.User
        if (!ownsClient && !user.isAdministrator) throw AccessDenied()
    }

    private fun requireAdministrator(actor: Actor) {
        val user = actor as? Actor.User ?: throw AccessDenied()
        if (!user.isAdministrator) throw AccessDenied()
    }

    private fun Actor.userId(): UserId {
        val user = this as? Actor.User ?: throw AccessDenied()
        return UserId(user.userId.value)
    }
}
