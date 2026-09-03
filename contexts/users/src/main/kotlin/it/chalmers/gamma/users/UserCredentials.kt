package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.ActivationsTable
import it.chalmers.gamma.users.PasswordResetsTable
import it.chalmers.gamma.users.USER_LIFECYCLE_TOKEN_TTL
import it.chalmers.gamma.users.databaseNow
import it.chalmers.gamma.users.passwordResetTokenMatches
import it.chalmers.gamma.users.registrationTokenMatches
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

internal class UserCredentials(
    private val database: DatabaseFactory,
    private val passwordHasher: PasswordHasher,
) {
    fun prepareRegistration(input: NewUser): PreparedUserRegistration =
        run {
            val normalizedEmail = input.email.value.lowercase()
            PreparedUserRegistration(
                cid = input.cid,
                nick = input.nick,
                firstName = input.firstName,
                lastName = input.lastName,
                acceptanceYear = input.acceptanceYear,
                language = input.language,
                email = Email(normalizedEmail),
                passwordHash = passwordHasher.hash(input.password),
            )
        }

    fun createActivatedUser(
        registration: PreparedUserRegistration,
        claim: ActivationCodeClaim,
    ): UserId {
        if (registration.cid != claim.cid) {
            throw it.chalmers.gamma.platform.core
                .AccessDenied()
        }

        return translateUserUniqueConflict {
            database.transaction {
                val cutoff = databaseNow().minus(USER_LIFECYCLE_TOKEN_TTL)
                val currentCode =
                    ActivationsTable
                        .selectAll()
                        .where {
                            (ActivationsTable.cid eq claim.cid.value) and
                                registrationTokenMatches(claim.token) and
                                (ActivationsTable.createdAt greater cutoff)
                        }.forUpdate()
                        .limit(1)
                        .any()
                if (!currentCode) {
                    throw UserConflict("Activation token is invalid or expired")
                }
                lockAllowListReservation(registration.cid)
                requireUserAvailable(registration)

                val deleted =
                    ActivationsTable.deleteWhere {
                        (ActivationsTable.cid eq claim.cid.value) and registrationTokenMatches(claim.token)
                    }
                check(deleted == 1) { "Locked activation code disappeared before registration completed" }
                deleteAllowListReservation(registration.cid)
                insertUserRow(registration)
            }
        }
    }

    fun existingConfiguration(): AdministratorBootstrapResult? =
        database.transaction(readOnly = true) { existingAdministratorBootstrapResult() }

    fun createAdministrator(registration: PreparedUserRegistration): AdministratorBootstrapResult =
        try {
            translateUserUniqueConflict {
                database.transaction {
                    lockAdministratorAssignments()
                    if (AdminUsersTable.selectAll().limit(1).any()) {
                        return@transaction AdministratorBootstrapResult.ALREADY_CONFIGURED
                    }
                    lockActivationReservation(registration.cid)
                    lockAllowListReservation(registration.cid)
                    if (userCidExists(registration.cid)) {
                        return@transaction AdministratorBootstrapResult.ADMIN_CID_IN_USE
                    }
                    requireUserEmailAvailable(registration.email)

                    deleteActivationReservation(registration.cid)
                    deleteAllowListReservation(registration.cid)
                    val administratorId = insertUserRow(registration)
                    val now = userPersistenceTime()
                    AdminUsersTable.insert {
                        it[AdminUsersTable.userId] = administratorId.value
                        it[AdminUsersTable.createdAt] = now
                    }
                    GdprTrainedUsersTable.insert {
                        it[GdprTrainedUsersTable.userId] = administratorId.value
                        it[GdprTrainedUsersTable.createdAt] = now
                    }
                    AdministratorBootstrapResult.CREATED
                }
            }
        } catch (conflict: UserConflict) {
            existingConfiguration() ?: throw conflict
        }

    fun preparePasswordChange(
        userId: UserId,
        password: PlainTextPassword,
    ): PreparedPasswordChange =
        run {
            val passwordUser = database.loadPasswordUser(userId)
            PreparedPasswordChange(
                userId = userId,
                passwordHash = passwordHasher.hash(password),
                expectedVersion = passwordUser.version,
            )
        }

    fun persistClaimedPasswordChange(
        change: PreparedPasswordChange,
        claim: PasswordResetClaim,
    ) {
        if (change.userId != claim.userId) {
            throw it.chalmers.gamma.platform.core
                .AccessDenied()
        }

        database.transaction {
            // Password-reset issuance locks the user before replacing its reset row. Keeping that
            // same order here prevents a replacement and a completion from deadlocking.
            if (!updatePasswordIfCurrent(change)) {
                throw UserConflict("User is missing or changed while setting the password")
            }
            val cutoff = databaseNow().minus(USER_LIFECYCLE_TOKEN_TTL)
            val deleted =
                PasswordResetsTable.deleteWhere {
                    (PasswordResetsTable.userId eq claim.userId.value) and
                        passwordResetTokenMatches(claim.token) and
                        (PasswordResetsTable.createdAt greater cutoff)
                } == 1
            if (!deleted) {
                throw UserConflict("Password reset token is invalid or expired")
            }
        }
    }
}
