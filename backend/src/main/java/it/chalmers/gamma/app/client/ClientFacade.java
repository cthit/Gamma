package it.chalmers.gamma.app.client;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.authority.domain.AuthorityName;
import it.chalmers.gamma.app.client.domain.*;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedIn;

@Service
public class ClientFacade extends Facade {

    private final ClientRepository clientRepository;

    public ClientFacade(AccessGuard accessGuard,
                        ClientRepository clientRepository) {
        super(accessGuard);
        this.clientRepository = clientRepository;
    }

    /**
     * @return The client secret for the client
     */
    @Transactional
    public ClientAndApiKeySecrets create(NewClient newClient) {
        this.accessGuard.require(isAdmin());

        ClientSecret clientSecret = ClientSecret.generate();
        ApiKey apiKey = null;
        ApiKeyToken apiKeyToken = null;

        if (newClient.generateApiKey) {
            apiKeyToken = ApiKeyToken.generate();

            apiKey = new ApiKey(
                    ApiKeyId.generate(),
                    new PrettyName(newClient.prettyName),
                    new Text(
                            "Api nyckel för klienten: " + newClient.prettyName,
                            "Api key for client: " + newClient.prettyName
                    ),
                    ApiKeyType.CLIENT,
                    apiKeyToken
            );
        }

        List<Scope> scopes = new ArrayList<>();
        scopes.add(Scope.PROFILE);
        if (newClient.emailScope) {
            scopes.add(Scope.EMAIL);
        }

        ClientUid clientUid = ClientUid.generate();
        ClientId clientId = ClientId.generate();

        this.clientRepository.save(
                new Client(
                        clientUid,
                        clientId,
                        clientSecret,
                        new ClientRedirectUrl(newClient.redirectUrl),
                        new PrettyName(newClient.prettyName),
                        new Text(
                                newClient.svDescription,
                                newClient.enDescription
                        ),
                        scopes,
                        apiKey,
                        new ClientOwnerOfficial()
                )
        );
        return new ClientAndApiKeySecrets(
                clientUid.value(),
                clientId.value(),
                clientSecret.value(),
                apiKeyToken == null ? null : apiKeyToken.value()
        );
    }

    @Transactional
    public ClientAndApiKeySecrets createDev(NewClient newClient) {
        accessGuard.require(isSignedIn());

        return null;
    }

    public void delete(String clientUid) throws ClientFacade.ClientNotFoundException {
        this.accessGuard.require(isAdmin());

        try {
            this.clientRepository.delete(ClientUid.valueOf(clientUid));
        } catch (ClientRepository.ClientNotFoundException e) {
            throw new ClientFacade.ClientNotFoundException();
        }
    }

    public Optional<ClientDTO> get(String clientUid) {
        return this.clientRepository.get(ClientUid.valueOf(clientUid)).map(ClientDTO::new);
    }

    public List<ClientDTO> getAll() {
        this.accessGuard.require(isAdmin());

        return this.clientRepository.getAll()
                .stream()
                .map(ClientDTO::new)
                .toList();
    }

    public String resetClientSecret(String clientUid) throws ClientNotFoundException {
        this.accessGuard.require(isAdmin());

        Client client = this.clientRepository.get(ClientUid.valueOf(clientUid))
                .orElseThrow(ClientNotFoundException::new);
        ClientSecret newSecret = ClientSecret.generate();

        Client newClient = client.withClientSecret(newSecret);

        this.clientRepository.save(newClient);

        return newSecret.value();
    }

    public record NewClient(String redirectUrl,
                            String prettyName,
                            String svDescription,
                            String enDescription,
                            boolean generateApiKey,
                            boolean emailScope) {
    }

    public record ClientAndApiKeySecrets(
            UUID clientUid,
            String clientId,
            String clientSecret,
            String apiKeyToken
    ) {
    }

    public record ClientDTO(UUID clientUid,
                            String clientId,
                            String webServerRedirectUrl,
                            String prettyName,
                            String svDescription,
                            String enDescription,
                            boolean hasApiKey) {

        public ClientDTO(Client client) {
            this(client.clientUid().value(),
                    client.clientId().value(),
                    client.clientRedirectUrl().value(),
                    client.prettyName().value(),
                    client.description().sv().value(),
                    client.description().en().value(),
                    client.clientApiKey().isPresent()
            );
        }
    }

    public static class ClientNotFoundException extends Exception {
    }

}
