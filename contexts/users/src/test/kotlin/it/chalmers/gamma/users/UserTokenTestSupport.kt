package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.jdbc.upsert

// These fixtures arrange persisted tokens for another operation's test. Issuance,
// eligibility, retries, and mail compensation are tested through the request operations.
internal fun DatabaseFactory.seedActivationForTest(cid: Cid): RegistrationToken {
    val token = RegistrationToken(secureUserToken())
    commitTransaction {
        ActivationsTable.upsert(ActivationsTable.cid) {
            it[ActivationsTable.cid] = cid.value
            it[ActivationsTable.token] = token.value
            it[createdAt] = databaseNow()
        }
    }
    return token
}

internal fun DatabaseFactory.seedPasswordResetForTest(userId: UserId): PasswordResetToken {
    val token = PasswordResetToken(secureUserToken())
    commitTransaction {
        PasswordResetsTable.upsert(PasswordResetsTable.userId) {
            it[PasswordResetsTable.userId] = userId.value
            it[PasswordResetsTable.token] = token.value
            it[createdAt] = databaseNow()
        }
    }
    return token
}
