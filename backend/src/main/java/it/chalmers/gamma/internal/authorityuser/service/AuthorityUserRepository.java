package it.chalmers.gamma.internal.authorityuser.service;

import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.domain.AuthorityLevelName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityUserRepository extends JpaRepository<AuthorityUserEntity, AuthorityUserPK> {
    boolean existsById_AuthorityLevelName(AuthorityLevelName authorityLevelName);

    List<AuthorityUserEntity> findAuthorityUserEntitiesById_AuthorityLevelName(AuthorityLevelName authorityLevelName);
    List<AuthorityUserEntity> findAuthorityUserEntitiesById_UserId(UserId userId);
}
