// Spring response and form-binding signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.CreateUser
import it.chalmers.gamma.users.GammaPrincipal
import it.chalmers.gamma.users.Language
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.RequestActivation
import it.chalmers.gamma.users.RequestPasswordReset
import it.chalmers.gamma.users.UserAccessFlagKind
import it.chalmers.gamma.users.UserAccessFlags
import it.chalmers.gamma.users.UserDetails
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserProfile
import it.chalmers.gamma.users.UserQueries
import it.chalmers.gamma.users.views.NewUserForm
import it.chalmers.gamma.users.views.newUserFromForm
import it.chalmers.gamma.users.views.renderAccessFlags
import it.chalmers.gamma.users.views.renderActivateCid
import it.chalmers.gamma.users.views.renderCreateUser
import it.chalmers.gamma.users.views.renderDeleteAccount
import it.chalmers.gamma.users.views.renderEditMyAccount
import it.chalmers.gamma.users.views.renderEditUser
import it.chalmers.gamma.users.views.renderEmailSent
import it.chalmers.gamma.users.views.renderForgotPassword
import it.chalmers.gamma.users.views.renderMyAccount
import it.chalmers.gamma.users.views.renderUserAgreement
import it.chalmers.gamma.users.views.renderUserDetails
import it.chalmers.gamma.users.views.renderUsers
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
// Keeping the user route contract together makes its authorization and mutation surface auditable.
@Suppress("TooManyFunctions")
class UserController(
    private val userQueries: UserQueries,
    private val access: UserAccessFlags,
    private val userCreation: CreateUser,
    private val deletion: UserDeletionCascade,
) {
    @GetMapping("/users", produces = [MediaType.TEXT_HTML_VALUE])
    fun users(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @RequestParam(defaultValue = "") query: String,
    ): String =
        renderUsers(
            pageContext(authentication, csrfToken, request),
            userQueries.administrativeUsers(authentication.userId()),
            query,
        )

    @GetMapping("/users/create", produces = [MediaType.TEXT_HTML_VALUE])
    fun createUserPage(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderCreateUser(pageContext(authentication, csrfToken, request))

    @PostMapping("/users/create")
    fun createUser(
        authentication: Authentication,
        @ModelAttribute form: CreateUserForm,
    ): ResponseEntity<Void> {
        val id =
            userCreation.create(
                authentication.actor(),
                newUserFromForm(form.user),
            )
        return redirect("/users/${id.value}")
    }

    @GetMapping("/users/{userId}", produces = [MediaType.TEXT_HTML_VALUE])
    fun userDetails(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @org.springframework.web.bind.annotation.PathVariable userId: String,
        @RequestParam(required = false) updated: String?,
    ): ResponseEntity<String> {
        val user =
            userQueries.administrativeUser(authentication.userId(), UserId.parse(userId))?.profile ?: return notFound()
        return ResponseEntity
            .ok()
            .contentType(MediaType.TEXT_HTML)
            .body(
                renderUserDetails(
                    pageContext(authentication, csrfToken, request),
                    user.details(),
                    updated?.let {
                        "User updated"
                    },
                ),
            )
    }

    @GetMapping("/users/{userId}/edit", produces = [MediaType.TEXT_HTML_VALUE])
    fun editUser(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @org.springframework.web.bind.annotation.PathVariable userId: String,
    ): ResponseEntity<String> {
        val user =
            userQueries.administrativeUser(authentication.userId(), UserId.parse(userId))?.profile ?: return notFound()
        return ResponseEntity.ok(renderEditUser(pageContext(authentication, csrfToken, request), user))
    }

    @DeleteMapping("/users/{userId}")
    fun deleteUser(
        authentication: Authentication,
        @org.springframework.web.bind.annotation.PathVariable userId: String,
    ): ResponseEntity<Void> {
        val id = UserId.parse(userId)
        deletion.delete(AccountDeletion.Administrator(authentication.actor(), id))
        return redirect("/users")
    }

    @GetMapping("/admins", produces = [MediaType.TEXT_HTML_VALUE])
    fun admins(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): String =
        accessFlags(
            authentication,
            pageContext(authentication, csrfToken, request),
            UserAccessFlagKind.ADMINISTRATOR,
            "Admins",
            "/admins",
        )

    @PutMapping("/admins")
    fun updateAdmins(
        authentication: Authentication,
        @RequestParam(required = false) userId: List<String>?,
    ): ResponseEntity<Void> = updateAccessFlags(authentication, UserAccessFlagKind.ADMINISTRATOR, userId, "/admins")

    @GetMapping("/gdpr", produces = [MediaType.TEXT_HTML_VALUE])
    fun gdpr(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): String =
        accessFlags(
            authentication,
            pageContext(authentication, csrfToken, request),
            UserAccessFlagKind.GDPR_TRAINED,
            "GDPR",
            "/gdpr",
        )

    @PutMapping("/gdpr")
    fun updateGdpr(
        authentication: Authentication,
        @RequestParam(required = false) userId: List<String>?,
    ): ResponseEntity<Void> = updateAccessFlags(authentication, UserAccessFlagKind.GDPR_TRAINED, userId, "/gdpr")

    @GetMapping("/", "/me", produces = [MediaType.TEXT_HTML_VALUE])
    fun me(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @RequestParam(required = false) updated: String?,
        @RequestParam(required = false) passwordChanged: String?,
    ): String {
        val message =
            when {
                updated != null -> "You have successfully edited your information"
                passwordChanged != null -> "You have created a new password"
                else -> null
            }
        return renderMyAccount(
            pageContext(authentication, csrfToken, request),
            userQueries.myProfile(authentication.actor()),
            message,
        )
    }

    @GetMapping("/me/edit", produces = [MediaType.TEXT_HTML_VALUE])
    fun editMe(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): String =
        renderEditMyAccount(
            pageContext(authentication, csrfToken, request),
            userQueries.myProfile(authentication.actor()),
        )

    @GetMapping("/me/cancel-edit", produces = [MediaType.TEXT_HTML_VALUE])
    fun cancelEditMe(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): String =
        renderMyAccount(
            pageContext(authentication, csrfToken, request),
            userQueries.myProfile(authentication.actor()),
        )

    @GetMapping("/delete-your-account", produces = [MediaType.TEXT_HTML_VALUE])
    fun deleteAccountPage(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderDeleteAccount(pageContext(authentication, csrfToken, request))

    @DeleteMapping("/delete-your-account", produces = [MediaType.TEXT_HTML_VALUE])
    fun deleteAccount(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        session: HttpSession,
        @RequestParam password: String,
    ): ResponseEntity<String> {
        val deleted = deletion.delete(AccountDeletion.Personal(authentication.actor(), PlainTextPassword(password)))
        if (!deleted) {
            val body = renderDeleteAccount(pageContext(authentication, csrfToken, request), "Incorrect password")
            return ResponseEntity.status(409).body(body)
        }
        session.invalidate()
        return ResponseEntity.status(302).location(URI.create("/login?deleted")).build()
    }

    @GetMapping("/user-agreement", produces = [MediaType.TEXT_HTML_VALUE])
    fun userAgreement(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderUserAgreement(pageContext(authentication, csrfToken, request))

    @PutMapping("/user-agreement", produces = [MediaType.TEXT_HTML_VALUE])
    fun acceptUserAgreement(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderUserAgreement(pageContext(authentication, csrfToken, request))

    private fun accessFlags(
        authentication: Authentication,
        page: it.chalmers.gamma.platform.html.GammaPageContext,
        kind: UserAccessFlagKind,
        title: String,
        path: String,
    ): String =
        renderAccessFlags(
            page,
            title,
            path,
            access.list(authentication.actor(), kind),
        )

    private fun updateAccessFlags(
        authentication: Authentication,
        kind: UserAccessFlagKind,
        userIds: List<String>?,
        path: String,
    ): ResponseEntity<Void> {
        access.replace(
            authentication.actor(),
            kind,
            userIds.orEmpty().map(UserId::parse).toSet(),
        )
        return redirect(path)
    }
}

@RestController
class AccountActivationController(
    private val activationRequests: RequestActivation,
    private val resetRequests: RequestPasswordReset,
) {
    @GetMapping("/activate-cid", produces = [MediaType.TEXT_HTML_VALUE])
    fun activateCidPage(
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderActivateCid(pageContext(null, csrfToken, request, false))

    @PostMapping("/activate-cid")
    fun activateCid(
        @RequestParam cid: String,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        activationRequests.request(Actor.Anonymous, Cid(cid), request.remoteAddr)
        return redirect("/email-sent")
    }

    @GetMapping("/email-sent", produces = [MediaType.TEXT_HTML_VALUE])
    fun emailSent(
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderEmailSent(pageContext(null, csrfToken, request, false))

    @GetMapping("/forgot-password", produces = [MediaType.TEXT_HTML_VALUE])
    fun forgotPassword(
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderForgotPassword(pageContext(null, csrfToken, request, false))

    @PostMapping("/forgot-password", produces = [MediaType.TEXT_HTML_VALUE])
    fun requestPasswordReset(
        @RequestParam cidOrEmail: String,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): String {
        resetRequests.request(Actor.Anonymous, cidOrEmail, request.remoteAddr)
        return renderForgotPassword(pageContext(null, csrfToken, request, false), true)
    }
}

data class CreateUserForm(
    val cid: String,
    val nick: String,
    val firstName: String,
    val lastName: String,
    val acceptanceYear: Int,
    val language: Language,
    val email: String,
    val password: String,
) {
    val user = NewUserForm(cid, nick, firstName, lastName, acceptanceYear, language, email, password)
}

internal fun Authentication.userId() = UserId.parse((principal as GammaPrincipal).userId)

private fun UserProfile.details() = UserDetails(id, cid, nick, firstName, lastName, acceptanceYear, version)

internal fun redirect(path: String): ResponseEntity<Void> =
    ResponseEntity.status(302).location(URI.create(path)).build()

private fun <T : Any> notFound(): ResponseEntity<T> = ResponseEntity.notFound().build()
