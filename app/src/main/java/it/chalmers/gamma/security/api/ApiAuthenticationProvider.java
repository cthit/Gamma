package it.chalmers.gamma.security.api;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ApiAuthenticationProvider implements AuthenticationProvider {

  private final ApiKeyRepository apiKeyRepository;
  private final ClientRepository clientRepository;
  private final PasswordEncoder passwordEncoder;

  public ApiAuthenticationProvider(
      ApiKeyRepository apiKeyRepository,
      ClientRepository clientRepository,
      PasswordEncoder passwordEncoder) {
    this.apiKeyRepository = apiKeyRepository;
    this.clientRepository = clientRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    ApiAuthenticationToken apiAuthenticationToken = (ApiAuthenticationToken) authentication;
    final ApiKey apiKey =
        this.apiKeyRepository
            .getById(new ApiKeyId(apiAuthenticationToken.getCredentials().apiKeyId()))
            .orElseThrow(ApiAuthenticationException::new);

    if (!passwordEncoder.matches(
        apiAuthenticationToken.getCredentials().token(), apiKey.apiKeyToken().value())) {
      throw new ApiAuthenticationException();
    }

    final Optional<Client> maybeClient = this.clientRepository.getByApiKey(apiKey.apiKeyToken());

    return ApiAuthenticationToken.fromAuthenticatedApiKey(
        new ApiAuthentication() {
          @Override
          public ApiKey get() {
            return apiKey;
          }

          @Override
          public Optional<Client> getClient() {
            return maybeClient;
          }
        });
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return ApiAuthenticationToken.class.isAssignableFrom(authentication);
  }

  public static class ApiAuthenticationException extends AuthenticationException {
    public ApiAuthenticationException() {
      super("Exception when trying to authenticate api key");
    }
  }
}
