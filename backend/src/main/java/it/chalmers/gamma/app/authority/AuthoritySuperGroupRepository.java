package it.chalmers.gamma.app.authority;

import it.chalmers.gamma.adapter.secondary.jpa.authoritysupergroup.AuthoritySuperGroupPK;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.AuthoritySuperGroup;

import java.util.List;

public interface AuthoritySuperGroupRepository {

    void create(AuthoritySuperGroup authoritySuperGroup);
    void delete(AuthoritySuperGroupPK authoritySuperGroupPK);

    List<AuthoritySuperGroup> getAll();
    List<AuthoritySuperGroup> getByAuthorityLevel(AuthorityLevelName authorityLevelName);

    boolean existsBy(AuthorityLevelName authorityLevelName);

    class AuthoritySuperGroupNotFoundException extends Exception { }

}
