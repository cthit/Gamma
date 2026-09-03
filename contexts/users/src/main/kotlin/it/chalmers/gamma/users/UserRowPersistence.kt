package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.matchesStoredVersion
import it.chalmers.gamma.users.ActivationsTable
import it.chalmers.gamma.users.AllowListTable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDateTime
import java.time.ZoneOffset

internal fun JdbcTransaction.requireUserAvailable(registration: PreparedUserRegistration) {
    if (userCidExists(registration.cid)) throw UserConflict("CID is already in use")
    requireUserEmailAvailable(registration.email)
}

internal fun JdbcTransaction.lockActivationReservation(cid: Cid) {
    ActivationsTable
        .selectAll()
        .where { ActivationsTable.cid eq cid.value }
        .forUpdate()
        .limit(1)
        .any()
}

internal fun JdbcTransaction.lockAllowListReservation(cid: Cid) {
    AllowListTable
        .selectAll()
        .where { AllowListTable.cid eq cid.value }
        .forUpdate()
        .limit(1)
        .any()
}

internal fun JdbcTransaction.deleteActivationReservation(cid: Cid) {
    ActivationsTable.deleteWhere { ActivationsTable.cid eq cid.value }
}

internal fun JdbcTransaction.deleteAllowListReservation(cid: Cid) {
    AllowListTable.deleteWhere { AllowListTable.cid eq cid.value }
}

internal fun JdbcTransaction.requireUserEmailAvailable(email: Email) {
    if (UsersTable
            .selectAll()
            .where { UsersTable.email.lowerCase() eq email.value }
            .limit(1)
            .any()
    ) {
        throw UserConflict("Email is already in use")
    }
}

internal fun JdbcTransaction.insertUserRow(registration: PreparedUserRegistration): UserId {
    val id = UserId.generate()
    val now = userPersistenceTime()
    UsersTable.insert {
        it[UsersTable.id] = id.value
        it[cid] = registration.cid.value
        it[password] = registration.passwordHash.value
        it[nick] = registration.nick.value
        it[firstName] = registration.firstName.value
        it[lastName] = registration.lastName.value
        it[email] = registration.email.value
        it[language] = registration.language?.name
        it[userAgreementAccepted] = now
        it[acceptanceYear] = registration.acceptanceYear.value
        it[version] = 0
        it[locked] = false
        it[createdAt] = now
        it[updatedAt] = now
    }
    return id
}

internal fun JdbcTransaction.existingAdministratorBootstrapResult(): AdministratorBootstrapResult? =
    when {
        AdminUsersTable.selectAll().limit(1).any() -> AdministratorBootstrapResult.ALREADY_CONFIGURED

        UsersTable
            .selectAll()
            .where { UsersTable.cid eq ADMINISTRATOR_CID }
            .limit(1)
            .any() -> AdministratorBootstrapResult.ADMIN_CID_IN_USE

        else -> null
    }

internal fun DatabaseFactory.loadPasswordUser(userId: UserId): PasswordUser =
    findPasswordUser(userId) ?: throw UserNotFound(USER_NOT_FOUND_MESSAGE)

internal fun DatabaseFactory.findPasswordUser(userId: UserId): PasswordUser? =
    transaction(readOnly = true) {
        UsersTable
            .selectAll()
            .where { UsersTable.id eq userId.value }
            .limit(1)
            .firstOrNull()
            ?.let { user ->
                PasswordUser(
                    cid = user[UsersTable.cid],
                    nick = user[UsersTable.nick],
                    emailLocalPart = user[UsersTable.email].substringBefore('@'),
                    passwordHash = user[UsersTable.password]?.let(::PasswordHash),
                    version = user[UsersTable.version] ?: 0,
                )
            }
    }

internal fun JdbcTransaction.updatePasswordIfCurrent(change: PreparedPasswordChange): Boolean =
    UsersTable.update(
        where = {
            (UsersTable.id eq change.userId.value) and
                UsersTable.version.matchesStoredVersion(change.expectedVersion)
        },
    ) {
        it[UsersTable.password] = change.passwordHash.value
        it[version] = change.expectedVersion + 1
        it[updatedAt] = userPersistenceTime()
    } == 1

internal fun userPersistenceTime(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

internal data class PasswordUser(
    val cid: String,
    val nick: String,
    val emailLocalPart: String,
    val passwordHash: PasswordHash?,
    val version: Int,
) {
    override fun toString(): String = "PasswordUser(<redacted>)"
}

private const val ADMINISTRATOR_CID = "admin"
