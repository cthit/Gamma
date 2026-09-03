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
