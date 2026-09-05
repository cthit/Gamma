package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyName
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.oauth.ClientApiCredential
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.CreateClient
import it.chalmers.gamma.oauth.CreatedOAuthClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthApiKeyId
import it.chalmers.gamma.oauth.OAuthApiToken
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.UserAccountAccess

class CreateOAuthClient(
    private val database: DatabaseFactory,
    private val accounts: UserAccountAccess,
    private val clients: CreateClient,
    private val apiKeys: CreateApiKey,
) {
    fun create(
        actor: Actor,
        input: NewOAuthClient,
    ): CreatedOAuthClient {
        val frozen = input.copy(restrictedSuperGroupIds = input.restrictedSuperGroupIds.toSet())
        database.commitTransaction {
            val account = accounts.requireIn(this, actor)
            when (val owner = frozen.owner) {
                ClientOwner.Official -> if (!account.isAdministrator) throw AccessDenied()
                is ClientOwner.User -> if (owner.userId != account.userId) throw AccessDenied()
            }
        }
        require(frozen.owner is ClientOwner.Official || frozen.restrictedSuperGroupIds.isEmpty()) {
            "user client cannot have restrictions"
        }
        val preparedClient = clients.prepare(frozen)
        val preparedKey =
            if (frozen.generateApiKey) {
                apiKeys.prepare(
                    ApiKeyName(frozen.name.value),
                    LocalizedText.of(
                        "Api nyckel för klienten: ${frozen.name.value}",
                        "Api key for client: ${frozen.name.value}",
                    ),
                    ApiKeyType.CLIENT,
                )
            } else {
                null
            }
        val result =
            database.commitTransaction {
                val account = accounts.requireIn(this, actor)
                when (val owner = frozen.owner) {
                    ClientOwner.Official -> if (!account.isAdministrator) throw AccessDenied()
                    is ClientOwner.User -> if (owner.userId != account.userId) throw AccessDenied()
                }
                val credential =
                    if (preparedKey != null) {
                        val key = apiKeys.insertIn(this, preparedKey)
                        ClientApiCredential(OAuthApiKeyId(key.apiKey.id.value), OAuthApiToken(key.token.value))
                    } else {
                        null
                    }
                clients.insertIn(this, preparedClient, credential)
            }
        if (preparedKey != null) apiKeys.publishAfterCommit(preparedKey)
        return result
    }
}
