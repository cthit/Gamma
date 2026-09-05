package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

data class UserRegistration(
    val token: RegistrationToken,
    val nick: Nick,
    val firstName: FirstName,
    val lastName: LastName,
    val acceptanceYear: AcceptanceYear,
    val language: Language?,
    val email: Email,
    val password: PlainTextPassword,
    val confirmedPassword: String,
    val acceptedUserAgreement: Boolean,
) {
    override fun toString(): String = "UserRegistration(<redacted>)"
}

class RegisterUser(
    private val database: DatabaseFactory,
    private val passwordHasher: PasswordHasher,
) {
    // Token binding, hashing, locked revalidation, and registration form one complete business operation.
    @Suppress("LongMethod")
    fun register(
        actor: Actor,
        input: UserRegistration,
    ): UserId {
        if (actor != Actor.Anonymous) throw AccessDenied()
        val language = requireNotNull(input.language) { "Language is required" }
        if (input.password.value != input.confirmedPassword) throw UserConflict("Password was not confirmed")
        if (!input.acceptedUserAgreement) throw UserConflict("User agreement must be accepted")

        // The token determines the CID; the caller cannot submit another identity's CID.
        val cid =
            database.commitTransaction(readOnly = true) {
                ActivationsTable
                    .selectAll()
                    .where {
                        registrationTokenMatches(input.token) and
                            (ActivationsTable.createdAt greater databaseNow().minus(USER_LIFECYCLE_TOKEN_TTL))
                    }.limit(1)
                    .firstOrNull()
                    ?.let { Cid(it[ActivationsTable.cid]) }
                    ?: throw UserConflict("Activation token is invalid or expired")
            }
        val email = Email(input.email.value.lowercase())
        val passwordHash = passwordHasher.hash(input.password)

        return translateUserUniqueConflict {
            database.commitTransaction {
                // Issuance and retraction lock the allow-list row before touching the activation row.
                lockAllowListReservation(cid)
                val activation =
                    ActivationsTable
                        .selectAll()
                        .where {
                            (ActivationsTable.cid eq cid.value) and registrationTokenMatches(input.token)
                        }.forUpdate()
                        .limit(1)
                        .firstOrNull()
                // Evaluate expiry after acquiring the lock, including time spent waiting for another writer.
                if (activation == null ||
                    activation[ActivationsTable.createdAt] <= databaseNow().minus(USER_LIFECYCLE_TOKEN_TTL)
                ) {
                    throw UserConflict("Activation token is invalid or expired")
                }
                if (userCidExists(cid)) throw UserConflict("CID is already in use")
                requireUserEmailAvailable(email)
                val consumed =
                    ActivationsTable.deleteWhere {
                        (ActivationsTable.cid eq cid.value) and registrationTokenMatches(input.token)
                    }
                check(consumed == 1) { "Locked activation code disappeared before registration completed" }
                AllowListTable.deleteWhere { AllowListTable.cid eq cid.value }

                val userId = UserId.generate()
                val now = userPersistenceTime()
                UsersTable.insert {
                    it[id] = userId.value
                    it[UsersTable.cid] = cid.value
                    it[password] = passwordHash.value
                    it[nick] = input.nick.value
                    it[firstName] = input.firstName.value
                    it[lastName] = input.lastName.value
                    it[UsersTable.email] = email.value
                    it[UsersTable.language] = language.name
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
