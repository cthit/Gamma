package it.chalmers.gamma.app;

import org.springframework.stereotype.Service;

@Service
public class AuthorityFacade extends Facade {

    public AuthorityFacade(AccessGuard accessGuard) {
        super(accessGuard);
    }

}
