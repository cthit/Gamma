// Spring response signatures use Void at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.oauth.AuthorityName
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.OAuthClientNotFound
import it.chalmers.gamma.oauth.views.renderNewAuthority
import it.chalmers.gamma.oauth.views.renderSuperGroupAuthorityRow
import it.chalmers.gamma.oauth.views.renderUserAuthorityRow
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserQueries
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientAuthorityController(
    private val authorityCreation: CreateOAuthClientAuthority,
    private val authorityDeletion: DeleteOAuthClientAuthority,
    private val userQueries: UserQueries,
    private val organizations: OrganizationQueries,
) {
    @ExceptionHandler(OAuthClientNotFound::class)
    fun missingAuthority(): ResponseEntity<Void> = ResponseEntity.notFound().build()

    @GetMapping("/clients/{clientUid}/new-authority", produces = [MediaType.TEXT_HTML_VALUE])
    fun newAuthority(): String = renderNewAuthority()

    @GetMapping("/clients/authority/new-super-group", produces = [MediaType.TEXT_HTML_VALUE])
    fun newSuperGroupAuthority(): String = renderSuperGroupAuthorityRow(organizations.listSuperGroups())

    @GetMapping("/clients/authority/new-user", produces = [MediaType.TEXT_HTML_VALUE])
    fun newUserAuthority(authentication: Authentication): String =
        renderUserAuthorityRow(userQueries.administratorDirectoryUsers(authentication.userId()))

    @PostMapping("/clients/{clientUid}/authority")
    fun createAuthority(
        authentication: Authentication,
        @PathVariable clientUid: String,
        @RequestParam authority: String,
        @RequestParam(required = false) users: List<String>?,
        @RequestParam(required = false) superGroups: List<String>?,
    ): ResponseEntity<Void> {
        authorityCreation.create(
            authentication.actor(),
            ClientUid.parse(clientUid),
            AuthorityName(authority),
            users.orEmpty().mapTo(mutableSetOf(), UserId::parse),
            superGroups.orEmpty().mapTo(mutableSetOf(), java.util.UUID::fromString),
        )
        return redirect("/clients/$clientUid")
    }

    @DeleteMapping("/clients/{clientUid}/authority/{authority}")
    fun deleteAuthority(
        authentication: Authentication,
        @PathVariable clientUid: String,
        @PathVariable authority: String,
    ): ResponseEntity<Void> {
        authorityDeletion.delete(
            authentication.actor(),
            ClientUid.parse(clientUid),
            AuthorityName(authority),
        )
        return redirect("/clients/$clientUid")
    }
}
