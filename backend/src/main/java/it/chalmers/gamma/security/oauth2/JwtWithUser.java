package it.chalmers.gamma.security.oauth2;

import it.chalmers.gamma.app.user.domain.GammaUser;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtWithUser extends Jwt {

    private GammaUser user;

    public JwtWithUser(Jwt jwt, GammaUser user) {
        super(jwt.getTokenValue(),
                jwt.getIssuedAt(),
                jwt.getExpiresAt(),
                jwt.getHeaders(),
                jwt.getClaims());

        this.user = user;
    }

    public GammaUser getUser() {
        return user;
    }
}
