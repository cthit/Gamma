package it.chalmers.gamma.users

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.datetime

internal object AllowListTable : Table("g_allow_list") {
    val createdAt = datetime("created_at")
    val cid = varchar("cid", 10)

    override val primaryKey = PrimaryKey(cid)
}

internal object ActivationsTable : Table("g_user_activation") {
    val cid = varchar("cid", 10)
    val token = varchar("token", 100)
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(cid)
}

internal object PasswordResetsTable : Table("g_password_reset") {
    val token = varchar("token", 100).nullable()
    val userId = javaUUID("user_id")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(userId)
}
