package it.chalmers.gamma.app;

import it.chalmers.gamma.domain.user.User;

public interface UserAuthenticated extends Authenticated {
    User get();
}
