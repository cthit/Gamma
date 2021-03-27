package it.chalmers.gamma.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import it.chalmers.gamma.security.authentication.response.InvalidJWTTokenResponse;
import it.chalmers.gamma.security.authentication.response.ValidJwtResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;
    @Value("${security.jwt.token.issuer}")
    private String issuer;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    @GetMapping("/valid_token")
    public ValidJwtResponse validJWT(@RequestParam("token") String token) {
        try {
            Jws<Claims> claim = decodeToken(token);
            if (claim.getBody().getExpiration().before(new Date())) {
                throw new InvalidJWTTokenResponse();
            }
            return new ValidJwtResponse(true);
        } catch (InvalidJWTTokenResponse | IllegalArgumentException e) {
            return new ValidJwtResponse(false);
        }

    }

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
            throw new InvalidJWTTokenResponse();
        }
    }
}
