package it.chalmers.gamma.app.repository;

import it.chalmers.gamma.app.domain.apikey.ApiKeyToken;
import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.app.domain.client.ClientId;
import it.chalmers.gamma.app.domain.client.ClientUid;
import it.chalmers.gamma.app.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {

    void save(Client client);
    void delete(ClientUid clientId) throws ClientNotFoundException;

    List<Client> getAll();
    Optional<Client> get(ClientUid clientUid);
    Optional<Client> get(ClientId clientId);

    List<Client> getClientsByUserApproved(UserId id);

    Optional<Client> getByApiKey(ApiKeyToken apiKeyToken);

    class ClientNotFoundException extends Exception { }

}
