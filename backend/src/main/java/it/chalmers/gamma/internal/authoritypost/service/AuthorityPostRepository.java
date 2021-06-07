package it.chalmers.gamma.internal.authoritypost.service;

import java.util.List;

import it.chalmers.gamma.domain.AuthorityLevelName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityPostRepository extends JpaRepository<AuthorityPostEntity, AuthorityPostPK> {
    List<AuthorityPostEntity> findAuthoritiesById_AuthorityLevelName(AuthorityLevelName authorityLevelName);

    boolean existsById_AuthorityLevelName(AuthorityLevelName authorityLevelName);

}
