package it.chalmers.gamma.app.oauth;

import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientId;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.Scope;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.stereotype.Component;

@Component
public class GammaRegisteredClientRepository implements RegisteredClientRepository {

    private final ClientRepository clientRepository;

    public GammaRegisteredClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        //Use ClientFacade instead
        throw new UnsupportedOperationException();
    }

    @Override
    public RegisteredClient findById(String id) {
        Client client = this.clientRepository.get(ClientUid.valueOf(id))
                .orElseThrow(NullPointerException::new);

        return toRegisteredClient(client);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Client client = this.clientRepository.get(new ClientId(clientId))
                .orElseThrow(NullPointerException::new);

        return toRegisteredClient(client);
    }

    private RegisteredClient toRegisteredClient(Client client) {
        RegisteredClient.Builder builder = RegisteredClient
                .withId(client.clientUid().getValue())
                .clientId(client.clientId().value())
                .clientSecret(client.clientSecret().value())
                .redirectUri(client.redirectUrl().value())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientName(client.prettyName().value())
                .clientSettings(
                        ClientSettings
                                .builder()
                                .requireAuthorizationConsent(true)
                                .build()
                )
                .scope("openid");

        for (Scope scope : client.scopes()) {
            builder.scope(scope.name().toLowerCase());
        }

        return builder.build();
    }

}
