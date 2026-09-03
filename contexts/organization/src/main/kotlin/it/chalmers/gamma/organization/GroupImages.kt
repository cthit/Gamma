package it.chalmers.gamma.organization

import it.chalmers.gamma.media.MediaObjectId
import it.chalmers.gamma.media.MediaStore
import it.chalmers.gamma.media.MediaUri
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.UserId

class GroupImages(
    private val organizations: OrganizationStore,
    private val media: MediaStore,
) {
    @Suppress("TooGenericExceptionCaught")
    fun replace(
        actor: Actor,
        groupId: GroupId,
        kind: GroupImageKind,
        upload: GroupImageUpload,
    ) {
        requireGroupEditor(actor, groupId)
        val group = organizations.findGroup(groupId) ?: throw OrganizationNotFound("Group does not exist")
        val oldImage = group.image(kind)?.let(::StoredGroupImage)
        val operationId = GroupImageOperationId.generate()
        val saved = save(operationId, upload)
        try {
            persist(groupId, kind, oldImage?.uri, saved.uri)
        } catch (failure: Exception) {
            completeFailedReplacement(
                failure,
                FailedGroupImageReplacement(operationId, groupId, kind, saved, oldImage),
            )
        }
        completeCommittedChange(oldImage)
    }

    @Suppress("TooGenericExceptionCaught")
    fun delete(
        actor: Actor,
        groupId: GroupId,
        kind: GroupImageKind,
    ) {
        requireGroupEditor(actor, groupId)
        val group = organizations.findGroup(groupId) ?: throw OrganizationNotFound("Group does not exist")
        val oldImage = group.image(kind)?.let(::StoredGroupImage)
        try {
            persist(groupId, kind, oldImage?.uri, null)
        } catch (failure: Exception) {
            completeFailedDeletion(failure, groupId, kind, oldImage)
        }
        completeCommittedChange(oldImage)
    }

    private fun requireGroupEditor(
        actor: Actor,
        groupId: GroupId,
    ) {
        val user = actor as? Actor.User ?: throw AccessDenied()
        val userId = UserId(user.userId.value)
        if (user.isAdministrator) return
        if (organizations.membershipsForGroup(groupId).none { it.userId == userId }) throw AccessDenied()
    }

    private fun persist(
        groupId: GroupId,
        kind: GroupImageKind,
        expectedUri: String?,
        replacementUri: String?,
    ) {
        organizations.compareAndSetGroupImage(GroupImageChange(groupId, kind, expectedUri, replacementUri))
    }

    // Save failures are ambiguous: the storage adapter may have created operation-owned bytes
    // immediately before another failure became visible.
    @Suppress("TooGenericExceptionCaught")
    private fun save(
        operationId: GroupImageOperationId,
        upload: GroupImageUpload,
    ): StoredGroupImage =
        try {
            StoredGroupImage(
                media.save(MediaObjectId(operationId.value), upload.bytes, upload.declaredContentType).value,
            )
        } catch (failure: Exception) {
            val cleanupFailure =
                runGroupImageCompletion { media.delete(MediaObjectId(operationId.value)) }
            throw checkNotNull(combineGroupImageFailures(failure, cleanupFailure))
        }

    // Persistence failures can be ambiguous. Resolve ownership before compensation so a committed
    // image is never mistaken for abandoned media.
    @Suppress("TooGenericExceptionCaught")
    private fun completeFailedReplacement(
        failure: Throwable,
        replacement: FailedGroupImageReplacement,
    ): Nothing {
        if (failure is OrganizationConflict) {
            val cleanupFailure =
                runGroupImageCompletion { media.delete(MediaObjectId(replacement.operationId.value)) }
            throw checkNotNull(combineGroupImageFailures(failure, cleanupFailure))
        }

        var completionFailure: Throwable? = null
        val currentImage =
            try {
                organizations.findGroup(replacement.groupId)?.image(replacement.kind)
            } catch (ownershipFailure: Exception) {
                completionFailure = combineGroupImageFailures(completionFailure, ownershipFailure)
                null
            }

        if (completionFailure == null) {
            when (currentImage) {
                replacement.saved.uri -> {
                    // The pointer committed before the failure was delivered; remove the displaced image.
                }

                replacement.oldImage?.uri -> {
                    val cleanupFailure =
                        runGroupImageCompletion {
                            media.delete(
                                MediaObjectId(replacement.operationId.value),
                            )
                        }
                    completionFailure = combineGroupImageFailures(completionFailure, cleanupFailure)
                }

                else -> {
                    // A third pointer means this write either lost immediately or committed and was
                    // then superseded. Neither staged media nor the captured old image is current.
                    val stagedCleanupFailure =
                        runGroupImageCompletion {
                            media.delete(
                                MediaObjectId(replacement.operationId.value),
                            )
                        }
                    completionFailure = combineGroupImageFailures(completionFailure, stagedCleanupFailure)
                }
            }

            if (replacement.oldImage != null && currentImage != replacement.oldImage.uri) {
                val cleanupFailure =
                    runGroupImageCompletion {
                        media.delete(MediaUri(replacement.oldImage.uri))
                    }
                completionFailure = combineGroupImageFailures(completionFailure, cleanupFailure)
            }
        }

        throw checkNotNull(combineGroupImageFailures(failure, completionFailure))
    }

    // A failed return from persistence is ambiguous unless the CAS reported a conflict. Re-read
    // the pointer before deleting the captured immutable object: a null or different pointer means
    // this clear committed (or was superseded), while the captured URI still being current means it
    // must remain available.
    @Suppress("TooGenericExceptionCaught")
    private fun completeFailedDeletion(
        failure: Throwable,
        groupId: GroupId,
        kind: GroupImageKind,
        oldImage: StoredGroupImage?,
    ): Nothing {
        if (failure is OrganizationConflict) {
            throw failure
        }

        var currentImage: String? = null
        var ownershipResolved = false
        var completionFailure: Throwable? = null
        try {
            currentImage = organizations.findGroup(groupId)?.image(kind)
            ownershipResolved = true
        } catch (ownershipFailure: Exception) {
            completionFailure = combineGroupImageFailures(completionFailure, ownershipFailure)
        }

        if (ownershipResolved && oldImage != null && currentImage != oldImage.uri) {
            val cleanupFailure =
                runGroupImageCompletion {
                    media.delete(MediaUri(oldImage.uri))
                }
            completionFailure = combineGroupImageFailures(completionFailure, cleanupFailure)
        }

        throw checkNotNull(combineGroupImageFailures(failure, completionFailure))
    }

    @Suppress("TooGenericExceptionCaught")
    private fun completeCommittedChange(oldImage: StoredGroupImage?) {
        if (oldImage != null) {
            try {
                media.delete(MediaUri(oldImage.uri))
            } catch (_: Exception) {
                // The database change is committed; a failed best-effort file deletion must not report it as failed.
            }
        }
    }

    private fun Group.image(kind: GroupImageKind): String? =
        when (kind) {
            GroupImageKind.AVATAR -> avatarUri
            GroupImageKind.BANNER -> bannerUri
        }
}

private data class FailedGroupImageReplacement(
    val operationId: GroupImageOperationId,
    val groupId: GroupId,
    val kind: GroupImageKind,
    val saved: StoredGroupImage,
    val oldImage: StoredGroupImage?,
)

@Suppress("TooGenericExceptionCaught")
private fun runGroupImageCompletion(completion: () -> Unit): Throwable? =
    try {
        completion()
        null
    } catch (failure: Exception) {
        failure
    }

private fun combineGroupImageFailures(
    firstFailure: Throwable?,
    secondFailure: Throwable?,
): Throwable? {
    if (firstFailure == null) return secondFailure
    if (secondFailure == null) return firstFailure
    if (secondFailure !== firstFailure) firstFailure.addSuppressed(secondFailure)
    return firstFailure
}
