package it.chalmers.delta.db.repository;

import it.chalmers.delta.db.entity.Authority;
import it.chalmers.delta.db.entity.AuthorityLevel;
import it.chalmers.delta.db.entity.FKITSuperGroup;
import it.chalmers.delta.db.entity.Post;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    Authority findById_FkitSuperGroupAndId_Post(FKITSuperGroup superGroup, Post post);

    List<Authority> findAllByAuthorityLevel(AuthorityLevel authorityLevel);

    Authority findByInternalId(UUID id);

    void deleteByInternalId(UUID id);
}
