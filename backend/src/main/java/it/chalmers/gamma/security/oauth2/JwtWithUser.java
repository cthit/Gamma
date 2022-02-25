package it.chalmers.gamma.security.oauth2;

import it.chalmers.gamma.app.user.domain.User;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;

public class JwtWithUser extends Jwt {

    private User user;

    public JwtWithUser(Jwt jwt, User user) {
        super(jwt.getTokenValue(),
                jwt.getIssuedAt(),
                jwt.getExpiresAt(),
                jwt.getHeaders(),
                jwt.getClaims());

        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
