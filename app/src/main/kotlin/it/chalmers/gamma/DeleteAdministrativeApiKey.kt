package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.DeleteApiKey
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.AdministratorAccess

class DeleteAdministrativeApiKey(
    private val database: DatabaseFactory,
    private val administrators: AdministratorAccess,
    private val deletion: DeleteApiKey,
) {
    fun delete(
        actor: Actor,
        id: ApiKeyId,
    ) {
        database.commitTransaction {
            administrators.requireIn(this, actor)
            deletion.deleteIn(this, id)
        }
    }
}
