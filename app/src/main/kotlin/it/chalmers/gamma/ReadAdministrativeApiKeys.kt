package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKey
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.UserAccountAccess
import java.sql.Connection

class ReadAdministrativeApiKeys(
    private val database: DatabaseFactory,
    private val accounts: UserAccountAccess,
    private val keys: ApiKeyQueries,
) {
    // Current account authorization takes locks; the metadata is read in that protected snapshot.
    fun listApiKeys(actor: Actor): List<ApiKey> =
        database.commitTransaction(readOnly = false, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val account = accounts.requireIn(this, actor)
            if (!account.isAdministrator) throw AccessDenied()
            keys.listApiKeysIn(this)
        }

    fun findApiKey(
        actor: Actor,
        id: ApiKeyId,
    ): ApiKey? =
        database.commitTransaction(readOnly = false, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val account = accounts.requireIn(this, actor)
            if (!account.isAdministrator) throw AccessDenied()
            keys.findApiKeyIn(this, id)
        }
}
