package it.chalmers.gamma.apiaccess.views

import it.chalmers.gamma.apiaccess.ApiKey
import it.chalmers.gamma.apiaccess.ApiKeyAccountScaffoldSettings
import it.chalmers.gamma.apiaccess.ApiKeyInfoSettings
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.RawApiToken
import it.chalmers.gamma.apiaccess.SuperGroupTypeSetting
import it.chalmers.gamma.platform.core.SuperGroupType
import it.chalmers.gamma.platform.html.GammaPageContext
import it.chalmers.gamma.platform.html.csrfInput
import it.chalmers.gamma.platform.html.gammaPage
import it.chalmers.gamma.platform.html.methodOverrideInput
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
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
import kotlinx.html.stream.createHTML
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.textInput
import kotlinx.html.tr

fun renderApiKeys(
    page: GammaPageContext,
    keys: List<ApiKey>,
): String =
    gammaPage("Api keys", page) {
        a(href = "${page.contextPath}/api-keys/create") { +"Create api key" }
        table {
            tbody {
                keys.forEach { key ->
                    tr {
                        td { +key.name.value }
                        td { +key.type.name }
                        td { a(href = "${page.contextPath}/api-keys/${key.id.value}") { +"Details" } }
                    }
                }
            }
        }
    }

fun renderCreateApiKey(page: GammaPageContext): String =
    gammaPage("Create api key", page) {
        article {
            header { +"Create api key" }
            form(action = "${page.contextPath}/api-keys/create", method = FormMethod.post) {
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
                    +"Key type"
                    select {
                        name = "keyType"
                        listOf(ApiKeyType.INFO, ApiKeyType.ACCOUNT_SCAFFOLD, ApiKeyType.ALLOW_LIST).forEach { type ->
                            option {
                                value = type.name
                                +type.name
                            }
                        }
                    }
                }
                button { +"Create api key" }
            }
        }
    }

fun renderApiKeyDetails(
    page: GammaPageContext,
    key: ApiKey,
    token: RawApiToken? = null,
): String =
    gammaPage("Api key details", page) {
        article {
            header { +"Api key details" }
            p { +key.name.value }
            p { +key.type.name }
            form(action = "${page.contextPath}/api-keys/${key.id.value}/reset", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                button { +"Reset token" }
            }
            form(action = "${page.contextPath}/api-keys/${key.id.value}", method = FormMethod.post) {
                csrfInput(page.requiredCsrfToken())
                methodOverrideInput("delete")
                button { +"Delete" }
            }
        }
        token?.let {
            article {
                header { +"Credentials" }
                code { +it.value }
                code { +key.id.value.toString() }
                p { +"Authorization: pre-shared ${key.id.value}:${it.value}" }
            }
        }
    }

fun renderSuperGroupTypeOptions(
    types: List<SuperGroupType>,
    allowManaged: Boolean,
): String =
    createHTML().div {
        select {
            name = "superGroupTypes"
            types.sortedBy { it.value.lowercase() }.forEach { type ->
                option {
                    value = type.value
                    +type.value
                }
            }
        }
        if (allowManaged) {
            label {
                input(type = InputType.checkBox, name = "requiresManaged") { value = "on" }
                +"Requires managed account"
            }
        }
    }

fun parseInfoSettings(parameters: Map<String, List<String>>): ApiKeyInfoSettings =
    ApiKeyInfoSettings(
        parameters["version"]?.singleOrNull()?.toInt() ?: 0,
        submittedSuperGroupTypes(parameters).map(::SuperGroupType),
    )

fun parseAccountScaffoldSettings(parameters: Map<String, List<String>>): ApiKeyAccountScaffoldSettings {
    val indexedTypes = submittedIndexedSuperGroupTypes(parameters)
    val types =
        if (indexedTypes.isNotEmpty()) {
            indexedTypes.map { (index, type) ->
                SuperGroupTypeSetting(
                    SuperGroupType(type),
                    parameters["superGroupTypes[$index].requiresManaged"]?.singleOrNull() == "on",
                )
            }
        } else {
            submittedSuperGroupTypes(parameters).map { type ->
                SuperGroupTypeSetting(SuperGroupType(type), parameters["requiresManaged"]?.singleOrNull() == "on")
            }
        }
    return ApiKeyAccountScaffoldSettings(parameters["version"]?.singleOrNull()?.toInt() ?: 0, types)
}

private fun submittedSuperGroupTypes(parameters: Map<String, List<String>>): List<String> =
    parameters["superGroupType"]
        ?: parameters["superGroupTypes"]
        ?: submittedIndexedSuperGroupTypes(parameters).values.toList()

private fun submittedIndexedSuperGroupTypes(parameters: Map<String, List<String>>): Map<Int, String> =
    parameters.entries
        .mapNotNull { (name, values) ->
            val match = Regex("superGroupTypes\\[(\\d+)](?:\\.type)?").matchEntire(name) ?: return@mapNotNull null
            match.groupValues[1].toInt() to values.single()
        }.toMap()

private fun GammaPageContext.requiredCsrfToken(): String = checkNotNull(csrfToken)
