package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.app.apikey.ApiKeyRepository;
import it.chalmers.gamma.app.domain.ApiKeyToken;
import it.chalmers.gamma.app.user.UserService;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;



public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyAuthenticationFilter(UserService userService,
                                      ApiKeyRepository apiKeyRepository) {
        this.userService = userService;
        this.apiKeyRepository = apiKeyRepository;
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
        ApiKeyToken token = new ApiKeyToken(resolveToken(request));
        if (this.apiKeyRepository.getByToken(token).isPresent()) {
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
