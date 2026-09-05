package it.chalmers.gamma.apiaccess

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.datetime

internal object ApiKeySettingsTable : Table("g_api_key_settings") {
    val id = javaUUID("settings_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    val version = integer("version").nullable()
    val apiKeyId = javaUUID("api_key_id").nullable()
    override val primaryKey = PrimaryKey(id)
}

internal object ApiKeysTable : Table("g_api_key") {
    val id = javaUUID("api_key_id")
    val name = varchar("pretty_name", 30)
    val token = varchar("token", 255)
    val type = varchar("key_type", 30)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    val version = integer("version").nullable()
    val descriptionId = javaUUID("description").nullable()

    override val primaryKey = PrimaryKey(id)
}

internal object ApiKeyTypesTable : Table("g_api_key_to_super_group_type") {
    val settingsId = javaUUID("settings_id")
    val createdAt = datetime("created_at")
    val type = varchar("super_group_type_name", 30)
    override val primaryKey = PrimaryKey(settingsId, type)
}

internal object ManagedApiKeyTypesTable : Table("g_api_key_account_scaffold_requires_managed") {
    val settingsId = javaUUID("settings_id")
    val createdAt = datetime("created_at")
    val type = varchar("super_group_type_name", 30)
    override val primaryKey = PrimaryKey(settingsId, type)
}
