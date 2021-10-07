package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.domain.client.WebServerRedirectUrl;
import it.chalmers.gamma.app.port.repository.ClientRepository;
import it.chalmers.gamma.app.port.repository.ApiKeyRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ClientFacade extends Facade {

    private final ClientRepository clientRepository;
    private final ApiKeyRepository apiKeyRepository;

    public ClientFacade(AccessGuard accessGuard,
                        ClientRepository clientRepository,
                        ApiKeyRepository apiKeyRepository) {
        super(accessGuard);
        this.clientRepository = clientRepository;
        this.apiKeyRepository = apiKeyRepository;
    }

    public record NewClient(String webServerRedirectUrl,
                            String prettyName,
                            boolean autoApprove,
                            String svDescription,
                            String enDescription,
                            boolean generateApiKey,
                            List<String> restrictions) {
    }

    public record ClientAndApiKeySecrets(String clientSecret, String apiKeyToken) {

    }

    /**
     * @return The client secret for the client
     */
    public ClientAndApiKeySecrets create(NewClient newClient) {
        accessGuard.requireIsAdmin();
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

        this.clientRepository.save(new Client(
                ClientId.generate(),
                clientSecret,
                new WebServerRedirectUrl(newClient.webServerRedirectUrl),
                newClient.autoApprove,
                new PrettyName(newClient.prettyName),
                new Text(
                        newClient.svDescription,
                        newClient.enDescription
                ),
                newClient.restrictions.stream().map(AuthorityLevelName::new).toList(),
                Collections.emptyList(),
                apiKey
        ));
        return new ClientAndApiKeySecrets(clientSecret.value(), apiKeyToken == null ? null : apiKeyToken.value());
    }

    public void delete(String clientId) throws ClientRepository.ClientNotFoundException {
        accessGuard.requireIsAdmin();
        this.clientRepository.delete(new ClientId(clientId));
    }

    public record ClientDTO(String clientId,
                            String webServerRedirectUrl,
                            boolean autoApprove,
                            String prettyName,
                            String svDescription,
                            String enDescription,
                            List<String> restrictions,
                            List<UserFacade.UserDTO> approvedUsers,
                            boolean hasApiKey) {
        public ClientDTO(Client client) {
            this(client.clientId().value(),
                    client.webServerRedirectUrl().value(),
                    client.autoApprove(),
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
        accessGuard.requireIsAdmin();
        return this.clientRepository.getAll()
                .stream()
                .map(ClientDTO::new)
                .toList();
    }

    public String resetClientSecret(String clientId) throws ClientNotFoundException {
        accessGuard.requireIsAdmin();
        Client client = this.clientRepository.get(new ClientId(clientId))
                .orElseThrow(ClientNotFoundException::new);
        ClientSecret newSecret = ClientSecret.generate();

        this.clientRepository.save(client.withClientSecret(newSecret));
        return newSecret.value();
    }

    public static class ClientNotFoundException extends Exception { }


}
