package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as LocalizedTextsTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll

class DeletePost(
    private val database: DatabaseFactory,
) {
    fun delete(
        actor: Actor,
        postId: PostId,
    ) {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        if (!administrator.isAdministrator) throw AccessDenied()

        database.commitTransaction {
            // Keep the post list stable for creation and complete-list reordering.
            exec("LOCK TABLE g_post IN SHARE ROW EXCLUSIVE MODE")
            val nameId =
                PostsTable
                    .selectAll()
                    .where { PostsTable.id eq postId.value }
                    .forUpdate()
                    .limit(1)
                    .firstOrNull()
                    ?.get(PostsTable.nameId)
                    ?: throw OrganizationNotFound("Post does not exist")
            if (MembershipsTable.selectAll().where { MembershipsTable.postId eq postId.value }.count() != 0L) {
                throw OrganizationConflict("Post is still used by memberships")
            }
            PostsTable.deleteWhere { id eq postId.value }
            LocalizedTextsTable.deleteWhere { id eq nameId }
        }
    }
}
