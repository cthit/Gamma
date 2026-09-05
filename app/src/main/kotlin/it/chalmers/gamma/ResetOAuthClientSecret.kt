package it.chalmers.gamma

import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.RotateClientSecret
import it.chalmers.gamma.oauth.RotatedClientSecret
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.UserAccountAccess
import java.util.UUID
import java.util.concurrent.CancellationException

class ResetOAuthClientSecret(
    private val database: DatabaseFactory,
    private val accounts: UserAccountAccess,
    private val rotation: RotateClientSecret,
) {
    // Reservation, external preparation, current permission checks, and failure cleanup form one operation.
    @Suppress("TooGenericExceptionCaught")
    fun reset(
        actor: Actor,
        uid: ClientUid,
    ): RotatedClientSecret {
        val reservationId = UUID.randomUUID()
        try {
            val reservation =
                database.commitTransaction {
                    val account = accounts.requireIn(this, actor)
                    val target = rotation.lockIn(this, uid)
                    val ownsClient = (target.owner as? ClientOwner.User)?.userId == account.userId
                    if (!ownsClient && !account.isAdministrator) throw AccessDenied()
                    rotation.reserveIn(this, target, reservationId)
                }
            val prepared = rotation.prepare(reservation)
            return database.commitTransaction {
                val account = accounts.requireIn(this, actor)
                val target = rotation.lockIn(this, uid)
                val ownsClient = (target.owner as? ClientOwner.User)?.userId == account.userId
                if (!ownsClient && !account.isAdministrator) throw AccessDenied()
                rotation.replaceIn(this, target, prepared)
            }
        } catch (failure: Exception) {
            try {
                rotation.release(uid, reservationId)
            } catch (cleanupFailure: Exception) {
                if (cleanupFailure is CancellationException || cleanupFailure is InterruptedException) {
                    cleanupFailure.addSuppressed(failure)
                    throw cleanupFailure
                }
                failure.addSuppressed(cleanupFailure)
            }
            throw failure
        }
    }
}
