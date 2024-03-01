package it.chalmers.gamma.security.api;

import static org.springframework.security.core.authority.AuthorityUtils.NO_AUTHORITIES;

import it.chalmers.gamma.security.authentication.ApiAuthentication;
import java.util.UUID;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Transient;

@Transient
public class ApiAuthenticationToken extends AbstractAuthenticationToken {

  private final UUID apiKeyId;
  private final String apiKeyToken;
  private final ApiAuthentication apiPrincipal;

  private ApiAuthenticationToken(ApiAuthentication apiPrincipal) {
    super(NO_AUTHORITIES);
    this.apiKeyId = apiPrincipal.get().id().value();
    this.apiKeyToken = null;
    this.apiPrincipal = apiPrincipal;
    super.setAuthenticated(true);
  }

  private ApiAuthenticationToken(UUID apiKeyId, String apiKeyToken) {
    super(NO_AUTHORITIES);
    this.apiKeyId = apiKeyId;
    this.apiKeyToken = apiKeyToken;
    this.apiPrincipal = null;
    super.setAuthenticated(false);
  }

  public static ApiAuthenticationToken fromApiKeyToken(String apiKeyToken) {
    try {
      String[] parts = apiKeyToken.split(":");

      return new ApiAuthenticationToken(UUID.fromString(parts[0]), parts[1]);
    } catch (Exception e) {
      throw new ApiAuthenticationProvider.ApiAuthenticationException();
    }
  }

  public static ApiAuthenticationToken fromAuthenticatedApiKey(ApiAuthentication apiPrincipal) {
    return new ApiAuthenticationToken(apiPrincipal);
  }

  @Override
  public void setAuthenticated(boolean authenticated) {
    throw new IllegalCallerException(
        "You cannot call set authenticated on an already created ApiAuthenticationToken");
  }

  public record Credentials(UUID apiKeyId, String token) {}

  @Override
  public Credentials getCredentials() {
    return new Credentials(this.apiKeyId, this.apiKeyToken);
  }

  @Override
  public ApiAuthentication getPrincipal() {
    return this.apiPrincipal;
  }
}
