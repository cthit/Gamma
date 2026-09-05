package it.chalmers.gamma.users

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.datetime

private const val CREATED_AT_COLUMN = "created_at"

internal object UsersTable : Table("g_user") {
    val id = javaUUID("user_id")
    val cid = varchar("cid", 12)
    val password = varchar("password", 255).nullable()
    val nick = varchar("nick", 50)
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val email = varchar("email", 100)
    val language = varchar("language", 15).nullable()
    val acceptanceYear = integer("acceptance_year").nullable()
    val version = integer("version").nullable()
    val locked = bool("locked").nullable()
    val userAgreementAccepted = datetime("user_agreement_accepted")
    val createdAt = datetime(CREATED_AT_COLUMN)
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}

internal object UserAvatarsTable : Table("g_user_avatar_uri") {
    val userId = javaUUID("user_id").nullable()
    val avatarUri = varchar("avatar_uri", 255).nullable()
    val version = integer("version").nullable()
    val createdAt = datetime(CREATED_AT_COLUMN)
    val updatedAt = datetime("updated_at")
}

internal object AdminUsersTable : Table("g_admin_user") {
    val createdAt = datetime(CREATED_AT_COLUMN)
    val userId = javaUUID("user_id")

    override val primaryKey = PrimaryKey(userId)
}

internal object GdprTrainedUsersTable : Table("g_gdpr_trained") {
    val createdAt = datetime(CREATED_AT_COLUMN)
    val userId = javaUUID("user_id")

    override val primaryKey = PrimaryKey(userId)
}
