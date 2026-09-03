package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

internal class UserAccessFlags(
    private val database: DatabaseFactory,
) {
    fun list(
        administratorId: UserId,
        kind: UserAccessFlagKind,
    ): List<UserAccessFlag> =
        database.transaction {
            requireAdministratorForRead(administratorId)
            val enabledUserIds = assignedUserIds(kind).toHashSet()
            UsersTable
                .selectAll()
                .orderBy(UsersTable.cid, SortOrder.ASC)
                .map { row -> row.toUserAccessFlag(row[UsersTable.id] in enabledUserIds) }
        }

    fun replace(
        administratorId: UserId,
        kind: UserAccessFlagKind,
        selectedUserIds: Set<UserId>,
    ) {
        database.transaction {
            // Authorization must precede request validation: a demoted caller must not learn
            // whether selected users exist or which invariant their request violates.
            requireAdministrator(administratorId)
            val knownUserIds =
                UsersTable
                    .selectAll()
                    .forUpdate()
                    .mapTo(mutableSetOf()) { UserId(it[UsersTable.id]) }
            val administratorIds = assignedUserIds(UserAccessFlagKind.ADMINISTRATOR, lockForUpdate = true)
            if (kind == UserAccessFlagKind.ADMINISTRATOR && selectedUserIds.isEmpty()) {
                throw UserConflict("At least one administrator must remain")
            }
            if (!knownUserIds.containsAll(selectedUserIds)) {
                throw UserNotFound("Selected user does not exist")
            }

            val assignedUserIds =
                if (kind == UserAccessFlagKind.ADMINISTRATOR) {
                    administratorIds.mapTo(mutableSetOf(), ::UserId)
                } else {
                    assignedUserIds(kind, lockForUpdate = true).mapTo(mutableSetOf(), ::UserId)
                }
            val additions = selectedUserIds - assignedUserIds
            val removals = assignedUserIds - selectedUserIds

            // Promotions precede demotions so the final-administrator invariant is never
            // transiently violated, regardless of directory ordering.
            addAssignments(kind, additions)
            removeAssignments(kind, removals)
        }
    }

    private fun JdbcTransaction.assignedUserIds(
        kind: UserAccessFlagKind,
        lockForUpdate: Boolean = false,
    ): List<UUID> {
        val query =
            when (kind) {
                UserAccessFlagKind.ADMINISTRATOR -> AdminUsersTable.selectAll()
                UserAccessFlagKind.GDPR_TRAINED -> GdprTrainedUsersTable.selectAll()
            }
        if (lockForUpdate) query.forUpdate()
        return when (kind) {
            UserAccessFlagKind.ADMINISTRATOR -> query.map { it[AdminUsersTable.userId] }
            UserAccessFlagKind.GDPR_TRAINED -> query.map { it[GdprTrainedUsersTable.userId] }
        }
    }

    private fun JdbcTransaction.addAssignments(
        kind: UserAccessFlagKind,
        userIds: Set<UserId>,
    ) {
        val createdAt = LocalDateTime.now(ZoneOffset.UTC)
        userIds.forEach { userId ->
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
    }

    private fun JdbcTransaction.removeAssignments(
        kind: UserAccessFlagKind,
        userIds: Set<UserId>,
    ) {
        if (userIds.isEmpty()) return
        val rawUserIds = userIds.map(UserId::value)
        when (kind) {
            UserAccessFlagKind.ADMINISTRATOR -> {
                AdminUsersTable.deleteWhere { AdminUsersTable.userId inList rawUserIds }
            }

            UserAccessFlagKind.GDPR_TRAINED -> {
                GdprTrainedUsersTable.deleteWhere { GdprTrainedUsersTable.userId inList rawUserIds }
            }
        }
    }

    private fun ResultRow.toUserAccessFlag(enabled: Boolean): UserAccessFlag =
        UserAccessFlag(
            userId = UserId(this[UsersTable.id]),
            firstName = FirstName(this[UsersTable.firstName]),
            nick = Nick(this[UsersTable.nick]),
            lastName = LastName(this[UsersTable.lastName]),
            enabled = enabled,
        )
}
