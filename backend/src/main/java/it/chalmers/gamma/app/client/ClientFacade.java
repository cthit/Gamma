package it.chalmers.gamma.app.client;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.ApiKeyRepository;
import it.chalmers.gamma.domain.client.Client;
import it.chalmers.gamma.domain.client.ClientId;
import it.chalmers.gamma.domain.client.ClientSecret;
import it.chalmers.gamma.domain.apikey.ApiKeyToken;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Transactional
    public void create(Client newClient, ClientSecret clientSecret) {
        accessGuard.requireIsAdmin();
        this.clientRepository.create(newClient, clientSecret);
    }

    @Transactional
    public void createWithApiKey(Client newClient,
                                 ClientSecret clientSecret,
                                 ApiKeyToken apiKeyToken) {
        accessGuard.requireIsAdmin();

//        ApiKey apiKey = ApiKey.create(newClient.prettyName(), newClient.description(), ApiKeyType.CLIENT);
//        this.apiKeyRepository.create(apiKey, apiKeyToken);
//
//        newClient = newClient.withApiKey(apiKey);
//        this.create(newClient, clientSecret);
    }

    public void delete(ClientId clientId) throws ClientRepository.ClientNotFoundException {
        accessGuard.requireIsAdmin();
        this.clientRepository.delete(clientId);
    }

    public Optional<Client> get(ClientId clientId) {
        return this.clientRepository.get(clientId);
    }

    public List<Client> getAll() {
        accessGuard.requireIsAdmin();
        return this.clientRepository.getAll();
    }

    public ClientSecret resetClientSecret(ClientId clientId) throws ClientNotFoundException {
        accessGuard.requireIsAdmin();
        return this.clientRepository.resetClientSecret(clientId);
    }

    public static class ClientNotFoundException extends Exception { }


}
