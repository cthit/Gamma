package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.RotateApiKey
import it.chalmers.gamma.apiaccess.RotatedApiKey
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.AdministratorAccess

class RotateAdministrativeApiKey(
    private val database: DatabaseFactory,
    private val administrators: AdministratorAccess,
    private val rotation: RotateApiKey,
) {
    fun rotate(
        actor: Actor,
        id: ApiKeyId,
    ): RotatedApiKey {
        database.commitTransaction { administrators.requireIn(this, actor) }
        val prepared = rotation.prepare(id)
        val result =
            database.commitTransaction {
                administrators.requireIn(this, actor)
                rotation.replaceIn(this, prepared)
            }
        rotation.publishAfterCommit(prepared)
        return result
    }
}
