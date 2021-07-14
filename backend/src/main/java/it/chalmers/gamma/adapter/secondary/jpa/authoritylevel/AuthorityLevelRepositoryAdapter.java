package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.app.authority.AuthorityLevelRepository;
import it.chalmers.gamma.app.domain.AuthorityLevel;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorityLevelRepositoryAdapter implements AuthorityLevelRepository {

    private AuthorityLevelJpaRepository repository;

    @Override
    public void create(AuthorityLevelName authorityLevelName) {
        repository.save(new AuthorityLevelEntity(authorityLevelName));
    }

    @Override
    public void delete(AuthorityLevelName authorityLevelName) {
        repository.deleteById(authorityLevelName.value());
    }

    @Override
    public void save(AuthorityLevel authorityLevel) {
        repository.save(new AuthorityLevelEntity(authorityLevel));
    }

    @Override
    public List<AuthorityLevel> getAuthorityLevels() {
        return null;
    }

    @Override
    public Optional<AuthorityLevel> getAuthorityLevel(AuthorityLevel authorityLevel) {
        return Optional.empty();
    }
}
