package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.users.PasswordResetToken

class PasswordResetAdministration(
    private val passwordResets: PasswordResets,
) {
    fun create(
        actor: Actor,
        userId: UserId,
    ): PasswordResetToken = passwordResets.create(actor.userId(), userId)
}

private fun Actor.userId(): UserId {
    val user = this as? Actor.User ?: throw AccessDenied()
    return UserId(user.userId.value)
}
