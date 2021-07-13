package it.chalmers.gamma.app.authority;

import it.chalmers.gamma.adapter.secondary.jpa.authorityuser.AuthorityUserPK;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.AuthorityUser;

import java.util.List;

public interface AuthorityUserRepository {

    void create(AuthorityUser authorityUser);
    void delete(AuthorityUserPK authorityUserPK) throws AuthorityUserNotFoundException;

    List<AuthorityUser> getAll();
    List<AuthorityUser> getByAuthorityLevel(AuthorityLevelName authorityLevelName);

    boolean existsBy(AuthorityLevelName authorityLevelName);

    class AuthorityUserNotFoundException extends Exception { }

}
