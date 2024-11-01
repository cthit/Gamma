package it.chalmers.gamma.adapter.secondary.redis.oauth2;

import it.chalmers.gamma.app.oauth2.domain.GammaAuthorizationRepository;
import it.chalmers.gamma.app.oauth2.domain.GammaAuthorizationToken;
import java.util.Optional;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationRepositoryAdapter implements GammaAuthorizationRepository {

  private final AuthorizationRedisRepository authorizationRedisRepository;
  private final AuthorizationTokenMapperRedisRepository authorizationTokenMapperRedisRepository;

  public AuthorizationRepositoryAdapter(
      AuthorizationRedisRepository authorizationRedisRepository,
      AuthorizationTokenMapperRedisRepository authorizationTokenMapperRedisRepository) {
    this.authorizationRedisRepository = authorizationRedisRepository;
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
    this.authorizationRedisRepository.save(new AuthorizationValue(authorization));
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
