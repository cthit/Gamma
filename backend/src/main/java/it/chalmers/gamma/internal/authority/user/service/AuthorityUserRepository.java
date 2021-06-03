package it.chalmers.gamma.internal.authority.user.service;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityUserRepository extends JpaRepository<AuthorityUserEntity, AuthorityUserPK> {
    boolean existsById_AuthorityLevelName(AuthorityLevelName authorityLevelName);

    List<AuthorityUserEntity> findAuthorityUserEntitiesById_AuthorityLevelName(AuthorityLevelName authorityLevelName);
}
