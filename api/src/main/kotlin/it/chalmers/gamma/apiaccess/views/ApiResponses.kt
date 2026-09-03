package it.chalmers.gamma.apiaccess.views

import it.chalmers.gamma.api.AccountScaffoldSuperGroupProjection
import it.chalmers.gamma.api.InfoBlobProjection
import it.chalmers.gamma.api.InfoUserProjection
import it.chalmers.gamma.users.ApiUserProfile
import kotlinx.serialization.Serializable

@Serializable data class ApiError(
    val status: Int,
    val error: String,
    val message: String,
)

@Serializable data class AllowListRequest(
    val cids: List<String>? = null,
)

@Serializable data class AllowListAddedResponse(
    val name: String,
    val code: Int,
)

@Serializable
data class ApiUser(
    val cid: String,
    val nick: String,
    val firstName: String,
    val lastName: String,
    val id: String,
    val acceptanceYear: Int,
) {
    companion object {
        fun from(user: ApiUserProfile) =
            ApiUser(
                user.cid.value,
                user.nick.value,
                user.firstName.value,
                user.lastName.value,
                user.id.value.toString(),
                user.acceptanceYear.value,
            )
    }
}

@Serializable data class InfoUserResponse(
    val user: ApiUser,
    val groups: List<InfoUserGroup>,
) {
    companion object {
        fun from(value: InfoUserProjection) =
            InfoUserResponse(
                ApiUser.from(value.user),
                value.memberships.map {
                    InfoUserGroup(InfoGroup.from(it.group), InfoPost.from(it.post))
                },
            )
    }
}

@Serializable data class InfoUserGroup(
    val group: InfoGroup,
    val post: InfoPost,
)

@Serializable data class InfoGroup(
    val id: String,
    val name: String,
    val prettyName: String,
    val superGroup: InfoSuperGroup,
    val version: Int,
) {
    companion object {
        fun from(value: it.chalmers.gamma.organization.Group) =
            InfoGroup(
                value.id.value.toString(),
                value.name.value,
                value.prettyName.value,
                InfoSuperGroup.from(value.superGroup),
                value.version,
            )
    }
}

@Serializable data class InfoSuperGroup(
    val id: String,
    val version: Int,
    val name: String,
    val prettyName: String,
    val type: String,
    val svDescription: String,
    val enDescription: String,
) {
    companion object {
        fun from(value: it.chalmers.gamma.organization.SuperGroup) =
            InfoSuperGroup(
                value.id.value.toString(),
                value.version,
                value.name.value,
                value.prettyName.value,
                value.type.value,
                value.description.sv.value,
                value.description.en.value,
            )
    }
}

@Serializable data class InfoPost(
    val id: String,
    val version: Int,
    val svName: String,
    val enName: String,
    val emailPrefix: String,
    val order: Int,
) {
    companion object {
        fun from(value: it.chalmers.gamma.organization.Post) =
            InfoPost(
                value.id.value.toString(),
                value.version,
                value.name.sv.value,
                value.name.en.value,
                value.emailPrefix.value,
                value.order.value,
            )
    }
}

@Serializable data class InfoBlobResponse(
    val type: String,
    val superGroups: List<InfoBlobSuperGroup>,
) {
    companion object {
        fun from(value: InfoBlobProjection) =
            InfoBlobResponse(
                value.type.value,
                value.superGroups.map(InfoBlobSuperGroup::from),
            )
    }
}

@Serializable data class InfoBlobSuperGroup(
    val superGroup: InfoBlobSuperGroupDetails,
    val hasBanner: Boolean,
    val hasAvatar: Boolean,
    val members: List<InfoBlobMember>,
) {
    companion object {
        fun from(value: it.chalmers.gamma.api.InfoBlobSuperGroupProjection) =
            InfoBlobSuperGroup(
                InfoBlobSuperGroupDetails.from(value.superGroup),
                value.hasBanner,
                value.hasAvatar,
                value.members.map(InfoBlobMember::from),
            )
    }
}

@Serializable data class InfoBlobSuperGroupDetails(
    val id: String,
    val name: String,
    val prettyName: String,
    val type: String,
    val svDescription: String,
    val enDescription: String,
) {
    companion object {
        fun from(value: it.chalmers.gamma.organization.SuperGroup) =
            InfoBlobSuperGroupDetails(
                value.id.value.toString(),
                value.name.value,
                value.prettyName.value,
                value.type.value,
                value.description.sv.value,
                value.description.en.value,
            )
    }
}

@Serializable data class InfoBlobMember(
    val user: ApiUser,
    val post: InfoBlobPost,
    val unofficialPostName: String?,
) {
    companion object {
        fun from(value: it.chalmers.gamma.api.InfoBlobMemberProjection) =
            InfoBlobMember(
                ApiUser.from(value.user),
                InfoBlobPost(
                    value.post.id.value
                        .toString(),
                    value.post.name.sv.value,
                    value.post.name.en.value,
                    value.post.emailPrefix.value,
                ),
                value.unofficialPostName.value,
            )
    }
}

@Serializable data class InfoBlobPost(
    val id: String,
    val svName: String,
    val enName: String,
    val emailPrefix: String,
)

@Serializable data class AccountScaffoldPost(
    val postId: String,
    val svText: String,
    val enText: String,
    val emailPrefix: String,
)

@Serializable data class AccountScaffoldUser(
    val email: String,
    val cid: String,
    val firstName: String,
    val lastName: String,
    val nick: String,
) {
    companion object {
        fun from(value: ApiUserProfile) =
            AccountScaffoldUser(
                value.email.value,
                value.cid.value,
                value.firstName.value,
                value.lastName.value,
                value.nick.value,
            )
    }
}

@Serializable data class AccountScaffoldUserPost(
    val post: AccountScaffoldPost,
    val user: AccountScaffoldUser,
)

@Serializable data class AccountScaffoldGroup(
    val name: String,
    val prettyName: String,
    val members: List<AccountScaffoldUserPost>,
)

@Serializable data class AccountScaffoldSuperGroup(
    val name: String,
    val prettyName: String,
    val type: String,
    val groups: List<AccountScaffoldGroup>,
    val useManagedAccount: Boolean,
) {
    companion object {
        fun from(value: AccountScaffoldSuperGroupProjection) =
            AccountScaffoldSuperGroup(
                value.superGroup.name.value,
                value.superGroup.prettyName.value,
                value.superGroup.type.value,
                value.groups.map { group ->
                    AccountScaffoldGroup(
                        group.group.name.value,
                        group.group.prettyName.value,
                        group.members.map { member ->
                            AccountScaffoldUserPost(
                                AccountScaffoldPost(
                                    member.post.id.value
                                        .toString(),
                                    member.post.name.sv.value,
                                    member.post.name.en.value,
                                    member.post.emailPrefix.value,
                                ),
                                AccountScaffoldUser.from(member.user),
                            )
                        },
                    )
                },
                value.useManagedAccount,
            )
    }
}
