// Spring response signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.users.AcceptanceYear
import it.chalmers.gamma.users.ActivationCodes
import it.chalmers.gamma.users.Email
import it.chalmers.gamma.users.FirstName
import it.chalmers.gamma.users.Language
import it.chalmers.gamma.users.LastName
import it.chalmers.gamma.users.Nick
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.RegisterUser
import it.chalmers.gamma.users.RegistrationToken
import it.chalmers.gamma.users.UserRegistration
import it.chalmers.gamma.users.views.renderRegistration
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Year

@RestController
class RegistrationController(
    private val registrations: RegisterUser,
    private val activationCodes: ActivationCodes,
) {
    @GetMapping("/register", produces = [MediaType.TEXT_HTML_VALUE])
    fun registration(
        @RequestParam token: String,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val cid = activationCodes.findCid(RegistrationToken(token)) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(renderRegistration(pageContext(null, csrfToken, request, false), token, cid.value))
    }

    @PostMapping("/register")
    fun register(
        @ModelAttribute form: RegistrationForm,
    ): ResponseEntity<Void> {
        registrations.register(
            Actor.Anonymous,
            UserRegistration(
                token = RegistrationToken(form.token),
                nick = Nick(form.nick),
                firstName = FirstName(form.firstName),
                lastName = LastName(form.lastName),
                acceptanceYear = AcceptanceYear.of(form.acceptanceYear, Year.now().value),
                language = form.language,
                email = Email(form.email),
                password = PlainTextPassword(form.password),
                confirmedPassword = form.confirmPassword,
                acceptedUserAgreement = form.acceptUserAgreement,
            ),
        )
        return redirect("/login?account-created")
    }
}

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
)
