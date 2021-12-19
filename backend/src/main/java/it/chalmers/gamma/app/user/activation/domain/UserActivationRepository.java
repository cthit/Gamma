package it.chalmers.gamma.app.user.activation.domain;

import it.chalmers.gamma.app.user.domain.Cid;

import java.util.List;
import java.util.Optional;

public interface UserActivationRepository {

    UserActivationToken createUserActivationCode(Cid cid);
    Optional<UserActivation> get(Cid cid);
    List<UserActivation> getAll();
    Cid getByToken(UserActivationToken token);

    void removeActivation(Cid cid);
}
