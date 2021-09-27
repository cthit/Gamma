package it.chalmers.gamma.app.port.repository;

import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.useractivation.UserActivation;
import it.chalmers.gamma.app.domain.useractivation.UserActivationToken;

import java.util.List;

public interface UserActivationRepository {

    UserActivationToken createUserActivationCode(Cid cid);
    List<UserActivation> getAll();
    Cid getByToken(UserActivationToken token);

}
