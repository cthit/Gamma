package it.chalmers.gamma.adapter.secondary.jpa.authorityuser;

import it.chalmers.gamma.app.authority.AuthorityUserRepository;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.AuthorityUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorityUserRepositoryAdapter implements AuthorityUserRepository {
    @Override
    public void create(AuthorityUser authorityUser) {

    }

    @Override
    public void delete(AuthorityUserPK authorityUserPK) throws AuthorityUserNotFoundException {

    }

    @Override
    public List<AuthorityUser> getAll() {
        return null;
    }

    @Override
    public List<AuthorityUser> getByAuthorityLevel(AuthorityLevelName authorityLevelName) {
        return null;
    }

    @Override
    public boolean existsBy(AuthorityLevelName authorityLevelName) {
        return false;
    }
}
