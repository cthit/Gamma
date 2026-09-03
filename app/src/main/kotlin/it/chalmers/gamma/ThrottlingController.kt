@file:Suppress("ForbiddenVoid") // Spring uses Void for redirects with no response body.

package it.chalmers.gamma

import it.chalmers.gamma.platform.html.csrfInput
import it.chalmers.gamma.platform.html.gammaPage
import it.chalmers.gamma.platform.html.methodOverrideInput
import it.chalmers.gamma.throttling.ThrottleKey
import it.chalmers.gamma.throttling.ThrottlingAdministration
import jakarta.servlet.http.HttpServletRequest
import kotlinx.html.FormMethod
import kotlinx.html.button
import kotlinx.html.form
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.tr
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ThrottlingController(
    private val throttling: ThrottlingAdministration,
) {
    @GetMapping("/throttling", produces = [MediaType.TEXT_HTML_VALUE])
    fun throttling(
        authentication: Authentication,
        csrfToken: CsrfToken,
        request: HttpServletRequest,
    ): String {
        val page = pageContext(authentication, csrfToken, request)
        val snapshot = throttling.snapshot(authentication.actor())
        return gammaPage("Throttling", page) {
            table {
                tbody {
                    snapshot.entries.forEach { entry ->
                        tr {
                            td { +entry.key.value }
                            td { +entry.attempts.toString() }
                            td {
                                form(
                                    action = "${page.contextPath}/throttling/${entry.key.value}",
                                    method = FormMethod.post,
                                ) {
                                    csrfInput(csrfToken.token)
                                    methodOverrideInput("delete")
                                    button { +"Delete" }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @DeleteMapping("/throttling/{key}")
    fun delete(
        authentication: Authentication,
        @PathVariable key: String,
    ): ResponseEntity<Void> {
        throttling.delete(authentication.actor(), ThrottleKey(key))
        return redirect("/throttling")
    }
}
