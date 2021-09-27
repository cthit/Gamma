package it.chalmers.gamma.app.port.repository;

import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevel;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.user.UserAuthority;
import it.chalmers.gamma.app.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface AuthorityLevelRepository {

    void create(AuthorityLevelName authorityLevelName);

    void delete(AuthorityLevelName authorityLevel);

    void save(AuthorityLevel authorityLevel);

    List<AuthorityLevel> getAll();
    List<UserAuthority> getByUser(UserId userId);

    Optional<AuthorityLevel> get(AuthorityLevelName authorityLevelName);
}
