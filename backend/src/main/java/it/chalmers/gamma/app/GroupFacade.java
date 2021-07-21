package it.chalmers.gamma.app;

import org.springframework.stereotype.Service;

@Service
public class GroupFacade extends Facade {

    public GroupFacade(AccessGuard accessGuard) {
        super(accessGuard);
    }

}
