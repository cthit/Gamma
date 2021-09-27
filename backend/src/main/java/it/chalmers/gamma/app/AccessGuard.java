package it.chalmers.gamma.app;

import it.chalmers.gamma.app.domain.group.Group;
import it.chalmers.gamma.app.domain.user.User;

public interface AccessGuard {

    /**
     * Must have the authority level admin
     */
    void requireIsAdmin() throws AccessDeniedException;
    void requireIsAdminOrApi() throws AccessDeniedException;
    void requireUserIsPartOfGroup(Group group) throws AccessDeniedException;
    void requireSignedIn() throws AccessDeniedException;
    void requireIsUser(User user) throws AccessDeniedException;
    void requireNotSignedIn() throws AccessDeniedException;
    void requireIsClientApi() throws AccessDeniedException;

    class AccessDeniedException extends RuntimeException { }
}

