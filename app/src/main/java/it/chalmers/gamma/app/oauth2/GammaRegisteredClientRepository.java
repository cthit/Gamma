package it.chalmers.gamma.app.oauth2;

import it.chalmers.gamma.app.client.domain.*;
import java.util.List;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Component;

@Component
public class GammaRegisteredClientRepository implements RegisteredClientRepository {

  public static final String IS_OFFICIAL = "_is_official";

  /** The grant types every client gets. They are also advertised in the discovery documents. */
  public static final List<AuthorizationGrantType> GRANT_TYPES =
      List.of(AuthorizationGrantType.AUTHORIZATION_CODE);

  /**
   * The ways a client can authenticate with the token endpoint. They are also advertised in the
   * discovery documents.
   */
  public static final List<ClientAuthenticationMethod> CLIENT_AUTHENTICATION_METHODS =
      List.of(
          ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
          ClientAuthenticationMethod.CLIENT_SECRET_POST);

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
            .authorizationGrantTypes(grantTypes -> grantTypes.addAll(GRANT_TYPES))
            .clientAuthenticationMethods(methods -> methods.addAll(CLIENT_AUTHENTICATION_METHODS))
            .clientName(client.prettyName().value())
            .clientSettings(
                ClientSettings.builder()
                    .requireProofKey(false)
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
