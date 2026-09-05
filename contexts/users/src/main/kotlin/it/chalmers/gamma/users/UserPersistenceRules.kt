package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.postgresql.util.PSQLException
import java.sql.SQLException
import java.util.Collections
import java.util.IdentityHashMap

internal const val USER_NOT_FOUND_MESSAGE = "User does not exist"

internal fun JdbcTransaction.lockUserIfPresent(userId: UserId): Boolean =
    UsersTable
        .selectAll()
        .where { UsersTable.id eq userId.value }
        .forUpdate()
        .limit(1)
        .any()

internal fun JdbcTransaction.userCidExists(cid: Cid): Boolean =
    UsersTable
        .selectAll()
        .where { UsersTable.cid eq cid.value }
        .limit(1)
        .any()

internal fun JdbcTransaction.requireAdministrator(administratorId: UserId) {
    lockAdministratorAssignments()
    val administrator =
        AdminUsersTable
            .selectAll()
            .where { AdminUsersTable.userId eq administratorId.value }
            .forUpdate()
            .limit(1)
    if (!administrator.any()) throw AccessDenied()
}

/** Keeps a privileged read ordered with administrator demotion without serializing other reads. */
internal fun JdbcTransaction.requireAdministratorForRead(administratorId: UserId) {
    exec("LOCK TABLE g_admin_user IN SHARE MODE")
    val administratorExists =
        AdminUsersTable
            .selectAll()
            .where { AdminUsersTable.userId eq administratorId.value }
            .limit(1)
            .any()
    if (!administratorExists) throw AccessDenied()
}

/** Serializes authorization decisions with every change to the administrator set. */
internal fun JdbcTransaction.lockAdministratorAssignments() {
    exec("LOCK TABLE g_admin_user IN SHARE ROW EXCLUSIVE MODE")
}

internal fun JdbcTransaction.requireNotFinalAdministrator(
    userId: UserId,
    action: String,
) {
    val administrators =
        AdminUsersTable
            .selectAll()
            .forUpdate()
            .map { it[AdminUsersTable.userId] }
    if (administrators.size == 1 && administrators.single() == userId.value) {
        throw UserConflict("Cannot $action the final administrator")
    }
}

@Suppress("TooGenericExceptionCaught") // Exposed may wrap the PostgreSQL exception below an intermediate exception.
internal fun <T> translateUserUniqueConflict(operation: () -> T): T =
    try {
        operation()
    } catch (failure: Exception) {
        when {
            failure.isUniqueViolation(EMAIL_UNIQUE_CONSTRAINTS) -> throw UserConflict("Email is already in use")
            failure.isUniqueViolation(setOf(CID_UNIQUE_CONSTRAINT)) -> throw UserConflict("CID is already in use")
            else -> throw failure
        }
    }

private fun Throwable.isUniqueViolation(constraintNames: Set<String>): Boolean =
    sqlExceptions().any { sqlFailure -> sqlFailure.isUniqueViolationFor(constraintNames) }

private fun Throwable.sqlExceptions(): Sequence<SQLException> =
    sequence {
        val pending = ArrayDeque<Throwable>()
        val visited = Collections.newSetFromMap(IdentityHashMap<Throwable, Boolean>())
        pending.addLast(this@sqlExceptions)
        while (pending.isNotEmpty()) {
            val failure = pending.removeFirst()
            if (!visited.add(failure)) continue
            failure.cause?.let(pending::addLast)
            if (failure is SQLException) {
                yield(failure)
                failure.nextException?.let(pending::addLast)
            }
        }
    }

private fun SQLException.isUniqueViolationFor(constraintNames: Set<String>): Boolean {
    if (sqlState != UNIQUE_VIOLATION_SQL_STATE) return false
    val structuredConstraint = (this as? PSQLException)?.serverErrorMessage?.constraint
    return structuredConstraint in constraintNames ||
        constraintNames.any { constraint -> message?.contains(constraint) == true }
}

private const val UNIQUE_VIOLATION_SQL_STATE = "23505"
private const val CID_UNIQUE_CONSTRAINT = "g_user_cid_key"
private val EMAIL_UNIQUE_CONSTRAINTS = setOf("g_user_email_key")
