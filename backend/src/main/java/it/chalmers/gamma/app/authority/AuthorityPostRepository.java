package it.chalmers.gamma.app.authority;

import it.chalmers.gamma.adapter.secondary.jpa.authoritypost.AuthorityPostPK;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.AuthorityPost;

import java.util.List;

public interface AuthorityPostRepository {

    void create(AuthorityPost authorityPost);
    void delete(AuthorityPostPK authorityPostPK) throws AuthorityPostNotFoundException;

    List<AuthorityPost> getAll();
    List<AuthorityPost> getByAuthorityLevel(AuthorityLevelName authorityLevelName);

    boolean existsBy(AuthorityLevelName authorityLevelName);

    class AuthorityPostNotFoundException extends Exception { }

}
