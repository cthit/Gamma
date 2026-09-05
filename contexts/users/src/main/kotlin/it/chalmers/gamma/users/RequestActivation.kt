package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.throttling.FixedWindowThrottling
import it.chalmers.gamma.throttling.ThrottleKey
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.upsert
import java.security.SecureRandom
import java.time.Duration
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

class RequestActivation(
    private val database: DatabaseFactory,
    private val throttling: FixedWindowThrottling,
    private val mail: UserMail,
    private val tokenGenerator: () -> String = ::secureUserToken,
) {
    // Eligibility, throttling, issuance, delivery, compensation, and response privacy are one request story.
    @Suppress("LongMethod", "TooGenericExceptionCaught")
    fun request(
        actor: Actor,
        cid: Cid,
        sourceAddress: String? = null,
    ) {
        if (actor != Actor.Anonymous) throw AccessDenied()
        check(TransactionManager.currentOrNull() == null) { "An activation request cannot run inside a transaction" }
        val startedAt = System.nanoTime()
        val responseTime = MINIMUM_RESPONSE_TIME_MILLISECONDS + RANDOM.nextInt(RESPONSE_TIME_JITTER_MILLISECONDS)
        var cancelled = false
        try {
            val allowed =
                database.commitTransaction(readOnly = true) {
                    AllowListTable
                        .selectAll()
                        .where { AllowListTable.cid eq cid.value }
                        .limit(1)
                        .any()
                }
            if (!allowed) return
            if (!throttling.charge(ThrottleKey.digest("activation", cid.value), 3, Duration.ofHours(24))) return
            val token = RegistrationToken(tokenGenerator())
            val issued =
                database.commitTransaction {
                    // Retraction and registration use this same allow-list-before-activation lock order.
                    val stillAllowed =
                        AllowListTable
                            .selectAll()
                            .where { AllowListTable.cid eq cid.value }
                            .forUpdate()
                            .limit(1)
                            .any()
                    if (!stillAllowed) return@commitTransaction false
                    ActivationsTable.upsert(ActivationsTable.cid) {
                        it[ActivationsTable.cid] = cid.value
                        it[ActivationsTable.token] = token.value
                        it[createdAt] = databaseNow()
                    }
                    true
                }
            if (!issued) return
            try {
                mail.sendActivation(cid, token, sourceAddress)
            } catch (failure: CancellationException) {
                // Cancellation gives no reliable delivery outcome. Propagate it without deleting a possibly mailed token.
                throw failure
            } catch (failure: InterruptedException) {
                throw failure
            } catch (_: Exception) {
                database.commitTransaction {
                    ActivationsTable.deleteWhere {
                        (ActivationsTable.cid eq cid.value) and registrationTokenMatches(token)
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
            // Eligibility, throttling, issuance, and mail/cleanup failures must not disclose account state.
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
