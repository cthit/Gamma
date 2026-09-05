package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

enum class AdministratorBootstrapResult {
    ALREADY_CONFIGURED,
    ADMIN_CID_IN_USE,
    PASSWORD_REQUIRED,
    CREATED,
}

class UserBootstrap(
    private val database: DatabaseFactory,
    private val passwordHasher: PasswordHasher,
) {
    // Keep bootstrap checks, hashing, and the complete user/admin/GDPR insertion in one readable flow.
    @Suppress("LongMethod")
    fun ensureAdministrator(password: PlainTextPassword?): AdministratorBootstrapResult {
        val existing = database.commitTransaction(readOnly = true) { existingAdministratorBootstrapResult() }
        if (existing != null) return existing
        if (password == null) return AdministratorBootstrapResult.PASSWORD_REQUIRED
        val passwordHash = passwordHasher.hash(password)
        val cid = Cid("admin")

        return try {
            translateUserUniqueConflict {
                database.commitTransaction {
                    lockAdministratorAssignments()
                    if (AdminUsersTable.selectAll().limit(1).any()) {
                        return@commitTransaction AdministratorBootstrapResult.ALREADY_CONFIGURED
                    }
                    lockAllowListReservation(cid)
                    lockActivationReservation(cid)
                    if (userCidExists(cid)) return@commitTransaction AdministratorBootstrapResult.ADMIN_CID_IN_USE
                    requireUserEmailAvailable(Email("admin@chalmers.it"))

                    ActivationsTable.deleteWhere { ActivationsTable.cid eq cid.value }
                    AllowListTable.deleteWhere { AllowListTable.cid eq cid.value }
                    val administratorId = UserId.generate()
                    val now = userPersistenceTime()
                    UsersTable.insert {
                        it[id] = administratorId.value
                        it[UsersTable.cid] = cid.value
                        it[UsersTable.password] = passwordHash.value
                        it[nick] = "admin"
                        it[firstName] = "admin"
                        it[lastName] = "admin"
                        it[email] = "admin@chalmers.it"
                        it[language] = Language.EN.name
                        it[userAgreementAccepted] = now
                        it[acceptanceYear] = 2018
                        it[version] = 0
                        it[locked] = false
                        it[createdAt] = now
                        it[updatedAt] = now
                    }
                    AdminUsersTable.insert {
                        it[userId] = administratorId.value
                        it[createdAt] = now
                    }
                    GdprTrainedUsersTable.insert {
                        it[userId] = administratorId.value
                        it[createdAt] = now
                    }
                    AdministratorBootstrapResult.CREATED
                }
            }
        } catch (conflict: UserConflict) {
            // A competing creator may have configured the system or claimed the reserved CID.
            database.commitTransaction(readOnly = true) { existingAdministratorBootstrapResult() } ?: throw conflict
        }
    }
}
