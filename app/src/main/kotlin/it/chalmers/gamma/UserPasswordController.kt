// Spring response signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.users.ChangeMyPassword
import it.chalmers.gamma.users.CreatePasswordReset
import it.chalmers.gamma.users.MyPasswordChange
import it.chalmers.gamma.users.PasswordResetCompletion
import it.chalmers.gamma.users.PasswordResetToken
import it.chalmers.gamma.users.PasswordResets
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.ResetPassword
import it.chalmers.gamma.users.UserConflict
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.views.renderChangePassword
import it.chalmers.gamma.users.views.renderFinalizePasswordReset
import it.chalmers.gamma.users.views.renderUserDetails
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class UserPasswordController(
    private val passwordChanges: ChangeMyPassword,
    private val passwordResetCompletion: ResetPassword,
    private val passwordResets: PasswordResets,
    private val resetCreation: CreatePasswordReset,
    private val settings: AppSettings,
) {
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
            passwordChanges.change(
                authentication.actor(),
                MyPasswordChange(
                    PlainTextPassword(form.currentPassword),
                    PlainTextPassword(form.newPassword),
                    form.confirmNewPassword,
                ),
            )
            ResponseEntity.status(302).location(URI.create("/?passwordChanged=true")).build()
        } catch (conflict: UserConflict) {
            val body = renderChangePassword(pageContext(authentication, csrfToken, request), conflict.message)
            ResponseEntity.status(409).body(body)
        }

    @GetMapping("/forgot-password/finalize", produces = [MediaType.TEXT_HTML_VALUE])
    fun finalizePasswordReset(
        @RequestParam token: String,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val resetToken = PasswordResetToken(token)
        passwordResets.findUser(resetToken)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(renderFinalizePasswordReset(pageContext(null, csrfToken, request, false), token))
    }

    @PostMapping("/forgot-password/finalize")
    fun resetPassword(
        @RequestParam token: String,
        @RequestParam password: String,
        @RequestParam confirmPassword: String,
    ): ResponseEntity<Void> {
        passwordResetCompletion.reset(
            Actor.Anonymous,
            PasswordResetCompletion(PasswordResetToken(token), PlainTextPassword(password), confirmPassword),
        )
        return redirect("/login?password-reset")
    }

    @PostMapping("/users/{userId}/generate-password-link", produces = [MediaType.TEXT_HTML_VALUE])
    fun generatePasswordLink(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @org.springframework.web.bind.annotation.PathVariable userId: String,
    ): ResponseEntity<String> {
        val id = UserId.parse(userId)
        val issued = resetCreation.create(authentication.actor(), id)
        return ResponseEntity.ok(
            renderUserDetails(
                pageContext(authentication, csrfToken, request),
                issued.user,
                resetLink = "${settings.publicBaseUrl}/forgot-password/finalize?token=${issued.token.value}",
            ),
        )
    }
}

data class ChangePasswordForm(
    val currentPassword: String,
    val newPassword: String,
    val confirmNewPassword: String,
) {
    override fun toString(): String = "ChangePasswordForm(<redacted>)"
}
