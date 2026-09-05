package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as LocalizedTextsTable
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

class CreatePost(
    private val database: DatabaseFactory,
    private val access: OrganizationAccess,
) {
    fun create(
        actor: Actor,
        input: NewPost,
    ): PostId =
        database.commitTransaction {
            access.requireAdministratorIn(this, actor)
            require(
                input.name.sv.value
                    .isNotEmpty() &&
                    input.name.en.value
                        .isNotEmpty(),
            ) { "Post names must not be empty" }
            // Creation, deletion, and reordering must agree on the complete post list.
            exec("LOCK TABLE g_post IN SHARE ROW EXCLUSIVE MODE")
            val highestOrder = PostsTable.selectAll().maxOfOrNull { it[PostsTable.order] ?: 0 }
            val order = highestOrder?.plus(1) ?: 0
            val postId = PostId.generate()
            val textId = UUID.randomUUID()
            val now = LocalDateTime.now(ZoneOffset.UTC)
            LocalizedTextsTable.insert {
                it[id] = textId
                it[sv] = input.name.sv.value
                it[en] = input.name.en.value
                it[createdAt] = now
            }
            PostsTable.insert {
                it[id] = postId.value
                it[nameId] = textId
                it[emailPrefix] = input.emailPrefix.value
                it[version] = 0
                it[PostsTable.order] = order
                it[createdAt] = now
                it[updatedAt] = now
            }
            postId
        }
}
