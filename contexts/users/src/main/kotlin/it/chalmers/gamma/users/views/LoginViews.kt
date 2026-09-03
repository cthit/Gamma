package it.chalmers.gamma.users.views

import it.chalmers.gamma.platform.html.GammaPageContext
import it.chalmers.gamma.platform.html.csrfInput
import it.chalmers.gamma.platform.html.gammaPage
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.article
import kotlinx.html.button
import kotlinx.html.footer
import kotlinx.html.form
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.p

fun renderLogin(
    parameters: Map<String, String>,
    csrfToken: String,
    contextPath: String,
): String =
    gammaPage(
        title = "Gamma",
        page = GammaPageContext(csrfToken = csrfToken, contextPath = contextPath, showNavigation = false),
    ) {
        attributes["hx-boost"] = "false"
        article {
            if ("authorizing" in parameters) {
                p { +"Before deciding on authorizing the client, please verify your identity" }
            }
            form(action = "$contextPath/login", method = FormMethod.post) {
                csrfInput(csrfToken)
                input(name = "username") { placeholder = "Cid / Email" }
                input(type = InputType.password, name = "password") { placeholder = "Password" }
                button(classes = "outline contrast") {
                    attributes["data-loading-disable"] = ""
                    +"Login"
                }
            }
            footer {
                id = "links"
                loginMessage(parameters)?.let { message -> p { +message } }
                a(href = "$contextPath/activate-cid") { +"Register" }
                a(href = "$contextPath/forgot-password") { +"Forgot password" }
            }
        }
    }

fun renderAccountDeleted(): String =
    gammaPage("Account deleted", GammaPageContext(showNavigation = false)) {
        article { p { +"Your account has been deleted." } }
    }

fun renderErrorPage(
    status: Int,
    title: String,
    message: String,
): String = "<!doctype html><html><body><main><h1>$status - $title</h1><p>$message</p></main></body></html>"

private fun loginMessage(parameters: Map<String, String>): String? =
    when {
        "error" in parameters -> {
            "Invalid credentials or locked account due to system migration. Password reset may be needed."
        }

        "logout" in parameters -> {
            "You have been logged out."
        }

        "throttle" in parameters -> {
            "You have been throttled for attempting to sign in too many times..."
        }

        "deleted" in parameters -> {
            "Your account has been deleted."
        }

        "account-created" in parameters -> {
            "Your account has been created."
        }

        "password-reset" in parameters -> {
            "Your password was reset."
        }

        else -> {
            null
        }
    }
