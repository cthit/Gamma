package it.chalmers.gamma.filter;

import it.chalmers.gamma.apikey.ApiKeyService;
import it.chalmers.gamma.user.service.UserService;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;



public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;
    private final UserService userService;

    public ApiKeyAuthenticationFilter(ApiKeyService apiKeyService, UserService userService) {
        this.apiKeyService = apiKeyService;
        this.userService = userService;
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
        }
        filterChain.doFilter(request, response);
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
        UserDetails userDetails = this.userService.loadUserByUsername("admin");
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
    }

    private String removeBasic(String token) {
        return token.substring(11);
    }
}
