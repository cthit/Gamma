package it.chalmers.gamma.app.facade.internal;

import it.chalmers.gamma.app.domain.client.ClientUid;
import it.chalmers.gamma.app.domain.client.Scope;
import it.chalmers.gamma.app.facade.Facade;
import it.chalmers.gamma.app.usecase.AccessGuardUseCase;
import it.chalmers.gamma.app.domain.client.WebServerRedirectUrl;
import it.chalmers.gamma.app.repository.ClientRepository;
import it.chalmers.gamma.app.repository.ApiKeyRepository;
import it.chalmers.gamma.app.domain.apikey.ApiKey;
import it.chalmers.gamma.app.domain.apikey.ApiKeyId;
import it.chalmers.gamma.app.domain.apikey.ApiKeyType;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.app.domain.client.ClientId;
import it.chalmers.gamma.app.domain.client.ClientSecret;
import it.chalmers.gamma.app.domain.apikey.ApiKeyToken;

import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.app.domain.common.Text;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientFacade extends Facade {

    private final ClientRepository clientRepository;
    private final ApiKeyRepository apiKeyRepository;

    public ClientFacade(AccessGuardUseCase accessGuard,
                        ClientRepository clientRepository,
                        ApiKeyRepository apiKeyRepository) {
        super(accessGuard);
        this.clientRepository = clientRepository;
        this.apiKeyRepository = apiKeyRepository;
    }

    public record NewClient(String webServerRedirectUrl,
                            String prettyName,
                            String svDescription,
                            String enDescription,
                            boolean generateApiKey,
                            List<String> restrictions,
                            boolean emailScope) {
    }

    public record ClientAndApiKeySecrets(String clientSecret, String apiKeyToken) {

    }

    /**
     * @return The client secret for the client
     */
    public ClientAndApiKeySecrets create(NewClient newClient) {
        this.accessGuard.require()
                .isAdmin()
                .ifNotThrow();

        ClientSecret clientSecret = ClientSecret.generate();
        Optional<ApiKey> apiKey = Optional.empty();
        ApiKeyToken apiKeyToken = null;

        if (newClient.generateApiKey) {
            apiKeyToken = ApiKeyToken.generate();

            apiKey = Optional.of(
                    new ApiKey(
                            ApiKeyId.generate(),
                            new PrettyName(newClient.prettyName),
                            new Text(
                                    "Api nyckel för klienten: " + newClient.prettyName,
                                    "Api key for client: " + newClient.prettyName
                            ),
                            ApiKeyType.CLIENT,
                            apiKeyToken
                    )
            );
        }

        List<Scope> scopes = new ArrayList<>();
        scopes.add(Scope.PROFILE);
        if (newClient.emailScope) {
            scopes.add(Scope.EMAIL);
        }

        this.clientRepository.save(
                new Client(
                        ClientUid.generate(),
                        ClientId.generate(),
                        clientSecret,
                        new WebServerRedirectUrl(newClient.webServerRedirectUrl),
                        new PrettyName(newClient.prettyName),
                        new Text(
                                newClient.svDescription,
                                newClient.enDescription
                        ),
                        newClient.restrictions.stream().map(AuthorityLevelName::new).toList(),
                        scopes,
                        Collections.emptyList(),
                        apiKey
                )
        );
        return new ClientAndApiKeySecrets(clientSecret.value(), apiKeyToken == null ? null : apiKeyToken.value());
    }

    public void delete(String clientUid) throws ClientRepository.ClientNotFoundException {
        this.accessGuard.require()
                .isAdmin()
                .ifNotThrow();

        this.clientRepository.delete(ClientUid.valueOf(clientUid));
    }

    public record ClientDTO(UUID clientUid,
                            String clientId,
                            String webServerRedirectUrl,
                            String prettyName,
                            String svDescription,
                            String enDescription,
                            List<String> restrictions,
                            List<UserFacade.UserDTO> approvedUsers,
                            boolean hasApiKey) {
        public ClientDTO(Client client) {
            this(client.clientUid().value(),
                    client.clientId().value(),
                    client.webServerRedirectUrl().value(),
                    client.prettyName().value(),
                    client.description().sv().value(),
                    client.description().en().value(),
                    client.restrictions()
                            .stream()
                            .map(AuthorityLevelName::value)
                            .toList(),
                    client.approvedUsers()
                            .stream()
                            .map(UserFacade.UserDTO::new)
                            .toList(),
                    client.clientApiKey().isPresent()
            );
        }
    }

    public Optional<ClientDTO> get(String clientId) {
        return this.clientRepository.get(new ClientId(clientId)).map(ClientDTO::new);
    }

    public List<ClientDTO> getAll() {
        this.accessGuard.require()
                .isAdmin()
                .ifNotThrow();

        return this.clientRepository.getAll()
                .stream()
                .map(ClientDTO::new)
                .toList();
    }

    public String resetClientSecret(String clientUid) throws ClientNotFoundException {
        this.accessGuard.require()
                .isAdmin()
                .ifNotThrow();

        Client client = this.clientRepository.get(ClientUid.valueOf(clientUid))
                .orElseThrow(ClientNotFoundException::new);
        ClientSecret newSecret = ClientSecret.generate();

        Client newClient = client.withClientSecret(newSecret);

        this.clientRepository.save(newClient);

        return newSecret.value();
    }

    public static class ClientNotFoundException extends Exception { }


}
