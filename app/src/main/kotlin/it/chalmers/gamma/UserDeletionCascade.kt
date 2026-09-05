package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.DeleteOwnedApiKeys
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.oauth.DeleteOwnedOAuthClients
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.UserDeletion
import it.chalmers.gamma.users.UserId
import org.slf4j.LoggerFactory
import org.springframework.session.FindByIndexNameSessionRepository
import java.io.IOException
import java.util.concurrent.CancellationException

sealed interface AccountDeletion {
    data class Administrator(
        val actor: Actor,
        val userId: UserId,
    ) : AccountDeletion

    data class Personal(
        val actor: Actor,
        val password: PlainTextPassword,
    ) : AccountDeletion {
        override fun toString(): String = "PersonalAccountDeletion(<redacted>)"
    }
}

class UserDeletionCascade(
    private val database: DatabaseFactory,
    private val clients: DeleteOwnedOAuthClients,
    private val apiKeys: DeleteOwnedApiKeys,
    private val users: UserDeletion,
    private val sessions: FindByIndexNameSessionRepository<*>,
    private val media: MediaStore,
) {
    // Keep verification, the complete cross-context commit, and ambiguous-commit recovery visible together.
    @Suppress("TooGenericExceptionCaught")
    fun delete(request: AccountDeletion): Boolean {
        val verified =
            when (request) {
                is AccountDeletion.Personal -> {
                    users.verifyPersonalDeletion(request.actor, request.password)
                        ?: return false
                }

                is AccountDeletion.Administrator -> {
                    null
                }
            }
        var attemptedDeletion: DeletedAccount? = null
        val deleted =
            try {
                database.commitTransaction {
                    val target =
                        when (request) {
                            is AccountDeletion.Administrator -> {
                                users.lockForAdministratorDeletion(this, request.actor, request.userId)
                            }

                            is AccountDeletion.Personal -> {
                                users.lockForPersonalDeletion(this, checkNotNull(verified))
                            }
                        }
                    val account = DeletedAccount(target.userId, target.avatarUri)
                    attemptedDeletion = account
                    val ownedKeys = clients.deleteIn(this, target.userId).mapTo(mutableSetOf()) { ApiKeyId(it.value) }
                    apiKeys.deleteIn(this, ownedKeys)
                    users.deleteIn(this, target)
                    account
                }
            } catch (failure: Exception) {
                val attempted = attemptedDeletion ?: throw failure
                val stillExists =
                    try {
                        users.exists(attempted.userId)
                    } catch (ownershipFailure: Exception) {
                        throw combineDeletionFailures(failure, ownershipFailure)
                    }
                if (stillExists) throw failure
                // The earlier attempt may have committed before its acknowledgement failed. Never
                // claim success, but finish deletion effects only after confirming the user is gone.
                throw combineDeletionFailures(failure, completeDeletion(attempted))
            }
        completeDeletion(deleted)?.let { throw it }
        return true
    }

    // Session and media cleanup are independent; failure in either must not prevent the other attempt.
    @Suppress("TooGenericExceptionCaught")
    private fun completeDeletion(account: DeletedAccount): Throwable? {
        var failure: Throwable? = null
        try {
            sessions.findByPrincipalName(account.userId.value.toString()).keys.forEach(sessions::deleteById)
        } catch (sessionFailure: Exception) {
            failure = sessionFailure
        }
        if (account.avatarUri != null) {
            try {
                media.delete(MediaUri(account.avatarUri))
            } catch (_: IOException) {
                logger.warn("Deleted account avatar cleanup failed")
            } catch (mediaFailure: Exception) {
                failure = if (failure == null) mediaFailure else combineDeletionFailures(failure, mediaFailure)
            }
        }
        return failure
    }

    private data class DeletedAccount(
        val userId: UserId,
        val avatarUri: String?,
    ) {
        override fun toString(): String = "DeletedAccount(<redacted>)"
    }

    private companion object {
        val logger: org.slf4j.Logger = LoggerFactory.getLogger(UserDeletionCascade::class.java)
    }
}

private fun combineDeletionFailures(
    first: Throwable,
    second: Throwable?,
): Throwable {
    if (second == null || second === first) return first
    if ((second is CancellationException || second is InterruptedException) &&
        first !is CancellationException && first !is InterruptedException
    ) {
        second.addSuppressed(first)
        return second
    }
    first.addSuppressed(second)
    return first
}
