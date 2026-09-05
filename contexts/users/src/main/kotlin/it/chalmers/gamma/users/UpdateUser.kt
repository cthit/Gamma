package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.matchesStoredVersion
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.update

data class UserUpdate(
    val userId: UserId,
    val expectedVersion: Int,
    val nick: Nick,
    val firstName: FirstName,
    val lastName: LastName,
    val acceptanceYear: AcceptanceYear,
    val language: Language?,
    val email: Email,
) {
    init {
        require(expectedVersion >= 0) { "Expected version cannot be negative" }
    }

    override fun toString(): String = "UserUpdate(<redacted>)"
}

class UpdateUser(
    private val database: DatabaseFactory,
) {
    fun update(
        actor: Actor,
        input: UserUpdate,
    ) {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        translateUserUniqueConflict {
            database.commitTransaction {
                // Order authorization with demotion before revealing anything about the target.
                requireAdministrator(UserId(administrator.userId.value))
                val changed =
                    UsersTable.update({
                        (UsersTable.id eq input.userId.value) and
                            UsersTable.version.matchesStoredVersion(input.expectedVersion)
                    }) {
                        it[nick] = input.nick.value
                        it[firstName] = input.firstName.value
                        it[lastName] = input.lastName.value
                        it[acceptanceYear] = input.acceptanceYear.value
                        it[language] = input.language?.name
                        it[email] = input.email.value.lowercase()
                        it[version] = input.expectedVersion + 1
                        it[updatedAt] = userPersistenceTime()
                    }
                if (changed != 1) throw UserConflict("User is missing or has been changed")
            }
        }
    }
}
