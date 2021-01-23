package it.chalmers.gamma.authority;

import it.chalmers.gamma.authoritylevel.AuthorityLevel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    Optional<Authority> findById_SuperGroupIdAndId_PostId(UUID superGroupId, UUID postId);

    List<Authority> findAllByAuthorityLevelId(UUID authorityLevelId);

    Optional<Authority> findByInternalId(UUID id);

    boolean existsByInternalId(UUID id);

    void deleteByInternalId(UUID id);
}
