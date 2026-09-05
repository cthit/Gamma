package it.chalmers.gamma.users

import it.chalmers.gamma.media.MediaObjectId
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import java.util.concurrent.CancellationException

class UserAvatars(
    database: DatabaseFactory,
    private val media: MediaStore,
) {
    private val pointers = UserAvatarPointers(database)

    // Keep upload, committed pointer, and compensation together so object ownership stays visible.
    @Suppress("TooGenericExceptionCaught")
    fun replaceMyAvatar(
        actor: Actor,
        upload: UserAvatarUpload,
    ) {
        val userId = actor.userId()
        val oldAvatar = pointers.readForOwner(userId)
        val operationId = UserAvatarOperationId.generate()
        val saved =
            try {
                StoredUserAvatar(
                    media.save(MediaObjectId(operationId.value), upload.bytes, upload.declaredContentType).value,
                )
            } catch (failure: Exception) {
                // Storage may have written the operation's bytes before reporting failure.
                val cleanupFailure = runAvatarCleanup { media.delete(MediaObjectId(operationId.value)) }
                throw combineAvatarFailures(failure, cleanupFailure)
            }

        try {
            pointers.replaceAvatar(userId, operationId, saved, oldAvatar)
        } catch (failure: Exception) {
            // A retry conflict can follow an earlier committed attempt. Never infer ownership from the error type.
            val current =
                try {
                    pointers.currentAvatar(userId)
                } catch (ownershipFailure: Exception) {
                    // If ownership cannot be established, keep both objects.
                    throw combineAvatarFailures(failure, ownershipFailure)
                }
            var cleanupFailure: Throwable? = null
            if (current != saved) {
                cleanupFailure = runAvatarCleanup { media.delete(MediaObjectId(operationId.value)) }
            }
            if (oldAvatar != null && current != oldAvatar) {
                val oldCleanupFailure = runAvatarCleanup { media.delete(MediaUri(oldAvatar.uri)) }
                cleanupFailure =
                    if (cleanupFailure == null) {
                        oldCleanupFailure
                    } else {
                        combineAvatarFailures(cleanupFailure, oldCleanupFailure)
                    }
            }
            throw combineAvatarFailures(failure, cleanupFailure)
        }
        if (oldAvatar != null) media.delete(MediaUri(oldAvatar.uri))
    }

    @Suppress("TooGenericExceptionCaught")
    fun deleteMyAvatar(actor: Actor) {
        val userId = actor.userId()
        val oldAvatar = pointers.readForOwner(userId)
        try {
            pointers.clearAvatar(userId, oldAvatar)
        } catch (failure: Exception) {
            val current =
                try {
                    pointers.currentAvatar(userId)
                } catch (ownershipFailure: Exception) {
                    throw combineAvatarFailures(failure, ownershipFailure)
                }
            val cleanupFailure =
                if (oldAvatar != null && current != oldAvatar) {
                    runAvatarCleanup { media.delete(MediaUri(oldAvatar.uri)) }
                } else {
                    null
                }
            throw combineAvatarFailures(failure, cleanupFailure)
        }
        if (oldAvatar != null) media.delete(MediaUri(oldAvatar.uri))
    }

    @Suppress("TooGenericExceptionCaught")
    fun deleteUserAvatarAsAdministrator(
        actor: Actor,
        userId: UserId,
    ) {
        val administratorId = actor.userId()
        val oldAvatar = pointers.currentAvatarAsAdministrator(administratorId, userId)
        try {
            pointers.clearAvatarAsAdministrator(administratorId, userId, oldAvatar)
        } catch (failure: Exception) {
            val current =
                try {
                    // Internal compensation must still resolve ownership if the administrator was demoted.
                    pointers.currentAvatar(userId)
                } catch (ownershipFailure: Exception) {
                    throw combineAvatarFailures(failure, ownershipFailure)
                }
            val cleanupFailure =
                if (oldAvatar != null && current != oldAvatar) {
                    runAvatarCleanup { media.delete(MediaUri(oldAvatar.uri)) }
                } else {
                    null
                }
            throw combineAvatarFailures(failure, cleanupFailure)
        }
        if (oldAvatar != null) media.delete(MediaUri(oldAvatar.uri))
    }
}

// Shared only for failure precedence: an ordinary error must never conceal cancellation or interruption.
@Suppress("TooGenericExceptionCaught")
private fun runAvatarCleanup(cleanup: () -> Unit): Throwable? =
    try {
        cleanup()
        null
    } catch (failure: Exception) {
        failure
    }

private fun combineAvatarFailures(
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

private fun Actor.userId(): UserId {
    val user = this as? Actor.User ?: throw AccessDenied()
    return UserId(user.userId.value)
}
