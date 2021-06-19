package it.chalmers.gamma.internal.authoritysupergroup.service;

import it.chalmers.gamma.domain.AuthorityLevelName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthoritySuperGroupRepository extends JpaRepository<AuthoritySuperGroupEntity, AuthoritySuperGroupPK> {
    boolean existsById_AuthorityLevelName(AuthorityLevelName authorityLevelName);

    List<AuthoritySuperGroupEntity> findAuthoritySuperGroupEntitiesById_AuthorityLevelName(AuthorityLevelName authorityLevelName);
}
