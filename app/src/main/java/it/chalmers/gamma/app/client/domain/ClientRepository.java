package it.chalmers.gamma.app.client.domain;

import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.user.domain.UserId;
import java.util.List;
import java.util.Optional;

public interface ClientRepository {

  void save(Client client)
      throws AuthorityNotFoundRuntimeException,
          UserNotFoundRuntimeException,
          ClientIdAlreadyExistsRuntimeException;

  void delete(ClientUid clientId) throws ClientNotFoundException;

  List<Client> getAll();

  List<Client> getAllUserClients(UserId userId);

  Optional<Client> get(ClientUid clientUid);

  Optional<Client> get(ClientId clientId);

  void addClientApproval(UserId userId, ClientUid clientUid);

  boolean isApprovedByUser(UserId userId, ClientUid clientUid);

  List<Client> getClientsByUserApproved(UserId id);

  void deleteUserApproval(ClientUid clientUid, UserId userId);

  Optional<Client> getByApiKey(ApiKeyToken apiKeyToken);

  Optional<Client> getByApiKey(ApiKeyId apiKeyId);

  class ClientNotFoundException extends Exception {}

  class ClientIdAlreadyExistsRuntimeException extends RuntimeException {}

  class AuthorityNotFoundRuntimeException extends RuntimeException {}

  class UserNotFoundRuntimeException extends RuntimeException {}
}
