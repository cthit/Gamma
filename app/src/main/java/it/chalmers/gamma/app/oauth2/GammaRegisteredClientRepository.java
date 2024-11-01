package it.chalmers.gamma.app.oauth2;

import it.chalmers.gamma.app.client.domain.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Component;

@Component
public class GammaRegisteredClientRepository implements RegisteredClientRepository {

  public static final String IS_OFFICIAL = "_is_official";

  private final ClientRepository clientRepository;

  public GammaRegisteredClientRepository(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  @Override
  public void save(RegisteredClient registeredClient) {
    throw new UnsupportedOperationException("Use ClientFacade instead.");
  }

  @Override
  public RegisteredClient findById(String id) {
    try {
      return this.clientRepository
          .get(ClientUid.valueOf(id))
          .map(this::toRegisteredClient)
          .orElse(null);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  public RegisteredClient findByClientId(String clientId) {
    try {
      return this.clientRepository
          .get(new ClientId(clientId))
          .map(this::toRegisteredClient)
          .orElse(null);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private RegisteredClient toRegisteredClient(Client client) {
    RegisteredClient.Builder builder =
        RegisteredClient.withId(client.clientUid().getValue())
            .clientId(client.clientId().value())
            .clientSecret(client.clientSecret().value())
            .redirectUri(client.clientRedirectUrl().value())
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            .clientName(client.prettyName().value())
            .clientSettings(
                ClientSettings.builder()
                    .requireAuthorizationConsent(true)
                    .setting(IS_OFFICIAL, client.owner() instanceof ClientOwnerOfficial)
                    .build());

    builder.scope("openid");

    for (Scope scope : client.scopes()) {
      builder.scope(scope.name().toLowerCase());
    }

    return builder.build();
  }
}
