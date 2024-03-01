package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Authority;
import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.Post;

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
