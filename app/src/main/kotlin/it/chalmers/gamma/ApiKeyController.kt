// Spring response and form-binding signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiAccessNotFound
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyName
import it.chalmers.gamma.apiaccess.ApiKeyType
import it.chalmers.gamma.apiaccess.views.parseAccountScaffoldSettings
import it.chalmers.gamma.apiaccess.views.parseInfoSettings
import it.chalmers.gamma.apiaccess.views.renderApiKeyDetails
import it.chalmers.gamma.apiaccess.views.renderApiKeys
import it.chalmers.gamma.apiaccess.views.renderCreateApiKey
import it.chalmers.gamma.apiaccess.views.renderSuperGroupTypeOptions
import it.chalmers.gamma.organization.LocalizedText
import it.chalmers.gamma.organization.OrganizationQueries
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiKeyController(
    private val apiKeys: ReadAdministrativeApiKeys,
    private val settingsUpdates: UpdateApiKeySettings,
    private val creation: CreateAdministrativeApiKey,
    private val rotation: RotateAdministrativeApiKey,
    private val deletion: DeleteAdministrativeApiKey,
    private val organizations: OrganizationQueries,
) {
    @ExceptionHandler(ApiAccessNotFound::class)
    fun missingKey(): ResponseEntity<Void> = ResponseEntity.notFound().build()

    @GetMapping("/api-keys", produces = [MediaType.TEXT_HTML_VALUE])
    fun apiKeys(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderApiKeys(pageContext(authentication, csrfToken, request), apiKeys.listApiKeys(authentication.actor()))

    @GetMapping("/api-keys/create", produces = [MediaType.TEXT_HTML_VALUE])
    fun createPage(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ) = renderCreateApiKey(pageContext(authentication, csrfToken, request))

    @PostMapping("/api-keys/create")
    fun create(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @ModelAttribute form: ApiKeyForm,
    ): ResponseEntity<String> {
        val created =
            creation.create(
                authentication.actor(),
                ApiKeyName(form.prettyName),
                LocalizedText.of(form.svDescription, form.enDescription),
                form.keyType,
            )
        return ResponseEntity.ok(
            renderApiKeyDetails(pageContext(authentication, csrfToken, request), created.apiKey, created.token),
        )
    }

    @GetMapping("/api-keys/{apiKeyId}", produces = [MediaType.TEXT_HTML_VALUE])
    fun details(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable apiKeyId: String,
    ): ResponseEntity<String> {
        val id = ApiKeyId.parse(apiKeyId)
        val key = apiKeys.findApiKey(authentication.actor(), id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(renderApiKeyDetails(pageContext(authentication, csrfToken, request), key))
    }

    @PostMapping("/api-keys/{apiKeyId}/reset")
    fun reset(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
        @PathVariable apiKeyId: String,
    ): ResponseEntity<String> {
        val id = ApiKeyId.parse(apiKeyId)
        val result = rotation.rotate(authentication.actor(), id)
        return ResponseEntity.ok(
            renderApiKeyDetails(pageContext(authentication, csrfToken, request), result.apiKey, result.token),
        )
    }

    @DeleteMapping("/api-keys/{apiKeyId}")
    fun delete(
        authentication: Authentication,
        @PathVariable apiKeyId: String,
    ): ResponseEntity<Void> {
        deletion.delete(authentication.actor(), ApiKeyId.parse(apiKeyId))
        return redirect("/api-keys")
    }

    @GetMapping("/api-keys/new-super-group-type/info", produces = [MediaType.TEXT_HTML_VALUE])
    fun newInfoSuperGroupType(): String =
        renderSuperGroupTypeOptions(organizations.listSuperGroupTypes(), allowManaged = false)

    @GetMapping("/api-keys/new-super-group-type/account-scaffold", produces = [MediaType.TEXT_HTML_VALUE])
    fun newAccountScaffoldSuperGroupType(): String =
        renderSuperGroupTypeOptions(organizations.listSuperGroupTypes(), allowManaged = true)

    @PutMapping("/api-keys/{apiKeyId}/info-settings")
    fun updateInfoSettings(
        authentication: Authentication,
        @PathVariable apiKeyId: String,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        val parameters = request.parameterMap.mapValues { it.value.toList() }
        settingsUpdates.update(
            authentication.actor(),
            ApiKeyId.parse(apiKeyId),
            parseInfoSettings(parameters),
        )
        return redirect("/api-keys/$apiKeyId")
    }

    @PutMapping("/api-keys/{apiKeyId}/account-scaffold-settings")
    fun updateAccountScaffoldSettings(
        authentication: Authentication,
        @PathVariable apiKeyId: String,
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        val parameters = request.parameterMap.mapValues { it.value.toList() }
        settingsUpdates.update(
            authentication.actor(),
            ApiKeyId.parse(apiKeyId),
            parseAccountScaffoldSettings(parameters),
        )
        return redirect("/api-keys/$apiKeyId")
    }
}

data class ApiKeyForm(
    val prettyName: String,
    val svDescription: String,
    val enDescription: String,
    val keyType: ApiKeyType,
)
