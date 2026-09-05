package it.chalmers.gamma.users.views

import it.chalmers.gamma.platform.html.GammaPageContext
import it.chalmers.gamma.platform.html.csrfInput
import it.chalmers.gamma.platform.html.gammaPage
import it.chalmers.gamma.platform.html.methodOverrideInput
import it.chalmers.gamma.users.AcceptanceYear
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.Email
import it.chalmers.gamma.users.FirstName
import it.chalmers.gamma.users.Language
import it.chalmers.gamma.users.LastName
import it.chalmers.gamma.users.NewUser
import it.chalmers.gamma.users.Nick
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.UserProfile
import kotlinx.html.ButtonType
import kotlinx.html.FormEncType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.article
import kotlinx.html.button
import kotlinx.html.code
import kotlinx.html.form
import kotlinx.html.header
import kotlinx.html.hiddenInput
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.li
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
import kotlinx.html.textInput
import kotlinx.html.ul
import java.time.Year

data class NewUserForm(
    val cid: String,
    val nick: String,
    val firstName: String,
    val lastName: String,
    val acceptanceYear: Int,
    val language: Language,
    val email: String,
    val password: String,
)

fun newUserFromForm(form: NewUserForm) =
    NewUser(
        Cid(form.cid),
        Nick(form.nick),
        FirstName(form.firstName),
        LastName(form.lastName),
        AcceptanceYear.of(form.acceptanceYear, Year.now().value),
        form.language,
        Email(form.email),
        PlainTextPassword(form.password),
    )

fun renderMyAccount(
    page: GammaPageContext,
    user: UserProfile,
    message: String? = null,
): String =
    gammaPage("Gamma", page) {
        article {
            header { +"Your information" }
            message?.let { p { +it } }
            ul(classes = "tuple") {
                li { +"Cid: ${user.cid.value}" }
                li { +"Name: ${user.firstName.value} '${user.nick.value}' ${user.lastName.value}" }
                li { +"Email: ${user.email.value}" }
                li { +"Acceptance year: ${user.acceptanceYear.value}" }
            }
            form(action = "${page.contextPath}/me/edit", method = FormMethod.get) {
                button { +"Edit" }
            }
            form(action = "${page.contextPath}/me/edit-password", method = FormMethod.get) {
                button { +"Change password" }
            }
        }
        article {
            header { +"Your avatar" }
            img(src = "${page.contextPath}/images/user/avatar/${user.id.value}?v=${user.version}", alt = "Me avatar")
            form(
                action = "${page.contextPath}/me/avatar",
                method = FormMethod.post,
                encType = FormEncType.multipartFormData,
            ) {
                id = "update-me-avatar"
                csrfInput(page.requiredCsrfToken())
                methodOverrideInput("put")
                input(type = InputType.file, name = "file") { required = true }
                button { +"Upload avatar" }
            }
        }
    }

fun renderEditMyAccount(
    page: GammaPageContext,
    user: UserProfile,
): String =
    gammaPage("Edit your information", page) {
        article {
            header { +"Editing your information" }
            form(action = "${page.contextPath}/me", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                methodOverrideInput("put")
                hiddenInput {
                    name = "version"
                    value = user.version.toString()
                }
                label {
                    +"First name"
                    textInput(name = "firstName") { value = user.firstName.value }
                }
                label {
                    +"Last name"
                    textInput(name = "lastName") { value = user.lastName.value }
                }
                label {
                    +"Nick"
                    textInput(name = "nick") { value = user.nick.value }
                }
                label {
                    +"Email"
                    input(type = InputType.email, name = "email") { value = user.email.value }
                }
                label {
                    +"Language"
                    select {
                        name = "language"
                        Language.entries.forEach { language ->
                            option {
                                value = language.name
                                selected = language == user.language
                                +language.name
                            }
                        }
                    }
                }
                button { +"Save" }
            }
        }
    }

fun renderChangePassword(
    page: GammaPageContext,
    message: String? = null,
): String =
    gammaPage("Change password", page) {
        article {
            header { +"Creating a new password" }
            message?.let { p { +it } }
            form(action = "${page.contextPath}/me/edit-password", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                methodOverrideInput("put")
                label {
                    +"Current password"
                    input(type = InputType.password, name = "currentPassword")
                }
                label {
                    +"New password"
                    input(type = InputType.password, name = "newPassword")
                }
                label {
                    +"Confirm new password"
                    input(type = InputType.password, name = "confirmNewPassword")
                }
                button { +"Save new password" }
            }
        }
    }

fun renderDeleteAccount(
    page: GammaPageContext,
    message: String? = null,
): String =
    gammaPage("Delete your account", page) {
        article {
            header { +"Deleting your account" }
            message?.let { p { +it } }
            form(action = "${page.contextPath}/delete-your-account", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                methodOverrideInput("delete")
                label {
                    +"Password"
                    input(type = InputType.password, name = "password")
                }
                button { +"Delete the account" }
            }
        }
    }

fun renderActivateCid(page: GammaPageContext): String =
    gammaPage("Activate cid", page) {
        article {
            header { +"Activate cid" }
            form(action = "${page.contextPath}/activate-cid", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                label {
                    +"Cid"
                    textInput(name = "cid")
                }
                button { +"Activate cid" }
            }
        }
    }

fun renderEmailSent(page: GammaPageContext): String =
    gammaPage("Email sent", page) {
        article {
            header { +"Email sent" }
            p { +"An email should be sent to your student email" }
        }
    }

fun renderRegistration(
    page: GammaPageContext,
    token: String,
    cid: String,
): String =
    gammaPage("Register", page) {
        article {
            header { +"Finish setting up your account" }
            p { +"Cid: $cid" }
            form(action = "${page.contextPath}/register", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                hiddenInput {
                    name = "token"
                    value = token
                }
                label {
                    +"Email"
                    input(type = InputType.email, name = "email")
                }
                label {
                    +"Nick"
                    textInput(name = "nick")
                }
                label {
                    +"First name"
                    textInput(name = "firstName")
                }
                label {
                    +"Last name"
                    textInput(name = "lastName")
                }
                label {
                    +"Password"
                    input(type = InputType.password, name = "password")
                }
                label {
                    +"Confirm password"
                    input(type = InputType.password, name = "confirmPassword")
                }
                label {
                    +"Acceptance year"
                    select {
                        name = "acceptanceYear"
                        for (year in java.time.Year
                            .now()
                            .value downTo 2001) {
                            option {
                                value = year.toString()
                                +year.toString()
                            }
                        }
                    }
                }
                label {
                    +"Language"
                    select {
                        name = "language"
                        Language.entries.forEach { language ->
                            option {
                                value = language.name
                                +language.name
                            }
                        }
                    }
                }
                label {
                    input(type = InputType.checkBox, name = "acceptUserAgreement") { value = "true" }
                    +"Accept user agreement"
                }
                button { +"Create account" }
            }
        }
    }

fun renderForgotPassword(
    page: GammaPageContext,
    requested: Boolean = false,
): String =
    gammaPage("Reset password", page) {
        article {
            header { +"Reset password" }
            if (requested) {
                p { +"You should have received an email with a link for resetting your password." }
            } else {
                form(action = "${page.contextPath}/forgot-password", method = FormMethod.post) {
                    csrfInput(page.requiredCsrfToken())
                    label {
                        +"Cid or email"
                        textInput(name = "cidOrEmail")
                    }
                    button { +"Reset password" }
                }
            }
        }
    }

fun renderFinalizePasswordReset(
    page: GammaPageContext,
    token: String,
): String =
    gammaPage("Finalize resetting password", page) {
        article {
            header { +"Finalize resetting password" }
            form(action = "${page.contextPath}/forgot-password/finalize", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                hiddenInput {
                    name = "token"
                    value = token
                }
                label {
                    +"Password"
                    input(type = InputType.password, name = "password")
                }
                label {
                    +"Confirm password"
                    input(type = InputType.password, name = "confirmPassword")
                }
                button { +"Reset password" }
            }
        }
    }

fun renderUserAgreement(page: GammaPageContext): String =
    gammaPage("User agreement", page) {
        article {
            header { +"User agreement" }
            p { +"This agreement refers to IT:s user account system Gamma." }
        }
    }

private fun GammaPageContext.requiredCsrfToken(): String = checkNotNull(csrfToken)
