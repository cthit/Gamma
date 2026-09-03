package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor

class UserAccessAdministration(
    private val users: UserStore,
) {
    fun accessFlags(
        actor: Actor,
        kind: UserAccessFlagKind,
    ): List<UserAccessFlag> = users.listAccessFlags(actor.userId(), kind)

    fun replaceAccessFlags(
        actor: Actor,
        kind: UserAccessFlagKind,
        selectedUserIds: Set<UserId>,
    ) {
        users.replaceAccessFlags(actor.userId(), kind, selectedUserIds)
    }
}

private fun Actor.userId(): UserId {
    val user = this as? Actor.User ?: throw AccessDenied()
    return UserId(user.userId.value)
}
