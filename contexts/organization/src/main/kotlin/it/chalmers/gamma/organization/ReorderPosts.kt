package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDateTime
import java.time.ZoneOffset

class ReorderPosts(
    private val database: DatabaseFactory,
) {
    fun reorder(
        actor: Actor,
        ids: List<PostId>,
    ) {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        if (!administrator.isAdministrator) throw AccessDenied()

        database.commitTransaction {
            // Validate and write against one stable list; competing reorders cannot interleave.
            exec("LOCK TABLE g_post IN SHARE ROW EXCLUSIVE MODE")
            val existing = PostsTable.selectAll().map { PostId(it[PostsTable.id]) }.toSet()
            require(ids.size == existing.size && ids.toSet() == existing) {
                "The order must contain every post exactly once"
            }
            val now = LocalDateTime.now(ZoneOffset.UTC)
            for ((index, id) in ids.withIndex()) {
                PostsTable.update({ PostsTable.id eq id.value }) {
                    it[order] = index
                    it[updatedAt] = now
                }
            }
        }
    }
}
