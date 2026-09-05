package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.matchesStoredVersion
import it.chalmers.gamma.throttling.FixedWindowThrottling
import it.chalmers.gamma.throttling.ThrottleKey
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.upsert
import java.security.SecureRandom
import java.time.Duration
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

class RequestPasswordReset(
    private val database: DatabaseFactory,
    private val throttling: FixedWindowThrottling,
    private val mail: UserMail,
    private val tokenGenerator: () -> String = ::secureUserToken,
) {
    // Keep the recipient check, throttle, committed token, mail, cleanup, and privacy delay visible together.
    @Suppress("LongMethod", "TooGenericExceptionCaught")
    fun request(
        actor: Actor,
        submittedIdentifier: String,
        sourceAddress: String? = null,
    ) {
        if (actor != Actor.Anonymous) throw AccessDenied()
        check(TransactionManager.currentOrNull() == null) { "A recovery request cannot run inside a transaction" }
        val startedAt = System.nanoTime()
        val responseTime = MINIMUM_RESPONSE_TIME_MILLISECONDS + RANDOM.nextInt(RESPONSE_TIME_JITTER_MILLISECONDS)
        var cancelled = false
        try {
            val normalized = submittedIdentifier.trim().lowercase()
            val identifier: UserIdentifier = if ('@' in normalized) Email(normalized) else Cid(normalized)
            val recipient =
                database.commitTransaction(readOnly = true) {
                    UsersTable
                        .select(UsersTable.id, UsersTable.email, UsersTable.version)
                        .where {
                            when (identifier) {
                                is Cid -> UsersTable.cid eq identifier.value
                                is Email -> UsersTable.email.lowerCase() eq identifier.value
                                else -> error("Unsupported recovery identifier")
                            }
                        }.limit(1)
                        .firstOrNull()
                        ?.let { row ->
                            RecoveryRecipient(
                                UserId(row[UsersTable.id]),
                                Email(row[UsersTable.email]),
                                row[UsersTable.version] ?: 0,
                            )
                        }
                } ?: return
            if (!throttling.charge(
                    ThrottleKey.digest("password-reset", recipient.email.value),
                    3,
                    Duration.ofHours(24),
                )
            ) {
                return
            }
            val token = PasswordResetToken(tokenGenerator())
            val issued =
                database.commitTransaction {
                    // Do not issue to an address whose owner changed during throttling. Completion locks user then token too.
                    val recipientIsCurrent =
                        UsersTable
                            .select(UsersTable.id)
                            .where {
                                (UsersTable.id eq recipient.userId.value) and
                                    (UsersTable.email eq recipient.email.value) and
                                    UsersTable.version.matchesStoredVersion(recipient.version)
                            }.forUpdate()
                            .limit(1)
                            .any()
                    if (!recipientIsCurrent) return@commitTransaction false
                    PasswordResetsTable.upsert(PasswordResetsTable.userId) {
                        it[PasswordResetsTable.userId] = recipient.userId.value
                        it[PasswordResetsTable.token] = token.value
                        it[createdAt] = databaseNow()
                    }
                    true
                }
            if (!issued) return
            try {
                mail.sendPasswordReset(recipient.email, token, sourceAddress)
            } catch (failure: CancellationException) {
                throw failure
            } catch (failure: InterruptedException) {
                throw failure
            } catch (_: Exception) {
                // Another request may already have replaced this token. Withdraw only this request's token.
                database.commitTransaction {
                    PasswordResetsTable.deleteWhere {
                        (PasswordResetsTable.userId eq recipient.userId.value) and passwordResetTokenMatches(token)
                    }
                }
            }
        } catch (failure: CancellationException) {
            cancelled = true
            throw failure
        } catch (failure: InterruptedException) {
            cancelled = true
            throw failure
        } catch (_: Exception) {
            // Missing/invalid identities and ordinary dependency failures have the same public response.
        } finally {
            if (!cancelled) {
                val remaining = responseTime - TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt)
                if (remaining > 0) Thread.sleep(remaining)
            }
        }
    }

    private companion object {
        const val MINIMUM_RESPONSE_TIME_MILLISECONDS = 3_000L
        const val RESPONSE_TIME_JITTER_MILLISECONDS = 1_500
        val RANDOM = SecureRandom()
    }
}

private data class RecoveryRecipient(
    val userId: UserId,
    val email: Email,
    val version: Int,
) {
    override fun toString(): String = "RecoveryRecipient(<redacted>)"
}
