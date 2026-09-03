package it.chalmers.gamma.throttling

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor

class ThrottlingAdministration(
    private val entries: ThrottleEntryStore,
) {
    fun snapshot(actor: Actor): ThrottleEntrySnapshot {
        requireAdministrator(actor)
        return entries.snapshot()
    }

    fun delete(
        actor: Actor,
        key: ThrottleKey,
    ) {
        requireAdministrator(actor)
        entries.delete(key)
    }

    private fun requireAdministrator(actor: Actor) {
        val user = actor as? Actor.User ?: throw AccessDenied()
        if (!user.isAdministrator) throw AccessDenied()
    }
}
