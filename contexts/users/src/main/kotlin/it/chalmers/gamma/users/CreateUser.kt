package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

class CreateUser(
    private val database: DatabaseFactory,
    private val passwordHasher: PasswordHasher,
) {
    // Keep authorization around password work and the complete reservation-to-user transition visible.
    @Suppress("LongMethod")
    fun create(
        actor: Actor,
        input: NewUser,
    ): UserId {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        val administratorId = UserId(administrator.userId.value)
        database.commitTransaction { requireAdministratorForRead(administratorId) }
        val email = Email(input.email.value.lowercase())
        val passwordHash = passwordHasher.hash(input.password)

        return translateUserUniqueConflict {
            database.commitTransaction {
                requireAdministrator(administratorId)
                // Match issuance, retraction, and registration: allow-list row before activation row.
                AllowListTable
                    .selectAll()
                    .where { AllowListTable.cid eq input.cid.value }
                    .forUpdate()
                    .limit(1)
                    .any()
                ActivationsTable
                    .selectAll()
                    .where { ActivationsTable.cid eq input.cid.value }
                    .forUpdate()
                    .limit(1)
                    .any()
                if (userCidExists(input.cid)) throw UserConflict("CID is already in use")
                requireUserEmailAvailable(email)

                ActivationsTable.deleteWhere { cid eq input.cid.value }
                AllowListTable.deleteWhere { cid eq input.cid.value }
                val userId = UserId.generate()
                val now = userPersistenceTime()
                UsersTable.insert {
                    it[id] = userId.value
                    it[cid] = input.cid.value
                    it[password] = passwordHash.value
                    it[nick] = input.nick.value
                    it[firstName] = input.firstName.value
                    it[lastName] = input.lastName.value
                    it[UsersTable.email] = email.value
                    it[language] = input.language?.name
                    it[userAgreementAccepted] = now
                    it[acceptanceYear] = input.acceptanceYear.value
                    it[version] = 0
                    it[locked] = false
                    it[createdAt] = now
                    it[updatedAt] = now
                }
                userId
            }
        }
    }
}
