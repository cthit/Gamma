package it.chalmers.gamma.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import it.chalmers.gamma.response.InvalidJWTTokenResponse;
import it.chalmers.gamma.service.ITUserService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

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
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthenticationFilter extends OncePerRequestFilter {

    private final String secretKey;
    private final String issuer;
    private final ITUserService itUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    public AuthenticationFilter(ITUserService itUserService, String secretKey, String issuer) {
        this.itUserService = itUserService;
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
            Jws<Claims> claim = decodeToken(encodedToken);
            String token = null;
            if (claim != null) {
                token = (String) claim.getBody().get("user_name");
            }
            if (token != null) {
                Authentication auth = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(request, response);
    }

    private Authentication getAuthentication(String cid) {
        UserDetails userDetails = this.itUserService.loadUserByUsername(cid);
        if (userDetails == null) {
            throw new InvalidJWTTokenResponse();
        }
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
            null, userDetails.getAuthorities());
    }

    /*
     * If this doesn't crash, then the JWT token is valid.
     */
    private Jws<Claims> decodeToken(String token) {
        try {
            return Jwts.parser()
                .requireIssuer(this.issuer)
                .setSigningKey(Base64.getEncoder().encodeToString(
                        this.secretKey.getBytes(Charset.forName("UTF-8")))
                )
                .parseClaimsJws(token);
        } catch (MalformedJwtException | SignatureException e) {
            LOGGER.warn(e.getMessage());
            return null;
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