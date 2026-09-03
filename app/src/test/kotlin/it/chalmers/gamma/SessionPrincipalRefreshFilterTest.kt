package it.chalmers.gamma

import it.chalmers.gamma.users.GammaPrincipal
import it.chalmers.gamma.users.SessionAccess
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SessionPrincipalRefreshFilterTest {
    @AfterTest
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `refreshes administrator status for an active session`() {
        authenticate(administrator = false)

        SessionPrincipalRefreshFilter { SessionAccess(locked = false, administrator = true) }
            .doFilter(MockHttpServletRequest(), MockHttpServletResponse(), MockFilterChain())

        val authentication = assertNotNull(SecurityContextHolder.getContext().authentication)
        val principal = assertIs<GammaPrincipal>(authentication.principal)
        assertTrue(principal.administrator)
        assertTrue(authentication.authorities.any { it.authority == "ROLE_ADMIN" })
    }

    @Test
    fun `removes administrator status after demotion`() {
        authenticate(administrator = true)

        SessionPrincipalRefreshFilter { SessionAccess(locked = false, administrator = false) }
            .doFilter(MockHttpServletRequest(), MockHttpServletResponse(), MockFilterChain())

        val authentication = assertNotNull(SecurityContextHolder.getContext().authentication)
        val principal = assertIs<GammaPrincipal>(authentication.principal)
        assertFalse(principal.administrator)
        assertFalse(authentication.authorities.any { it.authority == "ROLE_ADMIN" })
    }

    @Test
    fun `clears and invalidates a locked user's session`() {
        authenticate(administrator = false)
        val request = MockHttpServletRequest()
        val session = assertNotNull(request.getSession(true))

        SessionPrincipalRefreshFilter { SessionAccess(locked = true, administrator = false) }
            .doFilter(request, MockHttpServletResponse(), MockFilterChain())

        assertEquals(null, SecurityContextHolder.getContext().authentication)
        assertFailsWith<IllegalStateException> { session.creationTime }
    }

    private fun authenticate(administrator: Boolean) {
        val principal = GammaPrincipal(UUID.randomUUID().toString(), "student", administrator)
        val authorities =
            if (administrator) {
                listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
            } else {
                emptyList()
            }
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken.authenticated(principal, null, authorities)
    }
}
