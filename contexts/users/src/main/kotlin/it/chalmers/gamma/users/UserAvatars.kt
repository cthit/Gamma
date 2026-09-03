package it.chalmers.gamma.users

import it.chalmers.gamma.media.MediaObjectId
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor

class UserAvatars(
    private val users: UserStore,
    private val media: MediaStore,
) {
    @Suppress("TooGenericExceptionCaught")
    fun replaceMyAvatar(
        actor: Actor,
        upload: UserAvatarUpload,
    ) {
        val userId = actor.userId()
        val operationId = UserAvatarOperationId.generate()
        val avatar = saveAvatar(operationId, upload)
        val previousAvatar =
            try {
                users.replaceAvatar(userId, operationId, avatar)
            } catch (failure: Exception) {
                completeFailedReplacement(failure, userId, operationId, avatar)
            }
        completeCommittedChange(previousAvatar)
    }

    @Suppress("TooGenericExceptionCaught")
    fun deleteMyAvatar(actor: Actor) {
        val userId = actor.userId()
        val oldAvatar = users.currentAvatar(userId)
        try {
            users.clearAvatar(userId, oldAvatar)
        } catch (failure: Exception) {
            completeFailedDeletion(failure, oldAvatar) { users.currentAvatar(userId) }
        }
        completeCommittedDeletion(oldAvatar)
    }

    @Suppress("TooGenericExceptionCaught")
    fun deleteUserAvatarAsAdministrator(
        actor: Actor,
        userId: UserId,
    ) {
        val administratorId = actor.userId()
        val oldAvatar = users.currentAvatarAsAdministrator(administratorId, userId)
        try {
            users.clearAvatarAsAdministrator(administratorId, userId, oldAvatar)
        } catch (failure: Exception) {
            completeFailedDeletion(failure, oldAvatar) {
                users.currentAvatarAsAdministrator(administratorId, userId)
            }
        }
        completeCommittedDeletion(oldAvatar)
    }

    // Save failures are ambiguous: storage may have written operation-derived content before failing.
    @Suppress("TooGenericExceptionCaught")
    private fun saveAvatar(
        operationId: UserAvatarOperationId,
        upload: UserAvatarUpload,
    ): StoredUserAvatar =
        try {
            StoredUserAvatar(
                media.save(MediaObjectId(operationId.value), upload.bytes, upload.declaredContentType).value,
            )
        } catch (failure: Exception) {
            completeAfterFailure(failure) { media.delete(MediaObjectId(operationId.value)) }
        }

    // Persistence failures can be ambiguous. Resolve the pointer before deleting operation-owned media.
    @Suppress("TooGenericExceptionCaught")
    private fun completeFailedReplacement(
        failure: Throwable,
        userId: UserId,
        operationId: UserAvatarOperationId,
        savedAvatar: StoredUserAvatar,
    ): Nothing {
        if (failure is UserNotFound) {
            completeAfterFailure(failure) { media.delete(MediaObjectId(operationId.value)) }
        }

        val currentAvatar =
            try {
                users.currentAvatar(userId)
            } catch (completionFailure: Exception) {
                throw preferredFailure(failure, completionFailure)
            }
        if (currentAvatar != savedAvatar) {
            try {
                media.delete(MediaObjectId(operationId.value))
            } catch (completionFailure: Exception) {
                throw preferredFailure(failure, completionFailure)
            }
        }
        throw failure
    }

    // UserNotFound, UserConflict, and AccessDenied are raised before a clear commits. Any
    // other return failure is ambiguous, so resolve the pointer before deciding
    // whether the captured immutable object is no longer referenced by this user.
    @Suppress("TooGenericExceptionCaught")
    private fun completeFailedDeletion(
        failure: Throwable,
        oldAvatar: StoredUserAvatar?,
        resolveCurrentAvatar: () -> StoredUserAvatar?,
    ): Nothing {
        if (failure is UserNotFound || failure is UserConflict || failure is AccessDenied) {
            throw failure
        }

        val currentAvatar =
            try {
                resolveCurrentAvatar()
            } catch (completionFailure: Exception) {
                throw preferredFailure(failure, completionFailure)
            }
        if (oldAvatar != null && currentAvatar != oldAvatar) {
            try {
                media.delete(MediaUri(oldAvatar.uri))
            } catch (completionFailure: Exception) {
                throw preferredFailure(failure, completionFailure)
            }
        }
        throw failure
    }

    private fun completeCommittedDeletion(oldAvatar: StoredUserAvatar?) {
        completeCommittedChange(oldAvatar)
    }

    @Suppress("TooGenericExceptionCaught")
    private fun completeAfterFailure(
        failure: Throwable,
        completion: () -> Unit,
    ): Nothing {
        try {
            completion()
        } catch (completionFailure: Exception) {
            throw preferredFailure(failure, completionFailure)
        }
        throw failure
    }

    private fun completeCommittedChange(oldAvatar: StoredUserAvatar?) {
        if (oldAvatar != null) media.delete(MediaUri(oldAvatar.uri))
    }
}

private fun preferredFailure(
    operationFailure: Throwable,
    completionFailure: Throwable,
): Throwable {
    if (completionFailure !== operationFailure) operationFailure.addSuppressed(completionFailure)
    return operationFailure
}

private fun Actor.userId(): UserId {
    val user = this as? Actor.User ?: throw AccessDenied()
    return UserId(user.userId.value)
}
