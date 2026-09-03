package it.chalmers.gamma.oauth

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.datetime

private const val CLIENT_UID_COLUMN = "client_uid"
private const val CREATED_AT_COLUMN = "created_at"

internal object ClientsTable : Table("g_client") {
    val uid = javaUUID(CLIENT_UID_COLUMN)
    val clientId = varchar("client_id", 100).nullable()
    val secret = varchar("client_secret", 255)
    val redirectUri = varchar("redirect_uri", 256)
    val name = varchar("pretty_name", 30)
    val createdAt = datetime(CREATED_AT_COLUMN)
    val descriptionId = javaUUID("description").nullable()
    val official = bool("official")
    val createdBy = javaUUID("created_by").nullable()
    override val primaryKey = PrimaryKey(uid)
}

internal object ClientScopesTable : Table("g_client_scope") {
    val clientUid = javaUUID(CLIENT_UID_COLUMN)
    val scope = varchar("scope", 30)
    val createdAt = datetime(CREATED_AT_COLUMN)
    override val primaryKey = PrimaryKey(clientUid, scope)
}

internal object UserApprovalsTable : Table("g_user_approval") {
    val createdAt = datetime(CREATED_AT_COLUMN)
    val userId = javaUUID("user_id")
    val clientUid = javaUUID(CLIENT_UID_COLUMN)
    override val primaryKey = PrimaryKey(userId, clientUid)
}

internal object ClientApiKeysTable : Table("g_client_api_key") {
    val createdAt = datetime(CREATED_AT_COLUMN)
    val clientUid = javaUUID(CLIENT_UID_COLUMN)
    val apiKeyId = javaUUID("api_key_id")
    override val primaryKey = PrimaryKey(clientUid)
}

internal object ClientAuthoritiesTable : Table("g_client_authority") {
    val createdAt = datetime(CREATED_AT_COLUMN)
    val clientUid = javaUUID(CLIENT_UID_COLUMN)
    val name = varchar("authority_name", 30)
    override val primaryKey = PrimaryKey(clientUid, name)
}

internal object ClientAuthorityUsersTable : Table("g_client_authority_user") {
    val createdAt = datetime(CREATED_AT_COLUMN)
    val userId = javaUUID("user_id")
    val clientUid = javaUUID(CLIENT_UID_COLUMN)
    val authorityName = varchar("authority_name", 30)
    override val primaryKey = PrimaryKey(userId, clientUid, authorityName)
}

internal object ClientAuthoritySuperGroupsTable : Table("g_client_authority_super_group") {
    val createdAt = datetime(CREATED_AT_COLUMN)
    val superGroupId = javaUUID("super_group_id")
    val clientUid = javaUUID(CLIENT_UID_COLUMN)
    val authorityName = varchar("authority_name", 30)
    override val primaryKey = PrimaryKey(superGroupId, clientUid, authorityName)
}

internal object ClientRestrictionsTable : Table("g_client_restriction") {
    val createdAt = datetime(CREATED_AT_COLUMN)
    val restrictionId = javaUUID("restriction_id")
    val clientUid = javaUUID(CLIENT_UID_COLUMN)
    override val primaryKey = PrimaryKey(clientUid)
}

internal object ClientRestrictionSuperGroupsTable : Table("g_client_restriction_super_group") {
    val createdAt = datetime(CREATED_AT_COLUMN)
    val superGroupId = javaUUID("super_group_id")
    val restrictionId = javaUUID("restriction_id")
    override val primaryKey = PrimaryKey(superGroupId, restrictionId)
}
