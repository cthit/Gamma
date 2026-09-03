package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.matchesStoredVersion
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

internal class UserCommands(
    private val database: DatabaseFactory,
    private val passwordHasher: PasswordHasher,
    private val registrations: UserCredentials,
) {
    fun createUserAsAdministrator(
        administratorId: UserId,
        input: NewUser,
    ): UserId {
        database.transaction { requireAdministratorForRead(administratorId) }
        val registration = registrations.prepareRegistration(input)
        return translateUserUniqueConflict {
            database.transaction {
                requireAdministrator(administratorId)
                lockActivationReservation(registration.cid)
                lockAllowListReservation(registration.cid)
                requireUserAvailable(registration)

                deleteActivationReservation(registration.cid)
                deleteAllowListReservation(registration.cid)
                insertUserRow(registration)
            }
        }
    }

    fun updateUser(profile: UserProfile) {
        translateUserUniqueConflict {
            database.transaction { updateUserRow(profile) }
        }
    }

    fun updateUserAsAdministrator(
        administratorId: UserId,
        profile: UserProfile,
    ) {
        translateUserUniqueConflict {
            database.transaction {
                requireAdministrator(administratorId)
                lockedStateForUpdate(profile)
                updateUserRow(profile)
            }
        }
    }

    fun checkPassword(
        userId: UserId,
        password: PlainTextPassword,
    ): Boolean =
        database.findPasswordUser(userId)?.passwordHash.let { hash ->
            if (hash == null) {
                passwordHasher.verifyAgainstDummy(password)
                false
            } else {
                passwordHasher.verify(password, hash)
            }
        }

    fun changePassword(
        userId: UserId,
        currentPassword: PlainTextPassword,
        newPassword: PlainTextPassword,
    ): Boolean {
        val change = preparePasswordChange(userId, currentPassword, newPassword) ?: return false
        persistAuthenticatedPasswordChange(change)
        return true
    }

    fun deleteUser(userId: UserId): String? =
        database.transaction {
            lockAdministratorAssignments()
            val userExists =
                UsersTable
                    .selectAll()
                    .where { UsersTable.id eq userId.value }
                    .forUpdate()
                    .limit(1)
                    .any()
            if (!userExists) throw UserNotFound(USER_NOT_FOUND_MESSAGE)
            requireNotFinalAdministrator(userId, "delete")
            val avatarUri =
                UserAvatarsTable
                    .selectAll()
                    .where { UserAvatarsTable.userId eq userId.value }
                    .limit(1)
                    .firstOrNull()
                    ?.get(UserAvatarsTable.avatarUri)
            if (UsersTable.deleteWhere { UsersTable.id eq userId.value } != 1) {
                throw UserNotFound(USER_NOT_FOUND_MESSAGE)
            }
            avatarUri
        }

    private fun preparePasswordChange(
        userId: UserId,
        currentPassword: PlainTextPassword,
        newPassword: PlainTextPassword,
    ): PreparedPasswordChange? {
        val passwordUser = database.findPasswordUser(userId)
        val currentPasswordMatches =
            passwordUser?.passwordHash.let { passwordHash ->
                if (passwordHash == null) {
                    passwordHasher.verifyAgainstDummy(currentPassword)
                    false
                } else {
                    passwordHasher.verify(currentPassword, passwordHash)
                }
            }
        if (!currentPasswordMatches) return null

        val authenticatedUser = checkNotNull(passwordUser)
        return PreparedPasswordChange(
            userId = userId,
            passwordHash = passwordHasher.hash(newPassword),
            expectedVersion = authenticatedUser.version,
        )
    }

    private fun JdbcTransaction.updateUserRow(profile: UserProfile) {
        val changed =
            UsersTable.update(
                where = {
                    (UsersTable.id eq profile.id.value) and
                        (UsersTable.cid eq profile.cid.value) and
                        UsersTable.version.matchesStoredVersion(profile.version)
                },
            ) {
                it[nick] = profile.nick.value
                it[firstName] = profile.firstName.value
                it[lastName] = profile.lastName.value
                it[email] = profile.email.value.lowercase()
                it[language] = profile.language?.name
                it[acceptanceYear] = profile.acceptanceYear.value
                it[locked] = profile.locked
                it[version] = profile.version + 1
                it[updatedAt] = userPersistenceTime()
            }
        if (changed != 1) throw UserConflict("User is missing or has been changed")
    }

    private fun JdbcTransaction.lockedStateForUpdate(profile: UserProfile): Boolean {
        val user =
            UsersTable
                .selectAll()
                .where {
                    (UsersTable.id eq profile.id.value) and
                        UsersTable.version.matchesStoredVersion(profile.version)
                }.forUpdate()
                .limit(1)
                .firstOrNull()
                ?: throw UserConflict("User is missing or has been changed")
        return user[UsersTable.locked] == true
    }

    private fun persistAuthenticatedPasswordChange(change: PreparedPasswordChange) {
        database.transaction {
            val credentialIsCurrent =
                UsersTable
                    .selectAll()
                    .where {
                        (UsersTable.id eq change.userId.value) and
                            UsersTable.version.matchesStoredVersion(change.expectedVersion)
                    }.forUpdate()
                    .limit(1)
                    .any()
            if (!credentialIsCurrent) {
                throw UserConflict("Credentials changed while setting the password")
            }

            val changed =
                UsersTable.update(
                    where = {
                        (UsersTable.id eq change.userId.value) and
                            UsersTable.version.matchesStoredVersion(change.expectedVersion)
                    },
                ) {
                    it[UsersTable.password] = change.passwordHash.value
                    it[version] = change.expectedVersion + 1
                    it[updatedAt] = userPersistenceTime()
                }
            check(changed == 1) { "Locked credential version disappeared before update" }
        }
    }
}
