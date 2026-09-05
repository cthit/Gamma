package it.chalmers.gamma.organization

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.datetime

private const val CREATED_AT_COLUMN = "created_at"

internal object SuperGroupTypesTable : Table("g_super_group_type") {
    val name = varchar("super_group_type_name", 30)
    val createdAt = datetime(CREATED_AT_COLUMN)

    override val primaryKey = PrimaryKey(name)
}

internal object SuperGroupsTable : Table("g_super_group") {
    val id = javaUUID("super_group_id")
    val name = varchar("e_name", 50)
    val prettyName = varchar("pretty_name", 50)
    val type = varchar("super_group_type_name", 30)
    val descriptionId = javaUUID("description").nullable()
    val version = integer("version").nullable()
    val createdAt = datetime(CREATED_AT_COLUMN)
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}

internal object GroupsTable : Table("g_group") {
    val id = javaUUID("group_id")
    val name = varchar("e_name", 50)
    val prettyName = varchar("pretty_name", 50)
    val superGroupId = javaUUID("super_group_id")
    val version = integer("version").nullable()
    val createdAt = datetime(CREATED_AT_COLUMN)
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}

internal object GroupImagesTable : Table("g_group_images_uri") {
    val createdAt = datetime(CREATED_AT_COLUMN)
    val updatedAt = datetime("updated_at")
    val groupId = javaUUID("group_id").nullable()
    val avatarUri = varchar("avatar_uri", 255).nullable()
    val bannerUri = varchar("banner_uri", 255).nullable()
    val version = integer("version").nullable()
}

internal object PostsTable : Table("g_post") {
    val id = javaUUID("post_id")
    val nameId = javaUUID("post_name")
    val emailPrefix = varchar("email_prefix", 20).nullable()
    val version = integer("version").nullable()
    val order = integer("post_order").nullable()
    val createdAt = datetime(CREATED_AT_COLUMN)
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}

internal object MembershipsTable : Table("g_membership") {
    val createdAt = datetime(CREATED_AT_COLUMN)
    val userId = javaUUID("user_id")
    val groupId = javaUUID("group_id")
    val postId = javaUUID("post_id")
    val unofficialPostName = varchar("unofficial_post_name", 50).nullable()

    override val primaryKey = PrimaryKey(userId, groupId, postId)
}
