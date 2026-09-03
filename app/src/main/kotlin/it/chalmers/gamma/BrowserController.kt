package it.chalmers.gamma

import it.chalmers.gamma.users.GammaPrincipal
import it.chalmers.gamma.users.views.renderAccountDeleted
import it.chalmers.gamma.users.views.renderLogin
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class BrowserController {
    @GetMapping("/login", produces = [MediaType.TEXT_HTML_VALUE])
    fun login(
        @RequestParam parameters: Map<String, String>,
        authentication: Authentication?,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        if (authentication?.principal is GammaPrincipal) {
            return ResponseEntity.status(302).location(URI.create(request.contextPath + "/")).build()
        }
        val body = renderLogin(parameters, csrfToken.token, request.contextPath)
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(body)
    }

    @GetMapping("/account-deleted", produces = [MediaType.TEXT_HTML_VALUE])
    fun accountDeleted(): String = renderAccountDeleted()
}
