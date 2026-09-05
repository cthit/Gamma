package it.chalmers.gamma.throttling

/** Completes one security mutation before returning control to its caller. */
internal fun completeSecurityOperation(operation: () -> Unit) = operation()
