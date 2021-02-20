package it.chalmers.gamma.domain.authority.data;

import java.util.List;
import java.util.UUID;

import it.chalmers.gamma.domain.post.PostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {

    void removeById_SuperGroupIdAndId_PostIdAndId_AuthorityLevelName(UUID superGroupId,
                                                                     PostId postId,
                                                                     String authorityLevelName);

    boolean existsById_SuperGroupIdAndId_PostIdAndId_AuthorityLevelName(UUID superGroupId,
                                                                        PostId postId,
                                                                        String authorityLevelName);

    List<Authority> findAuthoritiesById_AuthorityLevelName(String authorityLevelName);
    List<Authority> findAuthoritiesById_SuperGroupIdAndId_PostId(UUID superGroupId, PostId postId);
}
