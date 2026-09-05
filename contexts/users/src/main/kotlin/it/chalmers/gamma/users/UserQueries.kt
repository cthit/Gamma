package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.sql.Connection
import java.time.Year

class UserQueries(
    private val database: DatabaseFactory,
) {
    fun myProfile(actor: Actor): UserProfile =
        database.commitTransaction(readOnly = true) {
            val user = actor as? Actor.User ?: throw AccessDenied()
            usersWithAvatars()
                .selectAll()
                .where { UsersTable.id eq user.userId.value }
                .limit(1)
                .firstOrNull()
                ?.toUserProfile()
                ?: throw UserNotFound(USER_NOT_FOUND_MESSAGE)
        }

    fun findUser(identifier: UserIdentifier): UserProfile? =
        database.commitTransaction(readOnly = true) {
            usersWithAvatars()
                .selectAll()
                .where {
                    when (identifier) {
                        is UserId -> UsersTable.id eq identifier.value
                        is Cid -> UsersTable.cid eq identifier.value
                        is Email -> UsersTable.email.lowerCase() eq identifier.value.lowercase()
                        else -> error("Unsupported user identifier type")
                    }
                }.limit(1)
                .firstOrNull()
                ?.toUserProfile()
        }

    fun usersByIdsIn(
        transaction: JdbcTransaction,
        userIds: Set<UserId>,
    ): List<UserProfile> {
        database.requireTransaction(transaction)
        if (userIds.isEmpty()) return emptyList()
        return usersWithAvatars()
            .selectAll()
            .where { UsersTable.id inList userIds.map(UserId::value) }
            .map { it.toUserProfile() }
    }

    fun directoryUserPage(request: DirectoryUserPageRequest): DirectoryUserPage {
        val scope = request.scope
        return database.commitTransaction(
            readOnly = scope.access != DirectoryUserAccess.ADMINISTRATOR,
            isolationLevel = Connection.TRANSACTION_REPEATABLE_READ,
        ) {
            directoryUserPageIn(this, request)
        }
    }

    fun directoryUserPageIn(
        transaction: JdbcTransaction,
        request: DirectoryUserPageRequest,
    ): DirectoryUserPage {
        database.requireTransaction(transaction)
        val scope = request.scope
        if (scope.access == DirectoryUserAccess.ADMINISTRATOR) {
            transaction.requireAdministratorForRead(scope.userId)
        }
        val users = usersWithAvatars().selectAll()
        val conditions =
            listOfNotNull(
                directoryTextCondition(request.query),
                request.afterCid?.let { UsersTable.cid greater it.value },
                directoryScopeCondition(request.scope),
            )
        conditions.reduceOrNull { combined, condition -> combined and condition }?.let { condition ->
            users.where { condition }
        }
        val matchedUsers =
            users
                .orderBy(UsersTable.cid, SortOrder.ASC)
                .limit(MAXIMUM_DIRECTORY_PAGE_SIZE + 1)
                .map { it.toUserProfile() }
                .map { it.toDirectoryUser() }
        val pageUsers = matchedUsers.take(MAXIMUM_DIRECTORY_PAGE_SIZE)
        return DirectoryUserPage(
            users = pageUsers,
            nextCid = pageUsers.lastOrNull()?.cid?.takeIf { matchedUsers.size > MAXIMUM_DIRECTORY_PAGE_SIZE },
        )
    }

    fun directoryUsersByIdsIn(
        transaction: JdbcTransaction,
        userIds: Set<UserId>,
    ): List<DirectoryUser> {
        database.requireTransaction(transaction)
        if (userIds.isEmpty()) return emptyList()
        return usersWithAvatars()
            .selectAll()
            .where { UsersTable.id inList userIds.map(UserId::value) }
            .orderBy(UsersTable.cid, SortOrder.ASC)
            .map { it.toUserProfile().toDirectoryUser() }
    }

    fun findDirectoryUserIn(
        transaction: JdbcTransaction,
        userId: UserId,
    ): DirectoryUser? {
        database.requireTransaction(transaction)
        return usersWithAvatars()
            .selectAll()
            .where { UsersTable.id eq userId.value }
            .limit(1)
            .firstOrNull()
            ?.toUserProfile()
            ?.toDirectoryUser()
    }

    fun administrativeUser(
        administratorId: UserId,
        userId: UserId,
    ): AdministrativeUser? =
        database.commitTransaction(readOnly = false, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            requireAdministratorForRead(administratorId)
            val gdprTrained =
                GdprTrainedUsersTable
                    .selectAll()
                    .where { GdprTrainedUsersTable.userId eq userId.value }
                    .limit(1)
                    .any()
            usersWithAvatars()
                .selectAll()
                .where { UsersTable.id eq userId.value }
                .limit(1)
                .firstOrNull()
                ?.toUserProfile()
                ?.let { profile -> AdministrativeUser(profile, gdprTrained) }
        }

    fun administrativeUsers(administratorId: UserId): List<UserProfile> =
        database.commitTransaction(readOnly = false, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            requireAdministratorForRead(administratorId)
            usersWithAvatars()
                .selectAll()
                .orderBy(UsersTable.cid, SortOrder.ASC)
                .map { it.toUserProfile() }
        }

    fun apiUsersIn(transaction: JdbcTransaction): List<ApiUserProfile> {
        database.requireTransaction(transaction)
        val gdprTrainedUserIds =
            GdprTrainedUsersTable.selectAll().mapTo(
                mutableSetOf(),
            ) { it[GdprTrainedUsersTable.userId] }
        return usersWithAvatars()
            .selectAll()
            .orderBy(UsersTable.cid, SortOrder.ASC)
            .map { row -> row.toUserProfile().toApiUserProfile(row[UsersTable.id] in gdprTrainedUserIds) }
    }

    fun apiUsersByIdsIn(
        transaction: JdbcTransaction,
        userIds: Set<UserId>,
    ): List<ApiUserProfile> {
        database.requireTransaction(transaction)
        if (userIds.isEmpty()) return emptyList()
        val ids = userIds.map(UserId::value)
        val gdprTrainedUserIds =
            GdprTrainedUsersTable
                .selectAll()
                .where { GdprTrainedUsersTable.userId inList ids }
                .mapTo(mutableSetOf()) { it[GdprTrainedUsersTable.userId] }
        return usersWithAvatars()
            .selectAll()
            .where { UsersTable.id inList ids }
            .map { row -> row.toUserProfile().toApiUserProfile(row[UsersTable.id] in gdprTrainedUserIds) }
    }

    fun apiUserIn(
        transaction: JdbcTransaction,
        userId: UserId,
    ): ApiUserProfile? {
        database.requireTransaction(transaction)
        val gdprTrained =
            GdprTrainedUsersTable
                .selectAll()
                .where { GdprTrainedUsersTable.userId eq userId.value }
                .limit(1)
                .any()
        return usersWithAvatars()
            .selectAll()
            .where { UsersTable.id eq userId.value }
            .limit(1)
            .firstOrNull()
            ?.toUserProfile()
            ?.toApiUserProfile(gdprTrained)
    }

    private fun usersWithAvatars() =
        UsersTable.join(
            otherTable = UserAvatarsTable,
            joinType = JoinType.LEFT,
            onColumn = UsersTable.id,
            otherColumn = UserAvatarsTable.userId,
        )

    private fun directoryTextCondition(query: String): Op<Boolean>? =
        query
            .trim()
            .lowercase()
            .takeIf(String::isNotEmpty)
            ?.split(Regex("\\s+"))
            ?.take(MAXIMUM_DIRECTORY_QUERY_TERMS)
            ?.map { term ->
                val pattern = "%$term%"
                (UsersTable.cid.lowerCase() like pattern) or
                    (UsersTable.nick.lowerCase() like pattern) or
                    (UsersTable.firstName.lowerCase() like pattern) or
                    (UsersTable.lastName.lowerCase() like pattern)
            }?.reduce { combined, term -> combined and term }

    private fun directoryScopeCondition(scope: DirectoryUserScope): Op<Boolean>? =
        when (scope.access) {
            DirectoryUserAccess.ADMINISTRATOR -> {
                null
            }

            DirectoryUserAccess.VISIBLE_TO_USER -> {
                UsersTable.locked.isNull() or
                    (UsersTable.locked eq false) or
                    (UsersTable.id eq scope.userId.value)
            }
        }

    private fun ResultRow.toUserProfile(): UserProfile =
        UserProfile(
            id = UserId(this[UsersTable.id]),
            cid = Cid(this[UsersTable.cid]),
            nick = Nick(this[UsersTable.nick]),
            firstName = FirstName(this[UsersTable.firstName]),
            lastName = LastName(this[UsersTable.lastName]),
            acceptanceYear =
                AcceptanceYear.of(
                    checkNotNull(this[UsersTable.acceptanceYear]) { "User acceptance year is missing" },
                    currentYear = Year.now().value,
                ),
            language = this[UsersTable.language]?.let(Language::valueOf),
            email = Email(this[UsersTable.email]),
            version = this[UsersTable.version] ?: 0,
            locked = this[UsersTable.locked] == true,
            avatarUri = this[UserAvatarsTable.avatarUri],
        )

    private fun UserProfile.toDirectoryUser() =
        DirectoryUser(id, cid, nick, firstName, lastName, acceptanceYear, version, locked)

    private fun UserProfile.toApiUserProfile(gdprTrained: Boolean) =
        ApiUserProfile(id, cid, nick, firstName, lastName, acceptanceYear, email, locked, gdprTrained)

    private companion object {
        const val MAXIMUM_DIRECTORY_QUERY_TERMS = 5
    }
}
