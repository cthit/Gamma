package it.chalmers.gamma.users

import java.util.UUID

class UserAuthentication(
    private val users: UserStore,
) {
    fun authenticate(
        identifier: UserIdentifier,
        password: PlainTextPassword,
    ): UserProfile? {
        val user = users.findUser(identifier)
        val passwordUserId = user?.id ?: MISSING_USER_ID
        val passwordMatches = users.checkPassword(passwordUserId, password)
        val currentUser = users.findUser(passwordUserId)
        if (user == null || !passwordMatches) return null
        if (currentUser == null || currentUser.id != user.id || currentUser.locked) return null
        return currentUser
    }

    fun sessionUserExists(userId: UserId): Boolean = users.findUser(userId) != null

    fun verifyCurrentPassword(
        userId: UserId,
        password: PlainTextPassword,
    ): Boolean {
        val user = users.findUser(userId) ?: return false
        return !user.locked && users.checkPassword(userId, password)
    }
}

private val MISSING_USER_ID = UserId(UUID(0, 0))
