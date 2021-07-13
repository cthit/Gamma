package it.chalmers.gamma.adapter.secondary.jpa.authorityuser;

import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityUserJpaRepository extends JpaRepository<AuthorityUserEntity, AuthorityUserPK> {
    boolean existsById_AuthorityLevelName(AuthorityLevelName authorityLevelName);

    List<AuthorityUserEntity> findAuthorityUserEntitiesById_AuthorityLevelName(AuthorityLevelName authorityLevelName);
    List<AuthorityUserEntity> findAuthorityUserEntitiesById_UserId(UserId userId);
}
