package it.chalmers.gamma.organization

import it.chalmers.gamma.media.MediaObjectId
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import java.util.concurrent.CancellationException

class GroupImages(
    database: DatabaseFactory,
    private val media: MediaStore,
) {
    private val pointers = GroupImagePointers(database)

    // Keep the upload, committed pointer, and compensation decisions together; any phase can fail.
    @Suppress("TooGenericExceptionCaught")
    fun replace(
        actor: Actor,
        groupId: GroupId,
        kind: GroupImageKind,
        upload: GroupImageUpload,
    ) {
        val oldImage = pointers.readForEditor(actor, groupId, kind)?.let(::StoredGroupImage)
        val operationId = MediaObjectId(GroupImageOperationId.generate().value)
        val saved =
            try {
                StoredGroupImage(media.save(operationId, upload.bytes, upload.declaredContentType).value)
            } catch (failure: Exception) {
                // Save may have written bytes before reporting failure; only this operation owns them.
                val cleanupFailure = runGroupImageCompletion { media.delete(operationId) }
                throw combineGroupImageFailures(failure, cleanupFailure)
            }

        try {
            pointers.change(actor, GroupImageChange(groupId, kind, oldImage?.uri, saved.uri))
        } catch (failure: Exception) {
            // Even a CAS conflict can follow an earlier committed attempt whose acknowledgement failed.
            // Read the actual pointer before deleting either immutable object. If that read fails, retain both.
            val current =
                try {
                    pointers.current(groupId, kind)
                } catch (ownershipFailure: Exception) {
                    throw combineGroupImageFailures(failure, ownershipFailure)
                }
            var completionFailure: Throwable? = null
            if (current != saved.uri) {
                completionFailure = runGroupImageCompletion { media.delete(operationId) }
            }
            if (oldImage != null && current != oldImage.uri) {
                val cleanupFailure = runGroupImageCompletion { media.delete(MediaUri(oldImage.uri)) }
                completionFailure =
                    if (completionFailure == null) {
                        cleanupFailure
                    } else {
                        combineGroupImageFailures(completionFailure, cleanupFailure)
                    }
            }
            throw combineGroupImageFailures(failure, completionFailure)
        }

        if (oldImage != null) {
            try {
                media.delete(MediaUri(oldImage.uri))
            } catch (failure: CancellationException) {
                throw failure
            } catch (failure: InterruptedException) {
                throw failure
            } catch (_: Exception) {
                // The new pointer committed; ordinary best-effort cleanup failures do not undo that result.
            }
        }
    }

    // A failed acknowledgement can hide a committed clear; resolve ownership before deleting bytes.
    @Suppress("TooGenericExceptionCaught")
    fun delete(
        actor: Actor,
        groupId: GroupId,
        kind: GroupImageKind,
    ) {
        val oldImage = pointers.readForEditor(actor, groupId, kind)?.let(::StoredGroupImage)
        try {
            pointers.change(actor, GroupImageChange(groupId, kind, oldImage?.uri, null))
        } catch (failure: Exception) {
            val current =
                try {
                    pointers.current(groupId, kind)
                } catch (ownershipFailure: Exception) {
                    throw combineGroupImageFailures(failure, ownershipFailure)
                }
            val completionFailure =
                if (oldImage != null && current != oldImage.uri) {
                    runGroupImageCompletion { media.delete(MediaUri(oldImage.uri)) }
                } else {
                    null
                }
            throw combineGroupImageFailures(failure, completionFailure)
        }
        if (oldImage != null) {
            try {
                media.delete(MediaUri(oldImage.uri))
            } catch (failure: CancellationException) {
                throw failure
            } catch (failure: InterruptedException) {
                throw failure
            } catch (_: Exception) {
                // Clearing the pointer already committed; preserve that result for ordinary cleanup failures.
            }
        }
    }
}

// Cleanup must be attempted without replacing the original failure or swallowing a control-flow exception.
@Suppress("TooGenericExceptionCaught")
private fun runGroupImageCompletion(completion: () -> Unit): Throwable? =
    try {
        completion()
        null
    } catch (failure: Exception) {
        failure
    }

private fun combineGroupImageFailures(
    firstFailure: Throwable,
    secondFailure: Throwable?,
): Throwable {
    if (secondFailure == null || secondFailure === firstFailure) return firstFailure
    if ((secondFailure is CancellationException || secondFailure is InterruptedException) &&
        firstFailure !is CancellationException && firstFailure !is InterruptedException
    ) {
        secondFailure.addSuppressed(firstFailure)
        return secondFailure
    }
    firstFailure.addSuppressed(secondFailure)
    return firstFailure
}
