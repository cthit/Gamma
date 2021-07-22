package it.chalmers.gamma.app;

import it.chalmers.gamma.app.domain.User;

public interface UserAuthenticated extends Authenticated {
    User get();
}
