// Spring response signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.users.AcceptanceYear
import it.chalmers.gamma.users.Email
import it.chalmers.gamma.users.FirstName
import it.chalmers.gamma.users.Language
import it.chalmers.gamma.users.LastName
import it.chalmers.gamma.users.MyProfileUpdate
import it.chalmers.gamma.users.Nick
import it.chalmers.gamma.users.UpdateMyEmail
import it.chalmers.gamma.users.UpdateMyProfile
import it.chalmers.gamma.users.UpdateUser
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserUpdate
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Year

@RestController
class UserProfileController(
    private val userUpdates: UpdateUser,
    private val profileUpdates: UpdateMyProfile,
    private val emailUpdates: UpdateMyEmail,
) {
    @PutMapping("/users/{userId}")
    fun updateUser(
        authentication: Authentication,
        @PathVariable userId: String,
        @ModelAttribute form: UpdateUserForm,
    ): ResponseEntity<Void> {
        userUpdates.update(
            authentication.actor(),
            UserUpdate(
                userId = UserId.parse(userId),
                expectedVersion = form.version,
                nick = Nick(form.nick),
                firstName = FirstName(form.firstName),
                lastName = LastName(form.lastName),
                acceptanceYear = AcceptanceYear.of(form.acceptanceYear, Year.now().value),
                language = form.language,
                email = Email(form.email),
            ),
        )
        return redirect("/users/$userId?updated=true")
    }

    @PutMapping("/me")
    fun updateMe(
        authentication: Authentication,
        @ModelAttribute form: UpdateMyProfileForm,
    ): ResponseEntity<Void> {
        profileUpdates.update(
            authentication.actor(),
            MyProfileUpdate(
                nick = Nick(form.nick),
                firstName = FirstName(form.firstName),
                lastName = LastName(form.lastName),
                language = form.language,
                email = Email(form.email),
                expectedVersion = form.version,
            ),
        )
        return redirect("/?updated=true")
    }

    @PutMapping("/me/email")
    fun updateMyEmail(
        authentication: Authentication,
        @RequestParam email: String,
    ): ResponseEntity<Void> {
        emailUpdates.update(authentication.actor(), Email(email))
        return redirect("/?updated=true")
    }
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
