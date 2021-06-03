package it.chalmers.gamma.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.internal.user.service.UserDTO;
import it.chalmers.gamma.internal.user.service.UserDetailsImpl;
import it.chalmers.gamma.security.authentication.response.InvalidJWTTokenResponse;
import it.chalmers.gamma.internal.user.service.UserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String secretKey;
    private final String issuer;
    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(UserService userService, String secretKey, String issuer) {
        this.userService = userService;
        this.secretKey = secretKey;
        this.issuer = issuer;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        String encodedToken = resolveToken(request);
        if (encodedToken != null) {
            try {
                Jws<Claims> claim = decodeToken(encodedToken);
                String token = null;
                if (claim != null) {
                    token = (String) claim.getBody().get("user_name");
                }
                if (token != null) {
                    Authentication auth = getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (SignatureException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private Authentication getAuthentication(String cid) {
        UserDTO user;
        try {
            user = this.userService.get(new Cid(cid));
        } catch (UserService.UserNotFoundException e) {
            throw new InvalidJWTTokenResponse();
        }
        return new UsernamePasswordAuthenticationToken(
                user.cid().get(),
                null,
                Collections.emptyList()
        );
    }

    /*
     * If this doesn't crash, then the JWT token is valid.
     */
    private Jws<Claims> decodeToken(String token) {
        try {
            return Jwts.parser()
                    .requireIssuer(this.issuer)
                    .setSigningKey(Base64.getEncoder().encodeToString(
                            this.secretKey.getBytes(StandardCharsets.UTF_8))
                    )
                    .parseClaimsJws(token);
        } catch (MalformedJwtException | SignatureException e) {
            LOGGER.warn(e.getMessage());
            throw new SignatureException(e.getMessage());
        }
    }

    private String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return removeBearer(bearerToken);
        }
        return null;
    }

    private String removeBearer(String token) {
        return token.substring(7);
    }

}