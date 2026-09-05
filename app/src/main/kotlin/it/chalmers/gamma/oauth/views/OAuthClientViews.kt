package it.chalmers.gamma.oauth.views

import it.chalmers.gamma.oauth.ClientAuthority
import it.chalmers.gamma.oauth.ClientName
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.CreatedOAuthClient
import it.chalmers.gamma.oauth.NewOAuthClient
import it.chalmers.gamma.oauth.OAuthClient
import it.chalmers.gamma.oauth.PersonalOAuthClient
import it.chalmers.gamma.oauth.RawClientSecret
import it.chalmers.gamma.oauth.RedirectUri
import it.chalmers.gamma.organization.SuperGroup
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.html.GammaPageContext
import it.chalmers.gamma.platform.html.csrfInput
import it.chalmers.gamma.platform.html.gammaPage
import it.chalmers.gamma.platform.html.methodOverrideInput
import it.chalmers.gamma.users.DirectoryUser
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.article
import kotlinx.html.button
import kotlinx.html.code
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.header
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.li
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.textInput
import kotlinx.html.tr
import kotlinx.html.ul
import java.util.UUID

fun renderClients(
    page: GammaPageContext,
    title: String,
    createPath: String,
    clients: List<OAuthClient>,
): String =
    gammaPage(title, page) {
        a(href = page.contextPath + createPath) { +"Create new client" }
        table {
            tbody {
                clients.forEach { client ->
                    tr {
                        td { +client.name.value }
                        td { +client.clientId.value }
                        td { a(href = "${page.contextPath}/clients/${client.uid.value}") { +"Details" } }
                    }
                }
            }
        }
    }

fun renderCreateClient(
    page: GammaPageContext,
    personal: Boolean,
): String =
    gammaPage("Create client", page) {
        article {
            header { +if (personal) "Create new client" else "Create client" }
            form(
                action = page.contextPath + if (personal) "/my-clients" else "/clients/create",
                method = FormMethod.post,
            ) {
                attributes["id"] = "create-clients"
                csrfInput(page.requiredCsrfToken())
                label {
                    +"Pretty name"
                    textInput(name = "prettyName")
                }
                label {
                    +"Swedish description"
                    textInput(name = "svDescription")
                }
                label {
                    +"English description"
                    textInput(name = "enDescription")
                }
                label {
                    +"Redirect URL"
                    textInput(name = "redirectUrl")
                }
                label {
                    input(type = InputType.checkBox, name = "emailScope") { value = "true" }
                    +"Email scope"
                }
                label {
                    input(type = InputType.checkBox, name = "generateApiKey") { value = "true" }
                    +"Generate API key"
                }
                if (!personal) {
                    div { attributes["id"] = "client-restrictions" }
                    button(type = ButtonType.button) {
                        attributes["data-hx-get"] = "${page.contextPath}/clients/create/new-restriction"
                        attributes["data-hx-target"] = "#client-restrictions"
                        attributes["data-hx-swap"] = "beforeend"
                        +"Add restriction"
                    }
                }
                button { +"Create" }
            }
        }
    }

data class OAuthClientForm(
    val prettyName: String,
    val svDescription: String,
    val enDescription: String,
    val redirectUrl: String,
    val emailScope: Boolean = false,
    val generateApiKey: Boolean = false,
    val restrictions: List<String>? = null,
)

fun newOAuthClient(
    form: OAuthClientForm,
    owner: ClientOwner,
) = NewOAuthClient(
    RedirectUri(form.redirectUrl),
    ClientName(form.prettyName),
    LocalizedText.of(form.svDescription, form.enDescription),
    form.emailScope,
    owner,
    form.generateApiKey,
    form.restrictions.orEmpty().mapTo(mutableSetOf(), UUID::fromString),
)

fun renderClientRestrictionRow(superGroups: List<SuperGroup>): String =
    kotlinx.html.stream.createHTML().div {
        select {
            name = "restrictions"
            superGroups.sortedBy { it.prettyName.value.lowercase() }.forEach { superGroup ->
                option {
                    value = superGroup.id.value.toString()
                    +superGroup.prettyName.value
                }
            }
        }
    }

fun renderNewAuthority(): String =
    kotlinx.html.stream.createHTML().div {
        textInput(name = "authority")
        button(type = ButtonType.button) { +"Add authority" }
    }

fun renderSuperGroupAuthorityRow(superGroups: List<SuperGroup>): String =
    kotlinx.html.stream.createHTML().div {
        select {
            name = "superGroups"
            superGroups.sortedBy { it.prettyName.value.lowercase() }.forEach { superGroup ->
                option {
                    value = superGroup.id.value.toString()
                    +superGroup.prettyName.value
                }
            }
        }
    }

fun renderUserAuthorityRow(users: List<DirectoryUser>): String =
    kotlinx.html.stream.createHTML().div {
        select {
            name = "users"
            users.sortedBy { it.nick.value.lowercase() }.forEach { user ->
                option {
                    value = user.id.value.toString()
                    +user.nick.value
                }
            }
        }
    }

fun renderClientDetails(
    page: GammaPageContext,
    client: OAuthClient,
    secret: RawClientSecret? = null,
    apiCredential: String? = null,
    authorities: List<ClientAuthority> = emptyList(),
): String =
    gammaPage("Client details", page) {
        article {
            header { +"Client details" }
            p { +client.name.value }
            ul(classes = "tuple") {
                li {
                    +"Client id: "
                    span { +client.clientId.value }
                }
                li {
                    +"Redirect URI: "
                    span { +client.redirectUri.value }
                }
            }
            form(action = "${page.contextPath}/clients/${client.uid.value}", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                methodOverrideInput("delete")
                button { +"Delete" }
            }
        }
        article {
            header { +"Credentials" }
            secret?.let { code { +it.value } }
            apiCredential?.let { code { +"Authorization: pre-shared $it" } }
            form(action = "${page.contextPath}/clients/${client.uid.value}/reset", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                button { +"Reset client secret" }
            }
        }
        article {
            header { +"Authorities" }
            form(action = "${page.contextPath}/clients/${client.uid.value}/authority", method = FormMethod.post) {
                attributes["id"] = "create-client-authority"
                csrfInput(page.requiredCsrfToken())
                textInput(name = "authority")
                button(type = ButtonType.submit) { +"Create authority" }
            }
            authorities.forEach { authority ->
                article {
                    header { +authority.name.value }
                    form(
                        action = "${page.contextPath}/clients/${client.uid.value}/authority/${authority.name.value}",
                        method = FormMethod.post,
                    ) {
                        csrfInput(page.requiredCsrfToken())
                        methodOverrideInput("delete")
                        button { +"Delete" }
                    }
                }
            }
        }
    }

fun renderUserClients(
    page: GammaPageContext,
    clients: List<PersonalOAuthClient>,
): String =
    gammaPage("User clients", page) {
        table {
            tbody {
                clients.forEach { (client, owner) ->
                    tr {
                        td { +client.name.value }
                        td { +client.clientId.value }
                        td {
                            owner?.let {
                                a(href = "${page.contextPath}/users/${it.id.value}") {
                                    +"${it.firstName.value} '${it.nick.value}' ${it.lastName.value}"
                                }
                                +" "
                                a(href = "mailto:${it.email.value}") { +it.email.value }
                            }
                        }
                    }
                }
            }
        }
    }

fun renderApprovedClients(
    page: GammaPageContext,
    clients: List<OAuthClient>,
): String =
    gammaPage("Accepted clients", page) {
        table {
            tbody {
                clients.forEach { client ->
                    tr {
                        td { +client.name.value }
                        td {
                            form(
                                action = "${page.contextPath}/me/accepted-clients/${client.uid.value}",
                                method = FormMethod.post,
                            ) {
                                csrfInput(page.requiredCsrfToken())
                                methodOverrideInput("delete")
                                button { +"Retract approval" }
                            }
                        }
                    }
                }
            }
        }
    }

private fun GammaPageContext.requiredCsrfToken(): String = checkNotNull(csrfToken)
