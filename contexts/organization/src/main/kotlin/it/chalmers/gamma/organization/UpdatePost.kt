package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as LocalizedTextsTable
import it.chalmers.gamma.platform.database.matchesStoredVersion
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDateTime
import java.time.ZoneOffset

data class PostUpdate(
    val postId: PostId,
    val expectedVersion: Int,
    val name: LocalizedText,
    val emailPrefix: EmailPrefix,
)

class UpdatePost(
    private val database: DatabaseFactory,
) {
    fun update(
        actor: Actor,
        input: PostUpdate,
    ) {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        if (!administrator.isAdministrator) throw AccessDenied()

        database.commitTransaction {
            val nameId =
                PostsTable
                    .selectAll()
                    .where {
                        (PostsTable.id eq input.postId.value) and
                            PostsTable.version.matchesStoredVersion(input.expectedVersion)
                    }.limit(1)
                    .firstOrNull()
                    ?.get(PostsTable.nameId)
                    ?: throw OrganizationConflict("Post is missing or has been changed")
            val changed =
                PostsTable.update(
                    where = {
                        (PostsTable.id eq input.postId.value) and
                            PostsTable.version.matchesStoredVersion(input.expectedVersion)
                    },
                ) {
                    it[emailPrefix] = input.emailPrefix.value
                    it[version] = input.expectedVersion + 1
                    it[updatedAt] = LocalDateTime.now(ZoneOffset.UTC)
                }
            if (changed != 1) throw OrganizationConflict("Post is missing or has been changed")
            LocalizedTextsTable.update({ LocalizedTextsTable.id eq nameId }) {
                it[sv] = input.name.sv.value
                it[en] = input.name.en.value
            }
        }
    }
}
