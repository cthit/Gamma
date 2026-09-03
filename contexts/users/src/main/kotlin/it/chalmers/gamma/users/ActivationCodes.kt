package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.ActivationsTable
import it.chalmers.gamma.users.AllowListTable
import it.chalmers.gamma.users.PendingActivation
import it.chalmers.gamma.users.RegistrationToken
import it.chalmers.gamma.users.USER_LIFECYCLE_TOKEN_TTL
import it.chalmers.gamma.users.databaseNow
import it.chalmers.gamma.users.registrationTokenMatches
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.upsert
import java.security.SecureRandom
import java.time.ZoneOffset

class ActivationCodes(
    private val database: DatabaseFactory,
    private val tokenGenerator: () -> String = ::secureUserToken,
) {
    fun allowedCids(): List<Cid> =
        database.transaction(readOnly = true) {
            AllowListTable.selectAll().orderBy(AllowListTable.cid).map { Cid(it[AllowListTable.cid]) }
        }

    fun allowedCids(administratorId: UserId): List<Cid> =
        database.transaction {
            requireAdministratorForRead(administratorId)
            AllowListTable.selectAll().orderBy(AllowListTable.cid).map { Cid(it[AllowListTable.cid]) }
        }

    fun isAllowed(cid: Cid): Boolean =
        database.transaction(readOnly = true) {
            AllowListTable
                .selectAll()
                .where { AllowListTable.cid eq cid.value }
                .limit(1)
                .any()
        }

    fun pendingActivations(administratorId: UserId): List<PendingActivation> =
        database.transaction {
            requireAdministratorForRead(administratorId)
            ActivationsTable
                .selectAll()
                .orderBy(ActivationsTable.cid)
                .map { row ->
                    PendingActivation(
                        cid = Cid(row[ActivationsTable.cid]),
                        createdAt = row[ActivationsTable.createdAt].toInstant(ZoneOffset.UTC),
                    )
                }
        }

    fun findCid(token: RegistrationToken): Cid? =
        database.transaction(readOnly = true) {
            ActivationsTable
                .selectAll()
                .where {
                    registrationTokenMatches(token) and
                        (ActivationsTable.createdAt greater databaseNow().minus(USER_LIFECYCLE_TOKEN_TTL))
                }.limit(1)
                .firstOrNull()
                ?.let { Cid(it[ActivationsTable.cid]) }
        }

    fun allow(cid: Cid) {
        database.transaction { insertAllowedCid(cid) }
    }

    fun allow(
        administratorId: UserId,
        cid: Cid,
    ) {
        database.transaction {
            requireAdministrator(administratorId)
            insertAllowedCid(cid)
        }
    }

    fun allow(cids: Collection<Cid>) {
        database.transaction { cids.forEach { cid -> insertAllowedCid(cid) } }
    }

    fun retract(
        administratorId: UserId,
        cid: Cid,
    ) {
        database.transaction {
            requireAdministrator(administratorId)
            val allowed =
                AllowListTable
                    .selectAll()
                    .where { AllowListTable.cid eq cid.value }
                    .forUpdate()
                    .limit(1)
                    .any()
            if (!allowed) throw UserNotFound("CID is not on the allow list")
            ActivationsTable.deleteWhere { ActivationsTable.cid eq cid.value }
            if (AllowListTable.deleteWhere { AllowListTable.cid eq cid.value } != 1) {
                throw UserNotFound("CID is not on the allow list")
            }
        }
    }

    fun create(cid: Cid): RegistrationToken =
        database.transaction {
            val allowed =
                AllowListTable
                    .selectAll()
                    .where { AllowListTable.cid eq cid.value }
                    .forUpdate()
                    .limit(1)
                    .any()
            if (!allowed) throw UserNotFound("CID is not on the allow list")

            val token = RegistrationToken(tokenGenerator())
            ActivationsTable.upsert(ActivationsTable.cid) {
                it[ActivationsTable.cid] = cid.value
                // Gamma 2.5.1 stored presented tokens verbatim. Keep reading that format so
                // activations survive the 2.6.0 rollout and a possible rollback.
                it[ActivationsTable.token] = token.value
                it[createdAt] = databaseNow()
            }
            token
        }

    fun delete(
        administratorId: UserId,
        cid: Cid,
    ) {
        database.transaction {
            requireAdministrator(administratorId)
            if (ActivationsTable.deleteWhere { ActivationsTable.cid eq cid.value } != 1) {
                throw UserNotFound("Activation code does not exist")
            }
        }
    }

    fun deleteIfMatches(
        cid: Cid,
        token: RegistrationToken,
    ): Boolean =
        database.transaction {
            ActivationsTable.deleteWhere {
                (ActivationsTable.cid eq cid.value) and registrationTokenMatches(token)
            } == 1
        }

    internal fun claim(token: RegistrationToken): ActivationCodeClaim? =
        database.transaction {
            val cutoff = databaseNow().minus(USER_LIFECYCLE_TOKEN_TTL)
            ActivationsTable
                .selectAll()
                .where {
                    registrationTokenMatches(token) and
                        (ActivationsTable.createdAt greater cutoff)
                }.forUpdate()
                .limit(1)
                .firstOrNull()
                ?.let { row -> ActivationCodeClaim(Cid(row[ActivationsTable.cid]), token) }
        }

    fun purgeExpired(): Int =
        database.transaction {
            val cutoff = databaseNow().minus(USER_LIFECYCLE_TOKEN_TTL)
            ActivationsTable.deleteWhere { ActivationsTable.createdAt lessEq cutoff }
        }

    private fun JdbcTransaction.insertAllowedCid(cid: Cid) {
        val inserted =
            AllowListTable.insertIgnore {
                it[AllowListTable.cid] = cid.value
                it[createdAt] = databaseNow()
            }
        if (inserted.insertedCount != 1) throw UserConflict("CID is already allowed")
        if (userCidExists(cid)) throw UserConflict("CID is already a user")
    }
}

internal data class ActivationCodeClaim(
    val cid: Cid,
    val token: RegistrationToken,
) {
    override fun toString(): String = "ActivationCodeClaim(<redacted>)"
}

private const val USER_TOKEN_CHARACTERS =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789"
private val userTokenRandom = SecureRandom()

internal fun secureUserToken(): String =
    CharArray(72) {
        USER_TOKEN_CHARACTERS[userTokenRandom.nextInt(USER_TOKEN_CHARACTERS.length)]
    }.concatToString()
