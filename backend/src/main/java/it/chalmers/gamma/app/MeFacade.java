package it.chalmers.gamma.app;

import org.springframework.stereotype.Service;

@Service
public class MeFacade extends Facade {

    public MeFacade(AccessGuard accessGuard) {
        super(accessGuard);
    }

}
