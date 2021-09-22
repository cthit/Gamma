package it.chalmers.gamma.app.authoritylevel;

import it.chalmers.gamma.domain.authoritylevel.AuthorityLevel;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.user.UserAuthority;
import it.chalmers.gamma.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface AuthorityLevelRepository {

    void create(AuthorityLevelName authorityLevelName);
    void create(AuthorityLevel authorityLevel);

    void delete(AuthorityLevelName authorityLevel);

    void save(AuthorityLevel authorityLevel);

    List<AuthorityLevel> getAll();
    List<UserAuthority> getByUser(UserId userId);

    Optional<AuthorityLevel> get(AuthorityLevelName authorityLevelName);
}
