package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.domain.ApiKeyToken;
import it.chalmers.gamma.internal.apikey.service.ApiKeyService;
import it.chalmers.gamma.internal.user.service.UserService;

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

    private final UserService userService;
    private final ApiKeyService apiKeyService;

    public ApiKeyAuthenticationFilter(UserService userService,
                                      ApiKeyService apiKeyService) {
        this.userService = userService;
        this.apiKeyService = apiKeyService;
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
        ApiKeyToken token = ApiKeyToken.valueOf(resolveToken(request));
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

    private Authentication getAdminAuthentication() {
        //UserDetails userDetails = this.userService.loadUserByUsername("admin");
        //return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
        return null;
    }

    private String removeBasic(String token) {
        return token.substring(11);
    }
}
