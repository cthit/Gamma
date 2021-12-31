package it.chalmers.gamma.app.client.domain;

import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.user.domain.UserId;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {

    void save(Client client)
            throws AuthorityLevelNotFoundRuntimeException, UserNotFoundRuntimeException, ClientIdAlreadyExistsRuntimeException;
    void delete(ClientUid clientId) throws ClientNotFoundException;

    List<Client> getAll();
    Optional<Client> get(ClientUid clientUid);
    Optional<Client> get(ClientId clientId);

    List<Client> getClientsByUserApproved(UserId id);

    Optional<Client> getByApiKey(ApiKeyToken apiKeyToken);

    class ClientNotFoundException extends Exception { }

    class ClientIdAlreadyExistsRuntimeException extends RuntimeException { }
    class AuthorityLevelNotFoundRuntimeException extends RuntimeException { }
    class UserNotFoundRuntimeException extends RuntimeException { }

}
