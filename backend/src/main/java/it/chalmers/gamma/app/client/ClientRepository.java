package it.chalmers.gamma.app.client;

import it.chalmers.gamma.app.domain.Client;
import it.chalmers.gamma.app.domain.ClientId;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {

    void create(Client client);
    void createWithApiKey(Client client);
    void delete(ClientId clientId) throws ClientNotFoundException;

    List<Client> getAll();
    Optional<Client> get(ClientId clientId);

    class ClientNotFoundException extends Exception { }

}
