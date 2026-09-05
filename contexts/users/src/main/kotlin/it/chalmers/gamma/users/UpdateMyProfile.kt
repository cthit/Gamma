package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.matchesStoredVersion
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.update

data class MyProfileUpdate(
    val nick: Nick,
    val firstName: FirstName,
    val lastName: LastName,
    val language: Language?,
    val email: Email,
    val expectedVersion: Int,
) {
    init {
        require(expectedVersion >= 0) { "Expected version cannot be negative" }
    }

    override fun toString(): String = "MyProfileUpdate(<redacted>)"
}

class UpdateMyProfile(
    private val database: DatabaseFactory,
) {
    fun update(
        actor: Actor,
        input: MyProfileUpdate,
    ) {
        val user = actor as? Actor.User ?: throw AccessDenied()
        val language = requireNotNull(input.language) { "Language is required" }
        translateUserUniqueConflict {
            database.commitTransaction {
                if (!lockUserIfPresent(UserId(user.userId.value))) throw UserNotFound(USER_NOT_FOUND_MESSAGE)
                // The personal form owns these fields only. Acceptance year and lock state stay untouched.
                val changed =
                    UsersTable.update({
                        (UsersTable.id eq user.userId.value) and
                            UsersTable.version.matchesStoredVersion(input.expectedVersion)
                    }) {
                        it[nick] = input.nick.value
                        it[firstName] = input.firstName.value
                        it[lastName] = input.lastName.value
                        it[email] = input.email.value.lowercase()
                        it[UsersTable.language] = language.name
                        it[version] = input.expectedVersion + 1
                        it[updatedAt] = userPersistenceTime()
                    }
                if (changed != 1) throw UserConflict("User is missing or has been changed")
            }
        }
    }
}
