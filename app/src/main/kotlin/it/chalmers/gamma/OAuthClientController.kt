// Spring response and form-binding signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.oauth.AuthorityName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.OAuthClientAdministration
import it.chalmers.gamma.oauth.RawClientSecret
import it.chalmers.gamma.oauth.views.OAuthClientForm
import it.chalmers.gamma.oauth.views.newOAuthClient
import it.chalmers.gamma.oauth.views.renderApprovedClients
import it.chalmers.gamma.oauth.views.renderClientDetails
import it.chalmers.gamma.oauth.views.renderClientRestrictionRow
import it.chalmers.gamma.oauth.views.renderClients
import it.chalmers.gamma.oauth.views.renderCreateClient
import it.chalmers.gamma.oauth.views.renderNewAuthority
import it.chalmers.gamma.oauth.views.renderSuperGroupAuthorityRow
import it.chalmers.gamma.oauth.views.renderUserAuthorityRow
import it.chalmers.gamma.oauth.views.renderUserClients
import it.chalmers.gamma.organization.OrganizationStore
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class OAuthClientController(
    private val clients: OAuthClientAdministration,
    private val userStore: UserStore,
    private val organizations: OrganizationStore,
) {
    @GetMapping("/clients", produces = [MediaType.TEXT_HTML_VALUE])
    fun officialClients(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderClients(
        pageContext(authentication, csrfToken, request),
        "Clients",
        "/clients/create",
        clients.listOfficialClients(authentication.actor()),
    )

    @GetMapping("/my-clients", produces = [MediaType.TEXT_HTML_VALUE])
    fun myClients(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderClients(
        pageContext(authentication, csrfToken, request),
        "My clients",
        "/my-clients/create",
        clients.listMyClients(authentication.actor()),
    )

    @GetMapping("/clients/create", produces = [MediaType.TEXT_HTML_VALUE])
    fun createOfficialClientPage(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderCreateClient(pageContext(authentication, csrfToken, request), personal = false)

    @GetMapping("/my-clients/create", produces = [MediaType.TEXT_HTML_VALUE])
    fun createPersonalClientPage(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderCreateClient(pageContext(authentication, csrfToken, request), personal = true)

    @GetMapping("/clients/create/new-restriction", produces = [MediaType.TEXT_HTML_VALUE])
    fun newRestriction(): String = renderClientRestrictionRow(organizations.listSuperGroups())

    @PostMapping("/clients/create")
    fun createOfficialClient(
        authentication: Authentication,
        @ModelAttribute form: OAuthClientForm,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val created =
            clients.createOfficialClient(
                authentication.actor(),
                newOAuthClient(
                    form,
                    ClientOwner.Official,
                ),
            )
        return ResponseEntity.ok(
            renderClientDetails(
                pageContext(authentication, csrfToken, request),
                created.client,
                created.secret,
                created.apiCredential?.let { "${it.id.value}:${it.token.value}" },
            ),
        )
    }

    @PostMapping("/my-clients")
    fun createPersonalClient(
        authentication: Authentication,
        @ModelAttribute form: OAuthClientForm,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val created =
            clients.createMyClient(
                authentication.actor(),
                newOAuthClient(
                    form.copy(restrictions = null),
                    ClientOwner.User(authentication.userId()),
                ),
            )
        return ResponseEntity.ok(
            renderClientDetails(
                pageContext(authentication, csrfToken, request),
                created.client,
                created.secret,
                created.apiCredential?.let { "${it.id.value}:${it.token.value}" },
            ),
        )
    }

    @GetMapping("/clients/{clientUid}", produces = [MediaType.TEXT_HTML_VALUE])
    fun clientDetails(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable clientUid: String,
    ): String {
        val uid = ClientUid.parse(clientUid)
        return renderClientDetails(
            pageContext(authentication, csrfToken, request),
            clients.manageableClient(authentication.actor(), uid),
            authorities = clients.authorities(authentication.actor(), uid),
        )
    }

    @GetMapping("/clients/{clientUid}/authorities", produces = [MediaType.TEXT_HTML_VALUE])
    fun authorities(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable clientUid: String,
    ): String = clientDetails(authentication, csrfToken, request, clientUid)

    @GetMapping("/clients/{clientUid}/new-authority", produces = [MediaType.TEXT_HTML_VALUE])
    fun newAuthority(): String = renderNewAuthority()

    @GetMapping("/clients/authority/new-super-group", produces = [MediaType.TEXT_HTML_VALUE])
    fun newSuperGroupAuthority(): String = renderSuperGroupAuthorityRow(organizations.listSuperGroups())

    @GetMapping("/clients/authority/new-user", produces = [MediaType.TEXT_HTML_VALUE])
    fun newUserAuthority(authentication: Authentication): String =
        renderUserAuthorityRow(
            userStore
                .directoryUserPage(
                    DirectoryUserPageRequest(
                        "",
                        null,
                        DirectoryUserScope.administrator(authentication.userId()),
                    ),
                ).users,
        )

    @PostMapping("/clients/{clientUid}/reset")
    fun resetClientSecret(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable clientUid: String,
    ): ResponseEntity<String> {
        val uid = ClientUid.parse(clientUid)
        val secret = clients.resetSecret(authentication.actor(), uid)
        return ResponseEntity.ok(
            renderClientDetails(
                pageContext(authentication, csrfToken, request),
                clients.manageableClient(authentication.actor(), uid),
                secret,
                authorities = clients.authorities(authentication.actor(), uid),
            ),
        )
    }

    @DeleteMapping("/clients/{clientUid}")
    fun deleteClient(
        authentication: Authentication,
        @PathVariable clientUid: String,
    ): ResponseEntity<Void> {
        val uid = ClientUid.parse(clientUid)
        val personal = clients.manageableClient(authentication.actor(), uid).owner is ClientOwner.User
        clients.deleteClient(authentication.actor(), uid)
        return redirect(if (personal) "/my-clients" else "/clients")
    }

    @PostMapping("/clients/{clientUid}/authority")
    fun createAuthority(
        authentication: Authentication,
        @PathVariable clientUid: String,
        @RequestParam authority: String,
        @RequestParam(required = false) users: List<String>?,
        @RequestParam(required = false) superGroups: List<String>?,
    ): ResponseEntity<Void> {
        clients.createAuthority(
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
        clients.deleteAuthority(
            authentication.actor(),
            ClientUid.parse(clientUid),
            AuthorityName(authority),
        )
        return redirect("/clients/$clientUid")
    }

    @GetMapping("/user-clients", produces = [MediaType.TEXT_HTML_VALUE])
    fun userClients(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): String {
        val owned =
            clients.listPersonalClientsForAdministration(authentication.actor()).map { client ->
                val owner = client.owner as ClientOwner.User
                client to userStore.administrativeUser(authentication.userId(), owner.userId)?.profile
            }
        return renderUserClients(pageContext(authentication, csrfToken, request), owned)
    }

    @GetMapping("/me/accepted-clients", produces = [MediaType.TEXT_HTML_VALUE])
    fun approvedClients(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderApprovedClients(
        pageContext(authentication, csrfToken, request),
        clients.approvedClients(authentication.actor()),
    )

    @DeleteMapping("/me/accepted-clients/{clientUid}")
    fun revokeApproval(
        authentication: Authentication,
        @PathVariable clientUid: String,
    ): ResponseEntity<Void> {
        clients.revokeMyApproval(authentication.actor(), ClientUid.parse(clientUid))
        return redirect("/me/accepted-clients")
    }
}
