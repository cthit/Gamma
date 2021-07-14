package it.chalmers.gamma.app.authority;

import it.chalmers.gamma.app.domain.AuthorityLevel;
import it.chalmers.gamma.app.domain.AuthorityLevelName;

import java.util.List;
import java.util.Optional;

public interface AuthorityLevelRepository {

    void create(AuthorityLevelName authorityLevelName);
    void delete(AuthorityLevelName authorityLevel);
    
    void save(AuthorityLevel authorityLevel);

    List<AuthorityLevel> getAuthorityLevels();
    Optional<AuthorityLevel> getAuthorityLevel(AuthorityLevel authorityLevel);

}
