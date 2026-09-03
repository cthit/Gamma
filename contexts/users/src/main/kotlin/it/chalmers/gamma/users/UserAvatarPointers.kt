package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.time.LocalDateTime
import java.time.ZoneOffset

internal class UserAvatarPointers(
    private val database: DatabaseFactory,
) {
    fun replaceAvatar(
        userId: UserId,
        operationId: UserAvatarOperationId,
        avatar: StoredUserAvatar,
    ): StoredUserAvatar? =
        database.transaction {
            require(avatar.uri.startsWith("${operationId.value}.")) {
                "Stored avatar does not belong to its upload operation"
            }
            replaceUserAvatarPointer(
                userId,
                avatar.uri,
                now(),
                UserAvatarWriteCondition.Unconditional,
            )?.let(::StoredUserAvatar)
        }

    fun currentAvatar(userId: UserId): StoredUserAvatar? = database.transaction { currentAvatar(userId) }

    fun currentAvatarAsAdministrator(
        administratorId: UserId,
        userId: UserId,
    ): StoredUserAvatar? =
        database.transaction {
            requireAdministrator(administratorId)
            currentAvatar(userId)
        }

    fun clearAvatar(
        userId: UserId,
        expectedAvatar: StoredUserAvatar?,
    ) {
        database.transaction { clearAvatar(userId, expectedAvatar) }
    }

    fun clearAvatarAsAdministrator(
        administratorId: UserId,
        userId: UserId,
        expectedAvatar: StoredUserAvatar?,
    ) {
        database.transaction {
            requireAdministrator(administratorId)
            clearAvatar(userId, expectedAvatar)
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

    private fun JdbcTransaction.clearAvatar(
        userId: UserId,
        expectedAvatar: StoredUserAvatar?,
    ) {
        replaceUserAvatarPointer(
            userId,
            uri = null,
            now(),
            UserAvatarWriteCondition.CurrentUri(expectedAvatar?.uri),
        )
    }

    private fun now(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
}
