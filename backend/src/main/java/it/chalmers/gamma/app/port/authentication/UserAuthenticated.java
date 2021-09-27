package it.chalmers.gamma.app.port.authentication;

import it.chalmers.gamma.app.domain.user.User;

public interface UserAuthenticated extends Authenticated {
    User get();
}
