package it.chalmers.gamma.adapter.secondary.oauth;

import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.app.domain.client.ClientId;
import it.chalmers.gamma.app.repository.ClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

//@Component
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
                .orElseThrow(NullPointerException::new);

        System.out.println("hello");

        RegisteredClient rc = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(client.clientId().value())
                .clientSecret(client.clientSecret().value())
                .clientIdIssuedAt(Instant.now())
                .clientSecretExpiresAt(Instant.now().plusSeconds(100000))
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(client.webServerRedirectUrl().value())
                .scope("access")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientName(client.prettyName().value())
                .build();

        return rc;
    }
}
