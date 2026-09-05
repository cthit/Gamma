package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.update

data class PersonalPostName(
    val postId: PostId,
    val name: UnofficialPostName,
)

class ChangeMyPostNames(
    private val database: DatabaseFactory,
) {
    fun change(
        actor: Actor,
        groupId: GroupId,
        names: List<PersonalPostName>,
    ) {
        val user = actor as? Actor.User ?: throw AccessDenied()

        database.commitTransaction {
            for (submitted in names) {
                val changed =
                    MembershipsTable.update(
                        where = {
                            (MembershipsTable.userId eq user.userId.value) and
                                (MembershipsTable.groupId eq groupId.value) and
                                (MembershipsTable.postId eq submitted.postId.value)
                        },
                    ) {
                        it[unofficialPostName] = submitted.name.value
                    }
                if (changed != 1) throw AccessDenied()
            }
        }
    }
}
