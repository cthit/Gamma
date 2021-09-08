package it.chalmers.gamma.adapter.secondary.jpa.authoritylevel;

import it.chalmers.gamma.app.authority.AuthorityLevelRepository;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevel;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
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
    public List<AuthorityLevel> getAll() {
        return null;
    }

    @Override
    public Optional<AuthorityLevel> get(AuthorityLevelName authorityLevelName) {
        return Optional.empty();
    }

}
