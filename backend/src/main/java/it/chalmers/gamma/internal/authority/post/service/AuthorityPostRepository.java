package it.chalmers.gamma.internal.authority.post.service;

import java.util.List;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.domain.SuperGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityPostRepository extends JpaRepository<AuthorityPostEntity, AuthorityPostPK> {
    List<AuthorityPostEntity> findAuthoritiesById_AuthorityLevelName(AuthorityLevelName authorityLevelName);

    boolean existsById_AuthorityLevelName(AuthorityLevelName authorityLevelName);

}
