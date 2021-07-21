package it.chalmers.gamma.app;

import org.springframework.stereotype.Service;

@Service
public class SuperGroupFacade extends Facade {

    public SuperGroupFacade(AccessGuard accessGuard) {
        super(accessGuard);
    }

}
