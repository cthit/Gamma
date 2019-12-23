package it.chalmers.gamma.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import it.chalmers.gamma.response.InvalidJWTTokenResponse;
import it.chalmers.gamma.response.ValidJwtResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;
    @Value("${security.jwt.token.issuer}")
    private String issuer;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);


    @RequestMapping(value = "/valid_token", method = RequestMethod.GET)
    public ValidJwtResponse validJWT(@RequestParam("token") String token) {
        try {
            Jws<Claims> claim = decodeToken(token);
            if (claim.getBody().getExpiration().before(new Date())) {
                throw new InvalidJWTTokenResponse();
            }
            return new ValidJwtResponse(true);
        } catch (InvalidJWTTokenResponse e) {
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
