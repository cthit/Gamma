package it.chalmers.gamma.testing

internal object OwnedResources {
    // Resource ownership must be restored for every failure, including Errors.
    @Suppress("TooGenericExceptionCaught")
    fun <Owner : AutoCloseable, Dependent : AutoCloseable> acquire(
        owner: Owner,
        startOwner: (Owner) -> Unit,
        acquireDependent: (Owner) -> Dependent,
        initialize: (Dependent) -> Unit,
    ): Dependent {
        var dependent: Dependent? = null
        try {
            startOwner(owner)
            val acquiredDependent = acquireDependent(owner)
            dependent = acquiredDependent
            initialize(acquiredDependent)
            return acquiredDependent
        } catch (failure: Throwable) {
            close(dependent, owner, failure)
            throw failure
        }
    }

    fun close(
        dependent: AutoCloseable?,
        owner: AutoCloseable?,
    ) {
        close(dependent, owner, failure = null)
    }

    private fun close(
        dependent: AutoCloseable?,
        owner: AutoCloseable?,
        failure: Throwable?,
    ) {
        var primaryFailure = failure
        primaryFailure = closeResource(dependent, primaryFailure)
        primaryFailure = closeResource(owner, primaryFailure)
        if (failure == null && primaryFailure != null) {
            throw primaryFailure
        }
    }

    // Cleanup must retain Throwable identity and suppression order across all failure types.
    @Suppress("TooGenericExceptionCaught")
    private fun closeResource(
        resource: AutoCloseable?,
        primaryFailure: Throwable?,
    ): Throwable? {
        if (resource == null) return primaryFailure
        return try {
            resource.close()
            primaryFailure
        } catch (cleanupFailure: Throwable) {
            if (primaryFailure == null) {
                cleanupFailure
            } else {
                if (cleanupFailure !== primaryFailure) {
                    primaryFailure.addSuppressed(cleanupFailure)
                }
                primaryFailure
            }
        }
    }
}
