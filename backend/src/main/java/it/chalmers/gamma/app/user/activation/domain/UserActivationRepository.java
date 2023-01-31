package it.chalmers.gamma.app.user.activation.domain;

import it.chalmers.gamma.app.user.domain.Cid;

import java.util.List;
import java.util.Optional;

public interface UserActivationRepository {

    /**
     * Creates an activation token that is connected to the cid.
     * If there already is a token generated, then a new one will be generated.
     *
     * @param cid A cid that has been whitelisted
     * @return A token that can be used to create an account with the given cid
     */
    UserActivationToken createActivationToken(Cid cid) throws CidNotWhitelistedException;

    Optional<UserActivation> get(Cid cid);

    List<UserActivation> getAll();

    Cid getByToken(UserActivationToken token) throws TokenNotActivatedException;

    void removeActivation(Cid cid) throws CidNotActivatedException;

    class TokenNotActivatedException extends RuntimeException {
    }

    class CidNotActivatedException extends RuntimeException {
    }

    class CidNotWhitelistedException extends Exception {
    }
}
