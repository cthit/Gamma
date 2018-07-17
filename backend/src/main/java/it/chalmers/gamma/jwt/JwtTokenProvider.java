package it.chalmers.gamma.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import it.chalmers.gamma.response.InvalidJWTTokenResponse;
import it.chalmers.gamma.service.ITUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds;

    @Value("${security.jwt.token.issuer}")
    private String issuer;

    @Autowired
    private ITUserService itUserService;

    public String createToken(String cid) {

        Claims claims = Jwts.claims().setSubject(cid);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        System.out.println(validity.toString());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String cid){
        UserDetails userDetails = itUserService.loadUserByUsername(cid);
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword());
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    public String decodeToken(String token){
        return Jwts.parser()
                .requireIssuer(issuer)
                .setSigningKey(
                        TextCodec.BASE64.decode(secretKey)
                )
                .parseClaimsJws(token).getSignature();
    }

}
