package it.chalmers.gamma.app.authoritylevel.domain;

import it.chalmers.gamma.app.user.domain.UserAuthority;
import it.chalmers.gamma.app.user.domain.UserId;

import java.util.List;
import java.util.Optional;

public interface AuthorityLevelRepository {

    void create(AuthorityLevelName authorityLevelName) throws AuthorityLevelAlreadyExistsException;
    void delete(AuthorityLevelName authorityLevel) throws AuthorityLevelNotFoundException;
    void save(AuthorityLevel authorityLevel)
            throws AuthorityLevelNotFoundRuntimeException, SuperGroupNotFoundRuntimeException,
            SuperGroupPostNotFoundRuntimeException, UserNotFoundRuntimeException;

    List<AuthorityLevel> getAll();
    List<UserAuthority> getByUser(UserId userId);

    Optional<AuthorityLevel> get(AuthorityLevelName authorityLevelName);

    class AuthorityLevelAlreadyExistsException extends Exception {
        public AuthorityLevelAlreadyExistsException(String value) {
            super("Authority level: " + value + " already exists");
        }
    }
    class AuthorityLevelNotFoundException extends Exception { }

    class AuthorityLevelNotFoundRuntimeException extends RuntimeException { }

    /**
     * Can be avoided if you check that supergroups, posts, and users actually exists.
     * It happens when linking an authority level with one of the above, and it is not found in database.
     */
    class SuperGroupNotFoundRuntimeException extends RuntimeException { }
    class SuperGroupPostNotFoundRuntimeException extends RuntimeException { }
    class UserNotFoundRuntimeException extends RuntimeException { }


}
