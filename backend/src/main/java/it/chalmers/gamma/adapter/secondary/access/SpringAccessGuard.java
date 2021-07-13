package it.chalmers.gamma.adapter.secondary.access;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.domain.Group;
import it.chalmers.gamma.app.domain.User;
import org.springframework.stereotype.Component;

@Component
public class SpringAccessGuard implements AccessGuard {

    @Override
    public void requireIsAdminOrApi() throws AccessDeniedException {

    }

    @Override
    public void requireUserIsPartOfGroup(Group group) throws AccessDeniedException {

    }

    @Override
    public void requireSignedIn() throws AccessDeniedException {

    }

    @Override
    public void requireIsUser(User user) throws AccessDeniedException {

    }
}
