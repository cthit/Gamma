package it.chalmers.gamma.adapter.secondary.redis.oauth2;


import it.chalmers.gamma.util.Serializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import java.io.IOException;

@RedisHash(value = "gamma-authorization", timeToLive = 3600)
public class AuthorizationValue {

    @Id
    String id;
    String authorization;

    public AuthorizationValue() {
    }


    public AuthorizationValue(OAuth2Authorization authorization) {
        this.id = authorization.getId();
        try {
            this.authorization = Serializer.toString(authorization);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public OAuth2Authorization toOAuth2Authorization() {
        try {
            return (OAuth2Authorization) Serializer.fromString(this.authorization);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
