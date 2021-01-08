package it.chalmers.gamma.authority;

import it.chalmers.gamma.authority.Authority;
import it.chalmers.gamma.authority.AuthorityLevel;
import it.chalmers.gamma.supergroup.FKITSuperGroup;
import it.chalmers.gamma.post.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    Optional<Authority> findById_FkitSuperGroupAndId_Post(FKITSuperGroup superGroup, Post post);

    List<Authority> findAllByAuthorityLevel(AuthorityLevel authorityLevel);

    Optional<Authority> findByInternalId(UUID id);

    boolean existsByInternalId(UUID id);

    void deleteByInternalId(UUID id);
}
