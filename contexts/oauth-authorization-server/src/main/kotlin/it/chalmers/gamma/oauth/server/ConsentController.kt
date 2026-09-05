package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientId
import it.chalmers.gamma.oauth.ClientOwner
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.a
import kotlinx.html.article
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.footer
import kotlinx.html.form
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.hiddenInput
import kotlinx.html.html
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.stream.createHTML
import kotlinx.html.title
import kotlinx.html.ul
import org.springframework.http.MediaType
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ConsentController(
    private val consentDetails: ReadOAuthConsent,
) {
    @GetMapping("/oauth2/consent", produces = [MediaType.TEXT_HTML_VALUE])
    fun consent(
        @RequestParam("client_id", required = false) clientId: String?,
        @RequestParam("scope", required = false) scope: String?,
        @RequestParam("state", required = false) state: String?,
        csrfToken: CsrfToken,
    ): String {
        if (clientId == null) {
            return issuePage("Client id missing", "A client_id must be provided to authorize")
        }
        if (scope == null) {
            return issuePage("Client scopes missing", "A scope must be specified to authorize.")
        }
        if (state == null) {
            return issuePage("Client state missing", "A state must be specified to authorize.")
        }
        val parsedId = runCatching { ClientId(clientId) }.getOrNull()
        val details =
            parsedId?.let(consentDetails::read)
                ?: return issuePage(
                    "Client not found",
                    "A client with the given client id was not found.",
                )
        val client = details.client
        val requestedScopes = scope.split(' ').sorted()
        if (requestedScopes != client.scopes.map { it.wireValue }.sorted()) {
            return issuePage(
                "Mismatch scopes for client",
                "There is a mismatch between registered client scopes, and the scopes specified for this authorization.",
            )
        }
        val unofficialOwnerDescription =
            if (client.owner is ClientOwner.User) {
                describeUnofficialOwner(details.owner)
            } else {
                null
            }
        return createHTML().html {
            head {
                title { +"Authorize ${client.name.value} - Gamma" }
                meta(charset = "utf-8")
                meta(name = "viewport", content = "width=device-width, initial-scale=1")
                link(rel = "stylesheet", href = "/webjars/picocss__pico/2.0.6/css/pico.green.min.css")
                script(src = "/webjars/htmx.org/1.9.12/dist/htmx.min.js") {}
            }
            body(classes = "container") {
                main {
                    article {
                        header { +"Do you want to authorize ${client.name.value} to access:" }
                        if (unofficialOwnerDescription != null) {
                            div(classes = "error") {
                                attributes["role"] = "alert"
                                p {
                                    +"Warning! This application you are authorizing Gamma with is not an approved, "
                                    +"official client."
                                }
                                p { +unofficialOwnerDescription }
                            }
                        }
                        ul {
                            li { +"First and last name" }
                            if ("email" in requestedScopes) li { +"Email" }
                            li { +"Nickname" }
                            li { +"Preferred language" }
                            li { +"Cid" }
                            li { +"Authorities" }
                            li { +"Groups that you're apart of" }
                        }
                        form(action = "/oauth2/authorize", method = FormMethod.post) {
                            attributes["id"] = "deny-authorization"
                            authorizationRequestFields(clientId, state, emptyList(), csrfToken.token)
                        }
                        form(action = "/oauth2/authorize", method = FormMethod.post) {
                            attributes["id"] = "confirm-authorization"
                            authorizationRequestFields(clientId, state, requestedScopes, csrfToken.token)
                        }
                        footer {
                            button(type = ButtonType.submit, classes = "outline contrast") {
                                attributes["form"] = "deny-authorization"
                                +"Deny"
                            }
                            button(type = ButtonType.submit, classes = "outline contrast") {
                                attributes["form"] = "confirm-authorization"
                                +"Authorize"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun issuePage(
        issueTitle: String,
        description: String,
    ): String =
        createHTML().html {
            head {
                title { +"$issueTitle - Gamma" }
                meta(charset = "utf-8")
                meta(name = "viewport", content = "width=device-width, initial-scale=1")
                link(rel = "stylesheet", href = "/webjars/picocss__pico/2.0.6/css/pico.green.min.css")
            }
            body(classes = "container") {
                main {
                    article {
                        header { +issueTitle }
                        p { +description }
                        p {
                            +"Read more how to authorize with gamma here: "
                            a(href = "https://github.com/cthit/Gamma/wiki/Authenticating-With-Gamma") {
                                target = "_blank"
                                +"github.com/cthit/Gamma/wiki/Authenticating-With-Gamma"
                            }
                        }
                    }
                }
            }
        }
}

private fun describeUnofficialOwner(profile: it.chalmers.gamma.users.DirectoryUser?): String =
    if (profile == null) {
        "The owner's account is no longer available."
    } else {
        "It is created, and owned by ${profile.firstName.value} '${profile.nick.value}' ${profile.lastName.value}"
    }

private fun kotlinx.html.FORM.authorizationRequestFields(
    clientId: String,
    state: String,
    scopes: List<String>,
    browserCsrfToken: String,
) {
    hiddenInput {
        name = "client_id"
        value = clientId
    }
    hiddenInput {
        name = "state"
        value = state
    }
    scopes.forEach { requested ->
        hiddenInput {
            name = "scope"
            value = requested
        }
    }
    hiddenInput {
        name = "_csrf"
        value = browserCsrfToken
    }
}
