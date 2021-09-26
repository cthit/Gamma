package it.chalmers.gamma.app.client;

import it.chalmers.gamma.domain.apikey.ApiKeyId;
import it.chalmers.gamma.domain.apikey.ApiKeyToken;
import it.chalmers.gamma.domain.client.Client;
import it.chalmers.gamma.domain.client.ClientId;
import it.chalmers.gamma.domain.client.ClientSecret;
import it.chalmers.gamma.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {

    void create(Client client);
    void delete(ClientId clientId) throws ClientNotFoundException;
    ClientSecret resetClientSecret(ClientId clientId);

    List<Client> getAll();
    Optional<Client> get(ClientId clientId);

    List<Client> getClientsByUserApproved(UserId id);

    Optional<Client> getByApiKey(ApiKeyToken apiKeyToken);

    class ClientNotFoundException extends Exception { }

}
