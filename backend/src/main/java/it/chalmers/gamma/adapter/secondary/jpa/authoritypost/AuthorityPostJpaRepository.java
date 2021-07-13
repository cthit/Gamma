package it.chalmers.gamma.adapter.secondary.jpa.authoritypost;

import java.util.List;

import it.chalmers.gamma.app.domain.AuthorityLevelName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityPostJpaRepository extends JpaRepository<AuthorityPostEntity, AuthorityPostPK> {
    List<AuthorityPostEntity> findAuthoritiesById_AuthorityLevelName(AuthorityLevelName authorityLevelName);

    boolean existsById_AuthorityLevelName(AuthorityLevelName authorityLevelName);

}
