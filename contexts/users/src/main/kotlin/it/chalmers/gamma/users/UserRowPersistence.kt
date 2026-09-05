package it.chalmers.gamma.users

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.time.LocalDateTime
import java.time.ZoneOffset

internal fun JdbcTransaction.lockActivationReservation(cid: Cid) {
    ActivationsTable
        .selectAll()
        .where { ActivationsTable.cid eq cid.value }
        .forUpdate()
        .limit(1)
        .any()
}

internal fun JdbcTransaction.lockAllowListReservation(cid: Cid) {
    AllowListTable
        .selectAll()
        .where { AllowListTable.cid eq cid.value }
        .forUpdate()
        .limit(1)
        .any()
}

internal fun JdbcTransaction.requireUserEmailAvailable(email: Email) {
    if (UsersTable
            .selectAll()
            .where { UsersTable.email.lowerCase() eq email.value }
            .limit(1)
            .any()
    ) {
        throw UserConflict("Email is already in use")
    }
}

internal fun JdbcTransaction.existingAdministratorBootstrapResult(): AdministratorBootstrapResult? =
    when {
        AdminUsersTable.selectAll().limit(1).any() -> AdministratorBootstrapResult.ALREADY_CONFIGURED

        UsersTable
            .selectAll()
            .where { UsersTable.cid eq ADMINISTRATOR_CID }
            .limit(1)
            .any() -> AdministratorBootstrapResult.ADMIN_CID_IN_USE

        else -> null
    }

internal fun userPersistenceTime(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

private const val ADMINISTRATOR_CID = "admin"
