package it.chalmers.gamma.app.authority.domain;

import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.user.domain.UserId;

import java.util.List;
import java.util.Optional;

public interface ClientAuthorityRepository {

    void create(ClientUid clientUid, AuthorityName authorityName) throws ClientAuthorityAlreadyExistsException;

    void delete(ClientUid clientUid, AuthorityName authorityName) throws ClientAuthorityNotFoundException;

    void save(Authority authority)
            throws ClientAuthorityNotFoundRuntimeException, NotCompleteClientAuthorityException;

    List<Authority> getAllByClient(ClientUid clientUid);
    List<Authority> getAllByUser(ClientUid clientUid, UserId userId);
    Optional<Authority> get(ClientUid clientUid, AuthorityName authorityName);

    class ClientAuthorityAlreadyExistsException extends Exception {
        public ClientAuthorityAlreadyExistsException(String value) {
            super("Authority level: " + value + " already exists");
        }
    }

    class ClientAuthorityNotFoundException extends Exception {
    }

    class ClientAuthorityNotFoundRuntimeException extends RuntimeException {
    }

    /**
     * Can be avoided if you check that supergroups, posts, and users actually exists.
     * It happens when linking an authority level with one of the above, and it is not found in database.
     */
    class NotCompleteClientAuthorityException extends RuntimeException { }


}
