package it.chalmers.gamma.oauth

import at.favre.lib.crypto.bcrypt.BCrypt
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.datetime
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.update
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.Base64
import java.util.UUID

class RotateClientSecret(
    private val database: DatabaseFactory,
    private val bcryptCost: Int = 12,
    private val random: SecureRandom = SecureRandom(),
) {
    init {
        require(bcryptCost in 10..16)
    }

    /** The application checks this locked owner's authority before reserving or replacing a secret. */
    fun lockIn(
        transaction: JdbcTransaction,
        uid: ClientUid,
    ): LockedClientSecret {
        database.requireTransaction(transaction)
        val row =
            ClientsTable
                .select(ClientsTable.official, ClientsTable.createdBy, ClientsTable.secret)
                .where { ClientsTable.uid eq uid.value }
                .forUpdate()
                .firstOrNull()
                ?: throw OAuthClientNotFound("Client does not exist")
        val owner =
            if (row[ClientsTable.official]) {
                ClientOwner.Official
            } else {
                ClientOwner.User(UserId(checkNotNull(row[ClientsTable.createdBy])))
            }
        return LockedClientSecret(transaction, uid, owner, row[ClientsTable.secret])
    }

    fun reserveIn(
        transaction: JdbcTransaction,
        target: LockedClientSecret,
        reservationId: UUID,
    ): ClientSecretReservation {
        database.requireTransaction(transaction)
        check(target.transaction === transaction) { "Secret reservation requires its locked transaction" }
        val now = transaction.rotationTime()
        val existing =
            ClientSecretRotationsTable
                .selectAll()
                .where { ClientSecretRotationsTable.clientUid eq target.uid.value }
                .firstOrNull()
        if (existing != null) {
            // Retrying a lost acknowledgement must retain the original deadline, not extend the lease.
            if (existing[ClientSecretRotationsTable.reservationId] == reservationId &&
                existing[ClientSecretRotationsTable.expiresAt] > now
            ) {
                return ClientSecretReservation(target.uid, reservationId, target.storedSecret)
            }
            if (existing[ClientSecretRotationsTable.expiresAt] > now) {
                throw OAuthClientConflict("Client credentials are already being changed")
            }
            ClientSecretRotationsTable.deleteWhere { clientUid eq target.uid.value }
        }
        ClientSecretRotationsTable.insert {
            it[clientUid] = target.uid.value
            it[ClientSecretRotationsTable.reservationId] = reservationId
            // Hashing should take seconds. An abandoned request must not block this client indefinitely.
            it[expiresAt] = now.plusMinutes(5)
        }
        return ClientSecretReservation(target.uid, reservationId, target.storedSecret)
    }

    fun prepare(reservation: ClientSecretReservation): PreparedClientSecretRotation {
        check(TransactionManager.currentOrNull() == null) { "Secret preparation requires no active transaction" }
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        val secret = RawClientSecret(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes))
        val stored = "{bcrypt}" + BCrypt.withDefaults().hashToString(bcryptCost, secret.value.toCharArray())
        return PreparedClientSecretRotation(reservation, secret, stored)
    }

    fun replaceIn(
        transaction: JdbcTransaction,
        target: LockedClientSecret,
        prepared: PreparedClientSecretRotation,
    ): RotatedClientSecret {
        database.requireTransaction(transaction)
        check(target.transaction === transaction) { "Secret replacement requires its locked transaction" }
        val reservation = prepared.reservation
        check(target.uid == reservation.uid) { "Secret replacement requires its reserved client" }
        // A retry after a committed UPDATE must neither generate another secret nor overwrite a later reset.
        if (target.storedSecret != prepared.storedSecret) {
            if (target.storedSecret != reservation.previousSecret) {
                throw OAuthClientConflict("Client credentials changed during preparation")
            }
            val current =
                ClientSecretRotationsTable
                    .selectAll()
                    .where { ClientSecretRotationsTable.clientUid eq target.uid.value }
                    .firstOrNull()
            if (current == null || current[ClientSecretRotationsTable.reservationId] != reservation.id ||
                current[ClientSecretRotationsTable.expiresAt] <= transaction.rotationTime()
            ) {
                throw OAuthClientConflict("Client credential reservation expired or was replaced")
            }
            val changed =
                ClientsTable.update({ ClientsTable.uid eq target.uid.value }) {
                    it[secret] = prepared.storedSecret
                }
            if (changed != 1) throw OAuthClientNotFound("Client secret could not be replaced")
        }
        val released =
            ClientSecretRotationsTable.deleteWhere {
                (clientUid eq reservation.uid.value) and (reservationId eq reservation.id)
            }
        if (target.storedSecret != prepared.storedSecret && released != 1) {
            throw OAuthClientConflict("Client credential reservation could not be released")
        }
        val client = transaction.loadClients(setOf(target.uid)).single()
        return RotatedClientSecret(client, prepared.secret, transaction.loadClientAuthorities(target.uid))
    }

    /** Failure cleanup only releases this request's reservation, including an ambiguously committed claim. */
    fun release(
        uid: ClientUid,
        reservationId: UUID,
    ) {
        database.commitTransaction {
            val ownReservation =
                ClientSecretRotationsTable
                    .select(ClientSecretRotationsTable.clientUid)
                    .where {
                        (ClientSecretRotationsTable.clientUid eq uid.value) and
                            (ClientSecretRotationsTable.reservationId eq reservationId)
                    }.forUpdate()
                    .any()
            if (ownReservation) {
                val released =
                    ClientSecretRotationsTable.deleteWhere {
                        (clientUid eq uid.value) and (ClientSecretRotationsTable.reservationId eq reservationId)
                    }
                if (released != 1) throw OAuthClientConflict("Client credential reservation could not be released")
            }
        }
    }
}

class LockedClientSecret internal constructor(
    internal val transaction: JdbcTransaction,
    internal val uid: ClientUid,
    val owner: ClientOwner,
    internal val storedSecret: String,
) {
    override fun toString(): String = "LockedClientSecret(<redacted>)"
}

class ClientSecretReservation internal constructor(
    internal val uid: ClientUid,
    internal val id: UUID,
    internal val previousSecret: String,
) {
    override fun toString(): String = "ClientSecretReservation(<redacted>)"
}

class PreparedClientSecretRotation internal constructor(
    internal val reservation: ClientSecretReservation,
    val secret: RawClientSecret,
    internal val storedSecret: String,
) {
    override fun toString(): String = "PreparedClientSecretRotation(<redacted>)"
}

data class RotatedClientSecret(
    val client: OAuthClient,
    val secret: RawClientSecret,
    val authorities: List<ClientAuthority>,
)

internal object ClientSecretRotationsTable : Table("g_client_secret_rotation") {
    val clientUid = javaUUID("client_uid")
    val reservationId = javaUUID("reservation_id")
    val expiresAt = datetime("expires_at")
    override val primaryKey = PrimaryKey(clientUid)
}

private fun JdbcTransaction.rotationTime(): LocalDateTime =
    checkNotNull(
        exec("SELECT clock_timestamp() AT TIME ZONE 'UTC'") { result ->
            check(result.next())
            result.getTimestamp(1).toLocalDateTime()
        },
    )
