package it.chalmers.gamma.domain.authority.data;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {

    boolean existsById_SuperGroupIdAndId_PostIdAndId_AuthorityLevelName(UUID superGroupId,
                                                                        UUID postId,
                                                                        String authorityLevelName);

    List<Authority> findAuthoritiesById_AuthorityLevelName(String authorityLevelName);
    List<Authority> findAuthoritiesById_SuperGroupIdAndId_PostId(UUID superGroupId, UUID postId);
}
