package it.chalmers.gamma.adapter.secondary.redis.oauth2;

import it.chalmers.gamma.app.oauth2.domain.GammaAuthorizationRepository;
import it.chalmers.gamma.app.oauth2.domain.GammaAuthorizationToken;
import java.util.Optional;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationRepositoryAdapter implements GammaAuthorizationRepository {

  private final AuthorizationRedisRepository authorizationRedisRepository;
  private final StringRedisTemplate redis;
  private final AuthorizationTokenMapperRedisRepository authorizationTokenMapperRedisRepository;

  public AuthorizationRepositoryAdapter(
      AuthorizationRedisRepository authorizationRedisRepository,
      AuthorizationTokenMapperRedisRepository authorizationTokenMapperRedisRepository,
      StringRedisTemplate redis) {
    this.authorizationRedisRepository = authorizationRedisRepository;
    this.redis = redis;
    this.authorizationTokenMapperRedisRepository = authorizationTokenMapperRedisRepository;
  }

  @Override
  public void save(OAuth2Authorization authorization) {
    this.authorizationRedisRepository.save(new AuthorizationValue(authorization));

    var code = authorization.getToken(OAuth2AuthorizationCode.class);
    if (code != null) {
      this.authorizationTokenMapperRedisRepository.save(
          new AuthorizationTokenMapperValue(
              authorization.getId(), "code", code.getToken().getTokenValue()));
    }

    var accessToken = authorization.getToken(OAuth2AccessToken.class);
    if (accessToken != null) {
      this.authorizationTokenMapperRedisRepository.save(
          new AuthorizationTokenMapperValue(
              authorization.getId(), "access_token", accessToken.getToken().getTokenValue()));
    }

    var state = authorization.getAttribute("state");
    if (state != null) {
      this.authorizationTokenMapperRedisRepository.save(
          new AuthorizationTokenMapperValue(authorization.getId(), "state", state.toString()));
    }

    var oidcToken = authorization.getToken(OidcIdToken.class);
    if (oidcToken != null) {
      this.authorizationTokenMapperRedisRepository.save(
          new AuthorizationTokenMapperValue(
              authorization.getId(), "oidc", oidcToken.getToken().getTokenValue()));
    }
  }

  @Override
  public void remove(OAuth2Authorization authorization) {
    this.authorizationRedisRepository.deleteById(authorization.getId());
    deleteToken(authorization, OAuth2AuthorizationCode.class, "code");
    deleteToken(authorization, OAuth2AccessToken.class, "access_token");
    deleteToken(authorization, OidcIdToken.class, "oidc");
    Object state = authorization.getAttribute("state");
    if (state != null) {
      this.authorizationTokenMapperRedisRepository.deleteById("state:" + state);
    }
  }

  private <T extends OAuth2Token> void deleteToken(
      OAuth2Authorization authorization, Class<T> type, String key) {
    var token = authorization.getToken(type);
    if (token != null) {
      this.authorizationTokenMapperRedisRepository.deleteById(
          key + ":" + token.getToken().getTokenValue());
    }
  }

  @Override
  public void revokeForUser(String principalName, String registeredClientId) {
    revoke(principalName, registeredClientId);
  }

  @Override
  public void revokeForClient(String registeredClientId) {
    revoke(null, registeredClientId);
  }

  private void revoke(String principalName, String registeredClientId) {
    // Include live grants from older releases that have no principal/client indexes.
    // SCAN is incremental and does not load the historical repository membership set.
    String prefix = "gamma-authorization:";
    try (var keys =
        this.redis.scan(ScanOptions.scanOptions().match(prefix + "*").count(1000).build())) {
      while (keys.hasNext()) {
        String id = keys.next().substring(prefix.length());
        if (id.contains(":")) {
          continue; // Exclude expiry shadow copies and repository index keys.
        }
        this.findById(id)
            .filter(authorization ->
                (principalName == null || principalName.equals(authorization.getPrincipalName()))
                    && (registeredClientId == null
                        || registeredClientId.equals(authorization.getRegisteredClientId())))
            .ifPresent(this::remove);
      }
    }
  }

  @Override
  public Optional<OAuth2Authorization> findById(String id) {
    return this.authorizationRedisRepository
        .findById(id)
        .map(AuthorizationValue::toOAuth2Authorization);
  }

  @Override
  public Optional<OAuth2Authorization> findByToken(
      GammaAuthorizationToken gammaAuthorizationToken) {
    String token =
        gammaAuthorizationToken.type().name().toLowerCase() + ":" + gammaAuthorizationToken.value();
    Optional<AuthorizationTokenMapperValue> maybeTokenMapper =
        this.authorizationTokenMapperRedisRepository.findById(token);
    if (maybeTokenMapper.isPresent()) {
      Optional<AuthorizationValue> maybeAuthorizationValue =
          this.authorizationRedisRepository.findById(maybeTokenMapper.get().authorizationKey);
      return maybeAuthorizationValue.map(AuthorizationValue::toOAuth2Authorization);
    }

    return Optional.empty();
  }
}
