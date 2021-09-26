package it.chalmers.gamma.app.user;

import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.useractivation.UserActivation;
import it.chalmers.gamma.domain.useractivation.UserActivationToken;

import java.util.List;

public interface UserActivationRepository {

    UserActivationToken createUserActivationCode(Cid cid);
    List<UserActivation> getAll();
    Cid getByToken(UserActivationToken token);

}
