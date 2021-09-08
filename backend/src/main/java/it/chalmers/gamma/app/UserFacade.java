package it.chalmers.gamma.app;

import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserFacade extends Facade {

    public UserFacade(AccessGuard accessGuard) {
        super(accessGuard);
    }

    public User get(Cid cid) {
        return null;
    }

}
