package it.chalmers.gamma.users

import it.chalmers.gamma.UserDeletionCascade
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor

class MyAccount(
    private val users: UserStore,
    private val deletion: UserDeletionCascade,
) {
    fun profile(actor: Actor): UserProfile = users.findUser(actor.userId()) ?: throw UserNotFound("User does not exist")

    fun updateProfile(
        actor: Actor,
        update: MyProfileUpdate,
    ) {
        val userId = actor.userId()
        requireNotNull(update.language) { "Language is required" }
        val currentProfile = users.findUser(userId) ?: throw UserNotFound("User does not exist")
        users.updateUser(currentProfile.withEditableFields(update))
    }

    fun changePassword(
        actor: Actor,
        currentPassword: PlainTextPassword,
        newPassword: PlainTextPassword,
        confirmedPassword: String,
    ) {
        val userId = actor.userId()
        if (newPassword.value != confirmedPassword) throw UserConflict("Passwords do not match")
        if (!users.changePassword(userId, currentPassword, newPassword)) {
            throw UserConflict("Incorrect password")
        }
    }

    fun deleteMyAccount(
        actor: Actor,
        password: PlainTextPassword,
    ): Boolean {
        val userId = actor.userId()
        if (!users.checkPassword(userId, password)) return false
        deletion.delete(userId)
        return true
    }
}

private fun UserProfile.withEditableFields(update: MyProfileUpdate): UserProfile =
    copy(
        nick = update.nick,
        firstName = update.firstName,
        lastName = update.lastName,
        acceptanceYear = acceptanceYear,
        language = update.language,
        email = update.email,
        version = update.expectedVersion,
    )

private fun Actor.userId(): UserId {
    val user = this as? Actor.User ?: throw AccessDenied()
    return UserId(user.userId.value)
}
