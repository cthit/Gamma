package it.chalmers.gamma.adapter.secondary.jpa.authoritysupergroup;

import it.chalmers.gamma.app.authority.AuthoritySuperGroupRepository;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.AuthoritySuperGroup;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthoritySuperGroupRepositoryAdapter implements AuthoritySuperGroupRepository {
    @Override
    public void create(AuthoritySuperGroup authoritySuperGroup) {

    }

    @Override
    public void delete(AuthoritySuperGroupPK authoritySuperGroupPK) {

    }

    @Override
    public List<AuthoritySuperGroup> getAll() {
        return null;
    }

    @Override
    public List<AuthoritySuperGroup> getByAuthorityLevel(AuthorityLevelName authorityLevelName) {
        return null;
    }

    @Override
    public boolean existsBy(AuthorityLevelName authorityLevelName) {
        return false;
    }
}
