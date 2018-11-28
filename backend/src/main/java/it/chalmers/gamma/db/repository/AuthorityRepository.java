package it.chalmers.gamma.db.repository;

import it.chalmers.gamma.db.entity.Authority;
import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.Post;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    Authority findById_FkitGroupAndAndId_Post(FKITGroup group, Post post);
    List<Authority> findAllByAuthorityLevel(AuthorityLevel authorityLevel);
    Authority findByInternalId(UUID id);
    void deleteByInternalId(UUID id);
}
