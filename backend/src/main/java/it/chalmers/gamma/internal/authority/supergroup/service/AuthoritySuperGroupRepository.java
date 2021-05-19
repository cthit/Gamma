package it.chalmers.gamma.internal.authority.supergroup.service;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthoritySuperGroupRepository extends JpaRepository<AuthoritySuperGroup, AuthoritySuperGroupPK> {
    boolean existsById_AuthorityLevelName(AuthorityLevelName authorityLevelName);
}
