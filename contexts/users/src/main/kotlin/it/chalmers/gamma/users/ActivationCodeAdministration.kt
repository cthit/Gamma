package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.time.ZoneOffset

class ActivationCodeAdministration(
    private val database: DatabaseFactory,
) {
    fun allowCid(
        actor: Actor,
        cid: Cid,
    ) {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        database.commitTransaction {
            requireAdministrator(UserId(administrator.userId.value))
            val inserted =
                AllowListTable.insertIgnore {
                    it[AllowListTable.cid] = cid.value
                    it[createdAt] = databaseNow()
                }
            if (inserted.insertedCount != 1) throw UserConflict("CID is already allowed")
            // This rejection must roll back the just-inserted reservation.
            if (userCidExists(cid)) throw UserConflict("CID is already a user")
        }
    }

    fun retractCid(
        actor: Actor,
        cid: Cid,
    ) {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        database.commitTransaction {
            requireAdministrator(UserId(administrator.userId.value))
            // Issuance and registration lock the allow-list row before the activation row too.
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

    fun deleteActivation(
        actor: Actor,
        cid: Cid,
    ) {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        database.commitTransaction {
            requireAdministrator(UserId(administrator.userId.value))
            if (ActivationsTable.deleteWhere { ActivationsTable.cid eq cid.value } != 1) {
                throw UserNotFound("Activation code does not exist")
            }
        }
    }

    fun allowedCids(actor: Actor): List<Cid> {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        return database.commitTransaction {
            // A shared authority lock requires a writable transaction even though this operation only reads.
            requireAdministratorForRead(UserId(administrator.userId.value))
            AllowListTable.selectAll().orderBy(AllowListTable.cid).map { Cid(it[AllowListTable.cid]) }
        }
    }

    fun pendingActivations(actor: Actor): List<PendingActivation> {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        return database.commitTransaction {
            requireAdministratorForRead(UserId(administrator.userId.value))
            ActivationsTable.selectAll().orderBy(ActivationsTable.cid).map { row ->
                PendingActivation(
                    Cid(row[ActivationsTable.cid]),
                    row[ActivationsTable.createdAt].toInstant(ZoneOffset.UTC),
                )
            }
        }
    }
}
