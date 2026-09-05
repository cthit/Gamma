package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.matchesStoredVersion
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update

data class MyPasswordChange(
    val currentPassword: PlainTextPassword,
    val newPassword: PlainTextPassword,
    val confirmedPassword: String,
) {
    override fun toString(): String = "MyPasswordChange(<redacted>)"
}

class ChangeMyPassword(
    private val database: DatabaseFactory,
    private val passwordHasher: PasswordHasher,
) {
    fun change(
        actor: Actor,
        input: MyPasswordChange,
    ) {
        val user = actor as? Actor.User ?: throw AccessDenied()
        if (input.newPassword.value != input.confirmedPassword) throw UserConflict("Passwords do not match")
        val credential =
            database.commitTransaction(readOnly = true) {
                UsersTable
                    .select(UsersTable.password, UsersTable.version)
                    .where { UsersTable.id eq user.userId.value }
                    .limit(1)
                    .firstOrNull()
                    ?.let { row ->
                        PasswordCredential(row[UsersTable.password]?.let(::PasswordHash), row[UsersTable.version] ?: 0)
                    }
            }

        // Verification and hashing must neither hold a connection nor repeat on a transaction retry.
        val currentHash = credential?.hash
        if (currentHash == null) {
            passwordHasher.verifyAgainstDummy(input.currentPassword)
            throw UserConflict("Incorrect password")
        }
        if (!passwordHasher.verify(input.currentPassword, currentHash)) throw UserConflict("Incorrect password")
        val newHash = passwordHasher.hash(input.newPassword)
        database.commitTransaction {
            // The update checks the version after any row-lock wait. A concurrent profile or credential
            // change invalidates the password verification; it must never be overwritten by this request.
            val changed =
                UsersTable.update({
                    (UsersTable.id eq user.userId.value) and
                        UsersTable.version.matchesStoredVersion(credential.version)
                }) {
                    it[password] = newHash.value
                    it[version] = credential.version + 1
                    it[updatedAt] = userPersistenceTime()
                }
            if (changed != 1) throw UserConflict("Credentials changed while setting the password")
        }
    }
}

private data class PasswordCredential(
    val hash: PasswordHash?,
    val version: Int,
) {
    override fun toString(): String = "PasswordCredential(<redacted>)"
}
