package it.chalmers.gamma.app.authority;

import it.chalmers.gamma.domain.authoritylevel.AuthorityLevel;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;

import java.util.List;
import java.util.Optional;

public interface AuthorityLevelRepository {

    void create(AuthorityLevelName authorityLevelName);
    void delete(AuthorityLevelName authorityLevel);
    
    void save(AuthorityLevel authorityLevel);

    List<AuthorityLevel> getAll();
    Optional<AuthorityLevel> get(AuthorityLevelName authorityLevelName);

}
