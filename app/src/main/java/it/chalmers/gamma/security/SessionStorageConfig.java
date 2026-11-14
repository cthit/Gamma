package it.chalmers.gamma.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;

@Configuration
public class SessionStorageConfig {

  @Bean
  RedisSerializer<Object> springSessionDefaultRedisSerializer() {
    return new JdkSerializationRedisSerializer() {
      @Override
      public byte[] serialize(Object value) {
        // UserAuthentication contains a fresh database snapshot for this request only.
        // Persist the login identity and factor times, never the profile in its details.
        if (value instanceof SecurityContext context
            && context.getAuthentication() instanceof UsernamePasswordAuthenticationToken token
            && token.isAuthenticated()) {
          value =
              new SecurityContextImpl(
                  UsernamePasswordAuthenticationToken.authenticated(
                      token.getPrincipal(), null, token.getAuthorities()));
        }
        return super.serialize(value);
      }
    };
  }
}
