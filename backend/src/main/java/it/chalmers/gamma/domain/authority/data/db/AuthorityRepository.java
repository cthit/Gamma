package it.chalmers.gamma.domain.authority.data.db;

import java.util.List;

import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, AuthorityPK> {
    List<Authority> findAuthoritiesById_AuthorityLevelName(String authorityLevelName);
    List<Authority> findAuthoritiesById_SuperGroupIdAndId_PostId(SuperGroupId superGroupId, PostId postId);
}
