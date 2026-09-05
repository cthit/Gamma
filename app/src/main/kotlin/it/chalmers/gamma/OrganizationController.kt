// Spring response and form-binding signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.organization.ChangeMyPostNames
import it.chalmers.gamma.organization.CreateGroup
import it.chalmers.gamma.organization.DeleteGroup
import it.chalmers.gamma.organization.GroupEditor
import it.chalmers.gamma.organization.GroupId
import it.chalmers.gamma.organization.GroupImageKind
import it.chalmers.gamma.organization.GroupImageUpload
import it.chalmers.gamma.organization.GroupImages
import it.chalmers.gamma.organization.GroupUpdate
import it.chalmers.gamma.organization.NewGroup
import it.chalmers.gamma.organization.NewGroupMembership
import it.chalmers.gamma.organization.OrganizationName
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.PostId
import it.chalmers.gamma.organization.PrettyName
import it.chalmers.gamma.organization.ReadGroupPages
import it.chalmers.gamma.organization.SuperGroupId
import it.chalmers.gamma.organization.SuperGroupType
import it.chalmers.gamma.organization.SuperGroupTypes
import it.chalmers.gamma.organization.UnofficialPostName
import it.chalmers.gamma.organization.UpdateGroup
import it.chalmers.gamma.organization.views.parsePersonalPostNames
import it.chalmers.gamma.organization.views.renderGroupDetails
import it.chalmers.gamma.organization.views.renderGroupEditor
import it.chalmers.gamma.organization.views.renderGroups
import it.chalmers.gamma.organization.views.renderNewMember
import it.chalmers.gamma.organization.views.renderTypeDetails
import it.chalmers.gamma.organization.views.renderTypes
import it.chalmers.gamma.users.UserId
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

// Explicit operation dependencies keep transaction ownership visible at each HTTP call site.
@Suppress("LongParameterList")
@RestController
class OrganizationController(
    private val organizations: OrganizationQueries,
    private val groupPages: ReadGroupPages,
    private val images: GroupImages,
    private val groupCreation: CreateGroup,
    private val groupUpdates: UpdateGroup,
    private val groupDeletion: DeleteGroup,
    private val personalPostNames: ChangeMyPostNames,
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
            groupCreation.create(
                authentication.actor(),
                NewGroup(OrganizationName(name), PrettyName(prettyName), SuperGroupId.parse(superGroupId)),
                emptyList(),
            )
        return redirect("/groups/${id.value}")
    }

    @GetMapping("/groups/new-member", produces = [MediaType.TEXT_HTML_VALUE])
    fun newMember(authentication: Authentication): String {
        val options = groupPages.newMember(authentication.actor())
        return renderNewMember(options.users, options.posts)
    }

    @GetMapping("/groups/{groupId}", produces = [MediaType.TEXT_HTML_VALUE])
    fun groupDetails(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable groupId: String,
    ): ResponseEntity<String> {
        val details =
            groupPages.details(authentication.actor(), GroupId.parse(groupId))
                ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(
            renderGroupDetails(pageContext(authentication, csrfToken, request), details),
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
        val editor =
            groupPages.editor(authentication.actor(), GroupId.parse(groupId))
                ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(
            renderGroupEditor(pageContext(authentication, csrfToken, request), editor),
        )
    }

    @PutMapping("/groups/{groupId}")
    fun updateGroup(
        authentication: Authentication,
        @PathVariable groupId: String,
        @ModelAttribute form: UpdateGroupForm,
    ): ResponseEntity<Void> {
        groupUpdates.update(
            authentication.actor(),
            GroupUpdate(
                groupId = GroupId.parse(groupId),
                expectedVersion = form.version,
                name = OrganizationName(form.name),
                prettyName = PrettyName(form.prettyName),
                superGroupId = SuperGroupId.parse(form.superGroupId),
                memberships = memberships(form.userId, form.postId, form.unofficialPostName),
            ),
        )
        return redirect("/groups/$groupId")
    }

    @DeleteMapping("/groups/{groupId}")
    fun deleteGroup(
        authentication: Authentication,
        @PathVariable groupId: String,
    ): ResponseEntity<Void> {
        groupDeletion.delete(authentication.actor(), GroupId.parse(groupId))
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
        personalPostNames.change(
            authentication.actor(),
            GroupId.parse(groupId),
            submitted,
        )
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

    private fun memberships(
        users: List<String>?,
        posts: List<String>?,
        names: List<String>?,
    ): List<NewGroupMembership> =
        users.orEmpty().mapIndexed { index, userId ->
            NewGroupMembership(
                UserId.parse(userId),
                PostId.parse(checkNotNull(posts?.getOrNull(index))),
                UnofficialPostName(names?.getOrNull(index)?.ifBlank { null }),
            )
        }
}

@RestController
class OrganizationTypeController(
    private val organizations: OrganizationQueries,
    private val superGroupTypes: SuperGroupTypes,
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
        superGroupTypes.create(authentication.actor(), SuperGroupType(type))
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
        superGroupTypes.delete(authentication.actor(), SuperGroupType(type))
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

private fun String.toGroupImageKind(): GroupImageKind =
    when (this) {
        "avatar" -> GroupImageKind.AVATAR
        "banner" -> GroupImageKind.BANNER
        else -> throw IllegalArgumentException("Unknown image kind")
    }
