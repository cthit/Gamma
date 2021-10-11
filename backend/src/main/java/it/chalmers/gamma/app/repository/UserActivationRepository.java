package it.chalmers.gamma.app.repository;

import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.useractivation.UserActivation;
import it.chalmers.gamma.app.domain.useractivation.UserActivationToken;

import java.util.List;
import java.util.Optional;

public interface UserActivationRepository {

    UserActivationToken createUserActivationCode(Cid cid);
    Optional<UserActivation> get(Cid cid);
    List<UserActivation> getAll();
    Cid getByToken(UserActivationToken token);

    void removeActivation(Cid cid);
}
