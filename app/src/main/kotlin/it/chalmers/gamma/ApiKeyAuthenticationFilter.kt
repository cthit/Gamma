package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiCredentialAuthenticator
import it.chalmers.gamma.apiaccess.ApiKey
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.RawApiToken
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ApiClientCredentialId
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

internal class ApiKeyAuthenticationFilter(
    private val authenticator: ApiCredentialAuthenticator,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val credentials = request.getHeader("Authorization")?.toApiKeyCredentials()
        val key = credentials?.let { authenticator.authenticate(it.id, it.token) }
        if (key != null) {
            val principal =
                AuthenticatedApiKey(
                    key,
                    Actor.ApiClient(ApiClientCredentialId(key.id.value)),
                )
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken.authenticated(principal, null, emptyList())
        }
        filterChain.doFilter(request, response)
    }
}

internal data class AuthenticatedApiKey(
    val key: ApiKey,
    val actor: Actor.ApiClient,
) {
    override fun toString(): String = "AuthenticatedApiKey(<redacted>)"
}

private data class ApiKeyCredentials(
    val id: ApiKeyId,
    val token: RawApiToken,
)

private fun String.toApiKeyCredentials(): ApiKeyCredentials? {
    if (!startsWith("pre-shared ")) return null
    val credential = removePrefix("pre-shared ")
    val separator = credential.indexOf(':')
    if (separator < 1) return null
    val id = runCatching { ApiKeyId.parse(credential.substring(0, separator)) }.getOrNull() ?: return null
    val token = runCatching { RawApiToken(credential.substring(separator + 1)) }.getOrNull() ?: return null
    return ApiKeyCredentials(id, token)
}
