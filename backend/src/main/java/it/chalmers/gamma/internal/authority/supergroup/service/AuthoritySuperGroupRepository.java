package it.chalmers.gamma.internal.authority.supergroup.service;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthoritySuperGroupRepository extends JpaRepository<AuthoritySuperGroupEntity, AuthoritySuperGroupPK> {
    boolean existsById_AuthorityLevelName(AuthorityLevelName authorityLevelName);

    List<AuthoritySuperGroupEntity> findAuthoritySuperGroupEntitiesById_AuthorityLevelName(AuthorityLevelName authorityLevelName);
}
