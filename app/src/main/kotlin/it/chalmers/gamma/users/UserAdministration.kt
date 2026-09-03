package it.chalmers.gamma.users

import it.chalmers.gamma.UserDeletionCascade
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor

class UserAdministration(
    private val users: UserStore,
    private val deletion: UserDeletionCascade,
) {
    fun user(
        actor: Actor,
        userId: UserId,
    ): UserProfile? = users.administrativeUser(actor.userId(), userId)?.profile

    fun createUser(
        actor: Actor,
        user: NewUser,
    ): UserId = users.createUserAsAdministrator(actor.userId(), user)

    fun updateUser(
        actor: Actor,
        profile: UserProfile,
    ) {
        users.updateUserAsAdministrator(actor.userId(), profile)
    }

    fun deleteUser(
        actor: Actor,
        userId: UserId,
    ) {
        actor.requireAdministrator()
        deletion.delete(userId)
    }
}

private fun Actor.userId(): UserId {
    val user = this as? Actor.User ?: throw AccessDenied()
    return UserId(user.userId.value)
}

private fun Actor.requireAdministrator() {
    val user = this as? Actor.User ?: throw AccessDenied()
    if (!user.isAdministrator) throw AccessDenied()
}
