// Spring response and form-binding signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.organization.CreateSuperGroup
import it.chalmers.gamma.organization.DeleteSuperGroup
import it.chalmers.gamma.organization.LocalizedText
import it.chalmers.gamma.organization.NewSuperGroup
import it.chalmers.gamma.organization.OrganizationName
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.PrettyName
import it.chalmers.gamma.organization.SuperGroupId
import it.chalmers.gamma.organization.SuperGroupType
import it.chalmers.gamma.organization.SuperGroupUpdate
import it.chalmers.gamma.organization.UpdateSuperGroup
import it.chalmers.gamma.organization.views.renderSuperGroupDetails
import it.chalmers.gamma.organization.views.renderSuperGroupEditor
import it.chalmers.gamma.organization.views.renderSuperGroups
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
import org.springframework.web.bind.annotation.RestController

@RestController
class SuperGroupController(
    private val organizations: OrganizationQueries,
    private val superGroupCreation: CreateSuperGroup,
    private val superGroupUpdates: UpdateSuperGroup,
    private val superGroupDeletion: DeleteSuperGroup,
) {
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
            superGroupCreation.create(
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
        val details =
            organizations.superGroupDetails(SuperGroupId.parse(superGroupId))
                ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(
            renderSuperGroupDetails(
                pageContext(authentication, csrfToken, request),
                details.superGroup,
                details.groups,
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
        val editor =
            organizations.superGroupEditor(SuperGroupId.parse(superGroupId))
                ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(
            renderSuperGroupEditor(
                pageContext(authentication, csrfToken, request),
                editor.superGroupTypes,
                editor.superGroup,
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
        superGroupUpdates.update(
            authentication.actor(),
            SuperGroupUpdate(
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
        superGroupDeletion.delete(authentication.actor(), SuperGroupId.parse(superGroupId))
        return redirect("/super-groups")
    }
}

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
