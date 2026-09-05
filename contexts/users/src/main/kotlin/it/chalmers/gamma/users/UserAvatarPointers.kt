package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDateTime
import java.time.ZoneOffset

internal class UserAvatarPointers(
    private val database: DatabaseFactory,
) {
    fun replaceAvatar(
        userId: UserId,
        operationId: UserAvatarOperationId,
        avatar: StoredUserAvatar,
        expectedAvatar: StoredUserAvatar?,
    ): StoredUserAvatar? =
        database.commitTransaction {
            require(avatar.uri.startsWith("${operationId.value}.")) {
                "Stored avatar does not belong to its upload operation"
            }
            replaceUserAvatarPointer(
                userId,
                avatar.uri,
                LocalDateTime.now(ZoneOffset.UTC),
                expectedAvatar?.uri,
            )?.let(::StoredUserAvatar)
        }

    fun readForOwner(userId: UserId): StoredUserAvatar? =
        database.commitTransaction {
            if (!lockUserIfPresent(userId)) throw UserNotFound(USER_NOT_FOUND_MESSAGE)
            currentAvatar(userId)
        }

    // Internal ownership lookup also works after user deletion or administrator demotion.
    fun currentAvatar(userId: UserId): StoredUserAvatar? = database.commitTransaction { currentAvatar(userId) }

    fun currentAvatarAsAdministrator(
        administratorId: UserId,
        userId: UserId,
    ): StoredUserAvatar? =
        database.commitTransaction {
            requireAdministrator(administratorId)
            if (!lockUserIfPresent(userId)) throw UserNotFound(USER_NOT_FOUND_MESSAGE)
            currentAvatar(userId)
        }

    fun clearAvatar(
        userId: UserId,
        expectedAvatar: StoredUserAvatar?,
    ) {
        database.commitTransaction {
            replaceUserAvatarPointer(userId, null, LocalDateTime.now(ZoneOffset.UTC), expectedAvatar?.uri)
        }
    }

    fun clearAvatarAsAdministrator(
        administratorId: UserId,
        userId: UserId,
        expectedAvatar: StoredUserAvatar?,
    ) {
        database.commitTransaction {
            requireAdministrator(administratorId)
            replaceUserAvatarPointer(userId, null, LocalDateTime.now(ZoneOffset.UTC), expectedAvatar?.uri)
        }
    }

    private fun JdbcTransaction.currentAvatar(userId: UserId): StoredUserAvatar? =
        UserAvatarsTable
            .selectAll()
            .where { UserAvatarsTable.userId eq userId.value }
            .limit(1)
            .firstOrNull()
            ?.get(UserAvatarsTable.avatarUri)
            ?.let(::StoredUserAvatar)
}

private fun JdbcTransaction.replaceUserAvatarPointer(
    userId: UserId,
    uri: String?,
    now: LocalDateTime,
    expectedUri: String?,
): String? {
    require(uri == null || uri.length <= 255) { "Avatar URI is too long" }
    val user =
        UsersTable
            .selectAll()
            .where { UsersTable.id eq userId.value }
            .forUpdate()
            .limit(1)
            .firstOrNull()
            ?: throw UserNotFound(USER_NOT_FOUND_MESSAGE)
    val previousAvatar =
        UserAvatarsTable
            .selectAll()
            .where { UserAvatarsTable.userId eq userId.value }
            .limit(1)
            .firstOrNull()
            ?.get(UserAvatarsTable.avatarUri)
    if (previousAvatar != expectedUri) {
        throw UserConflict("User avatar has been changed")
    }
    UserAvatarsTable.deleteWhere { UserAvatarsTable.userId eq userId.value }
    if (uri != null) {
        UserAvatarsTable.insert {
            it[UserAvatarsTable.userId] = userId.value
            it[avatarUri] = uri
            it[version] = 0
            it[createdAt] = now
            it[updatedAt] = now
        }
    }
    val currentVersion = user[UsersTable.version] ?: 0
    UsersTable.update({ UsersTable.id eq userId.value }) {
        it[UsersTable.version] = currentVersion + 1
        it[updatedAt] = now
    }
    return previousAvatar
}
