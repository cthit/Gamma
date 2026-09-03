// Spring response and form-binding signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.organization.EmailPrefix
import it.chalmers.gamma.organization.Group
import it.chalmers.gamma.organization.GroupId
import it.chalmers.gamma.organization.GroupImageKind
import it.chalmers.gamma.organization.GroupImageUpload
import it.chalmers.gamma.organization.GroupImages
import it.chalmers.gamma.organization.LocalizedText
import it.chalmers.gamma.organization.Membership
import it.chalmers.gamma.organization.NewGroup
import it.chalmers.gamma.organization.NewGroupMembership
import it.chalmers.gamma.organization.NewPost
import it.chalmers.gamma.organization.NewSuperGroup
import it.chalmers.gamma.organization.OrganizationAdministration
import it.chalmers.gamma.organization.OrganizationName
import it.chalmers.gamma.organization.OrganizationStore
import it.chalmers.gamma.organization.Post
import it.chalmers.gamma.organization.PostId
import it.chalmers.gamma.organization.PrettyName
import it.chalmers.gamma.organization.SuperGroup
import it.chalmers.gamma.organization.SuperGroupId
import it.chalmers.gamma.organization.SuperGroupType
import it.chalmers.gamma.organization.UnofficialPostName
import it.chalmers.gamma.organization.views.GroupDetailsPage
import it.chalmers.gamma.organization.views.GroupEditor
import it.chalmers.gamma.organization.views.parsePersonalPostNames
import it.chalmers.gamma.organization.views.renderGroupDetails
import it.chalmers.gamma.organization.views.renderGroupEditor
import it.chalmers.gamma.organization.views.renderGroups
import it.chalmers.gamma.organization.views.renderNewMember
import it.chalmers.gamma.organization.views.renderPostDetails
import it.chalmers.gamma.organization.views.renderPostEditor
import it.chalmers.gamma.organization.views.renderPosts
import it.chalmers.gamma.organization.views.renderSuperGroupDetails
import it.chalmers.gamma.organization.views.renderSuperGroupEditor
import it.chalmers.gamma.organization.views.renderSuperGroups
import it.chalmers.gamma.organization.views.renderTypeDetails
import it.chalmers.gamma.organization.views.renderTypes
import it.chalmers.gamma.users.DirectoryUserPageRequest
import it.chalmers.gamma.users.DirectoryUserScope
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserStore
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class OrganizationController(
    private val organizations: OrganizationStore,
    private val administration: OrganizationAdministration,
    private val userStore: UserStore,
    private val images: GroupImages,
) {
    @GetMapping("/groups", produces = [MediaType.TEXT_HTML_VALUE])
    fun groups(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderGroups(pageContext(authentication, csrfToken, request), organizations.listGroups())

    @GetMapping("/groups/create", produces = [MediaType.TEXT_HTML_VALUE])
    fun createGroupPage(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderGroupEditor(
        pageContext(authentication, csrfToken, request),
        GroupEditor(organizations.listSuperGroups()),
    )

    @PostMapping("/groups/create")
    fun createGroup(
        authentication: Authentication,
        @RequestParam name: String,
        @RequestParam prettyName: String,
        @RequestParam superGroupId: String,
    ): ResponseEntity<Void> {
        val id =
            administration.createGroup(
                authentication.actor(),
                NewGroup(OrganizationName(name), PrettyName(prettyName), SuperGroupId.parse(superGroupId)),
                emptyList(),
            )
        return redirect("/groups/${id.value}")
    }

    @GetMapping("/groups/new-member", produces = [MediaType.TEXT_HTML_VALUE])
    fun newMember(authentication: Authentication): String =
        renderNewMember(
            userStore
                .directoryUserPage(
                    DirectoryUserPageRequest("", null, DirectoryUserScope.administrator(authentication.userId())),
                ).users,
            organizations.listPosts(),
        )

    @GetMapping("/groups/{groupId}", produces = [MediaType.TEXT_HTML_VALUE])
    fun groupDetails(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable groupId: String,
    ): ResponseEntity<String> {
        val id = GroupId.parse(groupId)
        val group = organizations.findGroup(id) ?: return ResponseEntity.notFound().build()
        val memberships = organizations.membershipsForGroup(id)
        val directory =
            userStore
                .directoryUserPage(
                    DirectoryUserPageRequest("", null, DirectoryUserScope.administrator(authentication.userId())),
                ).users
                .associateBy { it.id }
        val posts = organizations.listPosts().associateBy { it.id }
        return ResponseEntity.ok(
            renderGroupDetails(
                pageContext(authentication, csrfToken, request),
                GroupDetailsPage(group, memberships, directory, posts, authentication.userId()),
            ),
        )
    }

    @GetMapping("/groups/{groupId}/cancel-edit", produces = [MediaType.TEXT_HTML_VALUE])
    fun cancelGroupEdit(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable groupId: String,
    ): ResponseEntity<String> = groupDetails(authentication, csrfToken, request, groupId)

    @GetMapping("/groups/{groupId}/edit", produces = [MediaType.TEXT_HTML_VALUE])
    fun editGroup(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable groupId: String,
    ): ResponseEntity<String> {
        val id = GroupId.parse(groupId)
        val group = organizations.findGroup(id) ?: return ResponseEntity.notFound().build()
        val directory =
            userStore
                .directoryUserPage(
                    DirectoryUserPageRequest("", null, DirectoryUserScope.administrator(authentication.userId())),
                ).users
        return ResponseEntity.ok(
            renderGroupEditor(
                pageContext(authentication, csrfToken, request),
                GroupEditor(
                    superGroups = organizations.listSuperGroups(),
                    group = group,
                    users = directory,
                    posts = organizations.listPosts(),
                    memberships = organizations.membershipsForGroup(id),
                ),
            ),
        )
    }

    @PutMapping("/groups/{groupId}")
    fun updateGroup(
        authentication: Authentication,
        @PathVariable groupId: String,
        @ModelAttribute form: UpdateGroupForm,
    ): ResponseEntity<Void> {
        val id = GroupId.parse(groupId)
        val superGroup = checkNotNull(organizations.findSuperGroup(SuperGroupId.parse(form.superGroupId)))
        val memberships = memberships(id, form.userId, form.postId, form.unofficialPostName)
        val current = checkNotNull(organizations.findGroup(id))
        administration.updateGroup(
            authentication.actor(),
            current.copy(
                version = form.version,
                name = OrganizationName(form.name),
                prettyName = PrettyName(form.prettyName),
                superGroup = superGroup,
            ),
            memberships,
        )
        return redirect("/groups/$groupId")
    }

    @DeleteMapping("/groups/{groupId}")
    fun deleteGroup(
        authentication: Authentication,
        @PathVariable groupId: String,
    ): ResponseEntity<Void> {
        administration.deleteGroup(authentication.actor(), GroupId.parse(groupId))
        return redirect("/groups")
    }

    @GetMapping("/groups/{groupId}/my-posts")
    fun myPosts(
        @PathVariable groupId: String,
    ): ResponseEntity<Void> = redirect("/groups/$groupId")

    @PutMapping("/groups/{groupId}/my-posts")
    fun updateMyPosts(
        authentication: Authentication,
        request: HttpServletRequest,
        @PathVariable groupId: String,
    ): ResponseEntity<Void> {
        val submitted = parsePersonalPostNames(request.parameterMap.mapValues { it.value.toList() })
        submitted.forEach { (postId, name) ->
            administration.changeMyUnofficialPostName(
                authentication.actor(),
                GroupId.parse(groupId),
                postId,
                name,
            )
        }
        return redirect("/groups/$groupId")
    }

    @PutMapping("/groups/{kind}/{groupId}")
    fun uploadGroupImage(
        authentication: Authentication,
        @PathVariable kind: String,
        @PathVariable groupId: String,
        @RequestParam file: MultipartFile,
    ): ResponseEntity<Void> {
        require(!file.isEmpty) { "Image is required" }
        val imageKind = kind.toGroupImageKind()
        images.replace(
            authentication.actor(),
            GroupId.parse(groupId),
            imageKind,
            GroupImageUpload(file.bytes, file.contentType),
        )
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/groups/{kind}/{groupId}")
    fun deleteGroupImage(
        authentication: Authentication,
        @PathVariable kind: String,
        @PathVariable groupId: String,
    ): ResponseEntity<Void> {
        images.delete(authentication.actor(), GroupId.parse(groupId), kind.toGroupImageKind())
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/super-groups", produces = [MediaType.TEXT_HTML_VALUE])
    fun superGroups(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderSuperGroups(
        pageContext(authentication, csrfToken, request),
        organizations.listSuperGroups(),
    )

    @GetMapping("/super-groups/create", produces = [MediaType.TEXT_HTML_VALUE])
    fun createSuperGroupPage(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderSuperGroupEditor(pageContext(authentication, csrfToken, request), organizations.listSuperGroupTypes())

    @PostMapping("/super-groups")
    fun createSuperGroup(
        authentication: Authentication,
        @ModelAttribute form: CreateSuperGroupForm,
    ): ResponseEntity<Void> {
        val id =
            administration.createSuperGroup(
                authentication.actor(),
                NewSuperGroup(
                    OrganizationName(form.name),
                    PrettyName(form.prettyName),
                    SuperGroupType(form.type),
                    LocalizedText.of(form.svDescription, form.enDescription),
                ),
            )
        return redirect("/super-groups/${id.value}")
    }

    @GetMapping("/super-groups/{superGroupId}", produces = [MediaType.TEXT_HTML_VALUE])
    fun superGroupDetails(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable superGroupId: String,
    ): ResponseEntity<String> {
        val id = SuperGroupId.parse(superGroupId)
        val group = organizations.findSuperGroup(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(
            renderSuperGroupDetails(
                pageContext(authentication, csrfToken, request),
                group,
                organizations.listGroups(id),
            ),
        )
    }

    @GetMapping("/super-groups/{superGroupId}/edit", produces = [MediaType.TEXT_HTML_VALUE])
    fun editSuperGroup(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable superGroupId: String,
    ): ResponseEntity<String> {
        val group =
            organizations.findSuperGroup(SuperGroupId.parse(superGroupId))
                ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(
            renderSuperGroupEditor(
                pageContext(authentication, csrfToken, request),
                organizations.listSuperGroupTypes(),
                group,
            ),
        )
    }

    @GetMapping("/super-groups/{superGroupId}/cancel-edit", produces = [MediaType.TEXT_HTML_VALUE])
    fun cancelSuperGroupEdit(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable superGroupId: String,
    ): ResponseEntity<String> = superGroupDetails(authentication, csrfToken, request, superGroupId)

    @PutMapping("/super-groups/{superGroupId}")
    fun updateSuperGroup(
        authentication: Authentication,
        @PathVariable superGroupId: String,
        @ModelAttribute form: UpdateSuperGroupForm,
    ): ResponseEntity<Void> {
        administration.updateSuperGroup(
            authentication.actor(),
            SuperGroup(
                SuperGroupId.parse(superGroupId),
                form.version,
                OrganizationName(form.name),
                PrettyName(form.prettyName),
                SuperGroupType(form.type),
                LocalizedText.of(form.svDescription, form.enDescription),
            ),
        )
        return redirect("/super-groups/$superGroupId")
    }

    @DeleteMapping("/super-groups/{superGroupId}")
    fun deleteSuperGroup(
        authentication: Authentication,
        @PathVariable superGroupId: String,
    ): ResponseEntity<Void> {
        administration.deleteSuperGroup(authentication.actor(), SuperGroupId.parse(superGroupId))
        return redirect("/super-groups")
    }

    @GetMapping("/posts", produces = [MediaType.TEXT_HTML_VALUE])
    fun posts(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderPosts(pageContext(authentication, csrfToken, request), organizations.listPosts())

    @GetMapping("/posts/create", produces = [MediaType.TEXT_HTML_VALUE])
    fun createPostPage(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderPostEditor(pageContext(authentication, csrfToken, request))

    @PostMapping("/posts")
    fun createPost(
        authentication: Authentication,
        @RequestParam svName: String,
        @RequestParam enName: String,
        @RequestParam emailPrefix: String,
    ): ResponseEntity<Void> {
        val id =
            administration.createPost(
                authentication.actor(),
                NewPost(LocalizedText.of(svName, enName), EmailPrefix(emailPrefix)),
            )
        return redirect("/posts/${id.value}")
    }

    @GetMapping("/posts/{postId}", produces = [MediaType.TEXT_HTML_VALUE])
    fun postDetails(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable postId: String,
    ): ResponseEntity<String> {
        val post = organizations.findPost(PostId.parse(postId)) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(renderPostDetails(pageContext(authentication, csrfToken, request), post))
    }

    @GetMapping("/posts/{postId}/edit", produces = [MediaType.TEXT_HTML_VALUE])
    fun editPost(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable postId: String,
    ): ResponseEntity<String> {
        val post = organizations.findPost(PostId.parse(postId)) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(renderPostEditor(pageContext(authentication, csrfToken, request), post))
    }

    @PutMapping("/posts/{postId}")
    fun updatePost(
        authentication: Authentication,
        @PathVariable postId: String,
        @ModelAttribute form: UpdatePostForm,
    ): ResponseEntity<Void> {
        val id = PostId.parse(postId)
        val current = checkNotNull(organizations.findPost(id))
        administration.updatePost(
            authentication.actor(),
            current.copy(
                version = form.version,
                name = LocalizedText.of(form.svName, form.enName),
                emailPrefix = EmailPrefix(form.emailPrefix),
            ),
        )
        return redirect("/posts/$postId")
    }

    @DeleteMapping("/posts/{postId}")
    fun deletePost(
        authentication: Authentication,
        @PathVariable postId: String,
    ): ResponseEntity<Void> {
        administration.deletePost(authentication.actor(), PostId.parse(postId))
        return redirect("/posts")
    }

    @PutMapping("/posts/order")
    fun reorderPosts(
        authentication: Authentication,
        @RequestParam list: List<String>,
    ): ResponseEntity<Void> {
        administration.reorderPosts(authentication.actor(), list.map(PostId::parse))
        return ResponseEntity.ok().build()
    }

    private fun memberships(
        groupId: GroupId,
        users: List<String>?,
        posts: List<String>?,
        names: List<String>?,
    ): List<Membership> =
        users.orEmpty().mapIndexed { index, userId ->
            Membership(
                UserId.parse(userId),
                groupId,
                PostId.parse(checkNotNull(posts?.getOrNull(index))),
                UnofficialPostName(names?.getOrNull(index)?.ifBlank { null }),
            )
        }
}

@RestController
class OrganizationTypeController(
    private val organizations: OrganizationStore,
    private val administration: OrganizationAdministration,
) {
    @GetMapping("/types", produces = [MediaType.TEXT_HTML_VALUE])
    fun types(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderTypes(
        pageContext(authentication, csrfToken, request),
        organizations.listSuperGroupTypes(),
    )

    @PostMapping("/types")
    fun createType(
        authentication: Authentication,
        @RequestParam type: String,
    ): ResponseEntity<Void> {
        administration.createSuperGroupType(authentication.actor(), SuperGroupType(type))
        return redirect("/types")
    }

    @GetMapping("/types/{type}", produces = [MediaType.TEXT_HTML_VALUE])
    fun typeDetails(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable type: String,
    ): String {
        val value = SuperGroupType(type)
        return renderTypeDetails(
            pageContext(authentication, csrfToken, request),
            value,
            organizations.listSuperGroups(value),
        )
    }

    @DeleteMapping("/types/{type}")
    fun deleteType(
        authentication: Authentication,
        @PathVariable type: String,
    ): ResponseEntity<Void> {
        administration.deleteSuperGroupType(authentication.actor(), SuperGroupType(type))
        return redirect("/types")
    }
}

data class UpdateGroupForm(
    val name: String,
    val prettyName: String,
    val superGroupId: String,
    val version: Int,
    val userId: List<String>? = null,
    val postId: List<String>? = null,
    val unofficialPostName: List<String>? = null,
)

data class CreateSuperGroupForm(
    val name: String,
    val prettyName: String,
    val type: String,
    val svDescription: String,
    val enDescription: String,
)

data class UpdateSuperGroupForm(
    val name: String,
    val prettyName: String,
    val type: String,
    val svDescription: String,
    val enDescription: String,
    val version: Int,
)

data class UpdatePostForm(
    val svName: String,
    val enName: String,
    val emailPrefix: String,
    val version: Int,
)

private fun String.toGroupImageKind(): GroupImageKind =
    when (this) {
        "avatar" -> GroupImageKind.AVATAR
        "banner" -> GroupImageKind.BANNER
        else -> throw IllegalArgumentException("Unknown image kind")
    }
