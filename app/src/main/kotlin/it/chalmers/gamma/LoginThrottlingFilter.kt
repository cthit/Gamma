package it.chalmers.gamma

import it.chalmers.gamma.throttling.FixedWindowThrottling
import it.chalmers.gamma.throttling.ThrottleKey
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration

internal class LoginThrottlingFilter(
    private val throttling: FixedWindowThrottling,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (request.method.equals("POST", ignoreCase = true) && request.requestURI == request.contextPath + "/login") {
            val key = ThrottleKey.digest("login", request.remoteAddr)
            val allowed = throttling.charge(key, MAXIMUM_LOGIN_ATTEMPTS, LOGIN_WINDOW)
            if (!allowed) {
                response.sendRedirect(request.contextPath + "/login?throttle=true")
                return
            }
        }
        filterChain.doFilter(request, response)
    }
}

private const val MAXIMUM_LOGIN_ATTEMPTS = 50
private val LOGIN_WINDOW: Duration = Duration.ofHours(24)
