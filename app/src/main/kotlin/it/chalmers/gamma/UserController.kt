// Spring response and form-binding signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.users.AcceptanceYear
import it.chalmers.gamma.users.ActivationCodeAdministration
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.Email
import it.chalmers.gamma.users.FirstName
import it.chalmers.gamma.users.GammaPrincipal
import it.chalmers.gamma.users.Language
import it.chalmers.gamma.users.LastName
import it.chalmers.gamma.users.MyAccount
import it.chalmers.gamma.users.MyProfileUpdate
import it.chalmers.gamma.users.Nick
import it.chalmers.gamma.users.PasswordResetAdministration
import it.chalmers.gamma.users.PasswordResetToken
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.RegistrationToken
import it.chalmers.gamma.users.UserAccessAdministration
import it.chalmers.gamma.users.UserAccessFlagKind
import it.chalmers.gamma.users.UserAdministration
import it.chalmers.gamma.users.UserAvatarUpload
import it.chalmers.gamma.users.UserAvatars
import it.chalmers.gamma.users.UserConflict
import it.chalmers.gamma.users.UserDetails
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserLifecycle
import it.chalmers.gamma.users.UserProfile
import it.chalmers.gamma.users.UserStore
import it.chalmers.gamma.users.views.NewUserForm
import it.chalmers.gamma.users.views.newUserFromForm
import it.chalmers.gamma.users.views.renderAccessFlags
import it.chalmers.gamma.users.views.renderActivateCid
import it.chalmers.gamma.users.views.renderActivationCodes
import it.chalmers.gamma.users.views.renderAllowList
import it.chalmers.gamma.users.views.renderChangePassword
import it.chalmers.gamma.users.views.renderCreateUser
import it.chalmers.gamma.users.views.renderDeleteAccount
import it.chalmers.gamma.users.views.renderEditMyAccount
import it.chalmers.gamma.users.views.renderEditUser
import it.chalmers.gamma.users.views.renderEmailSent
import it.chalmers.gamma.users.views.renderFinalizePasswordReset
import it.chalmers.gamma.users.views.renderForgotPassword
import it.chalmers.gamma.users.views.renderMyAccount
import it.chalmers.gamma.users.views.renderRegistration
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
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import java.time.Year

@RestController
// Keeping the user route contract together makes its authorization and mutation surface auditable.
@Suppress("TooManyFunctions")
class UserController(
    administration: UserAdministrationWeb,
    account: MyAccountWeb,
    private val access: UserAccessAdministration,
    private val activationCodes: ActivationCodeAdministration,
    private val settings: AppSettings,
) {
    private val userStore = administration.userStore
    private val userAdministration = administration.userAdministration
    private val passwordResets = administration.passwordResets
    private val myAccount = account.myAccount
    private val avatars = account.avatars

    @GetMapping("/users", produces = [MediaType.TEXT_HTML_VALUE])
    fun users(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @RequestParam(defaultValue = "") query: String,
    ): String =
        renderUsers(
            pageContext(authentication, csrfToken, request),
            userStore.administrativeUsers(authentication.userId()),
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
            userAdministration.createUser(
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
        val user = userAdministration.user(authentication.actor(), UserId.parse(userId)) ?: return notFound()
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
        val user = userAdministration.user(authentication.actor(), UserId.parse(userId)) ?: return notFound()
        return ResponseEntity.ok(renderEditUser(pageContext(authentication, csrfToken, request), user))
    }

    @PutMapping("/users/{userId}")
    fun updateUser(
        authentication: Authentication,
        @org.springframework.web.bind.annotation.PathVariable userId: String,
        @ModelAttribute form: UpdateUserForm,
    ): ResponseEntity<Void> {
        val existing = checkNotNull(userAdministration.user(authentication.actor(), UserId.parse(userId)))
        userAdministration.updateUser(
            authentication.actor(),
            existing.copy(
                nick = Nick(form.nick),
                firstName = FirstName(form.firstName),
                lastName = LastName(form.lastName),
                acceptanceYear = AcceptanceYear.of(form.acceptanceYear, Year.now().value),
                language = form.language,
                email = Email(form.email),
                version = form.version,
            ),
        )
        return redirect("/users/$userId?updated=true")
    }

    @DeleteMapping("/users/{userId}")
    fun deleteUser(
        authentication: Authentication,
        @org.springframework.web.bind.annotation.PathVariable userId: String,
    ): ResponseEntity<Void> {
        val id = UserId.parse(userId)
        userAdministration.deleteUser(authentication.actor(), id)
        return redirect("/users")
    }

    @PostMapping("/users/{userId}/generate-password-link", produces = [MediaType.TEXT_HTML_VALUE])
    fun generatePasswordLink(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @org.springframework.web.bind.annotation.PathVariable userId: String,
    ): ResponseEntity<String> {
        val id = UserId.parse(userId)
        val token = passwordResets.create(authentication.actor(), id)
        val user = userAdministration.user(authentication.actor(), id) ?: return notFound()
        return ResponseEntity.ok(
            renderUserDetails(
                pageContext(authentication, csrfToken, request),
                user.details(),
                resetLink = "${settings.publicBaseUrl}/forgot-password/finalize?token=${token.value}",
            ),
        )
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

    @GetMapping("/allow-list", produces = [MediaType.TEXT_HTML_VALUE])
    fun allowList(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): String =
        renderAllowList(
            pageContext(authentication, csrfToken, request),
            activationCodes.allowedCids(authentication.actor()),
        )

    @PutMapping("/allow-list")
    fun allowCid(
        authentication: Authentication,
        @RequestParam cid: String,
    ): ResponseEntity<Void> {
        activationCodes.allowCid(authentication.actor(), Cid(cid))
        return redirect("/allow-list")
    }

    @DeleteMapping("/allow-list/{cid}")
    fun retractCid(
        authentication: Authentication,
        @org.springframework.web.bind.annotation.PathVariable cid: String,
    ): ResponseEntity<Void> {
        activationCodes.retractCid(authentication.actor(), Cid(cid))
        return redirect("/allow-list")
    }

    @GetMapping("/activation-codes", produces = [MediaType.TEXT_HTML_VALUE])
    fun activationCodes(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): String =
        renderActivationCodes(
            pageContext(authentication, csrfToken, request),
            activationCodes.pendingActivations(authentication.actor()),
        )

    @DeleteMapping("/activation-codes/{cid}")
    fun deleteActivation(
        authentication: Authentication,
        @org.springframework.web.bind.annotation.PathVariable cid: String,
    ): ResponseEntity<Void> {
        activationCodes.deleteActivation(authentication.actor(), Cid(cid))
        return redirect("/activation-codes")
    }

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
            myAccount.profile(authentication.actor()),
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
            myAccount.profile(authentication.actor()),
        )

    @GetMapping("/me/cancel-edit", produces = [MediaType.TEXT_HTML_VALUE])
    fun cancelEditMe(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): String =
        renderMyAccount(
            pageContext(authentication, csrfToken, request),
            myAccount.profile(authentication.actor()),
        )

    @PutMapping("/me")
    fun updateMe(
        authentication: Authentication,
        @ModelAttribute form: UpdateMyProfileForm,
    ): ResponseEntity<Void> {
        val profile = myAccount.profile(authentication.actor())
        myAccount.updateProfile(
            authentication.actor(),
            MyProfileUpdate(
                Nick(form.nick),
                FirstName(form.firstName),
                LastName(form.lastName),
                profile.acceptanceYear,
                form.language,
                Email(form.email),
                form.version,
            ),
        )
        return redirect("/?updated=true")
    }

    @PutMapping("/me/email")
    fun updateMyEmail(
        authentication: Authentication,
        @RequestParam email: String,
    ): ResponseEntity<Void> {
        val profile = myAccount.profile(authentication.actor())
        myAccount.updateProfile(
            authentication.actor(),
            MyProfileUpdate(
                profile.nick,
                profile.firstName,
                profile.lastName,
                profile.acceptanceYear,
                profile.language,
                Email(email),
                profile.version,
            ),
        )
        return redirect("/?updated=true")
    }

    @GetMapping("/me/edit-password", produces = [MediaType.TEXT_HTML_VALUE])
    fun changePasswordPage(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderChangePassword(pageContext(authentication, csrfToken, request))

    @PutMapping("/me/password", "/me/edit-password", produces = [MediaType.TEXT_HTML_VALUE])
    fun changePassword(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @ModelAttribute form: ChangePasswordForm,
    ): ResponseEntity<String> =
        try {
            myAccount.changePassword(
                authentication.actor(),
                PlainTextPassword(form.currentPassword),
                PlainTextPassword(form.newPassword),
                form.confirmNewPassword,
            )
            ResponseEntity.status(302).location(URI.create("/?passwordChanged=true")).build()
        } catch (conflict: UserConflict) {
            val body = renderChangePassword(pageContext(authentication, csrfToken, request), conflict.message)
            ResponseEntity.status(409).body(body)
        }

    @PutMapping("/me/avatar")
    fun uploadAvatar(
        authentication: Authentication,
        @RequestParam file: MultipartFile,
    ): ResponseEntity<Void> {
        require(!file.isEmpty) { "Choose an image to upload" }
        avatars.replaceMyAvatar(
            authentication.actor(),
            UserAvatarUpload(file.bytes, file.contentType),
        )
        return ResponseEntity.noContent().build()
    }

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
        val deleted = myAccount.deleteMyAccount(authentication.actor(), PlainTextPassword(password))
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
            access.accessFlags(authentication.actor(), kind),
        )

    private fun updateAccessFlags(
        authentication: Authentication,
        kind: UserAccessFlagKind,
        userIds: List<String>?,
        path: String,
    ): ResponseEntity<Void> {
        access.replaceAccessFlags(
            authentication.actor(),
            kind,
            userIds.orEmpty().map(UserId::parse).toSet(),
        )
        return redirect(path)
    }
}

@RestController
class AccountActivationController(
    private val lifecycle: UserLifecycle,
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
        lifecycle.requestActivation(Actor.Anonymous, Cid(cid), request.remoteAddr)
        return redirect("/email-sent")
    }

    @GetMapping("/email-sent", produces = [MediaType.TEXT_HTML_VALUE])
    fun emailSent(
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderEmailSent(pageContext(null, csrfToken, request, false))

    @GetMapping("/register", produces = [MediaType.TEXT_HTML_VALUE])
    fun registration(
        @RequestParam token: String,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val registrationToken = RegistrationToken(token)
        val cid =
            lifecycle.activationCid(Actor.Anonymous, registrationToken)
                ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(renderRegistration(pageContext(null, csrfToken, request, false), token, cid.value))
    }

    @PostMapping("/register")
    fun register(
        @ModelAttribute form: RegistrationForm,
    ): ResponseEntity<Void> {
        val registrationToken = RegistrationToken(form.token)
        val cid = checkNotNull(lifecycle.activationCid(Actor.Anonymous, registrationToken))
        lifecycle.register(
            Actor.Anonymous,
            registrationToken,
            newUserFromForm(form.user(cid)),
            form.confirmPassword,
            form.acceptUserAgreement,
        )
        return redirect("/login?account-created")
    }

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
        lifecycle.requestPasswordReset(Actor.Anonymous, cidOrEmail, request.remoteAddr)
        return renderForgotPassword(pageContext(null, csrfToken, request, false), true)
    }

    @GetMapping("/forgot-password/finalize", produces = [MediaType.TEXT_HTML_VALUE])
    fun finalizePasswordReset(
        @RequestParam token: String,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val resetToken = PasswordResetToken(token)
        lifecycle.passwordResetUser(Actor.Anonymous, resetToken)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(renderFinalizePasswordReset(pageContext(null, csrfToken, request, false), token))
    }

    @PostMapping("/forgot-password/finalize")
    fun resetPassword(
        @RequestParam token: String,
        @RequestParam password: String,
        @RequestParam confirmPassword: String,
    ): ResponseEntity<Void> {
        lifecycle.resetPassword(
            Actor.Anonymous,
            PasswordResetToken(token),
            PlainTextPassword(password),
            confirmPassword,
        )
        return redirect("/login?password-reset")
    }
}

data class UserAdministrationWeb(
    val userStore: UserStore,
    val userAdministration: UserAdministration,
    val passwordResets: PasswordResetAdministration,
)

data class MyAccountWeb(
    val myAccount: MyAccount,
    val avatars: UserAvatars,
)

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

data class UpdateUserForm(
    val nick: String,
    val firstName: String,
    val lastName: String,
    val acceptanceYear: Int,
    val language: Language,
    val email: String,
    val version: Int,
)

data class UpdateMyProfileForm(
    val nick: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val language: Language,
    val version: Int,
)

data class ChangePasswordForm(
    val currentPassword: String,
    val newPassword: String,
    val confirmNewPassword: String,
)

data class RegistrationForm(
    val token: String,
    val nick: String,
    val firstName: String,
    val lastName: String,
    val acceptanceYear: Int,
    val language: Language,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val acceptUserAgreement: Boolean = false,
) {
    fun user(cid: Cid) = NewUserForm(cid.value, nick, firstName, lastName, acceptanceYear, language, email, password)
}

internal fun Authentication.userId() = UserId.parse((principal as GammaPrincipal).userId)

private fun UserProfile.details() = UserDetails(id, cid, nick, firstName, lastName, acceptanceYear, version)

internal fun redirect(path: String): ResponseEntity<Void> =
    ResponseEntity.status(302).location(URI.create(path)).build()

private fun <T : Any> notFound(): ResponseEntity<T> = ResponseEntity.notFound().build()
