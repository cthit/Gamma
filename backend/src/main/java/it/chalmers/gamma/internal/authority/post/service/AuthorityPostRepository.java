package it.chalmers.gamma.internal.authority.post.service;

import java.util.List;

import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.post.service.PostId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityPostRepository extends JpaRepository<AuthorityPost, AuthorityPostPK> {
    List<AuthorityPost> findAuthoritiesById_AuthorityLevelName(AuthorityLevelName authorityLevelName);
    List<AuthorityPost> findAuthoritiesById_SuperGroupIdAndId_PostId(SuperGroupId superGroupId, PostId postId);

    boolean existsById_AuthorityLevelName(AuthorityLevelName authorityLevelName);

}
