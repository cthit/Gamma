package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyName
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.apiaccess.CreatedApiKey
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.AdministratorAccess

class CreateAdministrativeApiKey(
    private val database: DatabaseFactory,
    private val administrators: AdministratorAccess,
    private val creation: CreateApiKey,
) {
    fun create(
        actor: Actor,
        name: ApiKeyName,
        description: LocalizedText,
        type: ApiKeyType,
    ): CreatedApiKey {
        database.commitTransaction { administrators.requireIn(this, actor) }
        require(type != ApiKeyType.CLIENT) {
            "Cannot create api key with type client without creating a client at the same time"
        }
        val prepared = creation.prepare(name, description, type)
        val created =
            database.commitTransaction {
                administrators.requireIn(this, actor)
                creation.insertIn(this, prepared)
            }
        creation.publishAfterCommit(prepared)
        return created
    }
}
