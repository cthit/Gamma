package it.chalmers.gamma.internal.authority.user.service;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityUserRepository extends JpaRepository<AuthorityUserEntity, AuthorityUserPK> {
    boolean existsById_AuthorityLevelName(AuthorityLevelName authorityLevelName);
}
