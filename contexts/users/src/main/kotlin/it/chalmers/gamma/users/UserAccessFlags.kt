package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

class UserAccessFlags(
    private val database: DatabaseFactory,
) {
    fun list(
        actor: Actor,
        kind: UserAccessFlagKind,
    ): List<UserAccessFlag> {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        // The authorization lock needs a writable transaction even though this operation only reads.
        return database.commitTransaction {
            requireAdministratorForRead(UserId(administrator.userId.value))
            val enabledUserIds =
                when (kind) {
                    UserAccessFlagKind.ADMINISTRATOR -> {
                        AdminUsersTable
                            .selectAll()
                            .mapTo(mutableSetOf()) { it[AdminUsersTable.userId] }
                    }

                    UserAccessFlagKind.GDPR_TRAINED -> {
                        GdprTrainedUsersTable.selectAll().mapTo(mutableSetOf()) {
                            it[GdprTrainedUsersTable.userId]
                        }
                    }
                }
            UsersTable.selectAll().orderBy(UsersTable.cid, SortOrder.ASC).map { row ->
                UserAccessFlag(
                    userId = UserId(row[UsersTable.id]),
                    firstName = FirstName(row[UsersTable.firstName]),
                    nick = Nick(row[UsersTable.nick]),
                    lastName = LastName(row[UsersTable.lastName]),
                    enabled = row[UsersTable.id] in enabledUserIds,
                )
            }
        }
    }

    // Authorization, population validation, promotions, and demotions are one atomic access change.
    @Suppress("LongMethod")
    fun replace(
        actor: Actor,
        kind: UserAccessFlagKind,
        selectedUserIds: Set<UserId>,
    ) {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        database.commitTransaction {
            // A demoted caller must not learn whether selected users exist or which invariant they violate.
            requireAdministrator(UserId(administrator.userId.value))
            val knownUserIds = UsersTable.selectAll().forUpdate().mapTo(mutableSetOf()) { UserId(it[UsersTable.id]) }
            val administratorIds =
                AdminUsersTable.selectAll().forUpdate().mapTo(mutableSetOf()) {
                    UserId(it[AdminUsersTable.userId])
                }
            if (kind == UserAccessFlagKind.ADMINISTRATOR && selectedUserIds.isEmpty()) {
                throw UserConflict("At least one administrator must remain")
            }
            if (!knownUserIds.containsAll(selectedUserIds)) throw UserNotFound("Selected user does not exist")

            val assignedUserIds =
                when (kind) {
                    UserAccessFlagKind.ADMINISTRATOR -> {
                        administratorIds
                    }

                    UserAccessFlagKind.GDPR_TRAINED -> {
                        GdprTrainedUsersTable
                            .selectAll()
                            .forUpdate()
                            .mapTo(mutableSetOf()) { UserId(it[GdprTrainedUsersTable.userId]) }
                    }
                }
            val additions = selectedUserIds - assignedUserIds
            val removals = (assignedUserIds - selectedUserIds).map(UserId::value)
            val createdAt = userPersistenceTime()

            // Promote before demoting so rotating responsibility never leaves zero administrators.
            for (userId in additions) {
                when (kind) {
                    UserAccessFlagKind.ADMINISTRATOR -> {
                        AdminUsersTable.insert {
                            it[AdminUsersTable.userId] = userId.value
                            it[AdminUsersTable.createdAt] = createdAt
                        }
                    }

                    UserAccessFlagKind.GDPR_TRAINED -> {
                        GdprTrainedUsersTable.insert {
                            it[GdprTrainedUsersTable.userId] = userId.value
                            it[GdprTrainedUsersTable.createdAt] = createdAt
                        }
                    }
                }
            }
            if (removals.isNotEmpty()) {
                when (kind) {
                    UserAccessFlagKind.ADMINISTRATOR -> AdminUsersTable.deleteWhere { userId inList removals }
                    UserAccessFlagKind.GDPR_TRAINED -> GdprTrainedUsersTable.deleteWhere { userId inList removals }
                }
            }
        }
    }
}
