package it.chalmers.gamma.oauth.views

import it.chalmers.gamma.organization.Group
import it.chalmers.gamma.organization.Post
import it.chalmers.gamma.organization.SuperGroup
import it.chalmers.gamma.users.ApiUserProfile
import kotlinx.serialization.Serializable

@Serializable data class ClientApiUser(
    val cid: String,
    val nick: String,
    val firstName: String,
    val lastName: String,
    val id: String,
    val acceptanceYear: Int,
) {
    companion object {
        fun from(value: ApiUserProfile) =
            ClientApiUser(
                value.cid.value,
                value.nick.value,
                value.firstName.value,
                value.lastName.value,
                value.id.value.toString(),
                value.acceptanceYear.value,
            )
    }
}

@Serializable data class ClientApiSuperGroup(
    val id: String,
    val name: String,
    val prettyName: String,
    val type: String,
    val svDescription: String,
    val enDescription: String,
) {
    companion object {
        fun from(value: SuperGroup) =
            ClientApiSuperGroup(
                value.id.value.toString(),
                value.name.value,
                value.prettyName.value,
                value.type.value,
                value.description.sv.value,
                value.description.en.value,
            )
    }
}

@Serializable data class ClientApiGroup(
    val id: String,
    val name: String,
    val prettyName: String,
    val superGroup: ClientApiSuperGroup,
) {
    companion object {
        fun from(value: Group) =
            ClientApiGroup(
                value.id.value.toString(),
                value.name.value,
                value.prettyName.value,
                ClientApiSuperGroup.from(value.superGroup),
            )
    }
}

@Serializable data class ClientApiPost(
    val id: String,
    val version: Int,
    val svName: String,
    val enName: String,
) {
    companion object {
        fun from(value: Post) =
            ClientApiPost(value.id.value.toString(), value.version, value.name.sv.value, value.name.en.value)
    }
}

@Serializable data class ClientApiMembership(
    val id: String,
    val name: String,
    val prettyName: String,
    val superGroup: ClientApiSuperGroup,
    val post: ClientApiPost,
) {
    companion object {
        fun from(
            group: Group,
            post: Post,
        ) = ClientApiMembership(
            group.id.value.toString(),
            group.name.value,
            group.prettyName.value,
            ClientApiSuperGroup.from(group.superGroup),
            ClientApiPost.from(post),
        )
    }
}
