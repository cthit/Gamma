// Spring response and form-binding signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.oauth.ClientApprovals
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.OAuthClientNotFound
import it.chalmers.gamma.oauth.ReadOAuthClientDetails
import it.chalmers.gamma.oauth.ReadOAuthClientLists
import it.chalmers.gamma.oauth.views.OAuthClientForm
import it.chalmers.gamma.oauth.views.newOAuthClient
import it.chalmers.gamma.oauth.views.renderApprovedClients
import it.chalmers.gamma.oauth.views.renderClientDetails
import it.chalmers.gamma.oauth.views.renderClientRestrictionRow
import it.chalmers.gamma.oauth.views.renderClients
import it.chalmers.gamma.oauth.views.renderCreateClient
import it.chalmers.gamma.oauth.views.renderUserClients
import it.chalmers.gamma.organization.OrganizationQueries
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
// Each dependency owns a distinct action or read; keep that wiring visible rather than hiding it in a bundle.
@Suppress("LongParameterList")
class OAuthClientController(
    private val clients: ReadOAuthClientLists,
    private val details: ReadOAuthClientDetails,
    private val creation: CreateOAuthClient,
    private val deletion: DeleteOAuthClient,
    private val secretReset: ResetOAuthClientSecret,
    private val approvals: ClientApprovals,
    private val organizations: OrganizationQueries,
) {
    @ExceptionHandler(OAuthClientNotFound::class)
    fun missingClient(): ResponseEntity<Void> = ResponseEntity.notFound().build()

    @GetMapping("/clients", produces = [MediaType.TEXT_HTML_VALUE])
    fun officialClients(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderClients(
        pageContext(authentication, csrfToken, request),
        "Clients",
        "/clients/create",
        clients.officialClients(authentication.actor()),
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
        clients.myClients(authentication.actor()),
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
            creation.create(
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
            creation.create(
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
        val result = details.read(authentication.actor(), uid)
        return renderClientDetails(
            pageContext(authentication, csrfToken, request),
            result.client,
            authorities = result.authorities,
        )
    }

    @GetMapping("/clients/{clientUid}/authorities", produces = [MediaType.TEXT_HTML_VALUE])
    fun authorities(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable clientUid: String,
    ): String = clientDetails(authentication, csrfToken, request, clientUid)

    @PostMapping("/clients/{clientUid}/reset")
    fun resetClientSecret(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable clientUid: String,
    ): ResponseEntity<String> {
        val uid = ClientUid.parse(clientUid)
        val result = secretReset.reset(authentication.actor(), uid)
        return ResponseEntity.ok(
            renderClientDetails(
                pageContext(authentication, csrfToken, request),
                result.client,
                result.secret,
                authorities = result.authorities,
            ),
        )
    }

    @DeleteMapping("/clients/{clientUid}")
    fun deleteClient(
        authentication: Authentication,
        @PathVariable clientUid: String,
    ): ResponseEntity<Void> {
        val uid = ClientUid.parse(clientUid)
        val personal = deletion.delete(authentication.actor(), uid) is ClientOwner.User
        return redirect(if (personal) "/my-clients" else "/clients")
    }

    @GetMapping("/user-clients", produces = [MediaType.TEXT_HTML_VALUE])
    fun userClients(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): String {
        val owned = clients.personalClientsForAdministration(authentication.actor())
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
        approvals.revoke(authentication.userId(), ClientUid.parse(clientUid))
        return redirect("/me/accepted-clients")
    }
}
