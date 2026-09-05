package it.chalmers.gamma.users.views

import it.chalmers.gamma.platform.html.GammaPageContext
import it.chalmers.gamma.platform.html.csrfInput
import it.chalmers.gamma.platform.html.gammaPage
import it.chalmers.gamma.platform.html.methodOverrideInput
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.Language
import it.chalmers.gamma.users.PendingActivation
import it.chalmers.gamma.users.UserAccessFlag
import it.chalmers.gamma.users.UserDetails
import it.chalmers.gamma.users.UserProfile
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.article
import kotlinx.html.button
import kotlinx.html.code
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.header
import kotlinx.html.hiddenInput
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.textInput
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr

fun renderUsers(
    page: GammaPageContext,
    users: List<UserProfile>,
    query: String,
): String =
    gammaPage("Users", page) {
        article {
            header { h1 { +"Users" } }
            form(action = "${page.contextPath}/users", method = FormMethod.get) {
                textInput(name = "query") { value = query }
                button { +"Search" }
            }
            table {
                thead {
                    tr {
                        th { +"Cid" }
                        th { +"Name" }
                        th {}
                    }
                }
                tbody {
                    users.filter { user -> user.matches(query) }.forEach { user ->
                        tr {
                            td { +user.cid.value }
                            td { +"${user.firstName.value} '${user.nick.value}' ${user.lastName.value}" }
                            td { a(href = "${page.contextPath}/users/${user.id.value}") { +"Details" } }
                        }
                    }
                }
            }
            a(href = "${page.contextPath}/users/create", classes = "button") { +"Create user" }
        }
    }

fun renderCreateUser(page: GammaPageContext): String =
    gammaPage("Create user", page) {
        article {
            header { +"Create user" }
            form(action = "${page.contextPath}/users/create", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                userFields()
                input(type = InputType.password, name = "password") { placeholder = "Password" }
                button { +"Create user" }
            }
        }
    }

fun renderUserDetails(
    page: GammaPageContext,
    user: UserDetails,
    message: String? = null,
    resetLink: String? = null,
): String =
    gammaPage("User details", page) {
        article {
            header { +"User details" }
            message?.let { p { +it } }
            p(classes = "tuple") {
                +"${user.firstName.value} '${user.nick.value}' ${user.lastName.value} "
                +"Cid: ${user.cid.value} Acceptance year: ${user.acceptanceYear.value}"
            }
            form(action = "${page.contextPath}/users/${user.id.value}/edit", method = FormMethod.get) {
                button { +"Edit user" }
            }
            form(action = "${page.contextPath}/users/${user.id.value}", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                methodOverrideInput("delete")
                button { +"Delete user" }
            }
        }
        article {
            header { +"Generate reset password link" }
            if (resetLink == null) p { +"Password link will appear here" } else code { +resetLink }
            form(
                action = "${page.contextPath}/users/${user.id.value}/generate-password-link",
                method = FormMethod.post,
            ) {
                csrfInput(page.requiredCsrfToken())
                button { +"Generate" }
            }
        }
    }

fun renderEditUser(
    page: GammaPageContext,
    user: UserProfile,
): String =
    gammaPage("Edit user", page) {
        article {
            header { +"Edit user" }
            form(action = "${page.contextPath}/users/${user.id.value}", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                methodOverrideInput("put")
                hiddenInput {
                    name = "version"
                    value = user.version.toString()
                }
                userFields(user)
                button { +"Save" }
            }
        }
    }

fun renderAccessFlags(
    page: GammaPageContext,
    title: String,
    path: String,
    flags: List<UserAccessFlag>,
): String =
    gammaPage(title, page) {
        article {
            header { +title }
            form(action = page.contextPath + path, method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                methodOverrideInput("put")
                table {
                    tbody {
                        flags.forEach { flag ->
                            tr {
                                td { +"${flag.firstName.value} '${flag.nick.value}' ${flag.lastName.value}" }
                                td {
                                    input(type = InputType.checkBox, name = "userId") {
                                        value = flag.userId.value.toString()
                                        checked = flag.enabled
                                    }
                                }
                            }
                        }
                    }
                }
                button { +"Save" }
            }
        }
    }

fun renderAllowList(
    page: GammaPageContext,
    allowed: List<Cid>,
): String =
    gammaPage("Allow lists", page) {
        article {
            header { +"Allow lists" }
            form(action = "${page.contextPath}/allow-list", method = FormMethod.post) {
                id = "allow-cid-form"
                csrfInput(page.requiredCsrfToken())
                methodOverrideInput("put")
                textInput(name = "cid")
            }
            button {
                attributes["form"] = "allow-cid-form"
                +"Allow cid"
            }
            table {
                tbody {
                    allowed.forEach { cid ->
                        tr {
                            td { +cid.value }
                            td {
                                form(action = "${page.contextPath}/allow-list/${cid.value}", method = FormMethod.post) {
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
    }

fun renderActivationCodes(
    page: GammaPageContext,
    activations: List<PendingActivation>,
): String =
    gammaPage("Activation codes", page) {
        article {
            header { +"Activation codes" }
            table {
                tbody {
                    activations.forEach { activation ->
                        tr {
                            td { +activation.cid.value }
                            td { +activation.createdAt.toString() }
                            td {
                                form(
                                    action = "${page.contextPath}/activation-codes/${activation.cid.value}",
                                    method = FormMethod.post,
                                ) {
                                    csrfInput(page.requiredCsrfToken())
                                    methodOverrideInput("delete")
                                    button { +"Delete activation" }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

private fun kotlinx.html.FlowContent.userFields(user: UserProfile? = null) {
    label {
        +"First name"
        textInput(name = "firstName") { value = user?.firstName?.value.orEmpty() }
    }
    label {
        +"Last name"
        textInput(name = "lastName") { value = user?.lastName?.value.orEmpty() }
    }
    label {
        +"Nick"
        textInput(name = "nick") { value = user?.nick?.value.orEmpty() }
    }
    if (user == null) {
        label {
            +"Cid"
            textInput(name = "cid")
        }
    }
    label {
        +"Email"
        input(type = InputType.email, name = "email") { value = user?.email?.value.orEmpty() }
    }
    label {
        +"Acceptance year"
        select {
            name = "acceptanceYear"
            yearOptions(user?.acceptanceYear?.value)
        }
    }
    label {
        +"Language"
        select {
            name = "language"
            Language.entries.forEach { language ->
                option {
                    value = language.name
                    selected = user?.language == language || (user == null && language == Language.EN)
                    +language.name
                }
            }
        }
    }
}

private fun kotlinx.html.SELECT.yearOptions(selectedYear: Int?) {
    val currentYear =
        java.time.Year
            .now()
            .value
    for (year in currentYear downTo 2001) {
        option {
            value = year.toString()
            selected = year == selectedYear
            +year.toString()
        }
    }
}

private fun UserProfile.matches(query: String): Boolean {
    if (query.isBlank()) return true
    val searchable = "${cid.value} ${firstName.value} ${lastName.value} ${nick.value}"
    return searchable.contains(query, ignoreCase = true)
}

private fun GammaPageContext.requiredCsrfToken(): String = checkNotNull(csrfToken)
