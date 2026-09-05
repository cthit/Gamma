package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.UserId

data class NewSuperGroup(
    val name: OrganizationName,
    val prettyName: PrettyName,
    val type: SuperGroupType,
    val description: LocalizedText,
)

data class NewGroup(
    val name: OrganizationName,
    val prettyName: PrettyName,
    val superGroupId: SuperGroupId,
)

data class NewPost(
    val name: LocalizedText,
    val emailPrefix: EmailPrefix,
)

data class GroupImageChange(
    val groupId: GroupId,
    val kind: GroupImageKind,
    val expectedUri: String?,
    val replacementUri: String?,
)

class OrganizationNotFound(
    message: String,
) : RuntimeException(message)

class OrganizationConflict(
    message: String,
) : RuntimeException(message)

data class NewGroupMembership(
    val userId: UserId,
    val postId: PostId,
    val unofficialPostName: UnofficialPostName,
)

// These complete projections keep parent metadata and related rows in the same database snapshot.
data class SuperGroupDetails(
    val superGroup: SuperGroup,
    val groups: List<Group>,
)

data class SuperGroupEditor(
    val superGroup: SuperGroup,
    val superGroupTypes: List<SuperGroupType>,
)
