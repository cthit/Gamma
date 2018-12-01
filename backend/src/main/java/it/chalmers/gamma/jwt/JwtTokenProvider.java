package it.chalmers.gamma.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import it.chalmers.gamma.service.ITUserService;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds;

    @Value("${security.jwt.token.issuer}")
    private String issuer;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Autowired
    private ITUserService itUserService;

    public String createToken(String cid) {

        Claims claims = Jwts.claims().setSubject(cid);

        Date now = new Date();
        Date validity = new Date(now.getTime() + this.validityInMilliseconds);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuer(this.issuer)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, this.secretKey)
            .compact();
    }

    public Authentication getAuthentication(String cid){
        UserDetails userDetails = this.itUserService.loadUserByUsername(cid);
        if(userDetails == null){
            throw new InvalidJWTTokenResponse();
        }
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                userDetails.getPassword(), userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return removeBearer(bearerToken);
        }
        return null;
    }

    public String removeBearer(String token) {
        return token.substring(7);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Jws<Claims> decodeToken(String token) {
        try {
            return Jwts.parser()
                .requireIssuer(this.issuer)
                .setSigningKey(this.secretKey)
                .parseClaimsJws(token);
        } catch (MalformedJwtException | SignatureException e) {
            LOGGER.warn(e.getMessage());
            return null;
        }
    }

}
