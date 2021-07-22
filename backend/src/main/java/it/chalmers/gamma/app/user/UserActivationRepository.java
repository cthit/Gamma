package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.UserActivation;
import it.chalmers.gamma.app.domain.UserActivationToken;

import java.util.List;

public interface UserActivationRepository {

    UserActivationToken createUserActivationCode(Cid cid);
    List<UserActivation> getAll();

}
