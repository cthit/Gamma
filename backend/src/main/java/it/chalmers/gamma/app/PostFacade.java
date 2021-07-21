package it.chalmers.gamma.app;

import org.springframework.stereotype.Service;

@Service
public class PostFacade extends Facade {

    public PostFacade(AccessGuard accessGuard) {
        super(accessGuard);
    }

}
