package it.chalmers.gamma.adapter.secondary.oauth;

import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.app.domain.client.ClientId;
import it.chalmers.gamma.app.repository.ClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

@Component
public class GammaRegisteredClientRepository implements RegisteredClientRepository {

    private final ClientRepository clientRepository;

    public GammaRegisteredClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RegisteredClient findById(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Client client = this.clientRepository.get(new ClientId(clientId))
                .orElseThrow();

        return RegisteredClient.withId("???")
                .clientId(client.clientId().value())
                .clientSecret(client.clientSecret().value())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(client.webServerRedirectUrl().value())
                .scope("access")
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .clientName(client.prettyName().value())
                .build();
    }
}
