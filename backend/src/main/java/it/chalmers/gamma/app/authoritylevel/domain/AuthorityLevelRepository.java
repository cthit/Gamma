package it.chalmers.gamma.app.authoritylevel.domain;

import it.chalmers.gamma.app.user.domain.UserAuthority;
import it.chalmers.gamma.app.user.domain.UserId;

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
