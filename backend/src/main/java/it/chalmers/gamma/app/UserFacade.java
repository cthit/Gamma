package it.chalmers.gamma.app;

import org.springframework.stereotype.Component;

@Component
public class UserFacade extends Facade {

    public UserFacade(AccessGuard accessGuard) {
        super(accessGuard);
    }

}
