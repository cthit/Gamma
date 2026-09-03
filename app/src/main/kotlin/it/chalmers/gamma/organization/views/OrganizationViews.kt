package it.chalmers.gamma.organization.views

import it.chalmers.gamma.organization.Group
import it.chalmers.gamma.organization.Membership
import it.chalmers.gamma.organization.Post
import it.chalmers.gamma.organization.PostId
import it.chalmers.gamma.organization.SuperGroup
import it.chalmers.gamma.organization.UnofficialPostName
import it.chalmers.gamma.platform.core.SuperGroupType
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.html.GammaPageContext
import it.chalmers.gamma.platform.html.csrfInput
import it.chalmers.gamma.platform.html.gammaPage
import it.chalmers.gamma.platform.html.methodOverrideInput
import it.chalmers.gamma.users.DirectoryUser
import kotlinx.html.ButtonType
import kotlinx.html.FormEncType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.article
import kotlinx.html.b
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.footer
import kotlinx.html.form
import kotlinx.html.header
import kotlinx.html.hiddenInput
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.li
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.textInput
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlinx.html.ul

fun renderGroups(
    page: GammaPageContext,
    groups: List<Group>,
): String =
    gammaPage("Groups", page) {
        if (page.viewer?.isAdmin == true) a(href = "${page.contextPath}/groups/create") { +"Create group" }
        table {
            tbody {
                groups.forEach { group ->
                    tr {
                        td { +group.prettyName.value }
                        td { +group.superGroup.prettyName.value }
                        td { a(href = "${page.contextPath}/groups/${group.id.value}") { +"Details" } }
                    }
                }
            }
        }
    }

fun renderGroupEditor(
    page: GammaPageContext,
    editor: GroupEditor,
): String =
    gammaPage(if (editor.group == null) "Create group" else "Edit group", page) {
        val group = editor.group
        article {
            header { +if (group == null) "Create group" else "Edit group details" }
            form(
                action =
                    if (group ==
                        null
                    ) {
                        "${page.contextPath}/groups/create"
                    } else {
                        "${page.contextPath}/groups/${group.id.value}"
                    },
                method = FormMethod.post,
            ) {
                id = "edit-group"
                csrfInput(page.requiredCsrfToken())
                if (group != null) {
                    methodOverrideInput("put")
                    hiddenInput {
                        name = "version"
                        value = group.version.toString()
                    }
                }
                label {
                    +"Name"
                    textInput(name = "name") { value = group?.name?.value.orEmpty() }
                }
                label {
                    +"Pretty name"
                    textInput(name = "prettyName") { value = group?.prettyName?.value.orEmpty() }
                }
                label {
                    +"Super group"
                    select {
                        name = "superGroupId"
                        editor.superGroups.forEach { superGroup ->
                            option {
                                value = superGroup.id.value.toString()
                                selected = superGroup.id == group?.superGroup?.id
                                +superGroup.prettyName.value
                            }
                        }
                    }
                }
                if (group != null) {
                    div {
                        id = "members"
                        editor.memberships.forEach { membership ->
                            memberRow(editor.users, editor.posts, membership)
                        }
                    }
                    button(type = ButtonType.button) {
                        attributes["data-hx-get"] = "${page.contextPath}/groups/new-member"
                        attributes["data-hx-target"] = "#members"
                        attributes["data-hx-swap"] = "beforeend"
                        +"Add member"
                    }
                }
                button { +if (group == null) "Create" else "Save" }
            }
        }
    }

data class GroupEditor(
    val superGroups: List<SuperGroup>,
    val group: Group? = null,
    val users: List<DirectoryUser> = emptyList(),
    val posts: List<Post> = emptyList(),
    val memberships: List<Membership> = emptyList(),
)

fun renderNewMember(
    users: List<DirectoryUser>,
    posts: List<Post>,
): String =
    kotlinx.html.stream
        .createHTML()
        .div { memberRow(users, posts, null) }

fun parsePersonalPostNames(parameters: Map<String, List<String>>): List<Pair<PostId, UnofficialPostName>> {
    val flatPostIds = parameters["postId"].orEmpty()
    val flatNames = parameters["unofficialPostName"].orEmpty()
    require(flatPostIds.size == flatNames.size) { "Every personal post must have one name" }
    if (flatPostIds.isNotEmpty()) {
        return flatPostIds.zip(flatNames).map { (postId, name) ->
            PostId.parse(postId) to UnofficialPostName(name.ifBlank { null })
        }
    }
    return parameters.entries.mapNotNull { (name, values) ->
        val match = Regex("postNames\\[([^]]+)]").matchEntire(name) ?: return@mapNotNull null
        PostId.parse(match.groupValues[1]) to UnofficialPostName(values.single().ifBlank { null })
    }
}

data class GroupDetailsPage(
    val group: Group,
    val memberships: List<Membership>,
    val users: Map<UserId, DirectoryUser>,
    val posts: Map<it.chalmers.gamma.organization.PostId, Post>,
    val ownUserId: UserId? = null,
)

fun renderGroupDetails(
    page: GammaPageContext,
    details: GroupDetailsPage,
): String =
    gammaPage("Group details", page) {
        article {
            header { +"Group details" }
            ul(classes = "tuple") {
                li { +"Group ID: ${details.group.id.value}" }
                li { +"Name: ${details.group.name.value}" }
                li { +"Pretty name: ${details.group.prettyName.value}" }
            }
            ul {
                details.memberships.forEach { membership ->
                    li {
                        +details.users[membership.userId]
                            ?.nick
                            ?.value
                            .orEmpty()
                        +" - "
                        +details.posts[membership.postId]
                            ?.name
                            ?.en
                            ?.value
                            .orEmpty()
                        membership.unofficialPostName.value?.let { +" - $it" }
                    }
                }
            }
            if (page.viewer?.isAdmin == true) {
                form(action = "${page.contextPath}/groups/${details.group.id.value}/edit", method = FormMethod.get) {
                    button { +"Edit" }
                }
                form(action = "${page.contextPath}/groups/${details.group.id.value}", method = FormMethod.post) {
                    attributes["data-hx-confirm"] = "Are you sure you want to delete this group?"
                    csrfInput(page.requiredCsrfToken())
                    methodOverrideInput("delete")
                    button { +"Delete" }
                }
            }
        }
        val ownMemberships = details.memberships.filter { it.userId == details.ownUserId }
        if (ownMemberships.isNotEmpty()) {
            article {
                header { +"Change unofficial post name for your posts" }
                form(
                    action = "${page.contextPath}/groups/${details.group.id.value}/my-posts",
                    method = FormMethod.post,
                ) {
                    csrfInput(page.requiredCsrfToken())
                    methodOverrideInput("put")
                    ownMemberships.forEach { membership ->
                        hiddenInput {
                            name = "postId"
                            value = membership.postId.value.toString()
                        }
                        label {
                            +details.posts[membership.postId]
                                ?.name
                                ?.en
                                ?.value
                                .orEmpty()
                            textInput(name = "unofficialPostName") {
                                value = membership.unofficialPostName.value.orEmpty()
                            }
                        }
                    }
                    button { +"Update unofficial post names" }
                }
            }
        }
        val canEditImages = page.viewer?.isAdmin == true || ownMemberships.isNotEmpty()
        groupImage(page, details.group, "avatar", canEditImages)
        groupImage(page, details.group, "banner", canEditImages)
    }

fun renderSuperGroups(
    page: GammaPageContext,
    groups: List<SuperGroup>,
): String =
    gammaPage("Super groups", page) {
        if (page.viewer?.isAdmin == true) a(href = "${page.contextPath}/super-groups/create") { +"Create super group" }
        table {
            tbody {
                groups.forEach { group ->
                    tr {
                        td { +group.prettyName.value }
                        td { +group.type.value }
                        td { a(href = "${page.contextPath}/super-groups/${group.id.value}") { +"Details" } }
                    }
                }
            }
        }
    }

fun renderSuperGroupEditor(
    page: GammaPageContext,
    types: List<SuperGroupType>,
    group: SuperGroup? = null,
): String =
    gammaPage(if (group == null) "Create super group" else "Edit super group", page) {
        article {
            header { +if (group == null) "Create super group" else "Edit ${group.prettyName.value}" }
            form(
                action =
                    if (group ==
                        null
                    ) {
                        "${page.contextPath}/super-groups"
                    } else {
                        "${page.contextPath}/super-groups/${group.id.value}"
                    },
                method = FormMethod.post,
            ) {
                csrfInput(page.requiredCsrfToken())
                if (group != null) {
                    methodOverrideInput("put")
                    hiddenInput {
                        name = "version"
                        value = group.version.toString()
                    }
                }
                label {
                    +"Name"
                    textInput(name = "name") { value = group?.name?.value.orEmpty() }
                }
                label {
                    +"Pretty name"
                    textInput(name = "prettyName") { value = group?.prettyName?.value.orEmpty() }
                }
                label {
                    +"Swedish description"
                    textInput(name = "svDescription") {
                        value =
                            group
                                ?.description
                                ?.sv
                                ?.value
                                .orEmpty()
                    }
                }
                label {
                    +"English description"
                    textInput(name = "enDescription") {
                        value =
                            group
                                ?.description
                                ?.en
                                ?.value
                                .orEmpty()
                    }
                }
                select {
                    name = "type"
                    types.forEach { type ->
                        option {
                            value = type.value
                            selected = type == group?.type
                            +type.value
                        }
                    }
                }
                button { +if (group == null) "Create" else "Save" }
            }
        }
    }

fun renderSuperGroupDetails(
    page: GammaPageContext,
    group: SuperGroup,
    children: List<Group>,
): String =
    gammaPage("Super group details", page) {
        article {
            header { +group.prettyName.value }
            ul(classes = "tuple") {
                li { +"Name: ${group.name.value}" }
                li { +"Pretty name: ${group.prettyName.value}" }
                li { +"Swedish description: ${group.description.sv.value}" }
                li { +"English description: ${group.description.en.value}" }
                li { +"Type: ${group.type.value}" }
            }
            if (page.viewer?.isAdmin == true) {
                form(
                    action = "${page.contextPath}/super-groups/${group.id.value}/edit",
                    method = FormMethod.get,
                ) { button { +"Edit" } }
                if (children.isEmpty()) {
                    form(action = "${page.contextPath}/super-groups/${group.id.value}", method = FormMethod.post) {
                        csrfInput(page.requiredCsrfToken())
                        methodOverrideInput("delete")
                        button { +"Delete" }
                    }
                }
            }
        }
    }

fun renderPosts(
    page: GammaPageContext,
    posts: List<Post>,
): String =
    gammaPage("Posts", page) {
        if (page.viewer?.isAdmin == true) a(href = "${page.contextPath}/posts/create") { +"Create post" }
        form(action = "${page.contextPath}/posts/order", method = FormMethod.post) {
            csrfInput(page.requiredCsrfToken())
            methodOverrideInput("put")
            table {
                tbody {
                    posts.forEach { post ->
                        tr {
                            td {
                                hiddenInput {
                                    name = "list"
                                    value = post.id.value.toString()
                                }
                                +post.name.en.value
                            }
                            td { a(href = "${page.contextPath}/posts/${post.id.value}") { +"Details" } }
                        }
                    }
                }
            }
        }
    }

fun renderPostEditor(
    page: GammaPageContext,
    post: Post? = null,
): String =
    gammaPage(if (post == null) "Create post" else "Edit post", page) {
        article {
            header { +if (post == null) "Create post" else "Post details" }
            form(
                action =
                    if (post ==
                        null
                    ) {
                        "${page.contextPath}/posts"
                    } else {
                        "${page.contextPath}/posts/${post.id.value}"
                    },
                method = FormMethod.post,
            ) {
                csrfInput(page.requiredCsrfToken())
                if (post != null) {
                    methodOverrideInput("put")
                    hiddenInput {
                        name = "version"
                        value = post.version.toString()
                    }
                }
                label {
                    +"Swedish name"
                    textInput(name = "svName") {
                        value =
                            post
                                ?.name
                                ?.sv
                                ?.value
                                .orEmpty()
                    }
                }
                label {
                    +"English name"
                    textInput(name = "enName") {
                        value =
                            post
                                ?.name
                                ?.en
                                ?.value
                                .orEmpty()
                    }
                }
                label {
                    +"Email prefix"
                    textInput(name = "emailPrefix") { value = post?.emailPrefix?.value.orEmpty() }
                }
                button { +if (post == null) "Create" else "Save" }
            }
        }
    }

fun renderPostDetails(
    page: GammaPageContext,
    post: Post,
): String =
    gammaPage("Post details", page) {
        article {
            header { +"Post details" }
            p { +post.name.sv.value }
            p { +post.name.en.value }
            p { +post.emailPrefix.value }
            if (page.viewer?.isAdmin == true) {
                form(
                    action = "${page.contextPath}/posts/${post.id.value}/edit",
                    method = FormMethod.get,
                ) { button { +"Edit post" } }
                form(action = "${page.contextPath}/posts/${post.id.value}", method = FormMethod.post) {
                    csrfInput(page.requiredCsrfToken())
                    methodOverrideInput("delete")
                    button { +"Delete" }
                }
            }
        }
    }

fun renderTypes(
    page: GammaPageContext,
    types: List<SuperGroupType>,
): String =
    gammaPage("Types", page) {
        article {
            header { +"Create new super group type" }
            form(action = "${page.contextPath}/types", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                textInput(name = "type")
                button { +"Save" }
            }
        }
        table {
            tbody {
                types.forEach { type ->
                    tr {
                        td { +type.value }
                        td { a(href = "${page.contextPath}/types/${type.value}") { +"Details" } }
                    }
                }
            }
        }
    }

fun renderTypeDetails(
    page: GammaPageContext,
    type: SuperGroupType,
    groups: List<SuperGroup>,
): String =
    gammaPage("Type details", page) {
        article {
            header { +"Type details" }
            p { +type.value }
            if (groups.isEmpty()) {
                form(action = "${page.contextPath}/types/${type.value}", method = FormMethod.post) {
                    csrfInput(page.requiredCsrfToken())
                    methodOverrideInput("delete")
                    button { +"Delete" }
                }
            }
        }
    }

private fun kotlinx.html.FlowContent.memberRow(
    users: List<DirectoryUser>,
    posts: List<Post>,
    membership: Membership?,
) {
    div(classes = "member-row") {
        select(classes = "userId") {
            name = "userId"
            users.forEach { user ->
                option {
                    value = user.id.value.toString()
                    selected = user.id == membership?.userId
                    +user.nick.value
                }
            }
        }
        select(classes = "postId") {
            name = "postId"
            posts.forEach { post ->
                option {
                    value = post.id.value.toString()
                    selected = post.id == membership?.postId
                    +post.name.en.value
                }
            }
        }
        textInput(name = "unofficialPostName", classes = "unofficialPostName") {
            value =
                membership?.unofficialPostName?.value.orEmpty()
        }
        button(type = ButtonType.button) {
            attributes["_"] = "on click remove closest .member-row"
            +"Delete"
        }
    }
}

private fun kotlinx.html.FlowContent.groupImage(
    page: GammaPageContext,
    group: Group,
    kind: String,
    canEdit: Boolean,
) {
    article {
        header { +"Group $kind" }
        img(src = "${page.contextPath}/images/group/$kind/${group.id.value}?v=${group.version}", alt = "Group $kind")
        if (canEdit) {
            form(
                action = "${page.contextPath}/groups/$kind/${group.id.value}",
                method = FormMethod.post,
                encType = FormEncType.multipartFormData,
            ) {
                csrfInput(page.requiredCsrfToken())
                methodOverrideInput("put")
                input(type = InputType.file, name = "file") { required = true }
                button { +"Upload ${kind.replaceFirstChar(Char::uppercase)}" }
            }
        }
    }
}

private fun GammaPageContext.requiredCsrfToken(): String = checkNotNull(csrfToken)
