package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeySettingsUpdate
import it.chalmers.gamma.apiaccess.ReplaceApiKeySettings
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.AdministratorAccess

class UpdateApiKeySettings(
    private val database: DatabaseFactory,
    private val administrators: AdministratorAccess,
    private val settings: ReplaceApiKeySettings,
) {
    fun update(
        actor: Actor,
        id: ApiKeyId,
        change: ApiKeySettingsUpdate,
    ) {
        database.commitTransaction {
            administrators.requireIn(this, actor)
            settings.replaceIn(this, id, change)
        }
    }
}
