package it.chalmers.gamma.filter;

import it.chalmers.gamma.service.ApiKeyService;
import it.chalmers.gamma.service.ITUserService;

import it.chalmers.gamma.service.SessionService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;



public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;
    private final ITUserService itUserService;
    private final SessionService sessionService;

    public ApiKeyAuthenticationFilter(ApiKeyService apiKeyService,
                                      ITUserService itUserService,
                                      SessionService sessionService) {
        this.apiKeyService = apiKeyService;
        this.itUserService = itUserService;
        this.sessionService = sessionService;
    }

    /*
    Authentication using API keys are not supported natively, so once a recognized API key is sent, the Server
    authenticates the session by giving the request the Principal and Authorities of the Admin user.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (this.apiKeyService.isValidApiKey(token)) {
            Authentication auth = getAdminAuthentication();
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
            HttpSession session = request.getSession();
            SecurityContextHolder.clearContext();
            this.sessionService.removeSessionFromRedis(session);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String resolveToken(HttpServletRequest req) {
        String basicToken = req.getHeader("Authorization");
        if (basicToken != null && basicToken.startsWith("pre-shared ")) {
            return removeBasic(basicToken);
        }
        return null;
    }
    // THIS IS SOOOOOOO UGLY
    // Can't figure out another, less hacky way to do it...
    private Authentication getAdminAuthentication() {
        UserDetails userDetails = this.itUserService.loadUserByUsername("admin");
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
    }

    private String removeBasic(String token) {
        return token.substring(11);
    }
}
