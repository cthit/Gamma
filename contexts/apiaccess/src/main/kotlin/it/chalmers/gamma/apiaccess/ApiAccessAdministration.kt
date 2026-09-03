package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.LocalizedText

class ApiAccessAdministration(
    private val apiKeys: ApiKeyStore,
) {
    fun listApiKeys(actor: Actor): List<ApiKey> {
        requireAdministrator(actor)
        return apiKeys.listApiKeys()
    }

    fun findApiKey(
        actor: Actor,
        id: ApiKeyId,
    ): ApiKey? {
        requireAdministrator(actor)
        return apiKeys.findApiKey(id)
    }

    fun infoSettings(
        actor: Actor,
        id: ApiKeyId,
    ): ApiKeyInfoSettings? {
        requireAdministrator(actor)
        val apiKey = apiKeys.findApiKey(id) ?: return null
        requireType(apiKey, ApiKeyType.INFO)
        return apiKeys.infoSettings(id) ?: throw ApiAccessNotFound("API key settings do not exist")
    }

    fun accountScaffoldSettings(
        actor: Actor,
        id: ApiKeyId,
    ): ApiKeyAccountScaffoldSettings? {
        requireAdministrator(actor)
        val apiKey = apiKeys.findApiKey(id) ?: return null
        requireType(apiKey, ApiKeyType.ACCOUNT_SCAFFOLD)
        return apiKeys.accountScaffoldSettings(id) ?: throw ApiAccessNotFound("API key settings do not exist")
    }

    fun createApiKey(
        actor: Actor,
        name: ApiKeyName,
        description: LocalizedText,
        type: ApiKeyType,
    ): CreatedApiKey {
        requireAdministrator(actor)
        require(type != ApiKeyType.CLIENT) {
            "Cannot create api key with type client without creating a client at the same time"
        }
        return apiKeys.createApiKey(name, description, type)
    }

    fun resetToken(
        actor: Actor,
        id: ApiKeyId,
    ): RawApiToken {
        requireAdministrator(actor)
        return apiKeys.resetToken(id)
    }

    fun updateInfoSettings(
        actor: Actor,
        id: ApiKeyId,
        settings: ApiKeyInfoSettings,
    ) {
        requireAdministrator(actor)
        val apiKey = apiKeys.findApiKey(id) ?: throw ApiAccessNotFound("API key does not exist")
        requireType(apiKey, ApiKeyType.INFO)
        apiKeys.infoSettings(id) ?: throw ApiAccessNotFound("API key settings do not exist")
        apiKeys.updateInfoSettings(id, settings)
    }

    fun updateAccountScaffoldSettings(
        actor: Actor,
        id: ApiKeyId,
        settings: ApiKeyAccountScaffoldSettings,
    ) {
        requireAdministrator(actor)
        val apiKey = apiKeys.findApiKey(id) ?: throw ApiAccessNotFound("API key does not exist")
        requireType(apiKey, ApiKeyType.ACCOUNT_SCAFFOLD)
        apiKeys.accountScaffoldSettings(id) ?: throw ApiAccessNotFound("API key settings do not exist")
        apiKeys.updateAccountScaffoldSettings(id, settings)
    }

    fun deleteApiKey(
        actor: Actor,
        id: ApiKeyId,
    ) {
        requireAdministrator(actor)
        apiKeys.deleteApiKey(id)
    }

    private fun requireAdministrator(actor: Actor) {
        val user = actor as? Actor.User ?: throw AccessDenied()
        if (!user.isAdministrator) throw AccessDenied()
    }

    @Suppress("TooGenericExceptionThrown") // Preserve the legacy settings endpoint's error contract.
    private fun requireType(
        apiKey: ApiKey,
        expectedType: ApiKeyType,
    ) {
        if (apiKey.type != expectedType) throw RuntimeException("Unexpected api key type")
    }
}

class ApiCredentialAuthenticator(
    private val credentials: ApiKeyStore,
) {
    fun authenticate(
        id: ApiKeyId,
        token: RawApiToken,
    ): ApiKey? {
        val credential = credentials.findApiKey(id) ?: return null
        return credentials.authenticate(credential.type, id, token)
    }
}
