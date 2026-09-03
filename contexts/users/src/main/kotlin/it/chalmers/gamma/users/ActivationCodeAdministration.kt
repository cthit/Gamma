package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.users.PendingActivation

class ActivationCodeAdministration(
    private val activationCodes: ActivationCodes,
) {
    fun allowCid(
        actor: Actor,
        cid: Cid,
    ) = activationCodes.allow(actor.userId(), cid)

    fun retractCid(
        actor: Actor,
        cid: Cid,
    ) = activationCodes.retract(actor.userId(), cid)

    fun deleteActivation(
        actor: Actor,
        cid: Cid,
    ) = activationCodes.delete(actor.userId(), cid)

    fun allowedCids(actor: Actor): List<Cid> = activationCodes.allowedCids(actor.userId())

    fun pendingActivations(actor: Actor): List<PendingActivation> = activationCodes.pendingActivations(actor.userId())
}

private fun Actor.userId(): UserId {
    val user = this as? Actor.User ?: throw AccessDenied()
    return UserId(user.userId.value)
}
