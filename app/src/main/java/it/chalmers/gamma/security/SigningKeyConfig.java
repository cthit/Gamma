package it.chalmers.gamma.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class SigningKeyConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(SigningKeyConfig.class);

  @Bean
  public SigningKeys signingKeys(
      OAuth2SigningKeyProperties properties,
      @Value("${application.production}") boolean production) {
    SigningKeys keys = SigningKeys.load(properties, production);

    if (properties.hasSigningKey()) {
      LOGGER.info("Signing OAuth2 tokens with key {}", keys.signingKey().getKeyID());
    } else {
      LOGGER.warn(
          "No OAuth2 signing key is configured, so a temporary key {} is used. Tokens signed with"
              + " it stop working when Gamma restarts. Set OAUTH2_SIGNING_KEY to keep them.",
          keys.signingKey().getKeyID());
    }
    for (RSAKey key : keys.verificationKeys()) {
      LOGGER.info("Publishing OAuth2 verification key {}", key.getKeyID());
    }

    return keys;
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource(SigningKeys keys) {
    return new ImmutableJWKSet<>(keys.publicJwkSet());
  }

  @Bean
  public JwtEncoder jwtEncoder(SigningKeys keys) {
    return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(keys.signingKey())));
  }

  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return NimbusJwtDecoder.withJwkSource(jwkSource).build();
  }
}
