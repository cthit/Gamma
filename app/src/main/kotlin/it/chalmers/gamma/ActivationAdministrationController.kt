// Spring response signatures stay explicit at the MVC boundary.
@file:Suppress("ForbiddenVoid")

package it.chalmers.gamma

import it.chalmers.gamma.users.ActivationCodeAdministration
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.UserNotFound
import it.chalmers.gamma.users.views.renderActivationCodes
import it.chalmers.gamma.users.views.renderAllowList
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ActivationAdministrationController(
    private val activationCodes: ActivationCodeAdministration,
) {
    @GetMapping("/allow-list", produces = [MediaType.TEXT_HTML_VALUE])
    fun allowList(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): String =
        renderAllowList(
            pageContext(authentication, csrfToken, request),
            activationCodes.allowedCids(authentication.actor()),
        )

    @PutMapping("/allow-list")
    fun allowCid(
        authentication: Authentication,
        @RequestParam cid: String,
    ): ResponseEntity<Void> {
        activationCodes.allowCid(authentication.actor(), Cid(cid))
        return redirect("/allow-list")
    }

    @DeleteMapping("/allow-list/{cid}")
    fun retractCid(
        authentication: Authentication,
        @org.springframework.web.bind.annotation.PathVariable cid: String,
    ): ResponseEntity<Void> {
        try {
            activationCodes.retractCid(authentication.actor(), Cid(cid))
        } catch (_: UserNotFound) {
            return ResponseEntity.notFound().build()
        }
        return redirect("/allow-list")
    }

    @GetMapping("/activation-codes", produces = [MediaType.TEXT_HTML_VALUE])
    fun activationCodes(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): String =
        renderActivationCodes(
            pageContext(authentication, csrfToken, request),
            activationCodes.pendingActivations(authentication.actor()),
        )

    @DeleteMapping("/activation-codes/{cid}")
    fun deleteActivation(
        authentication: Authentication,
        @org.springframework.web.bind.annotation.PathVariable cid: String,
    ): ResponseEntity<Void> {
        try {
            activationCodes.deleteActivation(authentication.actor(), Cid(cid))
        } catch (_: UserNotFound) {
            return ResponseEntity.notFound().build()
        }
        return redirect("/activation-codes")
    }
}
