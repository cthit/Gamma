package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.SuperGroupId
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.UserQueries
import java.sql.Connection

internal class OAuthClientAccess(
    private val database: DatabaseFactory,
    private val clients: OAuthProtocolClients,
    private val users: UserQueries,
    private val memberships: OrganizationQueries,
) {
    fun allowed(
        userId: UserId,
        clientUid: ClientUid,
    ): Boolean =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val user = users.findDirectoryUserIn(this, userId) ?: return@commitTransaction false
            if (user.locked) return@commitTransaction false
            val restrictions = clients.restrictionsIn(this, clientUid) ?: return@commitTransaction false
            if (restrictions.isEmpty()) return@commitTransaction true
            memberships.isMemberOfAnySuperGroupIn(this, userId, restrictions.mapTo(mutableSetOf(), ::SuperGroupId))
        }
}
