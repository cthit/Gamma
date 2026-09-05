package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class UpdateMyEmail(
    private val database: DatabaseFactory,
) {
    fun update(
        actor: Actor,
        email: Email,
    ) {
        val user = actor as? Actor.User ?: throw AccessDenied()
        translateUserUniqueConflict {
            database.commitTransaction {
                // This form sends no version. Serialize email changes and preserve all other profile fields.
                val current =
                    UsersTable
                        .selectAll()
                        .where { UsersTable.id eq user.userId.value }
                        .forUpdate()
                        .limit(1)
                        .firstOrNull()
                        ?: throw UserNotFound(USER_NOT_FOUND_MESSAGE)
                requireNotNull(current[UsersTable.language]) { "Language is required" }
                val changed =
                    UsersTable.update({ UsersTable.id eq user.userId.value }) {
                        it[UsersTable.email] = email.value.lowercase()
                        it[version] = (current[UsersTable.version] ?: 0) + 1
                        it[updatedAt] = userPersistenceTime()
                    }
                check(changed == 1) { "Locked user disappeared before email update" }
            }
        }
    }
}
