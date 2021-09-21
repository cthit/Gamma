package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.domain.user.User;

public interface UserAuthenticated extends Authenticated {
    User get();
}
