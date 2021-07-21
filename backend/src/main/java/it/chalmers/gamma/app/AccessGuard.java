package it.chalmers.gamma.app;

import it.chalmers.gamma.app.domain.Group;
import it.chalmers.gamma.app.domain.User;

public interface AccessGuard {

    void requireIsAdmin() throws AccessDeniedException;
    void requireIsAdminOrApi() throws AccessDeniedException;
    void requireUserIsPartOfGroup(Group group) throws AccessDeniedException;
    void requireSignedIn() throws AccessDeniedException;
    void requireIsUser(User user) throws AccessDeniedException;

    class AccessDeniedException extends RuntimeException { }
}

