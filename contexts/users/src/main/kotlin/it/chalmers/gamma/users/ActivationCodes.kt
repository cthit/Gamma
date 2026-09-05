package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.security.SecureRandom

class ActivationCodes(
    private val database: DatabaseFactory,
) {
    fun allowedCids(): List<Cid> =
        database.commitTransaction(readOnly = true) {
            AllowListTable.selectAll().orderBy(AllowListTable.cid).map { Cid(it[AllowListTable.cid]) }
        }

    fun findCid(token: RegistrationToken): Cid? =
        database.commitTransaction(readOnly = true) {
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
        database.commitTransaction {
            val inserted =
                AllowListTable.insertIgnore {
                    it[AllowListTable.cid] = cid.value
                    it[createdAt] = databaseNow()
                }
            if (inserted.insertedCount != 1) throw UserConflict("CID is already allowed")
            if (userCidExists(cid)) throw UserConflict("CID is already a user")
        }
    }
}

private const val USER_TOKEN_CHARACTERS =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789"
private val userTokenRandom = SecureRandom()

internal fun secureUserToken(): String =
    CharArray(72) {
        USER_TOKEN_CHARACTERS[userTokenRandom.nextInt(USER_TOKEN_CHARACTERS.length)]
    }.concatToString()
